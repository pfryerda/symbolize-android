package symbolize.app.Puzzle;

import java.util.ArrayList;
import java.util.LinkedList;
import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Session;

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

    private final boolean colour_enabled;
    private final byte draw_restriction;
    private final byte erase_restriction;
    private final byte drag_restriction;
    private final byte special_type;
    protected final ArrayList<LinkedList<Line>> boards;


    // Constructors
    //-------------


    public Level( final String hint, final byte draw_restriction, final byte erase_restriction, final byte drag_restriction,
                  final byte special_type, final boolean rotate_enabled, final boolean flip_enabled, final boolean colour_enabled,
                  final ArrayList<LinkedList<Line>> boards, final ArrayList<LinkedList<Line>> solutions, final ArrayList<Byte> unlocks )
    {
        super( hint, rotate_enabled, flip_enabled, solutions, unlocks );
        this.colour_enabled = colour_enabled;
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
    public byte Get_draw_restriction() {
        return draw_restriction;
    }

    @Override
    public byte Get_erase_restriction() {
        return erase_restriction;
    }

    @Override
    public byte Get_drag_restriction() {
        return drag_restriction;
    }

    @Override
    public byte Get_special_type() {
        return special_type;
    }


    // Public method
    //---------------

    public boolean Can_change_color() {
        return colour_enabled|| Session.DEV_MODE;
    }

    public boolean Can_shift() {
        return ( boards.size() > 1 );
    }
}