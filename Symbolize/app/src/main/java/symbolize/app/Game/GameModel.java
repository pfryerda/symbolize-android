package symbolize.app.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Enum.Action;
import symbolize.app.Common.Line;
import symbolize.app.Common.Enum.Owner;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Puzzle;

/*
 * The main game method contains information about whats on the board, what mode you are in,
 * how lines have you drawn/erased and a tracker of your past state for undo.
 */
public class GameModel {
    // Fields
    //--------

    private LinkedList<Line> graph;
    private ArrayList<Posn> levels;
    private int lines_drawn, lines_erased;
    private int shift_number;
    private GameView game_view;
    private GameModel past_state;


    // Constructors
    //--------------

    public GameModel( Context context, LinearLayout foreground, LinearLayout background, Bitmap foreground_bitmap, Bitmap background_bitmap ) {
        graph = new LinkedList<Line>();
        levels = new ArrayList<Posn>();
        lines_drawn = 0;
        lines_erased = 0;
        shift_number = 0;
        game_view = new GameView( context, foreground, background, foreground_bitmap, background_bitmap );
        past_state = null;
    }

    public GameModel( LinkedList<Line> graph, ArrayList<Posn> levels, int lines_drawn, int lines_erased, int shift_number, GameView game_view, GameModel past_state ) {
        this.graph = graph;
        this.levels = levels;
        this.lines_drawn = lines_drawn;
        this.lines_erased = lines_erased;
        this.shift_number = shift_number;
        this.game_view = game_view;
        this.past_state = past_state;
    }


    // Copy Constructor
    //-----------------

    public GameModel clone() {
        LinkedList<Line> clonedGraph = new LinkedList<Line>();
        for ( Line line : graph ) {
            clonedGraph.addLast( line.clone() );
        }
        return new GameModel( clonedGraph, levels, lines_drawn, lines_erased, shift_number, game_view, past_state );
    }


    // Public methods
    //------------------

    public void setPuzzle( Puzzle puzzle ) {
        graph.clear();
        levels.clear();
        for ( Line line : puzzle.Get_board() ) {
            graph.addLast( line.clone() );
        }
        for ( Posn point : puzzle.Get_levels() ) {
            levels.add(point.clone());
        }
        lines_drawn = 0;
        lines_erased = 0;
        past_state = null;

        game_view.Render_foreground( graph, levels );
    }

    public void Add_shadow( Line line ) {
        game_view.Render_shadow( line, graph, levels );
    }

    public void Add_shadow( Posn posn ) {
        game_view.Render_shadow( posn, graph, levels );
    }

    public void Remove_shadows() {
        game_view.Render_foreground( graph, levels );
    }


    // Geter methods
    //---------------

    public LinkedList<Line> Get_graph() {
        return graph;
    }

    public ArrayList<Posn> Get_levels() { return levels; }

    public int getLinesDrawn() {
        if ( GameActivity.DEVMODE ) {
            return -1;
        } else {
            return lines_drawn;
        }
    }

    public int getLinesErased() {
        if ( GameActivity.DEVMODE ) {
            return -1;
        } else {
            return lines_erased;
        }
    }

    public GameModel getPastState() {
        return past_state;
    }


    // Action methods
    //----------------

    public void action_basic( Action action, Line line ) {
        push_state();
        switch ( action ) {
            case Draw:
                graph.addLast(line);
                ++lines_drawn;
                break;

            case Erase:
                graph.remove(line);
                if (line.Get_owner() == Owner.App) {
                    ++lines_erased;
                } else {
                    --lines_drawn;
                }
                break;

            case Change_color:
                line.Edit( action );
                break;
        }
        game_view.Render_foreground(graph, levels);
    }

    public void action_motion( Action action ) {
        push_state();

        for ( Line line : graph ) {
            line.Edit( action );
        }

        for ( Posn posn : levels ) {
            posn.Edit( action );
        }

        game_view.Render_motion( action, graph, levels );
    }

    public void action_sensor( Action action, ArrayList<LinkedList<Line>> board ) {
        push_state();
        switch ( action ) {
            case Shift:
                shift_number = ( shift_number + 1 ) % board.size();
                graph.clear();
                for ( Line line : board.get( shift_number ) ) {
                    graph.addLast( line.clone() );
                }
                lines_drawn = 0;
                lines_erased = 0;
                break;
        }
        game_view.Render_motion( action, graph, levels );
    }


    // Private method
    //----------------

    private void push_state() {
        past_state = clone();
    }


    // Developer method
    //-----------------

    public void LogGraph() {
        String graph_string = "Xml for current graph";
        graph_string += "\n<graph>";
        for ( Line line : graph ) {
            graph_string += "\n" + line.Print_line();
        }
        graph_string += "\n</graph>";
        Log.i("Graph Log:", graph_string);
    }
}