package symbolize.app.DataAccess;

import symbolize.app.Common.Session;
import symbolize.app.Puzzle.PuzzleDB;
import symbolize.app.R;

/*
 * An all static data access API, for save data about which levels you have unlocked
 */
abstract public class UnlocksDataAccess {
    // Static fields
    //--------------

    private static DataAccessObject dao = new DataAccessObject( R.string.preference_unlocks_key );


    // Getter methods
    //----------------

    public static boolean Is_unlocked( int world ) {
        return dao.Get_property(world + "", false) || Session.DEV_MODE;
    }

    public static boolean Is_unlocked( int world, int level ) {
        return dao.Get_property(world + "-" + level, false) || Session.DEV_MODE;
    }


    // Setter methods
    //----------------

    public static void Unlock( final byte world ) {
        if( !Session.DEV_MODE ) {
            dao.Set_property( world + "", true );
            dao.Set_property( world + "-1", true );
        }
    }

    public static void Unlock( final byte world, final byte level ) {
        if( !Session.DEV_MODE ) {
            dao.Set_property( world + "-" + level, true );
        }
    }


    // Public methods
    //-----------------

    /*
     * Removes all unlocked levels expect for world 1, and level 1-1
     */
    public static void Remove_all_unlocks() {
        for ( int world  = 1; world <= PuzzleDB.NUMBER_OF_WORLDS; ++world ) {
            dao.Set_property( world + "", false, false );
            for ( int level = 1; level <= PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD; ++level ) {
                dao.Set_property( world + "-" + level, false, false );
            }
        }

        dao.Set_property( "1", true, false );
        dao.Set_property( "1-1", true, false );
        dao.Commit();
    }
}
