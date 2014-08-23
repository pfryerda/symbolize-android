package symbolize.app.Puzzle;

import java.util.ArrayList;
import java.util.LinkedList;
import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;

/*
 * Class used to store all the information about a given puzzle.
 */
public class Level extends Puzzle {
    // Inherited fields
    //------------------

    /*
    private final String hint;
    private final boolean rotate_enabled;
    private final boolean flip_enabled;
    private final boolean colour_enabled;
    private final ArrayList<LinkedList<Line>> solutions;
    */


    // Fields
    //--------

    private final int draw_restriction;
    private final int erase_restriction;
    private final int drag_restriction;
    private final int special_type;
    protected final ArrayList<LinkedList<Line>> boards;


    // Constructors
    //-------------


    public Level( final String hint, final int draw_restriction, final int erase_restriction, final int drag_restriction,
                  final int special_type, final boolean rotate_enabled, final boolean flip_enabled, final boolean colour_enabled,
                  final ArrayList<LinkedList<Line>> boards, final ArrayList<LinkedList<Line>> solutions, final ArrayList<Integer> unlocks )
    {
        super( hint, rotate_enabled, flip_enabled, colour_enabled, solutions, unlocks );
        this.draw_restriction = draw_restriction;
        this.erase_restriction = erase_restriction;
        this.drag_restriction = drag_restriction;
        this.special_type = special_type;
        this.boards = boards;
    }


    // Getter methods
    //----------------

    @Override
    public ArrayList<Posn> Get_levels() {
        return new ArrayList<Posn>();
    }

    @Override
    public ArrayList<LinkedList<Line>> Get_boards()  {
        return boards;
    }

    @Override
    public LinkedList<Line> Get_board() {
        return boards.get(0);
    }

    @Override
    public int Get_draw_restriction() {
        return draw_restriction;
    }

    @Override
    public int Get_erase_restriction() {
        return erase_restriction;
    }

    @Override
    public int Get_drag_restriction() {
        return drag_restriction;
    }

    @Override
    public int Get_special_type() {
        return special_type;
    }


    // Public method
    //---------------

    public boolean Can_shift() {
        return ( boards.size() > 1 );
    }
}