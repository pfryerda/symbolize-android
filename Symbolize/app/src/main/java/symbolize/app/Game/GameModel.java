package symbolize.app.Game;

import android.util.Log;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import symbolize.app.Common.Line;
import symbolize.app.Common.Player;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.DataAccess.ProgressDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.Puzzle.Puzzle;

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
    private GameModel past_state;


    // Constructors
    //--------------

    public GameModel()
    {
        this.graph = new LinkedList<Line>();
        this.levels = new ArrayList<Posn>();
        this.lines_drawn = 0;
        this.lines_erased = 0;
        this.lines_dragged = 0;
        this.shift_number = 0;
        this.past_state = null;
    }

    public GameModel( final LinkedList<Line> graph, final ArrayList<Posn> levels,
                      final int lines_drawn, final int lines_erased, final  int lines_dragged,
                      final int shift_number, final GameModel past_state )
    {
        this.graph = graph;
        this.levels = levels;
        this.lines_drawn = lines_drawn;
        this.lines_erased = lines_erased;
        this.lines_dragged = lines_dragged;
        this.shift_number = shift_number;
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
                shift_number, past_state );
    }


    // Public methods
    //----------------


    public void Add_line_via_draw( Line line ) {
        Player player = Player.Get_instance();

        if ( OptionsDataAccess.Is_snap_drawing() && !player.Is_in_world_view() ) {
            line.Snap();
        }

        ArrayList<Posn> completed_levels = Get_completed_levels();
        if ( !player.Is_in_world_view() || completed_levels.size() > 1 ) {
            line.Snap_to_levels( completed_levels );
            graph.addLast( line );
            ++lines_drawn;
        }
    }

    public void Remove_line_via_erase( Line line ) {
        graph.remove( line );
        if ( line.Get_owner() == Line.App ) {
            ++lines_erased;
        } else {
            --lines_drawn;
        }
    }

    public void Add_line_via_drag( Line line ) {
        graph.addLast( line );
    }

    public void Remove_line_via_drag( Line line ) {
        graph.remove( line );
        ++lines_dragged;
    }

    public void Shift_graph( ArrayList<LinkedList<Line>> shift_graphs ) {
        shift_number = ( shift_number + 1 ) % shift_graphs.size();
        graph.clear();
        for ( Line line : shift_graphs.get( shift_number ) ) {
            graph.addLast( line.clone() );
        }
        lines_drawn = 0;
        lines_erased = 0;
    }

    public void Update_view( Request request ) {
        request.graph = graph;
        request.levels = levels;
        GameView.Get_instance().Handle_render_request( request );
    }

    // Getter methods
    //---------------

    public LinkedList<Line> Get_graph() {
        return graph;
    }

    public ArrayList<Posn> Get_levels() { return levels; }

    public int Get_lines_drawn() {
        if ( Player.DEV_MODE ) {
            return -1;
        } else {
            return lines_drawn;
        }
    }

    public ArrayList<Posn> Get_completed_levels() {
        Player player = Player.Get_instance();

        ArrayList<Posn> unlocked_levels = new ArrayList<Posn>();
        for ( int i = 0; i < levels.size(); ++i ) {
            if ( ProgressDataAccess.Is_completed( player.Get_current_world(), i + 1 ) ) {
                unlocked_levels.add( levels.get( i ) );
            }
        }
        return unlocked_levels;
    }

    public ArrayList<Posn> Get_unlocked_levels() {
        Player player = Player.Get_instance();

        ArrayList<Posn> unlocked_levels = new ArrayList<Posn>();
        for ( int i = 0; i < levels.size(); ++i ) {
            if ( UnlocksDataAccess.Is_unlocked( player.Get_current_world(), i + 1 ) ) {
                unlocked_levels.add( levels.get( i ) );
            }
        }
        return unlocked_levels;
    }

    public int Get_lines_erased() {
        if ( Player.DEV_MODE ) {
            return -1;
        } else {
            return lines_erased;
        }
    }

    public int Get_lines_dragged() {
        if ( Player.DEV_MODE ) {
            return -1;
        } else {
            return lines_dragged;
        }
    }

    public GameModel getPastState() {
        return past_state;
    }

    public void Set_puzzle( final Puzzle puzzle ) {
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

    public void Push_state() {
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