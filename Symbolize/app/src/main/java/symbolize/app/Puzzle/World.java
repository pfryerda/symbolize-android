package symbolize.app.Puzzle;

import java.util.ArrayList;
import java.util.LinkedList;
import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;

/*
 * Class used to store all the information about a given world.
 */
public class World extends Puzzle {
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

    private final ArrayList<Posn> levels;


    // Constructor
    //-------------

    public World( final String hint, final boolean rotate_enabled, final boolean flip_enabled,
                  final ArrayList<Posn> levels, final ArrayList<LinkedList<Line>> solutions, final ArrayList<Byte> unlocks )
    {
        super( hint, rotate_enabled, flip_enabled, solutions, unlocks );
        this.levels = levels;
    }


    // Getter methods
    //---------------

    @Override
    public ArrayList<Posn> Get_levels() {
        return levels;
    }

    @Override
    public ArrayList<LinkedList<Line>> Get_boards() { return new ArrayList<LinkedList<Line>>(); }

    @Override
    public LinkedList<Line> Get_board() { return new LinkedList<Line>(); }

    @Override
    public byte Get_draw_restriction() {
        int number_of_vertices = PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD;
        return (byte) ( ( number_of_vertices * ( number_of_vertices - 1 ) ) / 2 );
    }

    @Override
    public byte Get_erase_restriction() { return 0; }

    @Override
    public byte Get_drag_restriction() {
        return 0;
    }

    @Override
    public boolean Can_change_color() { return false; }

    @Override
    public boolean Can_shift() { return false; }

    @Override
    public byte Get_special_type() {
        return 0;
    }
}
