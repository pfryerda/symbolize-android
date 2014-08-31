package symbolize.app.DataAccess;

import symbolize.app.Common.Session;
import symbolize.app.Puzzle.PuzzleDB;
import symbolize.app.R;

/*
 * An all static data access API for storing save data about what levels you have completed
 */
abstract public class ProgressDataAccess {
    // Static fields
    //--------------

    private static final DataAccessObject dao = new DataAccessObject( R.string.preference_progress_key );

    private static final boolean[][] progress =
            new boolean[PuzzleDB.NUMBER_OF_WORLDS][PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD + 1];


    // Static block
    //-------------

    static {
        for ( byte world = 1; world <= PuzzleDB.NUMBER_OF_WORLDS; ++world ) {
            progress[world - 1][0] = dao.Get_property( world + "", false );
            for ( byte level = 1; level <= PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD; ++level ) {
                progress[world - 1][level] = dao.Get_property( world + "-" + level, false );
            }
        }
    }


    // Getter methods
    //----------------

    public static boolean Is_completed( int world ) {
        return Is_completed( world, 0 );
    }

    public static boolean Is_completed( int world, int level ) {
        //return true;
        return progress[world - 1][level] || Session.DEV_MODE;
    }


    // Setter methods
    //----------------

    public static void Complete( int world ) {
        if( !Session.DEV_MODE ) {
            progress[world - 1][0] = true;
            dao.Set_property( world + "", true );
        }
    }

    public static void Complete( int world, int level ) {
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
     * Removes all saved data, i.e. levels completed
     */
    public static void Remove_all_progress() {
        for ( int world  = 1; world <= PuzzleDB.NUMBER_OF_WORLDS; ++world ) {
            progress[world -1][0] = false;
            dao.Set_property( world + "", false );
            for ( int level = 1; level <= PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD; ++level ) {
                progress[world -1 ][level] = false;
                dao.Set_property( world + "-" + level, false );
            }
        }
    }

    /*
     * Commit changes to disk
     */
    public static void Commit() {
        dao.Commit();
    }
}
