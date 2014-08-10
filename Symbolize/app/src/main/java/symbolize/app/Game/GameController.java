package symbolize.app.Game;


import android.widget.LinearLayout;

import java.util.ArrayList;

import symbolize.app.Common.Line;
import symbolize.app.Common.Player;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.R;

public class GameController {
    // Static fields
    //--------------
    private static GameController instance = new GameController();


    // Fields
    //--------

    private GameModel game_model;


    // Constructor/Get_Instance
    //--------------------------

    public static GameController Get_instance() {
        return instance;
    }

    private GameController() {
        game_model = new GameModel();
    }


    // Main method
    //-------------

    public boolean Handle_request( final Request request ) {
        if( request.Require_render() ) {
            game_model.Update_view( request );
        }

        if ( request.Require_undo() ) {
            game_model.Push_state();
        }

        switch ( request.type ) {
            case Request.Log:
                game_model.LogGraph();
                break;

            case Request.Check_correctness:
                return request.puzzle.Check_correctness( game_model.Get_graph() );

            case Request.Undo:
                if ( game_model.getPastState() == null ) {
                    GameView.Render_toast( R.string.nothing_to_undo );
                } else {
                    game_model = game_model.getPastState();
                }
                break;

            case Request.Draw:
                if ( game_model.Get_lines_drawn() < request.puzzle.Get_draw_restriction() ) {
                    game_model.Add_line_via_draw( request.request_line );
                } else {
                    GameView.Render_toast( R.string.out_of_lines );
                }
                break;

            case Request.Erase:
                for ( Line line : game_model.Get_graph() ) {
                    if ( line.Intersects( request.request_point ) ) {
                        if ( ( game_model.Get_lines_erased() < request.puzzle.Get_erase_restriction() )
                                || ( line.Get_owner() == Line.User ) )
                        {
                            game_model.Remove_line_via_erase( line );
                        } else {
                            GameView.Render_toast( R.string.out_of_erase );
                        }
                        break;
                    }
                }
                break;

            case Request.Drag_start:
                game_model.Remove_line_via_drag( request.request_line );
                break;

            case Request.Drag_end:
                if ( game_model.Get_lines_dragged() < request.puzzle.Get_drag_restriction() ) {
                    game_model.Add_line_via_drag( request.request_line );
                } else {
                    GameView.Render_toast( R.string.out_of_drag );
                    return Handle_request( new Request( Request.Undo ) );
                }
                break;

            case Request.Change_color:
                for ( Line line : game_model.Get_graph() ) {
                    if ( line.Intersects( request.request_point ) ) {
                        line.Edit( request.type );
                        break;
                    }
                }
                break;

            case Request.Shift:
                game_model.Shift_graph(request.shift_graphs);
                break;

            case Request.Rotate_left:
            case Request.Rotate_right:
            case Request.Flip_horizontally:
            case Request.Flip_vertically:
                for ( Line line : game_model.Get_graph() ) {
                    line.Edit( request.type );
                }

                for ( Posn posn : game_model.Get_levels() ) {
                    posn.Edit( request.type );
                }
                break;

            case Request.Load_level_via_world:
            case Request.Load_world_via_level:
            case Request.Load_puzzle_left:
            case Request.Load_puzzle_right:
            case Request.Reset:
                game_model.Set_puzzle(request.puzzle);
                break;

            case Request.Shadow_line:
                request.request_line.Snap_to_levels(game_model.Get_unlocked_levels());
                break;

            case Request.Shadow_point:
                break;

            default:
                return false;
        }

        if ( request.Require_render() ) {
            game_model.Update_view( request );
        }
        return true;
    }


    // Public methods
    //----------------

    public Line Get_line_of_interest( Posn point ) {
        for ( Line line : game_model.Get_graph() ) {
            if ( line.Intersects( point ) ) {
                return line;
            }
        }
        return null;
    }

    public int Get_tapped_level( Posn point ) {
        Player player = Player.Get_instance();
        ArrayList<Posn> levels = game_model.Get_levels();
        int level_found = 0;

        for ( int i = 0; i < levels.size(); ++i ) {
            if ( levels.get( i ).Approximately_equals( point ) && UnlocksDataAccess.Is_unlocked( player.Get_current_world(), i + 1 ) ) {
                level_found =  i + 1;
                player.Set_pivot( point );
            }
        }

        return level_found;
    }
}
