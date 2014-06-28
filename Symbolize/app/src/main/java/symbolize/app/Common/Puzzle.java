package symbolize.app.Common;

import java.util.ArrayList;
import java.util.LinkedList;

/*
 * Class used to store all the information about a given puzzle
 * and a method to check to see if your guess is correct.
 */
abstract public class Puzzle {
    // Fields
    //--------

    private final String hint;
    private final boolean rotateEnabled;
    private final boolean flipEnabled;
    private final boolean colourEnabled;
    private final ArrayList<LinkedList<Line>> solutions;


    // Constructors
    //-------------

    public Puzzle() {
        this.hint = "";
        this.rotateEnabled = false;
        this.flipEnabled = false;
        this.colourEnabled = false;
        this.solutions = new ArrayList<LinkedList<Line>>();
    }

    public Puzzle( String hint, boolean rotateEnabled, boolean flipEnabled,
                  boolean colourEnabled, ArrayList<LinkedList<Line>> solutions )
    {
        this.hint = hint;
        this.rotateEnabled = rotateEnabled;
        this.flipEnabled = flipEnabled;
        this.colourEnabled = colourEnabled;
        this.solutions = solutions;
    }


    // Methods
    //---------

    public String getHint() {
        return hint;
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

    public boolean checkCorrectness(LinkedList<Line> g) {
        for( LinkedList<Line> s : solutions ) {
            if ( s.size() == g.size() ) {
                @SuppressWarnings("unchecked")
                LinkedList<Line> soln = ( LinkedList<Line> ) s.clone();
                @SuppressWarnings("unchecked")
                LinkedList<Line> guess = ( LinkedList<Line> ) g.clone();
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


    // Abstract methods
    //------------------

    abstract public ArrayList<Posn> getLevels();

    abstract public ArrayList<LinkedList<Line>> getBoards();

    abstract public LinkedList<Line> getBoard();

    abstract public int getDrawRestirction();

    abstract public int getEraseRestirction();

    abstract public boolean canShift();
}