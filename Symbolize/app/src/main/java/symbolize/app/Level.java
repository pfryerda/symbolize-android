package symbolize.app;

import android.util.Log;

import org.simpleframework.xml.Default;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;

/*
 * Class used to store all the information about a given puzzle
 * and a method to check to see if your guess is correct.
 */
@Root
@Default
public class Level {
    // Fields
    //--------

    private final int levelNum;
    private final int worldNum;
    private final String hint;
    private final int drawRestirction;
    private final int eraseRestirction;
    private final boolean rotateEnabled;
    private final boolean flipEnabled;
    private final boolean colourEnabled;
    private final ArrayList<LinkedList<Line>> boards;
    private final LinkedList<LinkedList<Line>> solutions;


    // Constructor
    //-------------
    public Level( int levelNum, int worldNum, String hint, int drawRestirction, int eraseRestirction,
                  boolean rotateEnabled, boolean flipEnabled, boolean colourEnabled,
                  ArrayList<LinkedList<Line>> boards, LinkedList<LinkedList<Line>> solutions )
    {
        this.levelNum = levelNum;
        this.worldNum = worldNum;
        this.hint = hint;
        this.boards = boards;
        this.solutions = solutions;
        this.drawRestirction = drawRestirction;
        this.eraseRestirction = eraseRestirction;
        this.rotateEnabled = rotateEnabled;
        this.flipEnabled = flipEnabled;
        this.colourEnabled = colourEnabled;
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
        return boards.get( 0 );
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
        return ( boards.size() > 1 );
    }

    public ArrayList<LinkedList<Line>> getBoards()  {
        return boards;
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