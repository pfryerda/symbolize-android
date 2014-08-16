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

    public World( final String hint, final boolean rotate_enabled, final boolean flip_enabled, final boolean colour_enabled,
                  final ArrayList<Posn> levels, final ArrayList<LinkedList<Line>> solutions, final ArrayList<Integer> unlocks )
    {
        super( hint, rotate_enabled, flip_enabled, colour_enabled, solutions, unlocks );
        this.levels = levels;
    }


    // Getter methods
    //---------------

    public ArrayList<Posn> Get_levels() {
        return levels;
    }

    public ArrayList<LinkedList<Line>> Get_boards() { return new ArrayList<LinkedList<Line>>(); }

    public LinkedList<Line> Get_board() { return new LinkedList<Line>(); }

    public int Get_draw_restriction() {
        int number_of_vertices = PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD;
        return ( number_of_vertices * ( number_of_vertices - 1 ) ) / 2;
    }

    public int Get_erase_restriction() { return 0; }

    public int Get_drag_restriction() {
        return 0;
    }


    // Public method
    //----------------

    public boolean Can_shift() { return false; }
}
