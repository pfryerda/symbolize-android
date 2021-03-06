package app.symbolize.Game;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;
import app.symbolize.Animation.SymbolizeAnimation;
import app.symbolize.Common.Line;
import app.symbolize.Common.Posn;
import app.symbolize.Common.Session;
import app.symbolize.DataAccess.OptionsDataAccess;

/*
 * A class in charge of interpreting client touch interactions
 */
public class GameTouchHandler {
    // Constants
    //-----------

    public static final short TAPTHRESHOLD = 325;
    public static final short DOUBLETAPTHRESHOLD = 5 * TAPTHRESHOLD / 8;
    public static final short FLIPPINGTHRESHOLD = GameView.SCALING / 7;
    public static final int MINLINESIZESQR = (int) (17.5 * GameView.SCALING);
    public static final short ERASEDELAY = 250;
    public static final short DRAGDELAY = 375;


    // Fields
    //-------

    private GameTouchListener listener;

    private Posn point_one;
    private boolean is_point_one_down;
    private Posn point_one_end;

    private Posn point_two;
    private boolean is_point_two_down;
    private Posn point_two_end;

    private Posn previous_point;
    private Posn current_point;

    private boolean is_erase_delay_done;
    private Timer erase_timer;
    
    private Line drag_line;
    private boolean in_drag_mode;
    private Timer drag_timer;

    private boolean single_tap_complete;
    private Timer double_tap_timer;

    private long start_time;
    private boolean in_double_touch;


    // Interface
    //-----------

    interface GameTouchListener extends View.OnTouchListener {
        public void onDraw( final Line line );
        public void onErase( final Posn point );
        public void onFingerUp();
        public void onFingerMove( final Line line, final Posn point );
        public void onTap( final Posn point );
        public void onDoubleTap( final Posn point );
        public Line onDragStart( final Posn point );
        public void onDragEnd( final Line line );
        public void onEnterDoubleTouch();
        public void onRotateRight();
        public void onRotateLeft();
        public void onFlipHorizontally();
        public void onFlipVertically();
    }


    // Singleton setup
    //-----------------

    private static GameTouchHandler instance = new GameTouchHandler();

    public static GameTouchHandler Get_instance() {
        return instance;
    }


    // Constructor
    //--------------

    private GameTouchHandler() {
        // Set up timers so initial timer.cancel does not crash
        erase_timer = new Timer();
        drag_timer = new Timer();
        double_tap_timer = new Timer();

        single_tap_complete = false;

        // Set variables to default
        reset_vars();
    }


    // Setter methods
    //----------------

    public void Set_listener( GameTouchListener listener ) {
        this.listener = listener;
    }


    // Main method
    //-------------

    public boolean handle_touch( final MotionEvent event ) {
        if ( !SymbolizeAnimation.InAnimation ) {
            switch ( event.getActionMasked() ) {

                case MotionEvent.ACTION_DOWN: {
                    handle_finger_one_down( event );
                    return true;
                }

                case MotionEvent.ACTION_POINTER_DOWN: {
                    handle_finger_two_down( event );
                    return true;
                }

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP: {
                    // Handle finger up
                    if ( ( event.getActionIndex() == 1 ) || !is_point_one_down ) {
                        handle_finger_two_up( event );
                    } else if ( ( event.getActionIndex() == 0 ) || !is_point_two_down ) {
                        handle_finger_one_up( event );
                    }
                    
                    // Handle end of gesture
                    if ( in_double_touch && !is_point_one_down && !is_point_two_down ) {
                        handle_end_of_gesture();
                    }

                    // Stop timers
                    is_erase_delay_done = false;
                    erase_timer.cancel();
                    in_drag_mode = false;
                    drag_timer .cancel();
                    return true;
                }

                case MotionEvent.ACTION_MOVE: {
                    if ( !in_double_touch && ( event.getActionIndex() == 0 ) && point_one != null ) {
                        previous_point = ( current_point == null ) ? point_one : current_point;
                        current_point = get_point( event );
                        if( in_drag_mode ) {
                            handle_drag();
                        } else {
                            handle_finger_move();
                        }
                    }
                    return true;
                }

                case MotionEvent.ACTION_CANCEL:
                default: {
                    return false;
                }
            }
        } else {
            return true;
        }
    }


    // Private Methods
    //-----------------

    /*
     * Gets the point from the motion event and then scales the point accordingly.
     * Note: Also treats points off the canvas as though the user stop at the edge.
     *
     * @param: MotionEvent event: The motion event that contains information about where the user touched
     */
    private Posn get_point( final MotionEvent event ) {
        // Scale point and return it
        Posn point = new Posn( event.getX( event.getActionIndex() ), event.getY( event.getActionIndex() ) );
        point.Scale();
        return point;
    }

