package symbolize.app.Common;


import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.Dialog.SymbolizeDialog;
import symbolize.app.Game.GameView;
import symbolize.app.Puzzle.Puzzle;

public class Request {
    // Flags
    //-------

    public static final int Drag_start           = 0;
    public static final int Drag_end             = 1;
    public static final int None                 = 2;
    public static final int Reset                = 3;
    public static final int Background_change    = 4;
    public static final int Shadow_point         = 5;
    public static final int Shadow_line          = 6;
    public static final int Change_color         = 7;
    public static final int Draw                 = 8;
    public static final int Erase                = 9;
    public static final int Rotate_right         = 10;
    public static final int Rotate_left          = 11;
    public static final int Flip_horizontally    = 12;
    public static final int Flip_vertically      = 13;
    public static final int Shift                = 14;
    public static final int Load_level_via_world = 15;
    public static final int Load_world_via_level = 16;
    public static final int Load_puzzle_left     = 17;
    public static final int Load_puzzle_right    = 18;


    // Fields
    //--------

    public int type;

    public Puzzle puzzle;

    public Line request_line;
    public Posn request_point;

    public ArrayList<LinkedList<Line>> shift_graphs;

    public LinkedList<Line> graph;
    public ArrayList<Posn> levels;

    public LinearLayout linearLayout;
    public GameView game_view;

    public SymbolizeDialog dialog;


    // Constructors
    //-------------

    public Request( int type ) {
        this.type = type;

        this.puzzle = null;

        this.request_line = null;
        this.request_point = null;

        this.shift_graphs = null;

        this.graph = null;
        this.levels = null;

        this.linearLayout = null;
        this.game_view = null;

        this.dialog = null;
    }


    // Public Methods
    //---------------

    public boolean Require_render() {
        return Drag_end <= type && type <= Load_puzzle_right;
    }

    public boolean Require_undo() {
        return ( Change_color <= type && type <= Shift ) || type == Drag_start;
    }

    public boolean Is_animation_action() {
        return Rotate_right <= type && type <= Load_puzzle_right && OptionsDataAccess.Show_animations();
    }

    public boolean Is_shadow_action() {
        return Shadow_point <= type && type <= Shadow_line;
    }
}
