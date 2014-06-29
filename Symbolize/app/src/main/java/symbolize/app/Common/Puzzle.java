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
                            if( line.Approximately_equals( soln.getFirst() ) ) match = line;
                        } else {
                            if( line.Approximately_equals( soln.getFirst() ) && ( soln.getFirst().Score( line ) < soln.getFirst().Score( match ) ) ) match = line;
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

    abstract public ArrayList<Posn> Get_levels();

    abstract public ArrayList<LinkedList<Line>> Get_boards();

    abstract public LinkedList<Line> Get_board();

    abstract public int Get_draw_restriction();

    abstract public int Get_erase_restriction();

    abstract public boolean Can_shift();
}