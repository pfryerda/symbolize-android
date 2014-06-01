package symbolize.app;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import static symbolize.app.Constants.*;
import static symbolize.app.Constants.SCALING;

public class GameTouchListener implements View.OnTouchListener {

    private GameController gameController;
    private int SCREENSIZE;

    private Posn pointOne;
    private boolean isPointOneDown;
    private Posn pointOneEnd;

    private Posn pointTwo;
    private boolean isPointTwoDown;
    private Posn pointTwoEnd;

    private boolean inDoubleTouch;

    public GameTouchListener( GameController gameController, int SCREENSIZE ) {
        this.gameController = gameController;
        this.SCREENSIZE = SCREENSIZE;
        resetVars();
    }

    public boolean onTouch(View v, MotionEvent event) {
        int touchX;
        int touchY;

        switch ( event.getActionMasked() ) {
            case MotionEvent.ACTION_DOWN: // First figner down
                touchX = scaleNum( event.getX( event.getActionIndex() ) );
                touchY = scaleNum( event.getY( event.getActionIndex() ) );
                pointOne = new Posn(touchX, touchY);
                isPointOneDown = true;
                Log.d("ACTION_DOWN", "(" + pointOne.x() + "," + pointOne.y() + ")");

                return true;

            case MotionEvent.ACTION_POINTER_DOWN: // Second figner down
                touchX = scaleNum( event.getX( event.getActionIndex() ) );
                touchY = scaleNum( event.getY( event.getActionIndex() ) );
                pointTwo = new Posn(touchX, touchY);
                isPointTwoDown = true;
                Log.d("ACTION_POINTER_DOWN", "(" + pointTwo.x() + "," + pointTwo.y() + ")");

                inDoubleTouch = true;
                return true;

            case MotionEvent.ACTION_UP:         // First figner up
            case MotionEvent.ACTION_POINTER_UP: // Second figner up
                if ( ( event.getActionIndex() == 1 ) || !isPointOneDown ) {
                    touchX = scaleNum( event.getX( event.getActionIndex() ) );
                    touchY = scaleNum( event.getY( event.getActionIndex() ) );
                    pointTwoEnd = new Posn( touchX, touchY );
                    isPointTwoDown = false;
                    Log.d( "ACTION_POINTER_UP", "(" + pointTwoEnd.x() + "," + pointTwoEnd.y() + ")" );
                } else {
                    touchX = scaleNum(event.getX(event.getActionIndex()));
                    touchY = scaleNum(event.getY(event.getActionIndex()));
                    pointOneEnd = new Posn(touchX, touchY);
                    isPointOneDown = false;
                    Log.d("ACTION_UP", "(" + pointOneEnd.x() + "," + pointOneEnd.y() + ")");

                    if (gameController.isInDrawMode() && !inDoubleTouch) {
                        gameController.drawLine(new Line(pointOne, pointOneEnd, Owner.User));
                        resetVars();
                    }
                }

                if (inDoubleTouch && !isPointOneDown && !isPointTwoDown) {
                    boolean fliped = attemptToFlip();
                    if ( !fliped ) attemptToRotate();
                    resetVars();
                }

                return true;

            case MotionEvent.ACTION_MOVE: // Finger moves
                if ( gameController.isInEraseMode() && !inDoubleTouch ) {
                    touchX = scaleNum( event.getX() );
                    touchY = scaleNum( event.getY() );
                    pointOne = new Posn( touchX, touchY );
                    //Log.d("ACTION_MOVE", "("+startPoint.x()+","+startPoint.y()+")");
                    gameController.tryToErase( pointOne );
                }

                return true;

            default:
                return false;
        }
    }

    int scaleNum(float f) {
        return Math.round( f * SCALING / SCREENSIZE );
    }

    void resetVars() {
        pointOne = null;
        isPointOneDown = false;
        pointOneEnd = null;

        pointTwo = null;
        isPointTwoDown = false;
        pointTwoEnd = null;

        inDoubleTouch = false;
    }

    boolean attemptToFlip() {
        if ( ( ( pointOne.x() < SCALING/2 && pointTwo.x() < SCALING/2 && pointTwoEnd.x() > SCALING/2 && pointTwoEnd.x() > SCALING/2 ) ||
               ( pointOne.x() > SCALING/2 && pointTwo.x() > SCALING/2 && pointTwoEnd.x() < SCALING/2 && pointTwoEnd.x() < SCALING/2 ) ) &&
               ( pointOneEnd.y() - DRAWINGTHRESHOLD <= pointOne.y() && pointOne.y() <= pointOneEnd.y() + DRAWINGTHRESHOLD &&
                 pointTwoEnd.y() - DRAWINGTHRESHOLD <= pointTwo.y() && pointTwo.y() <= pointTwoEnd.y() + DRAWINGTHRESHOLD ) ) {
            gameController.flipHorizontally();
            return true;
        }
        else if ( ( ( pointOne.y() < SCALING/2 && pointTwo.y() < SCALING/2 && pointTwoEnd.y() > SCALING/2 && pointTwoEnd.y() > SCALING/2 ) ||
                   ( pointOne.y() > SCALING/2 && pointTwo.y() > SCALING/2 && pointTwoEnd.y() < SCALING/2 && pointTwoEnd.y() < SCALING/2 ) ) &&
                   ( pointOneEnd.x() - DRAWINGTHRESHOLD <= pointOne.x() && pointOne.x() <= pointOneEnd.x() + DRAWINGTHRESHOLD &&
                     pointTwoEnd.x() - DRAWINGTHRESHOLD <= pointTwo.x() && pointTwo.x() <= pointTwoEnd.x() + DRAWINGTHRESHOLD ) ) {
            gameController.flipVertically();
            return true;
        }
        return false;
    }

    boolean attemptToRotate() {
        if ( pointOne.y() >= pointTwo.y() ) {
            if ( ( /*pointOne.y() >= pointOneEnd.y() &&*/ pointOne.x() >= pointOneEnd.x() ) &&
                 ( /*pointTwo.y() <= pointTwoEnd.y() &&*/ pointTwo.x() <= pointTwoEnd.x() ) ) {
                gameController.rotateRight();
                return true;
            }
            else if ( ( /*pointOne.y() <= pointOneEnd.y() &&*/ pointOne.x() <= pointOneEnd.x() ) &&
                      ( /*pointTwo.y() >= pointTwoEnd.y() &&*/ pointTwo.x() >= pointTwoEnd.x() ) ) {
                gameController.rotateLeft();
                return true;
            }
        } else {
            if ( ( /*pointOne.y() <= pointOneEnd.y() &&*/ pointOne.x() <= pointOneEnd.x() ) &&
                 ( /*pointTwo.y() >= pointTwoEnd.y() &&*/ pointTwo.x() >= pointTwoEnd.x() ) ) {
                gameController.rotateRight();
                return true;
            }
            else if ( ( /*pointOne.y() >= pointOneEnd.y() &&*/ pointOne.x() >= pointOneEnd.x() ) &&
                      ( /*pointTwo.y() <= pointTwoEnd.y() &&*/ pointTwo.x() <= pointTwoEnd.x() ) ) {
                gameController.rotateLeft();
                return true;
            }
        }
        return false;
    }
}
