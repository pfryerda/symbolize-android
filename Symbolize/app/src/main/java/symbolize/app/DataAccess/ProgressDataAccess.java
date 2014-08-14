package symbolize.app.DataAccess;


import symbolize.app.Common.Player;
import symbolize.app.Puzzle.PuzzleDB;
import symbolize.app.R;

abstract public class ProgressDataAccess {
    // Static fields
    //--------------

    private static DataAccessObject dao = new DataAccessObject( R.string.preference_progress_key );

    // Inherited methods
    //-------------------
    /*
        protected static SharedPreferences dao;
        protected static SharedPreferences.Editor dao_editor;
     */


    // Public methods
    //----------------

    /*
     * Returns whether the give level/world is complete
     *
     * @param int world: The world of interest
     * @param int level: The level of interest
     */
    public static boolean Is_completed( int world ) {
        //return true;
        return dao.Get_property( world + "", false ) || Player.DEV_MODE;
    }

    public static boolean Is_completed( int world, int level ) {
        //return true;
        return dao.Get_property( world + "-" + level, false ) || Player.DEV_MODE;
    }

    /*
     * Set the the current level/world as completed
     */
    public static void Complete( int world ) {
        if( Player.DEV_MODE ) {
            dao.Set_property( world + "", true );
        }
    }

    public static void Complete( int world, int level ) {
        if( !Player.DEV_MODE ) {
            if ( level == 0 ) {
                Complete( world );
            } else {
                dao.Set_property( world + "-" + level, true );
            }
        }
    }

    public static void Remove_all_progress() {
        for ( int world  = 1; world <= PuzzleDB.NUMBEROFWORLDS; ++world ) {
            dao.Set_property( world + "", false, false );
            for ( int level = 1; level <= PuzzleDB.NUMBEROFLEVELSPERWORLD; ++level ) {
                dao.Set_property( world + "-" + level, false, false );
            }
        }

        dao.Commit();
    }
}
