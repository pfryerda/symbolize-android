package symbolize.app.Common;


import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Enum.Action;
import symbolize.app.Game.GameView;
import symbolize.app.R;

public class Request {
    // Fields
    //--------

    public Action action;
    public LinkedList<Line> graph;
    public ArrayList<Posn> levels;
    public GameView game_view;
    public ArrayList<LinkedList<Line>> board;
    public Line line;
    public boolean requires_animation;


    // Constructors
    //-------------

    public Request( Action action ) {
        this.action = action;
        this.requires_animation = false;
    }

    public Request( Action action, Line line ) {
        this.action = action;
        this.line = line;
        this.requires_animation = false;
    }

    public Request( Action action, ArrayList<LinkedList<Line>> board ) {
        this.action = action;
        this.board = board;
        this.requires_animation = false;
    }

    public Request( Action action, LinkedList<Line> graph, ArrayList<Posn> levels ) {
        this.action = action;
        this.graph = graph;
        this.levels = levels;
        this.requires_animation = false;
    }
}
