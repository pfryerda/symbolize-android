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
import symbolize.app.Common.Options;
import symbolize.app.Common.Player;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;
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
                      final Bitmap foreground_bitmap, final Bitmap background_bitmap,
                      final Options options )
    {
        this.graph = new LinkedList<Line>();
        this.levels = new ArrayList<Posn>();
        this.lines_drawn = 0;
        this.lines_erased = 0;
        this.lines_dragged = 0;
        this.shift_number = 0;
        this.player = player;
        this.game_view = new GameView( context, foreground, background,
                                       foreground_bitmap, background_bitmap, options );
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

    public void Handle_request( final Request request ) {
        if ( request.action != Action.Drag_end ) {
            push_state();
        }

        switch ( request.action ) {
            case Draw:
                graph.addLast( request.action_line );
                ++lines_drawn;
                break;

            case Erase:
                graph.remove( request.action_line );
                if ( request.action_line.Get_owner() == Owner.App ) {
                    ++lines_erased;
                } else {
                    --lines_drawn;
                }
                break;

            case Drag_start:
                graph.remove( request.action_line );
                ++lines_dragged;
                break;

            case Drag_end:
                graph.add( request.action_line );
                break;

            case Change_color:
                request.action_line.Edit( request.action );
                break;

            case Shift:
                shift_number = ( shift_number + 1 ) % request.shift_graphs.size();
                graph.clear();
                for ( Line line : request.shift_graphs.get( shift_number ) ) {
                    graph.addLast( line.clone() );
                }
                lines_drawn = 0;
                lines_erased = 0;
                break;

            case Rotate_left:
            case Rotate_right:
            case Flip_horizontally:
            case Flip_vertically:
                for ( Line line : graph ) {
                    line.Edit( request.action );
                }

                for ( Posn posn : levels ) {
                    posn.Edit( request.action );
                }
                break;

            case Load_level:
            case Load_world_via_level:
            case Load_world_left:
            case Load_world_right:
            case Reset:
                handle_set_puzzle( request.puzzle );
                break;

            default:
                break;
        }

        if ( request.action != Action.Drag_start ) {
            request.graph = graph;
            request.levels = Get_unlocked_levels();
            game_view.Render( request );
        }
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
            } else {
                unlocked_levels.add( null );
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


    // Private methods
    //-----------------

    private void handle_set_puzzle( final Puzzle puzzle ) {
        graph.clear();
        levels.clear();
        for ( Line line : puzzle.Get_board() ) {
            graph.addLast( line.clone() );
        }
        for ( Posn point : puzzle.Get_levels() ) {
            levels.add( point.clone() );
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