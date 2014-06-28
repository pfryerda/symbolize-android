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
}
