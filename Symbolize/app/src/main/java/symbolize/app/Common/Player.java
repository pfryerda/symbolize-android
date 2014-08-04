package symbolize.app.Common;

import android.content.Context;
import android.content.SharedPreferences;

import symbolize.app.Game.GameActivity;
import symbolize.app.Puzzle.PuzzleDB;
import symbolize.app.R;

public class Player {
    // Static field
    //-------------

    private static Player instance;
    public static final boolean DEVMODE = false;

    // Fields
    //--------

    private final SharedPreferences unlocks_dao = SymbolizeActivity
            .Get_context()
            .getSharedPreferences( SymbolizeActivity.Get_resource_string( R.string.preference_unlocks_key ),
                    Context.MODE_PRIVATE );
    private final SharedPreferences.Editor unlocks_editor = unlocks_dao.edit();

    private final SharedPreferences completed_dao = SymbolizeActivity
            .Get_context()
            .getSharedPreferences( SymbolizeActivity.Get_resource_string( R.string.preference_completed_key ),
                    Context.MODE_PRIVATE );
    private final SharedPreferences.Editor completed_editor = completed_dao.edit();

    private int current_world;
    private int current_level;
    private boolean draw_enabled;


    // Constructor
    //-------------

    public static Player Get_instance() {
        if ( instance == null ) {
            instance = new Player();
        }
        return instance;
    }

    private Player() {
        SharedPreferences settings_dao  = SymbolizeActivity
                .Get_context()
                .getSharedPreferences(SymbolizeActivity.Get_resource_string(R.string.preference_unlocks_key),
                        Context.MODE_PRIVATE );

        Unlock( 1 );
        Unlock( 1, 1 );

        current_world = settings_dao.getInt( "current_world", 1 );
        current_level = 0;
        this.draw_enabled = true;
    }


    // Public methods
    //---------------

    /*
     *
     */
    public String Get_current_puzzle_text() {
        if ( current_level == 0 ) {
            return "World: " + current_world;
        } else {
            return "Level: " + current_world + "-" + current_level;
        }
    }

    /*
     * Decrease the current world number and will wrap back to last world if at world 1
     */
    public void Decrease_world() {
        current_world = get_previous_world();
    }

    /*
     * Increase the current world number will wrap back to world 1 if at last world
     */
    public void Increase_world() {
        current_world = get_next_world();
    }

    /*
     * Set current level to 0 implying in a 'world' level
     */
    public void Set_to_world_level() {
        current_level = 0;
    }

    /*
     * Returns whether the give level/world is unlocked
     *
     * @param int world: The world of interest
     * @param int level: The level of interest
     */
    public boolean Is_unlocked( int world ) {
        //return true;
        return unlocks_dao.getBoolean( world + "", false ) || DEVMODE;
    }

    public boolean Is_unlocked( int world, int level ) {
        //return true;
        return unlocks_dao.getBoolean(world + "-" + level, false) || DEVMODE;
    }

    /*
     * Methods used to determine is the next/previous worlds are unlocked
     */
    public boolean Is_previous_world_unlocked() {
        return Is_unlocked( get_previous_world() );
    }

    public boolean Is_next_world_unlocked() {
        return Is_unlocked( get_next_world() );
    }

    /*
     * Set the the given level/world as unlocked
     *
     * @param int world: The world of interest
     * @param int world: The level of interest
     */
    public void Unlock( int world ) {
        unlocks_editor.putBoolean( world + "" , true );
        unlocks_editor.putBoolean( world + "-" + 1, true );
        unlocks_editor.commit();
    }

    public void Unlock( int world, int level ) {
        unlocks_editor.putBoolean( world + "-" + level, true );
        unlocks_editor.commit();
    }

    /*
     * Returns whether the give level/world is complete
     *
     * @param int world: The world of interest
     * @param int level: The level of interest
     */
    public boolean Is_completed( int world ) {
        //return true;
        return completed_dao.getBoolean( world + "", false ) || DEVMODE;
    }

    public boolean Is_completed( int world, int level ) {
        //return true;
        return completed_dao.getBoolean(world + "-" + level, false) || DEVMODE;
    }

    /*
     * Set the the current level/world as completed
     */
    public void Complete() {
        if( current_level == 0 ) {
            completed_editor.putBoolean( current_world + "", true );
        } else {
            completed_editor.putBoolean( current_world + "-" + current_level, true );
        }
        completed_editor.commit();
    }

    public void Delete_all_data() {
        for ( int world  = 1; world <= PuzzleDB.NUMBEROFWORLDS; ++world ) {
            unlocks_editor.putBoolean(world + "", false);
            for ( int level = 1; level <= PuzzleDB.NUMBEROFLEVELSPERWORLD; ++level ) {
                unlocks_editor.putBoolean( world + "-" + level, false );
            }
        }

        unlocks_editor.putBoolean( "1", true);
        unlocks_editor.putBoolean( "1-1", true );
        unlocks_editor.commit();
    }

    public void Commit_current_world() {
        SharedPreferences settings_dao  = SymbolizeActivity
                .Get_context()
                .getSharedPreferences( SymbolizeActivity.Get_resource_string( R.string.preference_unlocks_key ),
                        Context.MODE_PRIVATE );
        SharedPreferences.Editor settings_editor = settings_dao.edit();

        settings_editor.putInt( "current_world", current_world );
        settings_editor.commit();
    }


    // Getter methods
    //----------------

    public int Get_current_world() {
        return unlocks_dao.getInt( "current_world", 1 );
    }

    public int Get_current_level() {
        return current_level;
    }

    public boolean In_draw_mode() {
        return draw_enabled;
    }

    public boolean In_erase_mode() {
        return !draw_enabled;
    }


    // Setter method
    //--------------

    public void Set_draw_mode() {
        draw_enabled = true;
    }

    public void Set_erase_mode() {
        draw_enabled = false;
    }

    public void Set_current_level( int new_level ) {
        current_level = new_level;
    }


    // Private methods
    //-----------------

    private int get_next_world() {
        return ( Get_current_world() % PuzzleDB.NUMBEROFWORLDS ) + 1;
    }

    private int get_previous_world() {
        return ( Get_current_world() == 1 ) ? PuzzleDB.NUMBEROFWORLDS : Get_current_world() - 1;
    }
}
