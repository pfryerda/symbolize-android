package symbolize.app.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.LinearLayout;

import symbolize.app.Common.Level;
import symbolize.app.Common.Line;
import symbolize.app.Common.Owner;
import symbolize.app.Common.Posn;


/*
 * Class in charge of given a command from the user, manipulate the
 * main model (GameModel) and update the the display/view (GameView)
 */
public class GameController {
    // Fields
    //--------

    private GameModel gameModel;
    private GameView gameView;
    private Level currLevel;
    private boolean drawnEnabled;


    // Consturctor
    //--------------

    public GameController( Context context, LinearLayout foreground, LinearLayout background, Bitmap foreground_bitmpa, Bitmap backbroud_bitmap ) {
        this.gameModel = new GameModel();
        this.gameView = new GameView( context, foreground, background, foreground_bitmpa, backbroud_bitmap, gameModel );
        this.currLevel = null;
        this.drawnEnabled = true;
    }

    // Methods
    //---------

    /*
     * Method used for loading new levels, called once on game startup and again for any later
     * new level changes. Sets up GameModel, removes old data, and renders board to the screen
     *
     * @param Level level: The level that needs to be loaded
     */
    public void loadLevel( Level level ) {
        currLevel = level;
        gameModel.setLevel( level );
        gameView.renderGraph();
    }

    /*
     * Method that simply redraws the graph removing all shadows currently drawn on screen
     */
    public void removeShadows() {
        gameView.renderGraph();
    }

    // Geter methods
    //--------------


    public boolean isInDrawMode()  {
        return drawnEnabled;
    }

    public boolean isInEraseMode() {
        return !drawnEnabled;
    }


    // Button methods
    //---------------

    public boolean checkSolution() {
        return currLevel.checkCorrectness( gameModel.getGraph() );
		/* Check last level      */
		/* setLevel(next level!) */
    }

    public void reset() {
        loadLevel( currLevel );
    }

    public void undo() {
        if ( gameModel.getPastState() == null ) {
            gameView.renderToast( "There is nothing to undo" );
        } else {
            gameModel = gameModel.getPastState();
            gameView.renderUndo();
        }
    }

    public void enterDrawMode() {
        drawnEnabled = true;
    }

    public void enterEraseMode() {
        drawnEnabled = false;
    }


    // Action methods
    //----------------

    public void drawLine( Line line ) {
        if ( gameModel.getLinesDrawn() < currLevel.getDrawRestirction() ) {
            gameModel.addLine( line );
            gameView.renderLine( line );
        } else {
            gameView.renderToast( "Cannot draw any more lines " );
        }
    }

    public void drawShadowLine( Line line ) {
        gameView.renderShadowLine( line );
    }

    public void tryToErase( Posn point ) {
        for ( Line l : gameModel.getGraph() ) {
            if ( l.intersect( point ) ) {
                eraseLine(l);
                break;
            }
        }
    }

    public void drawShadowPoint( Posn point ) {
        gameView.renderShadowPoint( point );
    }

    private void eraseLine( Line line ) {
        if ( ( gameModel.getLinesErased() < currLevel.getEraseRestirction() ) || ( line.getOwner() == Owner.User ) ) {
            gameModel.removeLine( line );
            gameView.renderGraph();
        } else {
            gameView.renderToast( "Cannot erase any more lines" );
        }
    }

    public void rotateRight() {
        if ( currLevel.canRotate() ) {
            gameModel.rotateGraphR();
            gameView.renderRotateR();
        }
    }

    public void rotateLeft() {
        if ( currLevel.canRotate() ) {
            gameModel.rotateGraphL();
            gameView.renderRotateL();
        }
    }

    public void flipHorizontally() {
        if ( currLevel.canFlip() ) {
            gameModel.flipGraphH();
            gameView.renderFlipH();
        }
    }

    public void flipVertically() {
        if ( currLevel.canFlip() ) {
            gameModel.flipGraphV();
            gameView.renderFlipV();
        }
    }

    public void tryToChangeColor(Posn point) {
        if ( currLevel.canChangeColur() ) {
            for ( Line line : gameModel.getGraph( )) {
                if ( line.intersect( point ) ) {
                    changeColor( line );
                    break;
                }
            }
        }
    }

    private void changeColor( Line line ) {
        gameModel.pushState();
        line.editColor();
        gameView.renderLine( line );
    }

    public void shift() {
        if ( currLevel.canShift() ) {
            gameModel.shiftGraph( currLevel.getBoards() );
            gameView.renderShift();
        }
    }


    // Developer method
    //-----------------

    public void LogModel() {
        gameModel.LogGraph();
    }
}