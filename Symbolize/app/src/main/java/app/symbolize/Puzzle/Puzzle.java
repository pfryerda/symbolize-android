package app.symbolize.Puzzle;

import java.util.ArrayList;
import java.util.LinkedList;
import app.symbolize.Common.Line;
import app.symbolize.Common.Session;
import app.symbolize.Common.Posn;

/*
 * Class used to store all the information about a given puzzle
 * and a method to check to see if your guess is correct.
 */
abstract public class Puzzle {
    // Fields
    //--------

    private final String hint;
    private final boolean force_border;
    private final boolean rotate_enabled;
    private final boolean flip_enabled;
    private final ArrayList<LinkedList<Line>> solutions;
    private final ArrayList<Byte> unlocks;


    // Constructors
    //-------------


    public Puzzle( final String hint, final boolean force_border, final boolean rotate_enabled, final boolean flip_enabled,
                   final ArrayList<LinkedList<Line>> solutions, final ArrayList<Byte> unlocks )
    {
        this.hint = hint;
        this.force_border = force_border;
        this.rotate_enabled = rotate_enabled;
        this.flip_enabled = flip_enabled;
        this.solutions = solutions;
        this.unlocks = unlocks;
    }


    // Getter methods
    //----------------

    public String Get_hint() {
        return hint;
    }

    public boolean Is_border_forced() { return force_border; }

    public ArrayList<Byte> Get_unlocks() {
        return unlocks;
    }


    // Public methods
    //----------------

    public boolean Has_mechanics() {
        return Can_rotate() || Can_flip() || Can_shift() || Can_change_color();
    }

    public boolean Can_rotate() {
        return rotate_enabled || Session.DEV_MODE;
    }

    public boolean Can_flip() {
        return flip_enabled|| Session.DEV_MODE;
    }



    public boolean Is_special_enabled() {
        return Get_special_type() != 0;
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

    abstract public byte Get_draw_restriction();

    abstract public byte Get_erase_restriction();

    abstract public byte Get_drag_restriction();

    abstract public boolean Can_change_color();

    abstract public boolean Can_shift();

    abstract public byte Get_special_type();
}