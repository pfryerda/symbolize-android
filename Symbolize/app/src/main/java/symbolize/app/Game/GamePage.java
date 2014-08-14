package symbolize.app.Game;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.google.android.gms.ads.*;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;

import symbolize.app.Common.Line;
import symbolize.app.Common.Response;
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


public class GamePage extends Page
                      implements GameTouchHandler.GameTouchListener,
                                 GameShakeHandler.OnShakeListener {
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
        GameController.Get_instance().Handle_request( request, new Response() );

        GameTouchHandler.Get_instance().Set_listener( this );
        GameShakeHandler.Get_instance().Set_listener( this );
        findViewById(R.id.foreground).setOnTouchListener( this );
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
        GameController.Get_instance().Handle_request( request, new Response() );
    }

    public void On_check_button_clicked( final View view ) {
        final Player player = Player.Get_instance();
        final GameController controller = GameController.Get_instance();

        if ( Player.DEV_MODE ) {
            Request request = new Request( Request.Log );

            controller.Handle_request( request, new Response() );
        } else {
            Request request = new Request( Request.Check_correctness );
            request.puzzle = current_puzzle;

            ConfirmDialog confirmDialog = new ConfirmDialog();
            if ( controller.Handle_request( request, new Response() ) ) {
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
        GameController.Get_instance().Handle_request( request, new Response() );
    }

    public void On_draw_button_clicked( final View view ) {
        Player.Get_instance().Set_draw_mode();
    }

    public void On_erase_button_clicked( final View view ) {
        Player.Get_instance().Set_erase_mode();
    }


    // Interface methods
    //-------------------

    // Touch methods
    @Override
    public boolean onTouch( final View v, final MotionEvent event ) {
        return GameTouchHandler.Get_instance().handle_touch( event );
    }

    @Override
    public void onDraw( Line line ) {
        if ( Player.Get_instance().In_draw_mode() ) {
            Request request = new Request( Request.Draw );
            request.request_line = line;
            request.puzzle = current_puzzle;
            GameController.Get_instance().Handle_request( request, new Response());
        }
    }

    @Override
    public void onErase( final Posn point ) {
        if( Player.Get_instance().In_erase_mode() ) {
            Request request = new Request( Request.Erase );
            request.request_point = point;
            request.puzzle = current_puzzle;
            GameController.Get_instance().Handle_request( request, new Response() );
        }
    }

    @Override
    public void onFingerUp() {
        GameController.Get_instance().Handle_request( new Request( Request.None ), new Response() );
    }

    @Override
    public void onFingerMove( final Line line, final Posn point ) {
        Player player = Player.Get_instance();
        GameController controller = GameController.Get_instance();

        if ( player.In_draw_mode() ) {
            if ( OptionsDataAccess.Is_snap_drawing() && !player.Is_in_world_view() ) {
                line.Snap();
            }
            Request request = new Request( Request.Shadow_line );
            request.request_line = line;
            controller.Handle_request( request, new Response() );
        } else {
            Request request = new Request( Request.Shadow_point );
            request.request_point = point;
            controller.Handle_request( request, new Response() );
        }
    }

    @Override
    public void onTap( final Posn point ) {
        GameController controller = GameController.Get_instance();
        Player player = Player.Get_instance();

        if( player.Is_in_world_view() ) {
            Request request = new Request( Request.Fetch_level );
            request.request_point = point;
            Response response = new Response();
            controller.Handle_request( request, response );
            if( response.response_int > 0 ) {
                player.Set_current_level( response.response_int );
                load_level( PuzzleDB.Fetch_level(player.Get_current_world(), player.Get_current_level() ) );
            }
        } else {
            if ( current_puzzle.Can_change_color() ) {
                Request request = new Request( Request.Change_color );
                request.request_point = point;
                controller.Handle_request( request, new Response() );
            }
        }
    }

    @Override
    public Line onDragStart( Posn point ) {
        GameController controller = GameController.Get_instance();

        if ( Player.Get_instance().In_draw_mode() ) {
            Request request = new Request( Request.Drag_start );
            request.request_point = point;
            Response response = new Response();
            controller.Handle_request( request, response );
            return response.response_line.clone();
        }
        return null;
    }

    @Override
    public void onDragEnd( Line line ) {
        Request request = new Request( Request.Drag_end );
        request.request_line = line;
        request.puzzle = current_puzzle;
        GameController.Get_instance().Handle_request( request, new Response() );
    }

    @Override
    public void onEnterDoubleTouch() {
        GameController.Get_instance().Handle_request( new Request( Request.None ), new Response() );
    }

    @Override
    public void onRotateRight() {
        if ( current_puzzle.Can_rotate() ) {
            GameController.Get_instance().Handle_request( new Request( Request.Rotate_right ), new Response() );
        }
    }

    @Override
    public void onRotateLeft() {
        if ( current_puzzle.Can_rotate() ) {
            GameController.Get_instance().Handle_request( new Request( Request.Rotate_left ), new Response() );
        }
    }

    @Override
    public void onFlipHorizontally() {
        if ( current_puzzle.Can_flip() ) {
            GameController.Get_instance().Handle_request( new Request( Request.Flip_horizontally ), new Response() );
        }
    }

    @Override
    public void onFlipVertically() {
        if ( current_puzzle.Can_flip() ) {
            GameController.Get_instance().Handle_request( new Request( Request.Flip_vertically ), new Response() );
        }
    }

    //Sensor methods
    @Override
    public void onSensorChanged( final SensorEvent event ) {
        GameShakeHandler.Get_instance().handle_shake(event);
    }

    @Override
    public void onShake() {
        if ( current_puzzle.Can_shift() ) {
            Request request = new Request( Request.Shift );
            request.shift_graphs = current_puzzle.Get_boards();
            GameController.Get_instance().Handle_request( request, new Response() );
        }
    }

    @Override
    public void onAccuracyChanged( final Sensor sensor, final int accuracy ) {}


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
        GameController.Get_instance().Handle_request( request, new Response() );
    }

    private void load_level( final Level level ) {
        current_puzzle = level;

        Request request = new Request( Request.Load_level_via_world );
        request.puzzle = level;

        HintDialog hint_dialog = new HintDialog();
        hint_dialog.Set_attrs( current_puzzle );

        request.dialog = hint_dialog;

        GameController.Get_instance().Handle_request( request, new Response() );
    }


    // Method for pausing/resuming the game
    //---------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        sensor_manager.registerListener( this,
                sensor_manager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),
                SensorManager.SENSOR_DELAY_NORMAL );
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensor_manager.unregisterListener( this );
        Player.Get_instance().Commit_current_world();
    }
}
