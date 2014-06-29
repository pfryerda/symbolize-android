package symbolize.app.Common;

import java.util.ArrayList;
import java.util.LinkedList;

public class World extends Puzzle {
    // Inherited fields
    //------------------

    //private final String hint;
    //private final boolean rotateEnabled;
    //private final boolean flipEnabled;
    //private final boolean colourEnabled;
    //private final ArrayList<LinkedList<Line>> solutions;


    // Fields
    //--------

    private final ArrayList<Posn> levels;


    // Constructor
    //-------------

    public World( String hint, boolean rotateEnabled, boolean flipEnabled, boolean colourEnabled,
                  ArrayList<Posn> levels, ArrayList<LinkedList<Line>> solutions )
    {
        super( hint, rotateEnabled, flipEnabled, colourEnabled, solutions );
        this.levels = levels;
    }


    // Methods
    //---------

    public ArrayList<Posn> Get_levels() {
        return levels;
    }

    public ArrayList<LinkedList<Line>> Get_boards() { return new ArrayList<LinkedList<Line>>(); }

    public LinkedList<Line> Get_board() { return new LinkedList<Line>(); }

    public int Get_draw_restriction() {
        int number_of_vertcies = PuzzleDB.NUMBEROFLEVELSPERWORLD;
        return ( number_of_vertcies * ( number_of_vertcies - 1 ) ) / 2;
    }

    public int Get_erase_restriction() { return 0; }

    public boolean Can_shift() { return false; }
}
