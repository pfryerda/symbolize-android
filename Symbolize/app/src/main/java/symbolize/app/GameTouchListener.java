package symbolize.app;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import static symbolize.app.Constants.*;
import static symbolize.app.Constants.SCALING;

public class GameTouchListener implements View.OnTouchListener {
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

                return true;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {                         // Second finger down
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
                        Line line = new Line( pointOne, pointOneEnd, Owner.User );
                        if ( line.distSqr() >= MINLINESIZESQR ) {
                            onDraw( new Line( pointOne, pointOneEnd, Owner.User ) );
                        } else {
                            onTap( pointOneEnd );
                        }
                        resetVars();
                    }
                }

                if ( inDoubleTouch && !isPointOneDown && !isPointTwoDown ) {
                    boolean flipped = attemptToFlip();
                    if ( !flipped ) attemptToRotate();
                    resetVars();
                }

                isEraseDelayDone = false;
                timer.cancel();
                return true;
            }

            case MotionEvent.ACTION_MOVE: {                                 // Finger moves
                if ( !inDoubleTouch && isEraseDelayDone ) {
                    onErase( getPoint( event ) );
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
        float touchX = event.getX( event.getActionIndex() );
        float touchY = event.getY( event.getActionIndex() );

        int scaledX = Math.min( SCALING, Math.max( 0, Math.round( touchX * SCALING / SCREENSIZE.x ) ) );
        int scaledY = Math.min( SCALING, Math.max( 0, Math.round( touchY * SCALING / SCREENSIZE.x ) ) );

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
        if ( ( ( pointOne.x() < SCALING/2 && pointTwo.x() < SCALING/2 && pointTwoEnd.x() > SCALING/2 && pointTwoEnd.x() > SCALING/2 ) ||
               ( pointOne.x() > SCALING/2 && pointTwo.x() > SCALING/2 && pointTwoEnd.x() < SCALING/2 && pointTwoEnd.x() < SCALING/2 ) ) &&
               ( pointOneEnd.y() - FLIPPINGTHRESHOLD <= pointOne.y() && pointOne.y() <= pointOneEnd.y() + FLIPPINGTHRESHOLD &&
                 pointTwoEnd.y() - FLIPPINGTHRESHOLD <= pointTwo.y() && pointTwo.y() <= pointTwoEnd.y() + FLIPPINGTHRESHOLD ) ) {
            onFlipHorizontally();
            return true;
        }
        else if ( ( ( pointOne.y() < SCALING/2 && pointTwo.y() < SCALING/2 && pointTwoEnd.y() > SCALING/2 && pointTwoEnd.y() > SCALING/2 ) ||
                    ( pointOne.y() > SCALING/2 && pointTwo.y() > SCALING/2 && pointTwoEnd.y() < SCALING/2 && pointTwoEnd.y() < SCALING/2 ) ) &&
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

    public void onTap( Posn point ) {

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