    /*
     * Reverts all the class's fields to their default values.
     * This is usually done at the end of a gesture so that every gesture
     * has the same values at their start.
     */
    private void reset_vars() {
        point_one = null;
        is_point_one_down = false;
        point_one_end = null;

        point_two = null;
        is_point_two_down = false;
        point_two_end = null;

        previous_point = null;
        current_point = null;
        
        is_erase_delay_done = false;
        
        in_drag_mode = false;
        drag_line = null;

        in_double_touch = false;
    }

    /*
     * This method is called after a two-finger gesture and checks to see if the
     * given gesture resembles a flipping gesture.
     */
    private boolean attempt_to_flip() {
        if ( point_one == null || point_one_end == null || point_two == null || point_two_end == null ) {
            // Something went wrong bail!
            return false;
        }
        else if ( ( ( point_one.x() < GameView.SCALING/2 && point_two.x() < GameView.SCALING/2 && point_two_end.x() > GameView.SCALING/2 && point_two_end.x() > GameView.SCALING/2 ) ||
                    ( point_one.x() > GameView.SCALING/2 && point_two.x() > GameView.SCALING/2 && point_two_end.x() < GameView.SCALING/2 && point_two_end.x() < GameView.SCALING/2 ) ) &&
                    ( point_one_end.y() - FLIPPINGTHRESHOLD <= point_one.y() && point_one.y() <= point_one_end.y() + FLIPPINGTHRESHOLD &&
                      point_two_end.y() - FLIPPINGTHRESHOLD <= point_two.y() && point_two.y() <= point_two_end.y() + FLIPPINGTHRESHOLD ) )
        {
            listener.onFlipHorizontally();
            return true;
        }
        else if ( ( ( point_one.y() < GameView.SCALING/2 && point_two.y() < GameView.SCALING/2 && point_two_end.y() > GameView.SCALING/2 && point_two_end.y() > GameView.SCALING/2 ) ||
                    ( point_one.y() > GameView.SCALING/2 && point_two.y() > GameView.SCALING/2 && point_two_end.y() < GameView.SCALING/2 && point_two_end.y() < GameView.SCALING/2 ) ) &&
                    ( point_one_end.x() - FLIPPINGTHRESHOLD <= point_one.x() && point_one.x() <= point_one_end.x() + FLIPPINGTHRESHOLD &&
                      point_two_end.x() - FLIPPINGTHRESHOLD <= point_two.x() && point_two.x() <= point_two_end.x() + FLIPPINGTHRESHOLD ) )
        {
            listener.onFlipVertically();
            return true;
        }
        return false;
    }

    /*
     * This method is called if attempt_to_flip fails and checks to see if the
      * given gesture resembles a rotating gesture.
     */
    private boolean attempt_to_rotate() {
        if ( point_one == null || point_one_end == null || point_two == null || point_two_end == null ) {
            // Something went wrong bail!
            return false;
        }
        if ( point_one.y() >= point_two.y() ) {
            // Point one is the higher point, point two is the lower

            if ( ( point_one.x() >= point_one_end.x() ) && ( point_two.x() <= point_two_end.x() ) ) {
                listener.onRotateRight();
                return true;
            }
            else if ( ( point_one.x() <= point_one_end.x() ) && (  point_two.x() >= point_two_end.x() ) ) {
                listener.onRotateLeft();
                return true;
            } else if ( ( point_one.y() <= point_one_end.y() ) && ( point_two.y() >= point_two_end.y() ) ) {
                listener.onRotateRight();
                return true;
            }
            else if ( ( point_one.y() >= point_one_end.y() ) && (  point_two.y() <= point_two_end.y() ) ) {
                listener.onRotateLeft();
                return true;
            }

        } else {
            // Point two is the higher point, point one is the lower

            if ( ( point_one.x() <= point_one_end.x() ) && (  point_two.x() >= point_two_end.x() ) ) {
                listener.onRotateRight();
                return true;
            }
            else if ( ( point_one.x() >= point_one_end.x() ) && (  point_two.x() <= point_two_end.x() ) ) {
                listener.onRotateLeft();
                return true;
            } else if ( ( point_one.y() >= point_one_end.y() ) && (  point_two.y() <= point_two_end.y() ) ) {
                listener.onRotateRight();
                return true;
            }
            else if ( ( point_one.y() <= point_one_end.y() ) && (  point_two.y() >= point_two_end.y() ) ) {
                listener.onRotateLeft();
                return true;
            }

        }
        return false;
    }

