package symbolize.app.DataAccess;

import symbolize.app.Common.Session;
import symbolize.app.Puzzle.PuzzleDB;
import symbolize.app.R;

/*
 * An all static data access API, for save data about which levels you have unlocked
 */
public class UnlocksDataAccess {
    // Static field
    //--------------

    private static final DataAccessObject dao = new DataAccessObject( R.string.preference_unlocks_key );


    // Static methods
    //----------------

    /*
     * Commit changes to disk
     */
    public static void Commit() {
        dao.Commit();
    }



    // Field
    //--------

    private boolean[][] unlocked = new boolean[PuzzleDB.NUMBER_OF_WORLDS][PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD + 1];


    // Singleton setup
    //-----------------

    private static final UnlocksDataAccess instance = new UnlocksDataAccess();

    public static UnlocksDataAccess Get_instance() {
        return instance;
    }


    // Constructor
    //-------------

    private UnlocksDataAccess() {
        for ( byte world = 1; world <= PuzzleDB.NUMBER_OF_WORLDS; ++world ) {
            unlocked[world - 1][0] = dao.Get_property(world + "", false);
            for ( byte level = 1; level <= PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD; ++level ) {
                unlocked[world - 1][level] = dao.Get_property( world + "-" + level, false );
            }
        }
    }



    // Getter methods
    //----------------

    public boolean Is_unlocked( int world ) {
        return Is_unlocked( world, 0 );
    }

    public boolean Is_unlocked( int world, int level ) {
        return unlocked[world - 1][level] || Session.DEV_MODE;
    }


    // Setter methods
    //----------------

    public void Unlock( final byte world ) {
        if( !Session.DEV_MODE ) {
            unlocked[world - 1][0] = true;
            unlocked[world - 1][1] = true;

            dao.Set_property( world + "", true );
            dao.Set_property( world + "-1", true );
        }
    }

    public void Unlock( final byte world, final byte level ) {
        if( !Session.DEV_MODE ) {
            unlocked[world - 1][level] = true;
            dao.Set_property( world + "-" + level, true );
        }
    }


    // Public methods
    //-----------------

    /*
     * Removes all unlocked levels expect for world 1, and level 1-1
     */
    public void Remove_all_unlocks() {
        for ( int world  = 1; world <= PuzzleDB.NUMBER_OF_WORLDS; ++world ) {
            unlocked[world - 1][0] = false;
            dao.Set_property( world + "", false );
            for ( int level = 1; level <= PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD; ++level ) {
                unlocked[world - 1][level] = false;
                dao.Set_property( world + "-" + level, false );
            }
        }

        Unlock( (byte) 1 );
    }
}
