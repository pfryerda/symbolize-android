package symbolize.app.DataAccess;


import symbolize.app.Common.Session;
import symbolize.app.Puzzle.PuzzleDB;
import symbolize.app.R;

abstract public class UnlocksDataAccess {
    // Static fields
    //--------------

    private static DataAccessObject dao = new DataAccessObject( R.string.preference_unlocks_key );


    // Public methods
    //----------------

    /*
     * Returns whether the give level/world is unlocked
     *
     * @param int world: The world of interest
     * @param int level: The level of interest
     */
    public static boolean Is_unlocked( int world ) {
        //return true;
        return dao.Get_property(world + "", false) || Session.DEV_MODE;
    }

    public static boolean Is_unlocked( int world, int level ) {
        //return true;
        return dao.Get_property(world + "-" + level, false) || Session.DEV_MODE;
    }

    /*
     * Set the the given level/world as unlocked
     *
     * @param int world: The world of interest
     * @param int world: The level of interest
     */
    public static void Unlock( int world ) {
        if( !Session.DEV_MODE ) {
            dao.Set_property( world + "", true );
            dao.Set_property( world + "-1", true );
        }
    }

    public static void Unlock( int world, int level ) {
        if( !Session.DEV_MODE ) {
            dao.Set_property( world + "-" + level, true );
        }
    }

    public static void Remove_all_unlocks() {
        for ( int world  = 1; world <= PuzzleDB.NUMBEROFWORLDS; ++world ) {
            dao.Set_property( world + "", false, false );
            for ( int level = 1; level <= PuzzleDB.NUMBEROFLEVELSPERWORLD; ++level ) {
                dao.Set_property( world + "-" + level, false, false );
            }
        }

        dao.Set_property( "1", true, false );
        dao.Set_property( "1-1", true, false );
        dao.Commit();
    }
}
