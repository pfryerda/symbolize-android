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

    public ArrayList<Posn> getLevels() {
        return levels;
    }

    public ArrayList<LinkedList<Line>> getBoards() { return new ArrayList<LinkedList<Line>>(); }

    public LinkedList<Line> getBoard() { return new LinkedList<Line>(); }

    public int getDrawRestirction() {
        int number_of_vertcies = PuzzleDB.NUMBEROFLEVELSPERWORLD;
        return ( number_of_vertcies * ( number_of_vertcies - 1 ) ) / 2;
    }

    public int getEraseRestirction() { return 0; }

    public boolean canShift() { return false; }
}
