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
    //private final boolean rotateEnabled;
    //private final boolean flipEnabled;
    //private final boolean colourEnabled;
    //private final ArrayList<LinkedList<Line>> solutions;


    // Fields
    //--------

    private final int drawRestirction;
    private final int eraseRestirction;
    protected final ArrayList<LinkedList<Line>> boards;


    // Constructors
    //-------------

    public Level() {
        super();
        this.drawRestirction = 0;
        this.eraseRestirction = 0;
        this.boards = new ArrayList<LinkedList<Line>>();
    }

    public Level( String hint, int drawRestirction, int eraseRestirction,
                  boolean rotateEnabled, boolean flipEnabled, boolean colourEnabled,
                  ArrayList<LinkedList<Line>> boards, ArrayList<LinkedList<Line>> solutions )
    {
        super( hint, rotateEnabled, flipEnabled, colourEnabled, solutions );
        this.drawRestirction = drawRestirction;
        this.eraseRestirction = eraseRestirction;
        this.boards = boards;
    }

    // Methods
    //---------

    public LinkedList<Line> getBoard() {
        if ( boards.size() > 0 ) {
            return boards.get(0);
        } else {
            return new LinkedList<Line>();
        }
    }

    public int getDrawRestirction() {
        return drawRestirction;
    }

    public int getEraseRestirction() {
        return eraseRestirction;
    }

    public boolean canShift() {
        return ( boards.size() > 1 );
    }

    public ArrayList<LinkedList<Line>> getBoards()  {
        return boards;
    }

}