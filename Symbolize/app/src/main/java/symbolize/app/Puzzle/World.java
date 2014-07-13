package symbolize.app.Puzzle;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;

public class World extends Puzzle {
    // Inherited fields
    //------------------

    //private final String hint;
    //private final boolean rotate_enabled;
    //private final boolean flip_enabled;
    //private final boolean colour_enabled;
    //private final ArrayList<LinkedList<Line>> solutions;


    // Fields
    //--------

    private final ArrayList<Posn> levels;


    // Constructor
    //-------------

    public World( final String hint, final boolean rotate_enabled, final boolean flip_enabled, final boolean colour_enabled,
                  final ArrayList<Posn> levels, final ArrayList<LinkedList<Line>> solutions )
    {
        super( hint, rotate_enabled, flip_enabled, colour_enabled, solutions );
        this.levels = levels;
    }


    // Public method
    //----------------

    public boolean Can_shift() { return false; }


    // Getter methods
    //---------------

    public ArrayList<Posn> Get_levels() {
        return levels;
    }

    public ArrayList<LinkedList<Line>> Get_boards() { return new ArrayList<LinkedList<Line>>(); }

    public LinkedList<Line> Get_board() { return new LinkedList<Line>(); }

    public int Get_draw_restriction() {
        int number_of_vertices = PuzzleDB.NUMBEROFLEVELSPERWORLD;
        return ( number_of_vertices * ( number_of_vertices - 1 ) ) / 2;
    }

    public int Get_erase_restriction() { return 0; }

    public int Get_drag_restriction() {
        return 0;
    }
}
