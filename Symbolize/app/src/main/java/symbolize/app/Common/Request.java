package symbolize.app.Common;


import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Enum.Action;
import symbolize.app.Game.GameView;
import symbolize.app.Puzzle.Puzzle;
import symbolize.app.R;

public class Request {
    // Fields
    //--------

    public Action action;

    public Puzzle puzzle;

    public Line action_line;
    public Posn action_point;

    public ArrayList<LinkedList<Line>> shift_graphs;

    public LinkedList<Line> graph;
    public ArrayList<Posn> levels;

    public GameView game_view;



    // Constructors
    //-------------

    public Request( Action action ) {
        this.action = action;

        this.puzzle = null;

        this.action_line = null;
        this.action_point = null;

        this.shift_graphs = null;

        this.graph = null;
        this.levels = null;

        this.game_view = null;
    }


    // Method
    //-------

    public boolean Is_animation_action() {
        return action != Action.Draw       && action != Action.Erase &&
               action != Action.Drag_start && action != Action.Drag_end &&
               action != Action.Shadow_line && action != Action.Shadow_Point &&
               action != Action.Reset && action != Action.None && action != Action.Change_color;
    }

    public boolean Is_shadow_action() {
        return action == Action.Shadow_line || action == Action.Shadow_Point;
    }
}
