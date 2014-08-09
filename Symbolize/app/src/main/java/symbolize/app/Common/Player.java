package symbolize.app.Common;

import android.content.Context;
import android.content.SharedPreferences;

import symbolize.app.DataAccess.UnlocksDataAccess;
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

    private boolean in_world_view;
    private int current_world;
    private int current_level;
    private boolean draw_enabled;


    // Constructor/Get_instance
    //---------------------------

    public static Player Get_instance() {
        if ( instance == null ) {
            instance = new Player();
        }
        return instance;
    }

    private Player() {
        SharedPreferences settings_dao = SymbolizeActivity
                .Get_context()
                .getSharedPreferences(SymbolizeActivity.Get_resource_string(R.string.preference_unlocks_key),
                        Context.MODE_PRIVATE );

        this.in_world_view = true;
        this.current_world = settings_dao.getInt( "current_world", 1 );
        this.current_level = 0;
        this.draw_enabled = true;
    }


    // Public methods
    //---------------

    /*
     * Gets the text of the current world/level formated nicly
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
        current_world = Get_previous_world();
    }

    /*
     * Increase the current world number will wrap back to world 1 if at last world
     */
    public void Increase_world() {
        current_world = Get_next_world();
    }

    /*
     * Set current level to 0 implying in a 'world' level
     */
    public void Set_to_world_level() {
        current_level = 0;
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

    public boolean Is_in_world_view() {
        return in_world_view;
    }

    public int Get_next_world() {
        return ( Get_current_world() % PuzzleDB.NUMBEROFWORLDS ) + 1;
    }

    public int Get_previous_world() {
        return ( Get_current_world() == 1 ) ? PuzzleDB.NUMBEROFWORLDS : Get_current_world() - 1;
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

    public void Toggle_world_view() {
        in_world_view = !in_world_view;
    }
}
