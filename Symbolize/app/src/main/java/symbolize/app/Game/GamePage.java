package symbolize.app.Game;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.google.android.gms.ads.*;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import symbolize.app.Common.Line;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.Common.Player;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;
import symbolize.app.Common.Page;
import symbolize.app.DataAccess.ProgressDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.Dialog.ConfirmDialog;
import symbolize.app.Dialog.HintDialog;
import symbolize.app.Dialog.InfoDialog;
import symbolize.app.Dialog.OptionsDialog;
import symbolize.app.Puzzle.Level;
import symbolize.app.Puzzle.Puzzle;
import symbolize.app.Puzzle.PuzzleDB;
import symbolize.app.Home.HomePage;
import symbolize.app.Puzzle.World;
import symbolize.app.R;


public class GamePage extends Page {
    // Static block
    //--------------

    static {
        UnlocksDataAccess.Unlock( 1 );
    }


    // Fields
    //---------

    private final String LUKE = "Awesome";
    private Puzzle current_puzzle;
    private SensorManager sensor_manager;
    private GameShakeDetector shake_detector;



    // Main method
    //--------------

    /*
     * Main method called once app is ready
     *
     * @parama Bundle savedInstanceState: A mapping from String values to various Parcelable types
     */
    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        // Basic setup
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game );

        // Variable setup
        sensor_manager = ( SensorManager ) getSystemService( SENSOR_SERVICE );
        shake_detector =  new GameShakeDetector();

        // Ad setup
        AdView adView = ( AdView ) this.findViewById( R.id.game_adspace );
        AdRequest ad_request = new AdRequest.Builder()
                .addTestDevice( AdRequest.DEVICE_ID_EMULATOR )
                .addTestDevice( Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID ) ) // TOD: Manually put in our device ids for security
                .build();
        adView.setAdListener( new AdListener() {} );
        adView.loadAd( ad_request );

        // Set up the view
        GameView.Set_up_view();

        // Load las world used or '1' is none was last used
        current_puzzle = PuzzleDB.Fetch_world( Player.Get_instance().Get_current_world() );

        Request request = new Request( Request.Reset );
        request.puzzle = current_puzzle;
        GameController.Get_instance().Handle_request( request );

        Set_up_listeners();
    }


    // Button methods
    // ---------------

    public void On_left_button_clicked( final View view ) {
        Player player = Player.Get_instance();

        player.Decrease_world();
        load_world( PuzzleDB.Fetch_world( player.Get_current_world() ), Request.Load_puzzle_left );
    }

    public void On_back_button_clicked( final View view ) {
        Player player = Player.Get_instance();

        if ( player.Is_in_world_view() ) {
            startActivity( new Intent( getApplicationContext(), HomePage.class ) );
        } else {
            load_world( PuzzleDB.Fetch_world( player.Get_current_world() ), Request.Load_world_via_level );
        }
    }

    public void On_right_button_clicked( final View view ) {
        Player player = Player.Get_instance();

        player.Increase_world();
        load_world( PuzzleDB.Fetch_world( player.Get_current_world() ), Request.Load_puzzle_right );
    }

    public void On_settings_button_clicked( final View view ) {
        OptionsDialog options_dialog = new OptionsDialog();
        options_dialog.Show();
    }

    public void On_reset_button_clicked( final View view ) {
        Request request = new Request( Request.Reset );
        request.puzzle = current_puzzle;
        GameController.Get_instance().Handle_request( request );
    }

    public void On_check_button_clicked( final View view ) {
        final Player player = Player.Get_instance();
        final GameController controller = GameController.Get_instance();

        if ( Player.DEV_MODE ) {
            Request request = new Request( Request.Log );

            controller.Handle_request( request );
        } else {
            Request request = new Request( Request.Check_correctness );
            request.puzzle = current_puzzle;

            ConfirmDialog confirmDialog = new ConfirmDialog();
            if ( controller.Handle_request(request) ) {
                ProgressDataAccess.Complete( player.Get_current_world(), player.Get_current_level() );

                if ( player.Is_in_world_view() && player.Get_current_world() <= PuzzleDB.NUMBEROFWORLDS ) {
                    for( int unlock : current_puzzle.Get_unlocks() ) {
                        UnlocksDataAccess.Unlock( unlock );
                    }

                    confirmDialog.Set_attrs( getString( R.string.puzzle_complete_dialog_title ), getString( R.string.world_complete_dialog_msg ) );
                    confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                        @Override
                        public void OnDialogSuccess() {
                            player.Increase_world();
                            load_world( PuzzleDB.Fetch_world( player.Get_current_world() ), Request.Load_puzzle_right );
                        }

                        @Override
                        public void OnDialogFail() {}
                    } );
                    confirmDialog.Show();
                } else if( player.Is_in_world_view() && player.Get_current_world() > PuzzleDB.NUMBEROFWORLDS ) {
                    InfoDialog info_dialog = new InfoDialog();
                    info_dialog.Set_attrs( getString( R.string.puzzle_complete_dialog_title ),
                                          getString( R.string.game_complete_dialog_msg ) );
                    info_dialog.Show();
                } else if ( !player.Is_in_world_view() ) {
                    for( int unlock : current_puzzle.Get_unlocks() ) {
                        UnlocksDataAccess.Unlock( player.Get_current_world(), unlock );
                    }

                    confirmDialog.Set_attrs( getString( R.string.puzzle_complete_dialog_title ), getString( R.string.level_complete_dialog_msg ) );
                    confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                        @Override
                        public void OnDialogSuccess() {
                            load_world( PuzzleDB.Fetch_world( player.Get_current_world() ),
                                    Request.Load_world_via_level );
                        }

                        @Override
                        public void OnDialogFail() {}
                    } );
                    confirmDialog.Show();
                }

            } else {
                GameView.Render_toast( R.string.incorrect );
            }
        }
    }

    public void On_hint_button_clicked( final View view ) {
        HintDialog hint_dialog = new HintDialog();
        hint_dialog.Set_attrs( current_puzzle );
        hint_dialog.Show();
    }

    public void On_undo_button_clicked( final View view ) {
        Request request = new Request( Request.Undo );
        GameController.Get_instance().Handle_request(request);
    }

    public void On_draw_button_clicked( final View view ) {
        Player.Get_instance().Set_draw_mode();
    }

    public void On_erase_button_clicked( final View view ) {
        Player.Get_instance().Set_erase_mode();
    }


    // Private/Protected methods
    //--------------------------

    /*
     * Method used for loading new levels, called once on game startup and again for any later
     * new level changes. Sets up GameModel, removes old data, and renders board to the screen
     *
     * @param Level level: The level that needs to be loaded
     */
    private void load_world( final World world, int request_type ) {
        Player.Get_instance().Set_to_world();
        current_puzzle = world;

        Request request = new Request( request_type );
        request.puzzle = world;
        GameController.Get_instance().Handle_request(request);
    }

    private void load_level( final Level level ) {
        current_puzzle = level;

        Request request = new Request( Request.Load_level_via_world );
        request.puzzle = level;

        HintDialog hint_dialog = new HintDialog();
        hint_dialog.Set_attrs( current_puzzle );

        request.dialog = hint_dialog;

        GameController.Get_instance().Handle_request(request);
    }

    /*
     * Method called to set up event/gesture listeners for game
     *
     * @param: Linearlayout foreground: The Linearlayout to apply the event listeners to
     */
    private void Set_up_listeners() {
        final Player player = Player.Get_instance();
        final GameController controller = GameController.Get_instance();

        findViewById( R.id.foreground ).setOnTouchListener( new GameTouchListener() {
            @Override
            public void onDraw( Line line ) {
                if ( player.In_draw_mode() ) {
                    Request request = new Request( Request.Draw );
                    request.request_line = line;
                    request.puzzle = current_puzzle;
                    controller.Handle_request( request );
                }
            }

            @Override
            public void onErase( final Posn point ) {
                if( player.In_erase_mode() ) {
                    Request request = new Request( Request.Erase );
                    request.request_point = point;
                    request.puzzle = current_puzzle;
                    controller.Handle_request( request );
                }
            }

            @Override
            public void onFingerUp() {
                controller.Handle_request( new Request( Request.None ) );
            }

            @Override
            public void onFingerMove( final Line line, final Posn point ) {
                if ( player.In_draw_mode() ) {
                    if ( OptionsDataAccess.Is_snap_drawing() && !player.Is_in_world_view() ) {
                        line.Snap();
                    }
                    Request request = new Request( Request.Shadow_line );
                    request.request_line = line;
                    controller.Handle_request( request );
                } else {
                    Request request = new Request( Request.Shadow_point );
                    request.request_point = point;
                    controller.Handle_request( request );
                }
            }

            @Override
            public void onTap( final Posn point ) {
                int level_found = controller.Get_tapped_level( point );

                if ( level_found > 0 ) {
                    player.Set_current_level( level_found );
                    load_level( PuzzleDB.Fetch_level( player.Get_current_world(), player.Get_current_level() ) );
                } else {
                    if ( current_puzzle.Can_change_color() ) {
                        Request request = new Request( Request.Change_color );
                        request.request_point = point;
                        controller.Handle_request( request );
                    }
                }
            }

            @Override
            public Line onDragStart( Posn point ) {
                if ( player.In_draw_mode() ) {
                    Line line = controller.Get_line_of_interest( point );
                    if ( line != null ) {
                        Request request = new Request( Request.Drag_start );
                        request.request_line = line;
                        controller.Handle_request( request );
                        return line.clone();
                    }
                }
                return null;
            }

            @Override
            public void onDragEnd( Line line ) {
                Request request = new Request( Request.Drag_end );
                request.request_line = line;
                request.puzzle = current_puzzle;
                controller.Handle_request( request );
            }

            @Override
            public void onEnterDoubleTouch() {
                controller.Handle_request( new Request( Request.None ) );
            }

            @Override
            public void onRotateRight() {
                if ( current_puzzle.Can_rotate() ) {
                    controller.Handle_request( new Request( Request.Rotate_right ) );
                }
            }

            @Override
            public void onRotateLeft() {
                if ( current_puzzle.Can_rotate() ) {
                    controller.Handle_request( new Request( Request.Rotate_left ) );
                }
            }

            @Override
            public void onFlipHorizontally() {
                if ( current_puzzle.Can_flip() ) {
                    controller.Handle_request( new Request( Request.Flip_horizontally ) );
                }
            }

            @Override
            public void onFlipVertically() {
                if ( current_puzzle.Can_flip() ) {
                    controller.Handle_request( new Request( Request.Flip_vertically ) );
                }
            }
        } );

        shake_detector.setOnShakeListener( new GameShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                if ( current_puzzle.Can_shift() ) {
                    Request request = new Request( Request.Shift );
                    request.shift_graphs = current_puzzle.Get_boards();
                    controller.Handle_request( request );
                }
            }
        } );
    }

    // Methods used to stop sensors on pause ( save resources )
    //---------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        sensor_manager.registerListener( shake_detector,
                sensor_manager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),
                SensorManager.SENSOR_DELAY_NORMAL );
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensor_manager.unregisterListener( shake_detector );
        Player.Get_instance().Commit_current_world();
    }
}
