package symbolize.app.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Enum.Action;
import symbolize.app.Common.Line;
import symbolize.app.Common.Enum.Owner;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Puzzle;

/*
 * The main game method contains information about whats on the board, what mode you are in,
 * how lines have you drawn/erased and a tracker of your past state for undo.
 */
public class GameModel {
    // Fields
    //--------

    private LinkedList<Line> graph;
    private ArrayList<Posn> levels;
    private int linesDrawn, linesErased;
    private int shiftNumber;
    private GameView gameView;
    private GameModel pastState;


    // Constructors
    //--------------

    public GameModel( Context context, LinearLayout foreground, LinearLayout background, Bitmap foregoundBitmap, Bitmap backgroundBitmap ) {
        graph = new LinkedList<Line>();
        levels = new ArrayList<Posn>();
        linesDrawn = 0;
        linesErased = 0;
        shiftNumber = 0;
        gameView = new GameView( context, foreground, background, foregoundBitmap, backgroundBitmap, this );
        pastState = null;
    }

    public GameModel( LinkedList<Line> graph, ArrayList<Posn> levels, int linesDrawn, int linesErased, int shiftNumber, GameView gameView, GameModel pastState ) {
        this.graph = graph;
        this.levels = levels;
        this.linesDrawn = linesDrawn;
        this.linesErased = linesErased;
        this.shiftNumber = shiftNumber;
        this.gameView = gameView;
        this.pastState = pastState;
    }


    // Copy Constructor
    //-----------------

    public GameModel clone() {
        LinkedList<Line> clonedGraph = new LinkedList<Line>();
        for ( Line line : graph ) {
            clonedGraph.addLast( line.clone() );
        }
        return new GameModel( clonedGraph, levels, linesDrawn, linesErased, shiftNumber, gameView, pastState );
    }


    // Methods
    //---------

    public void setPuzzle( Puzzle puzzle ) {
        graph.clear();
        levels.clear();
        for ( Line line : puzzle.Get_board() ) {
            graph.addLast( line.clone() );
        }
        for ( Posn point : puzzle.Get_levels() ) {
            levels.add(point.clone());
        }
        linesDrawn = 0;
        linesErased = 0;
        pastState = null;

        gameView.Render_foreground(graph, levels);
    }

    public void Add_shadow( Line line ) {
        gameView.Render_shadow(line, graph, levels);
    }

    public void Add_shadow( Posn posn ) {
        gameView.Render_shadow(posn, graph, levels);
    }

    public void Remove_shadows() {
        gameView.Render_foreground(graph, levels);
    }

    public void Undo() {
        gameView.Render_foreground(graph, levels);
    }

    public void pushState() {
        pastState = clone();
    }


    // Geter methods
    //---------------

    public LinkedList<Line> getGraph() {
        return graph;
    }

    public ArrayList<Posn> Get_levels() { return levels; }

    public int getLinesDrawn() {
        if ( GameActivity.DEVMODE ) {
            return -1;
        } else {
            return linesDrawn;
        }
    }

    public int getLinesErased() {
        if ( GameActivity.DEVMODE ) {
            return -1;
        } else {
            return linesErased;
        }
    }

    public GameModel getPastState() {
        return pastState;
    }


    // Action methods
    //----------------

    public void action_basic( Action action, Line line ) {
        pushState();
        switch ( action ) {
            case Draw:
                graph.addLast(line);
                ++linesDrawn;
                break;

            case Erase:
                graph.remove(line);
                if (line.Get_owner() == Owner.App) {
                    ++linesErased;
                } else {
                    --linesDrawn;
                }
                break;

            case Change_color:
                line.Edit( action );
                break;
        }
        gameView.Render_foreground(graph, levels);
    }

    public void action_motion( Action action ) {
        pushState();

        for ( Line line : graph ) {
            line.Edit( action );
        }

        for ( Posn posn : levels ) {
            posn.Edit( action );
        }

        gameView.Render_motion( action, graph, levels );
    }

    public void action_sensor( Action action, ArrayList<LinkedList<Line>> board) {
        pushState();
        switch ( action ) {
            case Shift:
                shiftNumber = (shiftNumber + 1) % board.size();
                graph.clear();
                for ( Line line : board.get( shiftNumber ) ) {
                    graph.addLast( line.clone() );
                }
                linesDrawn = 0;
                linesErased = 0;
                break;
        }
        gameView.Render_motion( action, graph, levels );
    }


    // Developer methods
    //------------------

    public void LogGraph() {
        String graph_string = "Xml for current graph";
        graph_string += "\n<graph>";
        for ( Line line : graph ) {
            graph_string += "\n" + line.Print_line();
        }
        graph_string += "\n</graph>";
        Log.i("Graph Log:", graph_string);
    }
}