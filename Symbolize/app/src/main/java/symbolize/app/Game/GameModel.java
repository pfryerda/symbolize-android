package symbolize.app.Game;

import android.util.Log;
import java.util.ArrayList;
import java.util.LinkedList;
import symbolize.app.Animation.SymbolizeAnimation;
import symbolize.app.Common.Line;
import symbolize.app.Common.Session;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Communication.Request;
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
    private byte shift_number;
    private GameView game_view;
    private GameModel past_state;



    // Constructors
    //--------------

    public GameModel()  {
        this( new LinkedList<Line>(), new ArrayList<Posn>(), 0, 0, 0, (byte) 0, null, null );
    }

    public GameModel( final LinkedList<Line> graph, final ArrayList<Posn> levels,
                      final int lines_drawn, final int lines_erased, final  int lines_dragged,
                      final byte shift_number, final GameView game_view, final GameModel past_state )
    {
        this.graph = graph;
        this.levels = levels;
        this.lines_drawn = lines_drawn;
        this.lines_erased = lines_erased;
        this.lines_dragged = lines_dragged;
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
        return new GameModel( clonedGraph, levels, lines_drawn, lines_erased, lines_dragged,
                shift_number, game_view, past_state );
    }


    // Getter methods
    //---------------

    public int Get_lines_drawn() {
        if ( Session.DEV_MODE ) {
            return -1;
        } else {
            return lines_drawn;
        }
    }

    public int Get_lines_erased() {
        if ( Session.DEV_MODE ) {
            return -1;
        } else {
            return lines_erased;
        }
    }

    public int Get_lines_dragged() {
        if ( Session.DEV_MODE ) {
            return -1;
        } else {
            return lines_dragged;
        }
    }

    public boolean Can_undo() { return past_state != null; }

    public GameModel Get_past_state() {
        return past_state;
    }

    public ArrayList<Posn> Get_completed_levels() {
        Session session = Session.Get_instance();

        ArrayList<Posn> unlocked_levels = new ArrayList<Posn>();
        for ( int i = 0; i < levels.size(); ++i ) {
            if ( ProgressDataAccess.Is_completed( session.Get_current_world(), i + 1 ) ) {
                unlocked_levels.add( levels.get( i ) );
            }
        }
        return unlocked_levels;
    }

    public ArrayList<Posn> Get_unlocked_levels() {
        Session session = Session.Get_instance();

        ArrayList<Posn> unlocked_levels = new ArrayList<Posn>();
        for ( int i = 0; i < levels.size(); ++i ) {
            if ( UnlocksDataAccess.Is_unlocked( session.Get_current_world(), i + 1 ) ) {
                unlocked_levels.add( levels.get( i ) );
            }
        }
        return unlocked_levels;
    }

    public LinkedList<Line> Get_simplified_graph() {
        return simplify_graph( (LinkedList<Line> ) graph.clone() );
    }


    // Setter methods
    //----------------

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


    // Public methods
    //----------------

    /*
     * Given a point of the screen find out if there is a level on the screen roughly at the same spot
     *
     * @param Posn point: The point you are trying to match to a level
     * @return Integer: The level number of the close level
     */
    public Integer Fetch_level_number( Posn point ) {
        Session session = Session.Get_instance();
        int level_found;

        for ( int i = 0; i < levels.size(); ++i ) {
            if ( levels.get( i ).Approximately_equals( point ) && UnlocksDataAccess.Is_unlocked( session.Get_current_world(), i + 1 ) ) {
                level_found =  i + 1;
                session.Set_pivot( point );
                return level_found;
            }
        }
        return null;
    }

    /*
     * Checks whether the current graph matches the one in the solution
     *
     * @return boolean: true if correct false otherwise
     */
    public boolean Check_correctness() {
        return Session.Get_instance().Get_current_puzzle().Check_correctness( Get_simplified_graph() );
    }

    /*
     * Snaps the line accordingly, adds it to the graph, increase number of draw
     *
     * @param Line line: Raw pre snapped line wanting to add
     */
    public void Add_line_via_draw( Line line ) {
        Session session = Session.Get_instance();

        ArrayList<Posn> completed_levels = Get_completed_levels();
        if ( !session.Is_in_world_view() || completed_levels.size() > 1 ) {
            line.Snap_to_levels( completed_levels );
            graph.addLast( line );
            ++lines_drawn;
        }
    }

    /*
     * Removes a line from the game and updates lines erased accordingly
     *
     * @param Posn point: The point to check is interesting
     */
    public void Remove_line_via_erase( Line line ) {
        switch ( line.Get_owner() ) {
            case Line.App_drawn:
                ++lines_erased;
                break;

            case Line.User_drawn:
                --lines_drawn;
                break;

            case Line.User_dragged:
                --lines_dragged;
                break;
        }
        graph.remove( line );
    }

    /*
     * Simply adds a line to the graph, since the increase drag count was accounted for when the line was removed
     */
    public void Add_line_via_drag( Line line ) {
        graph.addLast( line );
    }

    /*
     * Removes a line from the graph given that the given point intersects one, and increase drag count
     *
     * @param Posn point: The point of interest
     * @return Line: Return the that was removes, if no line intersected return null
     */
    public Line Remove_line_via_drag( Posn point ) {
        Line line = Get_intersecting_line( point );
        if( line != null ) {
            graph.remove( line );
            if ( line.Get_owner() == Line.App_drawn ) {
                ++lines_dragged;
            }
        }
        return line;
    }

    /*
     * Given that a line intersects with the given point, change its color to the next color in the list
     *
     * @param Posn point: The point of interest
     */
    public void Change_color( Posn point ) {
        Line line = Get_intersecting_line( point );
        if ( line != null ) {
            line.Edit( Request.Change_color );
        }
    }

    /*
     * Shift graph from current level's graph to the next one in the list
     */
    public void Shift_graph() {
        ArrayList<LinkedList<Line>> shift_graphs = Session.Get_instance().Get_current_puzzle().Get_boards();
        shift_number = (byte) ( ( shift_number + 1 ) % shift_graphs.size() );
        graph.clear();
        for ( Line line : shift_graphs.get( shift_number ) ) {
            graph.addLast( line.clone() );
        }
        lines_drawn = 0;
        lines_erased = 0;
    }

    /*
     * Generic edit methods that calls edit on all lines and points with the request type
     *
     * @param int request_type: The type of request that will be given to all the edit methods
     */
    public void Edit( int request_type ) {
        for ( Line line : graph ) {
            line.Edit( request_type );
        }

        for ( Posn posn : levels ) {
            posn.Edit( request_type );
        }
    }

    /*
     * Updates the view accordingly to all the parameters
     *
     * @param boolean update_background: Whether of not to re render the background
     * @param SymbolizeAnimation animation: The animation to us before render
     * @param boolean requires_hint_box: Whether to show the hint box after animation
     * @param Line shadow_line: The shadow line to render after rendering - for drawing
     * @param Posn shadow_posn: the shadow point to render after rebdering - for erasing
     */
    public void Update_view( final boolean update_background ) {
        GameUIView.Update_ui( Can_undo() );
        if ( Session.DEV_MODE ) {
            game_view.Render( Get_simplified_graph(), levels, update_background );
        } else {
            game_view.Render( graph, levels, update_background );
        }
    }

    public void Update_view( final SymbolizeAnimation animation, final boolean requires_hint_box ) {
        GameUIView.Update_ui( Can_undo() );
        if ( Session.DEV_MODE ) {
            game_view.Render( Get_simplified_graph(), levels, animation, requires_hint_box );
        } else {
            game_view.Render( graph, levels, animation, requires_hint_box );
        }
    }

    public void Update_view( final Line shadow_line ) {
        GameUIView.Update_ui( Can_undo() );
        if ( Session.DEV_MODE ) {
            game_view.Render( Get_simplified_graph(), levels, shadow_line );
        } else {
            game_view.Render( graph, levels, shadow_line );
        }
    }

    public void Update_view( final Posn shadow_posn ) {
        GameUIView.Update_ui( Can_undo() );
        if ( Session.DEV_MODE ) {
            game_view.Render( Get_simplified_graph(), levels, shadow_posn );
        } else {
            game_view.Render( graph, levels, shadow_posn );
        }
    }

    /*
     * Reset the game view, use this on game start to enforce dimensions and such
     */
    public void Refresh_view_object() {
        this.game_view = new GameView();
    }

    /*
     * Push a backup of the current game model into past state for undo
     */
    public void Push_state() {
        past_state = clone();
    }

    public Line Get_intersecting_line( Posn point ) {
        for ( Line line : graph ) {
            if ( line.Intersects( point ) ) {
                return line;
            }
        }
        return null;
    }


    // Private methods
    //-----------------

    /*
     * given a graph will find two lines that intersect with similar
     * slopes and merges them, then recurses
     *
     * @param LinkedList<Line> graph: The graph wanting to simplify
     * @return LinkedList<Line>: Returns the simplified graph
     */
    private LinkedList<Line> simplify_graph( LinkedList<Line> cloned_graph ) {
        if ( cloned_graph.size() != 0 ) {
            for ( Line line_1 : cloned_graph ) {
                for ( Line line_2 : cloned_graph ) {
                    if ( line_1.Mergeable( line_2 ) ) {
                        // Remove old lines
                        cloned_graph.remove( line_1 );
                        cloned_graph.remove( line_2 );

                        // Find new line and add it
                        LinkedList<Line> guesses = new LinkedList<Line>();
                        guesses.add( new Line( line_1.Get_p1(), line_2.Get_p1() ) );
                        guesses.add( new Line( line_1.Get_p1(), line_2.Get_p2() ) );
                        guesses.add( new Line( line_1.Get_p2(), line_2.Get_p1() ) );
                        guesses.add( new Line( line_1.Get_p2(), line_2.Get_p2() ) );

                        Line merged_line = guesses.get( 0 );

                        for ( Line guess : guesses ) {
                            if ( merged_line.Distance_squared() < guess.Distance_squared() ) {
                                merged_line = guess;
                            }
                        }

                        cloned_graph.add( merged_line );

                        return simplify_graph( cloned_graph );
                    }
                }
            }
        }
       return cloned_graph;
    }


    // Developer method
    //-----------------

    /*
     * Log the current graph into the console log for building levels
     */
    public void LogGraph() {
        String graph_string = "Xml for current graph";
        graph_string += "\n<graph>";
        for ( Line line : Get_simplified_graph() ) {
            graph_string += "\n" + line.Print_line();
        }
        graph_string += "\n</graph>";
        Log.i("Graph Log:", graph_string);
    }
}