package symbolize.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

/*
 * The main game method contains information about whats on the board, what mode you are in,
 * how lines have you drawn/erased and a tracker of your past state for undo.
 */
public class GameModel {
    // Fields
    //--------

    private LinkedList<Line> graph;
    private int linesDrawn, linesErased;
    private int shiftNumber;
    private GameModel pastState;


    // Constructors
    //--------------

    public GameModel() {
        graph = new LinkedList<Line>();
        linesDrawn = 0;
        linesErased = 0;
        shiftNumber = 0;
        pastState = null;
    }

    public GameModel( LinkedList<Line> graph, int linesDrawn, int linesErased, int shiftNumber, GameModel pastState ) {
        this.graph = graph;
        this.linesDrawn = linesDrawn;
        this.linesErased = linesErased;
        this.shiftNumber = shiftNumber;
        this.pastState = pastState;
    }


    // Copy Constructor
    //-----------------

    public GameModel clone() {
        LinkedList<Line> clonedGraph = new LinkedList<Line>();
        for ( Line line : graph ) {
            clonedGraph.addLast( line.clone() );
        }
        return new GameModel( clonedGraph, linesDrawn, linesErased, shiftNumber, pastState );
    }


    // Methods
    //---------

    public void setLevel(Level level) {
        graph.clear();
        for ( Line line : level.getBoard() ) {
            graph.addLast( line.clone() );
        }
        linesDrawn = 0;
        linesErased = 0;
        pastState = null;
    }

    public void pushState() {
        pastState = clone();
    }


    // Geter methods
    //---------------

    public LinkedList<Line> getGraph() {
        return graph;
    }

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

    public void addLine(Line line) {
        pushState();
        graph.addLast( line );
        ++linesDrawn;
    }

    public void removeLine( Line line ) {
        pushState();
        graph.remove( line );
        if ( line.getOwner() == Owner.App ) {
            ++linesErased;
        } else{
            --linesDrawn;
        }
    }

    public void rotateGraphR() {
        pushState();
        for ( Line line : graph ) {
            line.rotateRight();
        }
    }

    public void rotateGraphL() {
        pushState();
        for ( Line line : graph ) {
            line.rotateLeft();
        }
    }

    public void flipGraphH() {
        pushState();
        for ( Line line : graph ) {
            line.flipH();
        }
    }

    public void flipGraphV() {
        pushState();
        for ( Line line : graph ) {
            line.flipV();
        }
    }

    public void shiftGraph( ArrayList<LinkedList<Line>> sg ) {
        pushState();
        shiftNumber = (shiftNumber + 1) % sg.size();
        graph.clear();
        for ( Line line : sg.get( shiftNumber ) ) {
            graph.addLast( line.clone() );
        }
        linesDrawn = 0;
        linesErased = 0;
    }


    // Developer methods
    //------------------

    public void LogGraph() {
        String graph_string =  "new LinkedList<Line>( Arrays.asList( ";
        for ( int i = 0; i < graph.size(); ++i ) {
            if (i == graph.size() - 1) {
                graph_string += graph.get(i).printLine();
            } else {
                graph_string += graph.get(i).printLine() + ", ";
            }
        }
        graph_string += " ) )";
        Log.i("Graph Log:", graph_string);
    }
}