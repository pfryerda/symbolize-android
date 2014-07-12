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
    private final boolean rotate_enabled;
    private final boolean flip_enabled;
    private final boolean colour_enabled;
    private final ArrayList<LinkedList<Line>> solutions;


    // Constructors
    //-------------

    public Puzzle() {
        this.hint = "";
        this.rotate_enabled = false;
        this.flip_enabled = false;
        this.colour_enabled = false;
        this.solutions = new ArrayList<LinkedList<Line>>();
    }

    public Puzzle( final String hint, final boolean rotate_enabled, final boolean flip_enabled,
                   final boolean colour_enabled, final ArrayList<LinkedList<Line>> solutions )
    {
        this.hint = hint;
        this.rotate_enabled = rotate_enabled;
        this.flip_enabled = flip_enabled;
        this.colour_enabled = colour_enabled;
        this.solutions = solutions;
    }


    // Public methods
    //----------------

    public boolean Can_rotate() {
        return rotate_enabled;
    }

    public boolean Can_flip() {
        return flip_enabled;
    }

    public boolean Can_change_color() {
        return colour_enabled;
    }

    public boolean Check_correctness( final LinkedList<Line> graph ) {
        for( LinkedList<Line> s : solutions ) {
            if ( s.size() == graph.size() ) {
                @SuppressWarnings("unchecked")
                LinkedList<Line> soln = ( LinkedList<Line> ) s.clone();
                @SuppressWarnings("unchecked")
                LinkedList<Line> guess = ( LinkedList<Line> ) graph.clone();
                while( !soln.isEmpty() ) {
                    Line match = null;
                    for ( Line line : guess ) {
                        if ( match == null ) {
                            if( line.Approximately_equals( soln.getFirst() ) ) {
                                match = line;
                            }
                        } else {
                            if( line.Approximately_equals( soln.getFirst() ) &&
                                    ( soln.getFirst().Score( line ) < soln.getFirst().Score( match ) ) ) {
                                match = line;
                            }
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


    // Getter methods
    //----------------

    public String getHint() {
        return hint;
    }


    // Abstract methods
    //------------------

    abstract public ArrayList<Posn> Get_levels();

    abstract public ArrayList<LinkedList<Line>> Get_boards();

    abstract public LinkedList<Line> Get_board();

    abstract public int Get_draw_restriction();

    abstract public int Get_erase_restriction();

    abstract public int Get_drag_restriction();

    abstract public boolean Can_shift();
}