package symbolize.app.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.LinearLayout;

import java.util.ArrayList;

import symbolize.app.Common.Level;
import symbolize.app.Common.Line;
import symbolize.app.Common.Owner;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Puzzle;


/*
 * Class in charge of given a command from the user, manipulate the
 * main model (GameModel) and update the the display/view (GameView)
 */
public class GameController {
    // Fields
    //--------

    private GameModel gameModel;
    private GameView gameView;
    private Puzzle currPuzzle;
    private boolean drawnEnabled;


    // Consturctor
    //--------------

    public GameController( Context context, LinearLayout foreground, LinearLayout background, Bitmap foreground_bitmpa, Bitmap backbroud_bitmap ) {
        this.gameModel = new GameModel();
        this.gameView = new GameView( context, foreground, background, foreground_bitmpa, backbroud_bitmap, gameModel );
        this.currPuzzle = null;
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
    public void loadPuzzle( Puzzle puzzle ) {
        currPuzzle = puzzle;
        gameModel.setPuzzle( puzzle );
        gameView.renderGraph();
    }

    /*
     * Method that simply redraws the graph removing all shadows currently drawn on screen
     */
    public void removeShadows() {
        gameView.renderGraph();
    }

    /*
     * Method that snaps a point or line to the level dots
     */
    public void snapToLevels( Line line ) {
        line.snapToLevels( gameModel.getLevels() );
    }

    public void snapToLevels( Posn point ) {
        point.snapToLevels( gameModel.getLevels() );
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
        return currPuzzle.checkCorrectness( gameModel.getGraph() );
		/* Check last level      */
		/* setLevel(next level!) */
    }

    public void reset() {
        loadPuzzle(currPuzzle);
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
        if ( gameModel.getLinesDrawn() < currPuzzle.getDrawRestirction() ) {
            gameModel.addLine( line );
            gameView.renderGraph();
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
        if ( ( gameModel.getLinesErased() < currPuzzle.getEraseRestirction() ) || ( line.getOwner() == Owner.User ) ) {
            gameModel.removeLine( line );
            gameView.renderGraph();
        } else {
            gameView.renderToast( "Cannot erase any more lines" );
        }
    }

    public void rotateRight() {
        if ( currPuzzle.canRotate() ) {
            gameModel.rotateGraphR();
            gameView.renderRotateR();
        }
    }

    public void rotateLeft() {
        if ( currPuzzle.canRotate() ) {
            gameModel.rotateGraphL();
            gameView.renderRotateL();
        }
    }

    public void flipHorizontally() {
        if ( currPuzzle.canFlip() ) {
            gameModel.flipGraphH();
            gameView.renderFlipH();
        }
    }

    public void flipVertically() {
        if ( currPuzzle.canFlip() ) {
            gameModel.flipGraphV();
            gameView.renderFlipV();
        }
    }

    public void tryToChangeColor( Posn point ) {
        if ( currPuzzle.canChangeColur() ) {
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
        if ( currPuzzle.canShift() ) {
            gameModel.shiftGraph( currPuzzle.getBoards() );
            gameView.renderShift();
        }
    }

    public int tryToMatchLevel( Posn point ) {
        ArrayList<Posn> levels = gameModel.getLevels();
        for ( int i = 0; i < levels.size(); ++i ) {
            if ( point.eq( levels.get( i ) ) ) {
                return i + 1;
            }
        }
        return 0;
    }


    // Developer method
    //-----------------

    public void LogModel() {
        gameModel.LogGraph();
    }
}