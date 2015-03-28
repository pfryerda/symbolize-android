package app.symbolize.Common;

import android.content.Context;
import android.content.SharedPreferences;

import app.symbolize.DataAccess.MetaDataAccess;
import app.symbolize.Game.GameUIView;
import app.symbolize.Puzzle.Puzzle;
import app.symbolize.Puzzle.PuzzleDB;
import app.symbolize.R;
import app.symbolize.Routing.Page;

/*
 * A singleton class that keeps track of the game's current state.
 */
public class Session {
    // Flags
    //-------

    public static final byte VERSION_ALPHA = 0;
    public static final byte VERSION_BETA  = 1;
    public static final byte VERSION_PROD  = 2;


    // Static fields
    //---------------

    public static final byte VERSION = VERSION_BETA;
    public static final boolean DEV_MODE = true;


    // Fields
    //--------


    private byte current_world;
    private byte current_level;

    private int highlight_color;
    private int dialog_color;
    private int world_color;
    private Puzzle current_puzzle;

    private boolean draw_enabled;

    private Posn current_pivot;


    // Singleton setup
    //-----------------

    private static Session instance = new Session();

    public static Session Get_instance() {
        return instance;
    }


    // Constructor
    //-------------

    private Session() {
        this.current_world = MetaDataAccess.Get_last_world();
        this.current_level = 0;

        this.highlight_color = GameUIView.BRIGHT_COLOR_ARRAY.get( current_world - 1 );
        this.dialog_color = GameUIView.LIGHT_COLOR_ARRAY.get( current_world - 1 );
        this.world_color = GameUIView.COLOR_ARRAY.get( current_world - 1 );
        this.current_puzzle = null;

        this.draw_enabled = MetaDataAccess.Get_last_draw_enabled();
        this.current_pivot = null;
    }


    // Getter methods
    //----------------

    public int Get_world_color() { return world_color; }

    public int Get_hightlight_color() {
        if(Page.Is_Game_page()) return highlight_color;
        else return Page.Get_context().getResources().getColor( R.color.gray );
    }

    public int Get_dialog_color() {
        if(Page.Is_Game_page()) return dialog_color;
        else return Page.Get_context().getResources().getColor( R.color.white );
    }

    public Puzzle Get_current_puzzle() {
        return current_puzzle;
    }

    public byte Get_current_world() {
        return current_world;
    }

    public byte Get_current_level() {
        return current_level;
    }

    public boolean In_draw_mode() {
        return draw_enabled;
    }

    public boolean In_erase_mode() {
        return !draw_enabled;
    }

    public boolean Is_in_world_view() {
        return current_level == 0;
    }

    public byte Get_next_world() {
        return (byte) ( ( Get_current_world() % PuzzleDB.NUMBER_OF_WORLDS ) + 1 );
    }

    public byte Get_previous_world() {
        return (byte) ( ( Get_current_world() == 1 ) ? PuzzleDB.NUMBER_OF_WORLDS : Get_current_world() - 1 );
    }

    public Posn Get_current_pivot() {
        return current_pivot;
    }


    // Setter method
    //--------------

    public void Update_puzzle() {
        current_puzzle = PuzzleDB.Fetch_puzzle();
        world_color = GameUIView.COLOR_ARRAY.get( current_world - 1 );
        highlight_color = GameUIView.BRIGHT_COLOR_ARRAY.get( current_world - 1 );
        dialog_color = GameUIView.LIGHT_COLOR_ARRAY.get( current_world - 1 );
        GameUIView.Highlight_current_mode();
    }

    public void Set_draw_mode() {
        draw_enabled = true;
    }

    public void Set_erase_mode() {
        draw_enabled = false;
    }


    public void Set_current_level( byte new_level ) {
        current_level = new_level;
    }

    public void Set_pivot( Posn pivot ) {
        this.current_pivot = pivot;
    }


    // Public methods
    //---------------

    /*
     * Gets the text of the current world/level and formats it nicely
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
        current_puzzle = PuzzleDB.Fetch_puzzle();
    }

    /*
     * Increase the current world number will wrap back to world 1 if at last world
     */
    public void Increase_world() {
        current_world = Get_next_world();
        current_puzzle = PuzzleDB.Fetch_puzzle();
    }

    /*
     * Set current level to 0 implying in a 'world' level
     */
    public void Set_to_world() {
        current_level = 0;
        current_puzzle = PuzzleDB.Fetch_puzzle();
    }

    /*
     * Reset session
     */
    public void Reset() {
        this.current_world = 1;
        this.current_level = 0;

        this.world_color = Page.Get_context().getResources().getColor( R.color.green );
        this.current_puzzle = null;

        this.draw_enabled = true;
        this.current_pivot = null;
    }
}
