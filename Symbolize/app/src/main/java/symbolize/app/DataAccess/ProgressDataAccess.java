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

    private static DataAccessObject dao = new DataAccessObject( R.string.preference_progress_key );


    // Getter methods
    //----------------

    public static boolean Is_completed( int world ) {
        //return true;
        return dao.Get_property( world + "", false ) || Session.DEV_MODE;
    }

    public static boolean Is_completed( int world, int level ) {
        //return true;
        return dao.Get_property( world + "-" + level, false ) || Session.DEV_MODE;
    }


    // Setter methods
    //----------------

    public static void Complete( int world ) {
        if( !Session.DEV_MODE ) {
            dao.Set_property( world + "", true );
        }
    }

    public static void Complete( int world, int level ) {
        if( !Session.DEV_MODE ) {
            if ( level == 0 ) {
                Complete( world );
            } else {
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
            dao.Set_property( world + "", false, false );
            for ( int level = 1; level <= PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD; ++level ) {
                dao.Set_property( world + "-" + level, false, false );
            }
        }

        dao.Commit();
    }
}
