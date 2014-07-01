package symbolize.app.Game;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import symbolize.app.Common.Animation.SymbolizeAnimation;
import symbolize.app.Common.Line;
import symbolize.app.Common.Enum.Owner;
import symbolize.app.Common.Posn;


public class GameTouchListener implements View.OnTouchListener {
    // Satic Fields
    //--------------

    public static final int TAPTHRESHOLD = 250;
    public static final int FLIPPINGTHRESHOLD = 140;
    public static final int MINLINESIZESQR = 10000;
    public static final int ERASEDELAY = 250;


    // Fields
    //-------

    private boolean is_erase_delay_done;
    private Timer timer;

    private Posn point_one;
    private boolean is_point_one_down;
    private Posn point_one_end;

    private Posn point_two;
    private boolean is_point_two_down;
    private Posn point_two_end;

    private long start_time;
    private boolean in_double_touch;


    // Constructor
    //--------------

    public GameTouchListener() {
        timer = new Timer();
        reset_vars();
    }


    // Main method
    //--------------

    public boolean onTouch( final View v, final MotionEvent event ) {
        if ( !SymbolizeAnimation.InAnimation ) {
            switch ( event.getActionMasked() ) {

                case MotionEvent.ACTION_DOWN: {                                 // First finger down
                    point_one = get_point( event );
                    is_point_one_down = true;

                    timer.cancel();
                    timer = new Timer();
                    timer.schedule( new TimerTask() {
                        @Override
                        public void run() {
                            is_erase_delay_done = true;
                            timer.cancel();
                        }
                    }, ERASEDELAY );

                    start_time = System.currentTimeMillis();
                    return true;
                }

                case MotionEvent.ACTION_POINTER_DOWN: {                         // Second finger down
                    onEnterDobuleTouch();
                    point_two = get_point( event );
                    is_point_two_down = true;
                    in_double_touch = true;
                    return true;
                }

                case MotionEvent.ACTION_UP:                                     // First finger up
                case MotionEvent.ACTION_POINTER_UP: {                           // Second finger up
                    if ( ( event.getActionIndex() == 1 ) || !is_point_one_down ) { // Original second finger up
                        point_two_end = get_point(event);
                        is_point_two_down = false;
                    } else {                                                    // Original first finger up
                        point_one_end = get_point(event);
                        is_point_one_down = false;

                        if ( !in_double_touch ) {
                            onFingerUp();
                            long endtime = System.currentTimeMillis();
                            Line line = new Line( point_one, point_one_end, Owner.User );
                            if ( line.Distance_squared() >= MINLINESIZESQR ) {
                                onDraw( new Line( point_one, point_one_end, Owner.User ) );
                            } else if ( ( endtime - start_time) <= TAPTHRESHOLD ) {
                                onTap( point_one_end );
                            }
                            start_time = endtime;
                            reset_vars();
                        }
                    }

                    if ( in_double_touch && !is_point_one_down && !is_point_two_down ) {
                        boolean flipped = attempt_to_flip();
                        if ( !flipped ) {
                            attempt_to_rotate();
                        }
                        reset_vars();
                    }

                    is_erase_delay_done = false;
                    timer.cancel();
                    return true;
                }

                case MotionEvent.ACTION_MOVE: {                                 // Finger moves
                    if ( !in_double_touch && point_one != null ) {
                        Posn pointTemp = get_point( event );
                        onFingerMove( new Line( point_one, pointTemp, Owner.App), pointTemp );
                        if ( is_erase_delay_done ) {
                            onErase( pointTemp );
                        }
                    }
                    return true;
                }

                case MotionEvent.ACTION_CANCEL: {
                    return true;
                }

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
        // Get point off of screen
        float touchX = event.getX( event.getActionIndex() );
        float touchY = event.getY( event.getActionIndex() );

        // Scale point and return it
        Posn point = new Posn( Math.round( touchX ), Math.round( touchY ) );
        point.Scale();
        return point;
    }

    /*
     * Reverts all the class's fields to their default values.
     * This is usually done at the end of a gesture so that every gesture
     * has the same values at their start.
     */
    private void reset_vars() {
        is_erase_delay_done = false;

        point_one = null;
        is_point_one_down = false;
        point_one_end = null;

        point_two = null;
        is_point_two_down = false;
        point_two_end = null;

        in_double_touch = false;
    }

    /*
     * This method is called after a two-finger gesture and checks to see if the
     * given gesture resembles a flipping gesture.
     */
    private boolean attempt_to_flip() {
        if ( point_one == null || point_one_end == null || point_two == null || point_two_end == null ) {
            return false;
        }
        else if ( ( ( point_one.x() < GameActivity.SCALING/2 && point_two.x() < GameActivity.SCALING/2 && point_two_end.x() > GameActivity.SCALING/2 && point_two_end.x() > GameActivity.SCALING/2 ) ||
                    ( point_one.x() > GameActivity.SCALING/2 && point_two.x() > GameActivity.SCALING/2 && point_two_end.x() < GameActivity.SCALING/2 && point_two_end.x() < GameActivity.SCALING/2 ) ) &&
                    ( point_one_end.y() - FLIPPINGTHRESHOLD <= point_one.y() && point_one.y() <= point_one_end.y() + FLIPPINGTHRESHOLD &&
                      point_two_end.y() - FLIPPINGTHRESHOLD <= point_two.y() && point_two.y() <= point_two_end.y() + FLIPPINGTHRESHOLD ) ) {
            onFlipHorizontally();
            return true;
        }
        else if ( ( ( point_one.y() < GameActivity.SCALING/2 && point_two.y() < GameActivity.SCALING/2 && point_two_end.y() > GameActivity.SCALING/2 && point_two_end.y() > GameActivity.SCALING/2 ) ||
                    ( point_one.y() > GameActivity.SCALING/2 && point_two.y() > GameActivity.SCALING/2 && point_two_end.y() < GameActivity.SCALING/2 && point_two_end.y() < GameActivity.SCALING/2 ) ) &&
                    ( point_one_end.x() - FLIPPINGTHRESHOLD <= point_one.x() && point_one.x() <= point_one_end.x() + FLIPPINGTHRESHOLD &&
                      point_two_end.x() - FLIPPINGTHRESHOLD <= point_two.x() && point_two.x() <= point_two_end.x() + FLIPPINGTHRESHOLD ) ) {
            onFlipVertically();
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
            return false;
        }
        if ( point_one.y() >= point_two.y() ) {
            if ( ( point_one.x() >= point_one_end.x() ) && ( point_two.x() <= point_two_end.x() ) ) {
                onRotateRight();
                return true;
            }
            else if ( ( point_one.x() <= point_one_end.x() ) && (  point_two.x() >= point_two_end.x() ) ) {
                onRotateLeft();
                return true;
            }
        } else {
            if ( ( point_one.x() <= point_one_end.x() ) && (  point_two.x() >= point_two_end.x() ) ) {
                onRotateRight();
                return true;
            }
            else if ( ( point_one.x() >= point_one_end.x() ) && (  point_two.x() <= point_two_end.x() ) ) {
                onRotateLeft();
                return true;
            }
        }
        return false;
    }


    // Methods to be overridden in MainActivity
    //------------------------------------------

    public void onDraw( final Line line ) {

    }

    public void onErase( final Posn point ) {

    }

    public void onFingerUp() {

    }

    public void onFingerMove( final Line line, final Posn point ) {

    }

    public void onTap( final Posn point ) {

    }

    public void onEnterDobuleTouch() {

    }

    public void onRotateRight() {

    }

    public void onRotateLeft() {

    }

    public void onFlipHorizontally() {

    }

    public void onFlipVertically() {

    }
}
