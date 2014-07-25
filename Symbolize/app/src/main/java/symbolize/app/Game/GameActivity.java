package symbolize.app.Game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.google.android.gms.ads.*;
import android.provider.Settings;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import symbolize.app.Common.Enum.Action;
import symbolize.app.Common.Line;
import symbolize.app.Common.Enum.Owner;
import symbolize.app.Common.Options;
import symbolize.app.Common.Player;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;
import symbolize.app.Puzzle.Level;
import symbolize.app.Puzzle.Puzzle;
import symbolize.app.Puzzle.PuzzleDB;
import symbolize.app.Home.HomeActivity;
import symbolize.app.Puzzle.World;
import symbolize.app.R;


public class GameActivity extends Activity  {
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
    private Options options;
    private PuzzleDB puzzleDB;

    private boolean in_world_view = true;
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
        puzzleDB = new PuzzleDB( getResources() );
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
        player = new Player  ( this.getSharedPreferences( getString( R.string.preference_unlocks_key ), Context.MODE_PRIVATE ) );
        options = new Options( this.getSharedPreferences( getString( R.string.preference_settings_key ), Context.MODE_PRIVATE ) );
        game_model = new GameModel( player, this, foreground, background, bitMap_fg, bitMap_bg );
        toast = Toast.makeText( this, "", Toast.LENGTH_SHORT );

        current_puzzle = puzzleDB.Fetch_world( 1 );
        game_model.Reset( current_puzzle );
        update_view();
        Set_up_listeners( foreground );
    }


    // Button methods
    // ---------------

    public void On_left_button_clicked( final View view ) {
        player.Decrease_world();
        load_world( puzzleDB.Fetch_world( player.Get_current_world() ), Action.Load_world_left );
    }

    public void On_back_button_clicked( final View view ) {
        if ( in_world_view ) {
            startActivity( new Intent( getApplicationContext(), HomeActivity.class ) );
        } else {
            in_world_view = true;
            load_world( puzzleDB.Fetch_world( player.Get_current_world() ), Action.Load_world_via_level );
            player.Set_to_world_level();
        }
    }

    public void On_right_button_clicked( final View view ) {
        player.Increase_world();
        load_world( puzzleDB.Fetch_world( player.Get_current_world() ), Action.Load_world_right );
    }

    public void On_settings_button_clicked( final View view ) {
        Render_toast( "Settings!" );
    }

    public void On_reset_button_clicked( final View view ) {
        game_model.Reset( current_puzzle );
    }

    public void On_check_button_clicked( final View view) {
        if ( Player.DEVMODE ) {
            game_model.LogGraph();
        } else {
            if ( current_puzzle.Check_correctness( game_model.Get_graph() ) ) {
                if ( player.Get_current_level() < PuzzleDB.NUMBEROFLEVELSPERWORLD ) {
                    player.Unlock( player.Get_current_world(), player.Get_current_level() + 1 );
                }
                Render_toast( "You are correct!" );
            } else {
                Render_toast( "You are incorrect" );
            }
        }
    }

    public void On_hint_button_clicked( final View view ) {
        Render_toast( "Hint" );
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
    private void load_world( final World world, Action action ) {
        current_puzzle = world;
        game_model.Set_world( world, action );
        update_view();
    }

    private void load_level( final Level level, final Posn pivot ) {
        // Load up puzzle
        current_puzzle = level;
        game_model.Set_level( level, pivot );
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
        game_model.Remove_shadows();
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
                if ( Player.DEVMODE ) {
                    line.Snap();
                }
                line.Snap_to_levels( game_model.Get_unlocked_levels() );
                if ( player.In_draw_mode() ) {
                    if ( game_model.Get_lines_drawn() < current_puzzle.Get_draw_restriction() ) {
                        game_model.Handle_request( new Request( Action.Draw, line ) );
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
                                    || ( line.Get_owner() == Owner.User ) )
                            {
                                game_model.Handle_request( new Request( Action.Erase, line ) );
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
                game_model.Remove_shadows();
            }

            @Override
            public void onFingerMove( final Line line, final Posn point ) {
                if ( player.In_draw_mode() ) {
                    if ( Player.DEVMODE ) {
                        line.Snap();
                    }
                    line.Snap_to_levels( game_model.Get_unlocked_levels() );
                    game_model.Add_shadow( line );
                } else {
                    game_model.Add_shadow( point );
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
                    in_world_view = false;
                    load_level( puzzleDB.Fetch_level( player.Get_current_world(), player.Get_current_level() ), pivot );
                } else {
                    if ( current_puzzle.Can_change_color() ) {
                        for ( Line line : game_model.Get_graph( )) {
                            if ( line.Intersects( point ) ) {
                                game_model.Handle_request( new Request( Action.Change_color, line ) );
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
                            game_model.Handle_request( new Request( Action.Drag_start, line ) );
                            return line.clone();
                        }
                    }
                }
                return null;
            }

            @Override
            public void onDragEnd( Line line ) {
                if ( game_model.Get_lines_dragged() < current_puzzle.Get_drag_restriction() ) {
                    game_model.Handle_request( new Request( Action.Drag_end, line ) );
                } else {
                    undo();
                    Render_toast( R.string.out_of_drag );
                }
            }

            @Override
            public void onEnterDoubleTouch() {
                game_model.Remove_shadows();
            }

            @Override
            public void onRotateRight() {
                if ( current_puzzle.Can_rotate() ) {
                    game_model.Handle_request( new Request( Action.Rotate_right ) );
                }
            }

            @Override
            public void onRotateLeft() {
                if ( current_puzzle.Can_rotate() ) {
                    game_model.Handle_request( new Request( Action.Rotate_left ) );
                }
            }

            @Override
            public void onFlipHorizontally() {
                if ( current_puzzle.Can_flip() ) {
                    game_model.Handle_request( new Request( Action.Flip_horizontally ) );
                }
            }

            @Override
            public void onFlipVertically() {
                if ( current_puzzle.Can_flip() ) {
                    game_model.Handle_request( new Request( Action.Flip_vertically ) );
                }
            }
        } );

        shake_detector.setOnShakeListener( new GameShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                if ( current_puzzle.Can_shift() ) {
                    game_model.Handle_request(new Request(Action.Shift, current_puzzle.Get_boards()));
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
    }
}
