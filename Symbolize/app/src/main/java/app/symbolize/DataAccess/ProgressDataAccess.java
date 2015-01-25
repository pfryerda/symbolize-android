package app.symbolize.DataAccess;

import app.symbolize.Common.Session;
import app.symbolize.Puzzle.PuzzleDB;
import app.symbolize.R;

/*
 * An all static data access API for storing save data about what levels you have completed
 */
public class ProgressDataAccess {
    // Static field
    //-------------

    private static final DataAccessObject dao = new DataAccessObject( R.string.preference_progress_key );


    // Static methods
    //----------------

    /*
     * Commit changes to disk
     */
    public static void Commit() {
        dao.Commit();
    }


    // Field
    //------

    private final boolean[][] progress =
            new boolean[PuzzleDB.NUMBER_OF_WORLDS][PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD + 1];


    // Singleton setup
    //-----------------

    private static final ProgressDataAccess instance = new ProgressDataAccess();

    public static ProgressDataAccess Get_instance() {
        return instance;
    }


    // Constructor
    //-------------

    private ProgressDataAccess() {
        for ( byte world = 1; world <= PuzzleDB.NUMBER_OF_WORLDS; ++world ) {
            progress[world - 1][0] = dao.Get_property( world + "", false );
            for ( byte level = 1; level <= PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD; ++level ) {
                progress[world - 1][level] = dao.Get_property( world + "-" + level, false );
            }
        }
    }


    // Getter methods
    //----------------

    public boolean Is_completed( int world ) {
        return Is_completed( world, 0 );
    }

    public boolean Is_completed( int world, int level ) {
        //return true;
        return progress[world - 1][level] || Session.DEV_MODE;
    }


    // Setter methods
    //----------------

    public void Complete( int world ) {
        if( !Session.DEV_MODE ) {
            progress[world - 1][0] = true;
            dao.Set_property( world + "", true );
        }
    }

    public void Complete( int world, int level ) {
        if( !Session.DEV_MODE ) {
            if ( level == 0 ) {
                Complete( world );
            } else {
                progress[world - 1][level] = true;
                dao.Set_property( world + "-" + level, true );
            }
        }
    }


    // Public methods
    //----------------

    /*
     * Gets a string representing the number of levels completed in a given world
     *
     * @param byte world: The world you want to see how levels are complete in
     *
     * @return String: "Number of levels complete in world / Number of levels in world"
     */
    public String Get_number_of_complete_levels_string( final byte world ) {
        byte number_of_complete_levels = 0;
        for ( byte level = 1; level <= PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD; ++level ) {
            if ( Is_completed( world, level ) ) {
                ++number_of_complete_levels;
            }
        }

        return " " + number_of_complete_levels + " / " + PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD;
    }

    /*
     * Removes all saved data, i.e. levels completed
     */
    public void Remove_all_progress() {
        for ( int world  = 1; world <= PuzzleDB.NUMBER_OF_WORLDS; ++world ) {
            progress[world -1][0] = false;
            dao.Set_property( world + "", false );
            for ( int level = 1; level <= PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD; ++level ) {
                progress[world -1 ][level] = false;
                dao.Set_property( world + "-" + level, false );
            }
        }
    }
}
