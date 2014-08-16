package symbolize.app.Game;

import symbolize.app.Animation.GameAnimationHandler;
import symbolize.app.Common.Session;
import symbolize.app.Common.Communication.Request;
import symbolize.app.Common.Communication.Response;
import symbolize.app.Puzzle.Puzzle;
import symbolize.app.R;

/*
 * A singleton class, the main game controllers updates the game based off the requests sent from the game page
 */
public class GameController {
    // Fields
    //--------

    private GameModel game_model;


    // Singleton setup
    //-----------------

    private static GameController instance = new GameController();

    public static GameController Get_instance() {
        return instance;
    }


    // Constructor
    //-------------

    private GameController() {
        game_model = new GameModel();
    }


    // Main method
    //-------------

    public boolean Handle_request( final Request request, final Response response ) {
        Session session = Session.Get_instance();
        Puzzle current_puzzle = session.Get_current_puzzle();

        if( request.Is_animation_action() ) {
            game_model.Update_view( false );
        }

        if ( request.Require_undo() ) {
            game_model.Push_state();
        }

        switch ( request.type ) {
            case Request.Log:
                game_model.LogGraph();
                break;

            case Request.Fetch_level:
                response.response_int = game_model.Fetch_level_number( request.request_point );
                break;

            case Request.Check_correctness:
                return game_model.Check_correctness();

            case Request.Undo:
                if ( game_model.getPastState() == null ) {
                    GameView.Render_toast( R.string.nothing_to_undo );
                } else {
                    game_model = game_model.getPastState();
                }
                break;

            case Request.Draw:
                if ( game_model.Get_lines_drawn() < session.Get_current_puzzle().Get_draw_restriction() ) {
                    game_model.Add_line_via_draw( request.request_line );
                } else {
                    GameView.Render_toast( R.string.out_of_lines );
                }
                break;

            case Request.Erase:
                if( !game_model.Remove_line_via_erase( request.request_point ) ) {
                    GameView.Render_toast( R.string.out_of_erase );
                }
                break;

            case Request.Drag_start:
                response.response_line = game_model.Remove_line_via_drag( request.request_point );
                break;

            case Request.Drag_end:
                if ( game_model.Get_lines_dragged() < current_puzzle.Get_drag_restriction() ) {
                    game_model.Add_line_via_drag( request.request_line );
                } else {
                    GameView.Render_toast( R.string.out_of_drag );
                    return Handle_request( new Request( Request.Undo ), new Response() );
                }
                break;

            case Request.Change_color:
                game_model.Change_color( request.request_point );
                break;

            case Request.Shift:
                game_model.Shift_graph();
                break;

            case Request.Rotate_left:
            case Request.Rotate_right:
            case Request.Flip_horizontally:
            case Request.Flip_vertically:
                game_model.Edit( request.type );
                break;

            case Request.Load_level_via_world:
            case Request.Load_world_via_level:
            case Request.Load_puzzle_left:
            case Request.Load_puzzle_right:
            case Request.Reset:
                game_model.Set_puzzle( session.Get_current_puzzle() );
                break;

            case Request.Shadow_line:
                request.request_line.Snap_to_levels( game_model.Get_unlocked_levels() );
                break;

            case Request.Load_puzzle_start:
                game_model.Set_puzzle( session.Get_current_puzzle() );
                game_model.Refresh_view_object();
                break;

            default:
                break;
        }

        if ( request.Require_render() ) {
            if ( request.Is_animation_action() ) {
                game_model.Update_view( GameAnimationHandler.Handle_request( request ),
                                        ( request.type == Request.Load_level_via_world ) );
            } else if ( request.type == Request.Shadow_line ) {
                game_model.Update_view( request.request_line );
            } else if ( request.type == Request.Shadow_point ) {
                game_model.Update_view( request.request_point );
            } else {
                game_model.Update_view( request.type == Request.Background_change );
            }
        }

        if( request.Is_invalid_type() ) {
            return false;
        }
        return true;
    }
}