    /*
     * Controls how the states change based off the first finger down, and informs the Game
     * controller/activity accordingly.
     *
     * @param MotionEvent event: The motion event that contains information about where the user touched
     */
    private void handle_finger_one_down( final MotionEvent event ) {
        point_one = get_point( event );
        is_point_one_down = true;

        // Start timers
        erase_timer.cancel();
        erase_timer = new Timer();
        erase_timer.schedule( new TimerTask() {
            @Override
            public void run() {
                is_erase_delay_done = true;
                erase_timer.cancel();
            }
        }, ERASEDELAY );

        drag_timer.cancel();
        drag_timer = new Timer();
        drag_timer.schedule( new TimerTask() {

            @Override
            public void run() {
                drag_line = listener.onDragStart( point_one );
                if ( drag_line != null ) {
                    in_drag_mode = true;
                }
                drag_timer.cancel();
            }
        }, DRAGDELAY );

        start_time = System.currentTimeMillis();
    }

    /*
     * Controls how the states change based off the first finger up, and informs the Game
     * controller/activity accordingly.
     *
     * @param MotionEvent event: The motion event that contains information about where the user touched
     */
    private void handle_finger_one_up( final MotionEvent event ) {
        point_one_end = get_point( event );
        is_point_one_down = false;

        if ( !in_double_touch ) {
            // End of draw, drag, or tap

            listener.onFingerUp();
            long end_time = System.currentTimeMillis();
            if ( in_drag_mode ) {
                listener.onDragEnd( drag_line );
            } else {
                Line line = new Line( point_one, point_one_end, Line.User_drawn );
                if ( line.Distance_squared() >= MINLINESIZESQR ) {
                    if ( OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_SNAP_DRAWING ) && !Session.Get_instance().Is_in_world_view()) {
                        line = new Line( point_one.Snap( true ), point_one_end.Snap( false ), Line.User_drawn );
                    }
                    listener.onDraw( line );
                } else if ( ( end_time - start_time) <= TAPTHRESHOLD ) {
                    if ( single_tap_complete ) {
                        listener.onDoubleTap( point_one_end );
                        single_tap_complete = false;
                        double_tap_timer.cancel();
                        double_tap_timer = new Timer();
                    } else {
                        listener.onTap( point_one_end );
                        single_tap_complete = true;
                        double_tap_timer.cancel();
                        double_tap_timer = new Timer();
                        double_tap_timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                single_tap_complete = false;
                            }
                        }, DOUBLETAPTHRESHOLD);
                    }
                }
            }
            start_time = end_time;
            reset_vars();
        }
    }

    /*
     * Controls how the states change based off the second finger down, and informs the Game
     * controller/activity accordingly
     *
     * @param MotionEvent event: The motion event that contains information about where the user touched
     */
    private void handle_finger_two_down( final MotionEvent event ) {
        if( in_drag_mode ) {
            // Second finger down while drag, cancel to drag!
            listener.onDragEnd( drag_line );
            reset_vars();
        } else {
            // Set up variables for gesture detection
            listener.onEnterDoubleTouch();
            point_two = get_point( event );
            is_point_two_down = true;
            in_double_touch = true;
        }
    }

    /*
     * Controls how the states change based off the second finger up, and informs the Game
     * controller/activity accordingly
     *
     * @param MotionEvent event: The motion event that contains information about where the user touched
     */
    private void handle_finger_two_up( final  MotionEvent event ) {
        point_two_end = get_point( event );
        is_point_two_down = false;
    }

    /*
     * Controls how the states change based off the end of a multi touch gesture, and informs the Game
     * controller/activity accordingly
     */
    private void handle_end_of_gesture() {
        boolean flipped = attempt_to_flip();
        if ( !flipped ) {
            attempt_to_rotate();
        }
        reset_vars();
    }

    /*
     * Controls how the states change based off a drag of the finger while dragging a line, and informs t
     * he Game controller/activity accordingly
     */
    private void handle_drag() {
        drag_line.Translate( (short) ( current_point.x() - previous_point.x() ),
                             (short) ( current_point.y() - previous_point.y() ) );
        listener.onFingerMove( drag_line, null );
    }

    /*
     * Controls how the states change based off the first finger moving, and informs the Game
     * controller/activity accordingly
     */
    private void handle_finger_move() {
        if ( point_one.Distance_squared( current_point ) > ( Posn.DRAWING_THRESHOLD * Posn.DRAWING_THRESHOLD ) ) {
            drag_timer.cancel();
        }
        Line line;
        if ( OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_SNAP_DRAWING ) && !Session.Get_instance().Is_in_world_view() ) {
            line = new Line( point_one.Snap( true ), current_point.Snap( false ), Line.App_drawn );
        } else {
            line = new Line( point_one, current_point, Line.App_drawn );
        }
        listener.onFingerMove( line, current_point );
        if ( is_erase_delay_done ) {
            listener.onErase( current_point );
        }
    }
}
