package symbolize.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;

/*
 * Class used to store all the information about a given puzzle
 * and a method to check to see if your guess is correct.
 */
public class Level {
    // Fields
    //--------

    private final int levelNum;
    private final int worldNum;
    private final String hint;
    private final LinkedList<Line> board;
    private final LinkedList<LinkedList<Line>> solutions;
    private final int drawRestirction;
    private final int eraseRestirction;
    private final boolean rotateEnabled;
    private final boolean flipEnabled;
    private final boolean colourEnabled;
    private final ArrayList<LinkedList<Line>> shiftGraphs; // shiftEnabled == (shiftGraph == null)


    // Constructor
    //-------------
    public Level( int levelNum, int worldNum, String hint, LinkedList<Line> board,
                 LinkedList<LinkedList<Line>> solutions, int drawRestirction, int eraseRestirction,
                    boolean rotateEnabled, boolean flipEnabled, boolean colourEnabled,
                        ArrayList<LinkedList<Line>> shiftGraphs ) {
        this.levelNum = levelNum;
        this.worldNum = worldNum;
        this.hint = hint;
        this.board = board;
        this.solutions = solutions;
        this.drawRestirction = drawRestirction;
        this.eraseRestirction = eraseRestirction;
        this.rotateEnabled = rotateEnabled;
        this.flipEnabled = flipEnabled;
        this.colourEnabled = colourEnabled;
        this.shiftGraphs = shiftGraphs;
    }

    // Methods
    //---------

    public int getLevelNum() {
        return levelNum;
    }

    public int getWorldNum() {
        return worldNum;
    }

    public String getHint() {
        return hint;
    }

    public LinkedList<Line> getBoard() {
        return board;
    }

    public int getDrawRestirction() {
        return drawRestirction;
    }

    public int getEraseRestirction() {
        return eraseRestirction;
    }

    public boolean canRotate() {
        return rotateEnabled;
    }

    public boolean canFlip() {
        return flipEnabled;
    }

    public boolean canChangeColur() {
        return colourEnabled;
    }

    public boolean canShift() {
        return (shiftGraphs != null);
    }

    public ArrayList<LinkedList<Line>> getShiftGraphs()  {
        return shiftGraphs;
    }

    public boolean checkCorrectness(LinkedList<Line> g) {
        for( LinkedList<Line> s : solutions ) {
            if ( s.size() == g.size() ) {
                @SuppressWarnings("unchecked")
                LinkedList<Line> soln = (LinkedList<Line>) s.clone();
                @SuppressWarnings("unchecked")
                LinkedList<Line> guess = (LinkedList<Line>) g.clone();
                while( !soln.isEmpty() ) {
                    Line match = null;
                    for ( Line line : guess ) {
                        if ( match == null ) {
                            if( line.eq( soln.getFirst() ) ) match = line;
                        } else {
                            if( line.eq( soln.getFirst() ) && ( soln.getFirst().score( line ) < soln.getFirst().score( match ) ) ) match = line;
                        }
                    }
                    if ( match == null ) break;
                    soln.removeFirst();
                    guess.remove( match );
                }
                if ( soln.isEmpty() ) return true;
            }
        }
        return false;
    }
}