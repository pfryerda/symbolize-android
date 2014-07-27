package symbolize.app.Common;


import java.util.ArrayList;
import java.util.LinkedList;
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
    public static final int Draw                 = 7;
    public static final int Erase                = 8;
    public static final int Rotate_right         = 9;
    public static final int Rotate_left          = 10;
    public static final int Flip_horizontally    = 11;
    public static final int Flip_vertically      = 12;
    public static final int Change_color         = 13;
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

    public Options options;
    public GameView game_view;



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

        this.options = null;
        this.game_view = null;
    }


    // Public Methods
    //---------------

    public boolean Require_render() {
        return 1 <= type && type <= 18;
    }

    public boolean Require_undo() {
        return 7 <= type && type <= 14;
    }

    public boolean Is_animation_action() {
        return 9 <= type && type <= 18;
    }

    public boolean Is_shadow_action() {
        return 5 <= type && type <= 6;
    }
}
