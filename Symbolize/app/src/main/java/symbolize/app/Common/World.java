package symbolize.app.Common;

import java.util.ArrayList;
import java.util.LinkedList;

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

    public World( String hint, boolean rotate_enabled, boolean flip_enabled, boolean colour_enabled,
                  ArrayList<Posn> levels, ArrayList<LinkedList<Line>> solutions )
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
}
