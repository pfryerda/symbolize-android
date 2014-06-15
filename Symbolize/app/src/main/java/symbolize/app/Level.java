package symbolize.app;

import android.util.Log;

import org.simpleframework.xml.Default;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

/*
 * Class used to store all the information about a given puzzle
 * and a method to check to see if your guess is correct.
 */
@Root
@Default
public class Level {
    // Static field
    //-------------
    private static final Serializer serializer = new Persister();

    // Fields
    //--------

    private final int worldNum;
    private final int levelNum;
    private final String hint;
    private final int drawRestirction;
    private final int eraseRestirction;
    private final boolean rotateEnabled;
    private final boolean flipEnabled;
    private final boolean colourEnabled;
    private final ArrayList<LinkedList<Line>> boards;
    private final LinkedList<LinkedList<Line>> solutions;


    // Constructor
    //-------------
    public Level( int worldNum, int levelNum, String hint, int drawRestirction, int eraseRestirction,
                  boolean rotateEnabled, boolean flipEnabled, boolean colourEnabled,
                  ArrayList<LinkedList<Line>> boards, LinkedList<LinkedList<Line>> solutions )
    {
        this.levelNum = levelNum;
        this.worldNum = worldNum;
        this.hint = hint;
        this.drawRestirction = drawRestirction;
        this.eraseRestirction = eraseRestirction;
        this.rotateEnabled = rotateEnabled;
        this.flipEnabled = flipEnabled;
        this.colourEnabled = colourEnabled;
        this.boards = boards;
        this.solutions = solutions;
    }

    // Methods
    //---------

    public int getLevelNum() {
        return levelNum;
    }

    public int getWorldNum() {
        return worldNum;
    }

    public String getHint() {
        return hint;
    }

    public LinkedList<Line> getBoard() {
        return boards.get( 0 );
    }

    public int getDrawRestirction() {
        return drawRestirction;
    }

    public int getEraseRestirction() {
        return eraseRestirction;
    }

    public boolean canRotate() {
        return rotateEnabled;
    }

    public boolean canFlip() {
        return flipEnabled;
    }

    public boolean canChangeColur() {
        return colourEnabled;
    }

    public boolean canShift() {
        return ( boards.size() > 1 );
    }

    public ArrayList<LinkedList<Line>> getBoards()  {
        return boards;
    }

    public boolean checkCorrectness(LinkedList<Line> g) {
        for( LinkedList<Line> s : solutions ) {
            if ( s.size() == g.size() ) {
                @SuppressWarnings("unchecked")
                LinkedList<Line> soln = (LinkedList<Line>) s.clone();
                @SuppressWarnings("unchecked")
                LinkedList<Line> guess = (LinkedList<Line>) g.clone();
                while( !soln.isEmpty() ) {
                    Line match = null;
                    for ( Line line : guess ) {
                        if ( match == null ) {
                            if( line.eq( soln.getFirst() ) ) match = line;
                        } else {
                            if( line.eq( soln.getFirst() ) && ( soln.getFirst().score( line ) < soln.getFirst().score( match ) ) ) match = line;
                        }
                    }
                    if ( match == null ) break;
                    soln.removeFirst();
                    guess.remove( match );
                }
                if ( soln.isEmpty() ) return true;
            }
        }
        return false;
    }

    /*
     * Method used to store it's self in the proper xml file given its world/level num
     */
    public void StoreLevelInXml() {
        try {
            serializer.write( this,
                    //new File(  "/res/xml/World_" + worldNum + "/level_" + levelNum ) );
                    new File( "../Symbolize/app/src/main/res/xml/World_ " + worldNum + "/level_" + levelNum ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    // Static method
    //--------------

    public static Level fetch_level(int worldNum, int levelNum) {
        try {
            return serializer.read(Level.class,
                new File(  "/res/xml/World_" + worldNum + "/level_" + levelNum ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null; // Should never happen!
    }
}