package symbolize.app.Game;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Level;
import symbolize.app.Common.Line;
import symbolize.app.Common.Owner;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Puzzle;
import symbolize.app.Common.World;

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
    private GameModel pastState;


    // Constructors
    //--------------

    public GameModel() {
        graph = new LinkedList<Line>();
        levels = new ArrayList<Posn>();
        linesDrawn = 0;
        linesErased = 0;
        shiftNumber = 0;
        pastState = null;
    }

    public GameModel( LinkedList<Line> graph, ArrayList<Posn> levels, int linesDrawn, int linesErased, int shiftNumber, GameModel pastState ) {
        this.graph = graph;
        this.levels = levels;
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
        return new GameModel( clonedGraph, levels, linesDrawn, linesErased, shiftNumber, pastState );
    }


    // Methods
    //---------

    public void setPuzzle( Puzzle puzzle ) {
        graph.clear();
        levels.clear();
        for ( Line line : puzzle.getBoard() ) {
            graph.addLast( line.clone() );
        }
        for ( Posn point : puzzle.getLevels() ) {
            levels.add(point.clone());
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

    public ArrayList<Posn> getLevels() { return levels; }

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
        for ( Posn point : levels ) {
            point.roateRight();
        }
    }

    public void rotateGraphL() {
        pushState();
        for ( Line line : graph ) {
            line.rotateLeft();
        }
        for ( Posn point : levels ) {
            point.roateLeft();
        }
    }

    public void flipGraphH() {
        pushState();
        for ( Line line : graph ) {
            line.flipH();
        }
        for ( Posn point : levels ) {
            point.flipH();
        }
    }

    public void flipGraphV() {
        pushState();
        for ( Line line : graph ) {
            line.flipV();
        }
        for ( Posn point : levels ) {
            point.flipV();
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
        String graph_string = "Xml for current graph";
        graph_string += "\n<graph>";
        for ( Line line : graph ) {
            graph_string += "\n" + line.printLine();
        }
        graph_string += "\n</graph>";
        Log.i("Graph Log:", graph_string);
    }
}