package app.symbolize.Game;

import android.util.Log;
import app.symbolize.Animation.GameAnimationHandler;
import app.symbolize.Common.Line;
import app.symbolize.Common.Session;
import app.symbolize.Common.Communication.Request;
import app.symbolize.Common.Communication.Response;
import app.symbolize.Puzzle.Puzzle;

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

    public void Handle_request( final Request request, final Response response ) {
        Session session = Session.Get_instance();

        if( is_valid_request( request, response ) ) {

            if ( request.require_pre_render() ) {
                game_model.Update_view();
            }

            if ( request.Require_undo() ) {
                game_model.Push_state();
            }

            boolean success = true;
            switch ( request.type ) {
                case Request.Log:
                    game_model.LogGraph();
                    break;

                case Request.Fetch_level:
                    if( !request.request_point.Is_dud() ) response.response_int = game_model.Fetch_level_number( request.request_point );
                    success = response.response_int > 0 && !request.request_point.Is_dud();
                    break;

                case Request.Check_correctness:
                    response.response_boolean = true;
                    break;

                case Request.Undo:
                    game_model = game_model.Get_past_state();
                    break;

                case Request.Draw:
                    success = !request.request_line.Is_dud() && game_model.Add_line_via_draw( request.request_line );
                    break;

                case Request.Erase:
                    if( !response.response_line.Is_dud() ) game_model.Remove_line_via_erase( response.response_line );
                    else success = false;
                    break;

                case Request.Drag_start:
                    response.response_line = game_model.Remove_line_via_drag( request.request_point );
                    success = response.response_line != null && !response.response_line.Is_dud();
                    break;

                case Request.Drag_end:
                    if( request.request_line != null ) {
                        request.request_line.Bound();
                        game_model.Add_line_via_drag( request.request_line );
                    }
                    break;

                case Request.Change_color:
                    if( !request.request_point.Is_dud() ) success = game_model.Change_color( request.request_point );
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
                    if( request.Is_special() ) game_model.Edit( request.type );
                    break;
            }

            if( !success && game_model.Get_past_state() != null ) game_model = game_model.Get_past_state();

            if ( request.Require_render() ) {
                if ( request.Is_animation_action() ) {
                    game_model.Update_view( GameAnimationHandler.Handle_request( request ), request.request_bool );
                } else if ( request.type == Request.Shadow_line && request.request_line != null && !request.request_line.Is_dud() ) {
                    game_model.Update_view( request.request_line );
                } else if (request.type == Request.Shadow_point) {
                    game_model.Update_view( request.request_point );
                } else {
                    game_model.Update_view();
                }
            }
        } else {
            GameUIView.Toast_error_request( request.type );
        }
    }


    // Private methods
    //-----------------

    private boolean is_valid_request( Request request, Response response ) {
        Puzzle current_puzzle = Session.Get_instance().Get_current_puzzle();

        switch ( request.type ) {
            case Request.Check_correctness:
                response.response_boolean = game_model.Check_correctness();
                return response.response_boolean;

            case Request.Undo:
                return game_model.Can_undo();

            case Request.Draw:
                return game_model.Get_lines_drawn() < current_puzzle.Get_draw_restriction();

            case Request.Erase:
                Line line = game_model.Get_intersecting_line( request.request_point );
                response.response_line = line;
                if ( line == null ) {
                    request.type = Request.None; // If not actually intersecting a line then suppress the error toast
                    return false;
                }

                return ( game_model.Get_lines_erased() < current_puzzle.Get_erase_restriction() )
                    || ( line.Get_owner() == Line.User_drawn )
                    || ( line.Get_owner() == Line.User_dragged );

            case Request.Drag_end:
                line = request.request_line;
                return ( game_model.Get_lines_dragged() < current_puzzle.Get_drag_restriction() + 1)
                    || ( line.Get_owner() == Line.User_drawn )
                    || ( line.Get_owner() == Line.User_dragged );

            default:
                return !request.Is_invalid_type();
        }
    }
}
