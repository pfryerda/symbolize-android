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

    public static final int Log                  = -1;
    public static final int Check_correctness    = 0;
    public static final int Drag_start           = 1;
    public static final int Undo                 = 2;
    public static final int Drag_end             = 3;
    public static final int None                 = 4;
    public static final int Reset                = 5;
    public static final int Background_change    = 6;
    public static final int Shadow_point         = 7;
    public static final int Shadow_line          = 8;
    public static final int Change_color         = 9;
    public static final int Draw                 = 10;
    public static final int Erase                = 11;
    public static final int Rotate_right         = 12;
    public static final int Rotate_left          = 13;
    public static final int Flip_horizontally    = 14;
    public static final int Flip_vertically      = 15;
    public static final int Shift                = 16;
    public static final int Load_level_via_world = 17;
    public static final int Load_world_via_level = 18;
    public static final int Load_puzzle_left     = 19;
    public static final int Load_puzzle_right    = 20;


    // Fields
    //--------

    public int type;

    public Puzzle puzzle;

    public Line request_line;
    public Posn request_point;

    public ArrayList<LinkedList<Line>> shift_graphs;

    public LinkedList<Line> graph;
    public ArrayList<Posn> levels;

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

        this.dialog = null;
    }


    // Public Methods
    //---------------

    public boolean Require_render() {
        return Undo <= type && type <= Load_puzzle_right;
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
