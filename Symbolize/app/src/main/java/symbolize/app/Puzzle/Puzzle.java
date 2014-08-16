package symbolize.app.Puzzle;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Line;
import symbolize.app.Common.Session;
import symbolize.app.Common.Posn;

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
    private final ArrayList<Integer> unlocks;


    // Constructors
    //-------------


    public Puzzle( final String hint, final boolean rotate_enabled, final boolean flip_enabled,
                   final boolean colour_enabled, final ArrayList<LinkedList<Line>> solutions, final ArrayList<Integer> unlocks )
    {
        this.hint = hint;
        this.rotate_enabled = rotate_enabled;
        this.flip_enabled = flip_enabled;
        this.colour_enabled = colour_enabled;
        this.solutions = solutions;
        this.unlocks = unlocks;
    }


    // Getter methods
    //----------------

    public String Get_hint() {
        return hint;
    }

    public ArrayList<Integer> Get_unlocks() {
        return unlocks;
    }


    // Public methods
    //----------------

    public boolean Can_rotate() {
        return rotate_enabled || Session.DEV_MODE;
    }

    public boolean Can_flip() {
        return flip_enabled|| Session.DEV_MODE;
    }

    public boolean Can_change_color() {
        return colour_enabled|| Session.DEV_MODE;
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