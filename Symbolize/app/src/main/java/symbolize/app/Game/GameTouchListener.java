package symbolize.app.Game;

import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import symbolize.app.Common.Line;
import symbolize.app.Common.Owner;
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

    private boolean isEraseDelayDone;
    private Timer timer;

    private Posn pointOne;
    private boolean isPointOneDown;
    private Posn pointOneEnd;

    private Posn pointTwo;
    private boolean isPointTwoDown;
    private Posn pointTwoEnd;

    private long startTime;
    private boolean inDoubleTouch;


    // Constructor
    //--------------

    public GameTouchListener() {
        timer = new Timer();
        resetVars();
    }


    // Primary method
    //----------------

    public boolean onTouch( View v, MotionEvent event ) {
        switch ( event.getActionMasked() ) {

            case MotionEvent.ACTION_DOWN: {                                 // First finger down
                pointOne = getPoint( event );
                isPointOneDown = true;

                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isEraseDelayDone = true;
                        timer.cancel();
                    }
                }, ERASEDELAY );

                startTime = System.currentTimeMillis();
                return true;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {                         // Second finger down
                onEnterDobuleTouch();
                pointTwo = getPoint( event );
                isPointTwoDown = true;
                inDoubleTouch = true;
                return true;
            }

            case MotionEvent.ACTION_UP:                                     // First finger up
            case MotionEvent.ACTION_POINTER_UP: {                           // Second finger up
                if ( ( event.getActionIndex() == 1 ) || !isPointOneDown ) { // Original second finger up
                    pointTwoEnd = getPoint( event );
                    isPointTwoDown = false;
                } else {                                                    // Original first finger up
                    pointOneEnd = getPoint(event);
                    isPointOneDown = false;

                    if ( !inDoubleTouch ) {
                        onFingerUp();
                        long endtime = System.currentTimeMillis();
                        Line line = new Line( pointOne, pointOneEnd, Owner.User );
                        if ( line.distSqr() >= MINLINESIZESQR ) {
                            onDraw( new Line( pointOne, pointOneEnd, Owner.User ) );
                        } else if( ( endtime - startTime ) <= TAPTHRESHOLD  ) {
                            onTap( pointOneEnd );
                        }
                        startTime = endtime;
                        resetVars();
                    }
                }

                if ( inDoubleTouch && !isPointOneDown && !isPointTwoDown ) {
                    boolean flipped = attemptToFlip();
                    if ( !flipped ) {
                        attemptToRotate();
                    }
                    resetVars();
                }

                isEraseDelayDone = false;
                timer.cancel();
                return true;
            }

            case MotionEvent.ACTION_MOVE: {                                 // Finger moves
                if ( !inDoubleTouch ) {
                    Posn pointTemp = getPoint( event );
                    onFingerMove( new Line( pointOne, pointTemp ), pointTemp );
                    if ( isEraseDelayDone ) {
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
    }


    // Methods
    //---------

    /*
     * Gets the point from the motion event and then scales the point accordingly.
     * Note: Also treats points off the canvas as though the user stop at the edge.
     *
     * @param: MotionEvent event: The motion event that contains information about where the user touched
     */
    Posn getPoint( MotionEvent event ) {
        // Get point off of screen
        float touchX = event.getX( event.getActionIndex() );
        float touchY = event.getY( event.getActionIndex() );

        // Scaled point accordingly and don't allow points off canvas
        int scaledX = Math.min( GameActivity.SCALING,
                Math.max( 0, Math.round( touchX * GameActivity.SCALING / GameActivity.SCREENSIZE.x ) ) );
        int scaledY = Math.min( GameActivity.SCALING,
                Math.max( 0, Math.round( touchY * GameActivity.SCALING / GameActivity.SCREENSIZE.x ) ) );

        // If in DEVMODE snap points to grid
        if ( GameActivity.DEVMODE ) {
            scaledX = scaledX - ( scaledX % ( GameActivity.SCALING / 10 ) );
            scaledY = scaledY - ( scaledY % ( GameActivity.SCALING / 10 ) );
        }

        return new Posn( scaledX, scaledY );
    }

    /*
     * Reverts all the class's fields to their default values.
     * This is usually done at the end of a gesture so that every gesture
     * has the same values at their start.
     */
    void resetVars() {
        isEraseDelayDone = false;

        pointOne = null;
        isPointOneDown = false;
        pointOneEnd = null;

        pointTwo = null;
        isPointTwoDown = false;
        pointTwoEnd = null;

        inDoubleTouch = false;
    }

    /*
     * This method is called after a two-finger gesture and checks to see if the
     * given gesture resembles a flipping gesture.
     */
    boolean attemptToFlip() {
        if ( ( ( pointOne.x() < GameActivity.SCALING/2 && pointTwo.x() < GameActivity.SCALING/2 && pointTwoEnd.x() > GameActivity.SCALING/2 && pointTwoEnd.x() > GameActivity.SCALING/2 ) ||
               ( pointOne.x() > GameActivity.SCALING/2 && pointTwo.x() > GameActivity.SCALING/2 && pointTwoEnd.x() < GameActivity.SCALING/2 && pointTwoEnd.x() < GameActivity.SCALING/2 ) ) &&
               ( pointOneEnd.y() - FLIPPINGTHRESHOLD <= pointOne.y() && pointOne.y() <= pointOneEnd.y() + FLIPPINGTHRESHOLD &&
                 pointTwoEnd.y() - FLIPPINGTHRESHOLD <= pointTwo.y() && pointTwo.y() <= pointTwoEnd.y() + FLIPPINGTHRESHOLD ) ) {
            onFlipHorizontally();
            return true;
        }
        else if ( ( ( pointOne.y() < GameActivity.SCALING/2 && pointTwo.y() < GameActivity.SCALING/2 && pointTwoEnd.y() > GameActivity.SCALING/2 && pointTwoEnd.y() > GameActivity.SCALING/2 ) ||
                    ( pointOne.y() > GameActivity.SCALING/2 && pointTwo.y() > GameActivity.SCALING/2 && pointTwoEnd.y() < GameActivity.SCALING/2 && pointTwoEnd.y() < GameActivity.SCALING/2 ) ) &&
                    ( pointOneEnd.x() - FLIPPINGTHRESHOLD <= pointOne.x() && pointOne.x() <= pointOneEnd.x() + FLIPPINGTHRESHOLD &&
                      pointTwoEnd.x() - FLIPPINGTHRESHOLD <= pointTwo.x() && pointTwo.x() <= pointTwoEnd.x() + FLIPPINGTHRESHOLD ) ) {
            onFlipVertically();
            return true;
        }
        return false;
    }

    /*
     * This method is called if attemptToFlip fails and checks to see if the
      * given gesture resembles a rotating gesture.
     */
    boolean attemptToRotate() {
        if ( pointOne.y() >= pointTwo.y() ) {
            if ( ( pointOne.x() >= pointOneEnd.x() ) && ( pointTwo.x() <= pointTwoEnd.x() ) ) {
                onRotateRight();
                return true;
            }
            else if ( ( pointOne.x() <= pointOneEnd.x() ) && (  pointTwo.x() >= pointTwoEnd.x() ) ) {
                onRotateLeft();
                return true;
            }
        } else {
            if ( ( pointOne.x() <= pointOneEnd.x() ) && (  pointTwo.x() >= pointTwoEnd.x() ) ) {
                onRotateRight();
                return true;
            }
            else if ( ( pointOne.x() >= pointOneEnd.x() ) && (  pointTwo.x() <= pointTwoEnd.x() ) ) {
                onRotateLeft();
                return true;
            }
        }
        return false;
    }


    // Methods to be overridden in MainActivity
    //------------------------------------------

    public void onDraw( Line line ) {

    }

    public void onErase( Posn point ) {

    }

    public void onFingerUp() {

    }

    public void onFingerMove( Line line, Posn point ) {

    }

    public void onTap( Posn point ) {

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
