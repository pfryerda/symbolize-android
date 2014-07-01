package symbolize.app.Common;

import java.util.ArrayList;
import java.util.LinkedList;

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
    protected final ArrayList<LinkedList<Line>> boards;


    // Constructors
    //-------------

    public Level() {
        super();
        this.draw_restriction = 0;
        this.erase_restriction = 0;
        this.boards = new ArrayList<LinkedList<Line>>();
    }

    public Level( String hint, int draw_restriction, int erase_restriction,
                  boolean rotate_enabled, boolean flip_enabled, boolean colour_enabled,
                  ArrayList<LinkedList<Line>> boards, ArrayList<LinkedList<Line>> solutions )
    {
        super( hint, rotate_enabled, flip_enabled, colour_enabled, solutions );
        this.draw_restriction = draw_restriction;
        this.erase_restriction = erase_restriction;
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

}