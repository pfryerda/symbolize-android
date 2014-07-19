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
import symbolize.app.Common.Player;
import symbolize.app.Common.Posn;
import symbolize.app.Puzzle.Level;
import symbolize.app.Puzzle.Puzzle;
import symbolize.app.Puzzle.World;

/*
 * The main game method contains information about whats on the board, what mode you are in,
 * how lines have you drawn/erased and a tracker of your past state for undo.
 */
public class GameModel {
    // Fields
    //--------

    private LinkedList<Line> graph;
    private ArrayList<Posn> levels;
    private int lines_drawn, lines_erased, lines_dragged;
    private int shift_number;
    private final Player player;
    private final GameView game_view;
    private GameModel past_state;


    // Constructors
    //--------------

    public GameModel( final Player player, final Context context,
                      final LinearLayout foreground, final LinearLayout background,
                      final Bitmap foreground_bitmap, final Bitmap background_bitmap )
    {
        this.graph = new LinkedList<Line>();
        this.levels = new ArrayList<Posn>();
        this.lines_drawn = 0;
        this.lines_erased = 0;
        this.lines_dragged = 0;
        this.shift_number = 0;
        this.player = player;
        this.game_view = new GameView( context, foreground, background, foreground_bitmap, background_bitmap );
        this.past_state = null;
    }

    public GameModel( final LinkedList<Line> graph, final ArrayList<Posn> levels,
                      final int lines_drawn, final int lines_erased, final  int lines_dragged,
                      final int shift_number, final Player player, final GameView game_view,
                      final GameModel past_state )
    {
        this.graph = graph;
        this.levels = levels;
        this.lines_drawn = lines_drawn;
        this.lines_erased = lines_erased;
        this.lines_dragged = lines_dragged;
        this.shift_number = shift_number;
        this.player = player;
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
        return new GameModel( clonedGraph, levels, lines_drawn, lines_erased, lines_dragged,
                shift_number, player, game_view, past_state );
    }


    // Public methods
    //------------------

    public void Reset( final  Puzzle puzzle ) {
        set_puzzle( puzzle );
        game_view.Render_foreground( graph, Get_unlocked_levels() );
    }

    public void Set_world( final World world, Action action ) {
        set_puzzle( world );
        game_view.Render_motion( action, graph, Get_unlocked_levels() );
    }

    public void Set_level( final Level level, Posn pivot ) {
        set_puzzle( level );
        game_view.Set_zoom_animations_pivot( pivot );
        game_view.Render_motion( Action.Load_level, graph, Get_unlocked_levels() );
    }

    public void Add_shadow( final Line line ) {
        game_view.Render_shadow( line, graph, Get_unlocked_levels() );
    }

    public void Add_shadow( final Posn posn ) {
        game_view.Render_shadow( posn, graph, Get_unlocked_levels() );
    }

    public void Remove_shadows() {
        game_view.Render_foreground( graph, Get_unlocked_levels() );
    }


    // Getter methods
    //---------------

    public LinkedList<Line> Get_graph() {
        return graph;
    }

    public ArrayList<Posn> Get_levels() { return levels; }

    public int Get_lines_drawn() {
        if ( Player.DEVMODE ) {
            return -1;
        } else {
            return lines_drawn;
        }
    }

    public ArrayList<Posn> Get_unlocked_levels() {
        ArrayList<Posn> unlocked_levels = new ArrayList<Posn>();
        for ( int i = 0; i < levels.size(); ++i ) {
            if ( player.Is_unlocked( player.Get_current_world(), i + 1 ) ) {
                unlocked_levels.add( levels.get( i ) );
            }
        }
        return unlocked_levels;
    }

    public int Get_lines_erased() {
        if ( Player.DEVMODE ) {
            return -1;
        } else {
            return lines_erased;
        }
    }

    public int Get_lines_dragged() {
        if ( Player.DEVMODE ) {
            return -1;
        } else {
            return lines_dragged;
        }
    }

    public GameModel getPastState() {
        return past_state;
    }


    // Action methods
    //----------------

    public void action_basic( final Action action, final Line line ) {
        if ( action != Action.Drag_end ) {
            push_state();
        }
        switch ( action ) {
            case Draw:
                graph.addLast( line );
                ++lines_drawn;
                break;

            case Erase:
                graph.remove( line );
                if ( line.Get_owner() == Owner.App ) {
                    ++lines_erased;
                } else {
                    --lines_drawn;
                }
                break;

            case Drag_start:
                graph.remove( line );
                ++lines_dragged;
                break;

            case Drag_end:
                graph.add( line );
                break;

            case Change_color:
                line.Edit( action );
                break;
        }
        if ( action != Action.Drag_start ) {
            game_view.Render_foreground( graph, Get_unlocked_levels() );
        }
    }

    public void action_motion( final Action action ) {
        push_state();

        for ( Line line : graph ) {
            line.Edit( action );
        }

        for ( Posn posn : levels ) {
            posn.Edit( action );
        }

        game_view.Render_motion( action, graph, levels );
    }

    public void action_sensor( final Action action, final ArrayList<LinkedList<Line>> board ) {
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


    // Private methods
    //-----------------

    private void set_puzzle( final Puzzle puzzle ) {
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
    }

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