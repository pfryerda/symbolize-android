package symbolize.app.Game;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.google.android.gms.ads.*;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import symbolize.app.Common.Line;
import symbolize.app.Common.Options;
import symbolize.app.Common.Player;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;
import symbolize.app.Common.SymbolizeActivity;
import symbolize.app.Dialog.ConfirmDialog;
import symbolize.app.Dialog.HintDialog;
import symbolize.app.Dialog.OptionsDialog;
import symbolize.app.Puzzle.Level;
import symbolize.app.Puzzle.Puzzle;
import symbolize.app.Puzzle.PuzzleDB;
import symbolize.app.Home.HomeActivity;
import symbolize.app.Puzzle.World;
import symbolize.app.R;


public class GameActivity extends SymbolizeActivity
                          implements OptionsDialog.OptionsDialogListener {
    // Static fields
    //---------------

    public static final int SCALING = 1000;
    public static Point SCREENSIZE;
    public static final String LUKE = "Awesome";


    // Fields
    //---------

    private GameModel game_model;
    private Toast toast;

    private Player player;

    private boolean in_world_view = true;
    private Puzzle current_puzzle;

    private SensorManager sensor_manager;
    private GameShakeDetector shake_detector;

    private FragmentManager dialog_fragment_manager;


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


        // Set up linerlayouts and bitamps
        final Display DISPLAY = getWindowManager().getDefaultDisplay();
        SCREENSIZE = new Point();
        DISPLAY.getSize( SCREENSIZE );
        int size = ( SCREENSIZE.y > SCREENSIZE.x ) ? SCREENSIZE.x : SCREENSIZE.y;

        LinearLayout background = (LinearLayout) findViewById( R.id.background );
        background.getLayoutParams().height = size;
        background.getLayoutParams().width = size;

        LinearLayout foreground = (LinearLayout) findViewById( R.id.canvas );
        foreground.getLayoutParams().height = size;
        foreground.getLayoutParams().width = size;

        Bitmap bitMap_fg = Bitmap.createScaledBitmap(
                Bitmap.createBitmap( size, size, Bitmap.Config.ARGB_8888 ), SCALING, SCALING, true );
        Bitmap bitMap_bg = Bitmap.createScaledBitmap(
                Bitmap.createBitmap( size, size, Bitmap.Config.ARGB_8888 ), SCALING, SCALING, true );

        int bar_height = ( SCREENSIZE.y - size - AdSize.BANNER.getHeightInPixels( this ) ) / 2;
        findViewById( R.id.buttons ).getLayoutParams().height = bar_height;
        findViewById( R.id.topbar ).getLayoutParams().height = bar_height;

        int button_width = SCREENSIZE.x / 5;
        findViewById( R.id.Check ).getLayoutParams().width = button_width;
        findViewById( R.id.Hint ).getLayoutParams().width = button_width;
        findViewById( R.id.Undo ).getLayoutParams().width = button_width;
        findViewById( R.id.Draw ).getLayoutParams().width = button_width;
        findViewById( R.id.Erase ).getLayoutParams().width = button_width;


        // Set up Game
        player = Player.Get_instance();
        game_model = new GameModel( player, this, foreground, background, bitMap_fg, bitMap_bg );

        toast = Toast.makeText( this, "", Toast.LENGTH_SHORT );

        dialog_fragment_manager = getFragmentManager();

        current_puzzle = PuzzleDB.Fetch_world( 1 );
        Request request = new Request( Request.Reset );
        request.puzzle = current_puzzle;
        game_model.Handle_request( request );

        update_view();
        Set_up_listeners( foreground );
    }


    // Button methods
    // ---------------

    public void On_left_button_clicked( final View view ) {
        player.Decrease_world();
        load_world( PuzzleDB.Fetch_world( player.Get_current_world() ), Request.Load_puzzle_left );
    }

    public void On_back_button_clicked( final View view ) {
        if ( in_world_view ) {
            startActivity( new Intent( getApplicationContext(), HomeActivity.class ) );
        } else {
            player.Set_to_world_level();
            load_world( PuzzleDB.Fetch_world( player.Get_current_world() ), Request.Load_world_via_level );
        }
    }

    public void On_right_button_clicked( final View view ) {
        player.Increase_world();
        load_world( PuzzleDB.Fetch_world( player.Get_current_world() ), Request.Load_puzzle_right );
    }

    public void On_settings_button_clicked( final View view ) {
        OptionsDialog options_dialog = new OptionsDialog();
        options_dialog.show( dialog_fragment_manager, getString( R.string.options_dialog_id ) );
    }

    public void On_reset_button_clicked( final View view ) {
        Request request = new Request( Request.Reset );
        request.puzzle = current_puzzle;
        game_model.Handle_request( request );
    }

    public void On_check_button_clicked( final View view) {
        if ( Player.DEVMODE ) {
            game_model.LogGraph();
        } else {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            if ( current_puzzle.Check_correctness( game_model.Get_graph() ) ) {
                if ( in_world_view && player.Get_current_world() <= PuzzleDB.NUMBEROFWORLDS ) {
                    for( int unlock : current_puzzle.Get_unlocks() ) {
                        player.Unlock( unlock );
                    }

                    confirmDialog.Set_attr( getString( R.string.puzzle_complete_dialog_title ), getString( R.string.world_complete_dialog_msg ) );
                    confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                        @Override
                        public void OnDialogSuccess() {
                            player.Increase_world();
                            load_world( PuzzleDB.Fetch_world( player.Get_current_world() ), Request.Load_puzzle_right );
                        }

                        @Override
                        public void OnDialogFail() {}
                    } );
                    confirmDialog.show( dialog_fragment_manager, getString( R.string.puzzle_complete_dialog_id ) );
                } else if( in_world_view && player.Get_current_world() > PuzzleDB.NUMBEROFWORLDS ) {

                    // TODO: You beat the game dialog

                } else if ( !in_world_view ) {
                    for( int unlock : current_puzzle.Get_unlocks() ) {
                        player.Unlock( player.Get_current_world(), unlock );
                    }

                    confirmDialog.Set_attr( getString( R.string.puzzle_complete_dialog_title ), getString( R.string.level_complete_dialog_msg ) );
                    confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                        @Override
                        public void OnDialogSuccess() {
                            load_world( PuzzleDB.Fetch_world( player.Get_current_world() ),
                                    Request.Load_world_via_level );
                        }

                        @Override
                        public void OnDialogFail() {}
                    } );
                    confirmDialog.show( dialog_fragment_manager, getString( R.string.puzzle_complete_dialog_id ) );
                }

            } else {
                Render_toast( R.string.incorrect );
            }
        }
    }

    public void On_hint_button_clicked( final View view ) {
        HintDialog hint_dialog = new HintDialog();
        hint_dialog.Set_attr( current_puzzle );
        hint_dialog.show( dialog_fragment_manager, getString( R.string.hint_dialog_id ) );
    }

    public void On_undo_button_clicked( final View view ) {
        if ( game_model.getPastState() == null ) {
            Render_toast( R.string.nothing_to_undo );
        } else {
            undo();
        }
    }

    public void On_draw_button_clicked( final View view ) {
        player.Set_draw_mode();
    }

    public void On_erase_button_clicked( final View view ) {
        player.Set_erase_mode();
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
        in_world_view = true;
        current_puzzle = world;

        Request request = new Request( request_type );
        request.puzzle = world;
        game_model.Handle_request( request );

        update_view();
    }

    private void load_level( final Level level, final Posn pivot ) {
        in_world_view = false;
        current_puzzle = level;

        Request request = new Request( Request.Load_level_via_world );
        request.puzzle = level;
        request.request_point = pivot;

        HintDialog hint_dialog = new HintDialog();
        hint_dialog.Set_attr( current_puzzle );

        request.dialog = hint_dialog;
        request.dialog_fragment_manager = dialog_fragment_manager;

        game_model.Handle_request( request );

        update_view();
    }

    /*
     * Method used to update button states for switching between world mode and level mode
     */
    private void update_view() {
        if ( in_world_view ) {
            ( (TextView) findViewById( R.id.Title ) ).setText(
                    getResources().getString( R.string.world ) + ": " + player.Get_current_world() );
        } else {
            ( (TextView) findViewById( R.id.Title ) ).setText(
                    getResources().getString( R.string.level ) + ": " + player.Get_current_world() + "-" + player.Get_current_level() );
        }

        Button left_button = ( Button ) findViewById( R.id.Left );
        Button right_button = ( Button ) findViewById( R.id.Right );

        if ( player.Is_previous_world_unlocked() && in_world_view ) {
            left_button.setVisibility( View.VISIBLE );
        } else {
            left_button.setVisibility( View.GONE );
        }

        if ( player.Is_next_world_unlocked() && in_world_view ) {
            right_button.setVisibility( View.VISIBLE );
        } else {
            right_button.setVisibility( View.GONE );
        }
    }

    /*
     * Updates the game model to previous state and updates the view
     */
    private void undo() {
        game_model = game_model.getPastState();
        game_model.Handle_request( new Request( Request.None ) );
    }

    /*
    * Display a toast with the given message
    *
    * @param: int/String msg: The message we want to output or the id for it in strings.xml
    */
    private void Render_toast( final String msg ){
        if ( toast == null | toast.getView().getWindowVisibility() != View.VISIBLE ) {
            toast.setText( msg );
            toast.show();
        }
        //Toast.makeText( this, msg, Toast.LENGTH_SHORT ).show();
    }

    private void Render_toast( final int msg_id ) {
        if ( toast == null | toast.getView().getWindowVisibility() != View.VISIBLE ) {
            toast.setText( getResources().getString( msg_id ) );
            toast.show();
        }
        //Toast.makeText( this, getResources().getString( msg_id ), Toast.LENGTH_SHORT ).show();
    }

    /*
     * Method called to set up event/gesture listeners for game
     *
     * @param: Linearlayout foreground: The Linearlayout to apply the event listeners to
     */
    private void Set_up_listeners( final LinearLayout foreground ){
        foreground.setOnTouchListener( new GameTouchListener() {
            @Override
            public void onDraw( Line line ) {
                if ( Options.Is_snap_drawing() ) {
                    line.Snap();
                }
                line.Snap_to_levels( game_model.Get_unlocked_levels() );
                if ( player.In_draw_mode() ) {
                    if ( game_model.Get_lines_drawn() < current_puzzle.Get_draw_restriction() ) {
                        Request request = new Request( Request.Draw );
                        request.request_line = line;
                        game_model.Handle_request( request );
                    } else {
                        Render_toast( R.string.out_of_lines );
                    }
                }
            }

            @Override
            public void onErase( final Posn point ) {
                if( player.In_erase_mode() ) {
                    for ( Line line : game_model.Get_graph() ) {
                        if ( line.Intersects( point ) ) {
                            if ( ( game_model.Get_lines_erased() < current_puzzle.Get_erase_restriction() )
                                    || ( line.Get_owner() == Line.User ) )
                            {
                                Request request = new Request( Request.Erase );
                                request.request_line = line;
                                game_model.Handle_request( request );
                            } else {
                                Render_toast( R.string.out_of_erase );
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFingerUp() {
                game_model.Handle_request( new Request( Request.None ) );
            }

            @Override
            public void onFingerMove( final Line line, final Posn point ) {
                if ( player.In_draw_mode() ) {
                    if ( Options.Is_snap_drawing() ) {
                        line.Snap();
                    }
                    line.Snap_to_levels( game_model.Get_unlocked_levels() );
                    Request request = new Request( Request.Shadow_line );
                    request.request_line = line;
                    game_model.Handle_request(request);
                } else {
                    Request request = new Request( Request.Shadow_point );
                    request.request_point = point;
                    game_model.Handle_request(request);
                }
            }

            @Override
            public void onTap( final Posn point ) {
                ArrayList<Posn> levels = game_model.Get_levels();
                int level_found = 0;
                Posn pivot = new Posn();
                for ( int i = 0; i < levels.size(); ++i ) {
                    if ( levels.get( i ).Approximately_equals( point ) && player.Is_unlocked( player.Get_current_world(), i + 1 ) ) {
                        level_found =  i + 1;
                        pivot = levels.get( i );
                    }
                }
                if ( level_found > 0 ) {
                    player.Set_current_level( level_found );
                    load_level( PuzzleDB.Fetch_level( player.Get_current_world(), player.Get_current_level() ), pivot );
                } else {
                    if ( current_puzzle.Can_change_color() ) {
                        for ( Line line : game_model.Get_graph() ) {
                            if ( line.Intersects( point ) ) {
                                Request request = new Request( Request.Change_color );
                                request.request_line = line;
                                game_model.Handle_request( request );
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public Line onDragStart( Posn point ) {
                if ( player.In_draw_mode() ) {
                    for ( Line line : game_model.Get_graph() ) {
                        if ( line.Intersects( point ) ) {
                            Request request = new Request( Request.Drag_start );
                            request.request_line = line;
                            game_model.Handle_request( request );
                            return line.clone();
                        }
                    }
                }
                return null;
            }

            @Override
            public void onDragEnd( Line line ) {
                if ( game_model.Get_lines_dragged() < current_puzzle.Get_drag_restriction() ) {
                    Request request = new Request( Request.Drag_end );
                    request.request_line = line;
                    game_model.Handle_request( request );
                } else {
                    undo();
                    Render_toast( R.string.out_of_drag );
                }
            }

            @Override
            public void onEnterDoubleTouch() {
                game_model.Handle_request( new Request( Request.None ) );
            }

            @Override
            public void onRotateRight() {
                if ( current_puzzle.Can_rotate() ) {
                    game_model.Handle_request( new Request( Request.Rotate_right ) );
                }
            }

            @Override
            public void onRotateLeft() {
                if ( current_puzzle.Can_rotate() ) {
                    game_model.Handle_request( new Request( Request.Rotate_left ) );
                }
            }

            @Override
            public void onFlipHorizontally() {
                if ( current_puzzle.Can_flip() ) {
                    game_model.Handle_request( new Request( Request.Flip_horizontally ) );
                }
            }

            @Override
            public void onFlipVertically() {
                if ( current_puzzle.Can_flip() ) {
                    game_model.Handle_request( new Request( Request.Flip_vertically ) );
                }
            }
        } );

        shake_detector.setOnShakeListener( new GameShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                if ( current_puzzle.Can_shift() ) {
                    Request request = new Request( Request.Shift );
                    request.shift_graphs = current_puzzle.Get_boards();
                    game_model.Handle_request( request );
                }
            }
        } );
    }

    @Override
    public void OnToggleGrid() {
        Options.Toggle_grid();
        Request request = new Request( Request.Background_change );
        game_model.Handle_request( request );
    }

    @Override
    public void OnToggleBorder() {
        Options.Toggle_border();
        Request request = new Request( Request.Background_change );
        game_model.Handle_request( request );
    }

    @Override
    public void OnToggleSnap() {
        Options.Toggle_snap();
        Request request = new Request( Request.Background_change );
        game_model.Handle_request( request );
    }

    @Override
    public void OnDeleteAllData() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.Set_attr( getString( R.string.delete_all_data_title ), getString( R.string.delete_all_data_msg ) );
        confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
            @Override
            public void OnDialogSuccess() {
                player.Delete_all_data();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }

            @Override
            public void OnDialogFail() {}
        } );
        confirmDialog.show( dialog_fragment_manager, getString( R.string.delete_all_data_id ) );
    }

    @Override
    public void OnEditVolume( int volume ) {
        Options.Set_volume( volume );
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
    }
}
