package symbolize.app.Common;

import android.content.SharedPreferences;
import symbolize.app.Puzzle.PuzzleDB;

public class Player {
    // Static field
    //-------------

    public static final boolean DEVMODE = false;

    // Fields
    //--------

    private boolean[] world_unlocks;
    private boolean[] level_unlocks;
    private SharedPreferences.Editor unlocks_editor;
    private Settings settings;
    private int current_world;
    private int current_level;
    private boolean draw_enabled;


    // Constructor
    //-------------

    public Player( SharedPreferences unlocks_dao, SharedPreferences settings_dao ) {
        this.unlocks_editor = unlocks_dao.edit();
        this.settings = new Settings( settings_dao );

        world_unlocks = new boolean[PuzzleDB.NUMBEROFWORLDS];
        level_unlocks = new boolean[PuzzleDB.NUMBEROFWORLDS*PuzzleDB.NUMBEROFLEVELSPERWORLD];

        // Read data from dao
        for ( int world = 1; world <= PuzzleDB.NUMBEROFWORLDS; ++world ) {
            world_unlocks[(world-1)] = unlocks_dao.getBoolean( world + "", false );
            for ( int level = 1; level <= PuzzleDB.NUMBEROFLEVELSPERWORLD; ++level ) {
                level_unlocks[(world-1)*PuzzleDB.NUMBEROFLEVELSPERWORLD + ( level - 1)] =
                        unlocks_dao.getBoolean(world + "-" + level, false);
            }
        }

        // Set default values
        world_unlocks[0] = true;
        level_unlocks[0] = true;
        current_world = 1;
        current_level = 0;
        this.draw_enabled = true;
    }


    // Public methods
    //---------------

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
        return true;
        //return world_unlocks[world - 1] || DEVMODE;
    }

    public boolean Is_unlocked( int world, int level ) {
        return true;
        //return level_unlocks[(world - 1)*PuzzleDB.NUMBEROFLEVELSPERWORLD + ( level - 1 )] || DEVMODE;
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
        level_unlocks[world - 1] = true;
        unlocks_editor.putBoolean( world + "" , true );
        unlocks_editor.commit();
    }

    public void Unlock( int world, int level ) {
        level_unlocks[(world-1)*PuzzleDB.NUMBEROFLEVELSPERWORLD + ( level -1 )] = true;
        unlocks_editor.putBoolean( world + "-" + level, true );
        unlocks_editor.commit();
    }


    // Getter methods
    //----------------

    public Settings Get_settings() {
        return settings;
    }

    public int Get_current_world() {
        return current_world;
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
        return ( current_world % PuzzleDB.NUMBEROFWORLDS ) + 1;
    }

    private int get_previous_world() {
        return ( current_world == 1 ) ? PuzzleDB.NUMBEROFWORLDS : current_world - 1;
    }
}
