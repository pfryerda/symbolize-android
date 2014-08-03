package symbolize.app.Puzzle;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Line;
import symbolize.app.Common.Player;
import symbolize.app.Common.Posn;
import symbolize.app.Puzzle.Puzzle;

/*
 * Class used to store all the information about a given puzzle
 * and a method to check to see if your guess is correct.
 */
public class Level extends Puzzle {
    // Inherited fields
    //------------------

    //private final String hint;
    //private final boolean rotate_enabled;
    //private final boolean flip_enabled;
    //private final boolean colour_enabled;
    //private final ArrayList<LinkedList<Line>> solutions;


    // Fields
    //--------

    private final int draw_restriction;
    private final int erase_restriction;
    private final int drag_restriction;
    protected final ArrayList<LinkedList<Line>> boards;


    // Constructors
    //-------------


    public Level( final String hint, final int draw_restriction, final int erase_restriction, final int drag_restriction,
                  final boolean rotate_enabled, final boolean flip_enabled, final boolean colour_enabled,
                  final ArrayList<LinkedList<Line>> boards, final ArrayList<LinkedList<Line>> solutions, final ArrayList<Integer> unlocks )
    {
        super( hint, rotate_enabled, flip_enabled, colour_enabled, solutions, unlocks );
        this.draw_restriction = draw_restriction;
        this.erase_restriction = erase_restriction;
        this.drag_restriction = drag_restriction;
        this.boards = boards;
    }

    // Public method
    //---------------

    public boolean Can_shift() {
        return ( boards.size() > 1 );
    }


    // Geter methods
    //----------------

    public ArrayList<Posn> Get_levels() {
        return new ArrayList<Posn>();
    }

    public ArrayList<LinkedList<Line>> Get_boards()  {
        return boards;
    }

    public LinkedList<Line> Get_board() {
        return boards.get(0);
    }

    public int Get_draw_restriction() {
        return draw_restriction;
    }

    public int Get_erase_restriction() {
        return erase_restriction;
    }

    public int Get_drag_restriction() {
        return drag_restriction;
    }

}