package symbolize.app.Game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.google.android.gms.ads.*;

import android.provider.Settings;
import android.util.Log;
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
import symbolize.app.Common.Posn;
import symbolize.app.Common.Puzzle;
import symbolize.app.Common.PuzzleDB;
import symbolize.app.Home.HomeActivity;
import symbolize.app.R;


public class GameActivity extends Activity  {
    // Static fields
    //---------------

    public static final boolean DEVMODE = true;
    public static final int SCALING = 1000;
    public static Point SCREENSIZE;
    public static final String LUKE = "Awesome";


    // Fields
    //---------

    private int current_world = 1;
    private int current_level = 0;
    private boolean in_world_view = true;
    private Puzzle current_puzzle;
    private PuzzleDB puzzleDB;

    private GameModel game_model;
    private boolean draw_enabled;

    private SensorManager sensor_manager;
    private ShakeDetector shake_detector;


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
        shake_detector =  new ShakeDetector();

        // Ad setup
        AdView adView = ( AdView ) this.findViewById( R.id.game_adspace );
        AdRequest ad_request = new AdRequest.Builder()
                .addTestDevice( AdRequest.DEVICE_ID_EMULATOR )
                .addTestDevice( Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID ) ) // TOD: Manually put in our device ids for security
                .build();
        adView.setAdListener(new AdListener() {} );
        adView.loadAd(ad_request);


        // Set up linerlayouts and bitamps
        final Display DISPLAY = getWindowManager().getDefaultDisplay();
        SCREENSIZE = new Point();
        DISPLAY.getSize( SCREENSIZE );
        int size = ( SCREENSIZE.y > SCREENSIZE.x ) ? SCREENSIZE.x : SCREENSIZE.y;

        LinearLayout background = (LinearLayout) findViewById(R.id.background);
        background.getLayoutParams().height = size;
        background.getLayoutParams().width = size;

        LinearLayout foreground = (LinearLayout) findViewById(R.id.canvas);
        foreground.getLayoutParams().height = size;
        foreground.getLayoutParams().width = size;

        Bitmap bitMap_fg = Bitmap.createScaledBitmap(
                Bitmap.createBitmap( size, size, Bitmap.Config.ARGB_8888 ), SCALING, SCALING, true );
        Bitmap bitMap_bg = Bitmap.createScaledBitmap(
                Bitmap.createBitmap( size, size, Bitmap.Config.ARGB_8888 ), SCALING, SCALING, true );


        // Set up Game
        game_model = new GameModel( this, foreground, background, bitMap_fg, bitMap_bg );
        draw_enabled = true;
        load_puzzle( puzzleDB.Fetch_world( 1 ) );  // Load world 1
        Set_up_listeners( foreground );
    }


    // Button methods
    // ---------------

    public void On_left_button_clicked( final View view ) {
        if ( in_world_view ) {
            current_world = ( current_world == 1 ) ? 7 : current_world - 1;
            load_puzzle( puzzleDB.Fetch_world( current_world ) );
        }
    }

    public void On_back_button_clicked( final View view ) {
        if ( in_world_view ) {
            startActivity( new Intent( getApplicationContext(), HomeActivity.class ) );
        } else {
            in_world_view = true;
            load_puzzle( puzzleDB.Fetch_world( current_world ) );
            current_level = 0;
        }
    }

    public void On_right_button_clicked( final View view ) {
        if ( in_world_view ) {
            current_world = ( current_world % 7 ) + 1;
            load_puzzle( puzzleDB.Fetch_world( current_world ) );
        }
    }

    public void On_settings_button_clicked( final View view ) {
        Render_toast( "Settings!" );
    }

    public void On_reset_button_clicked( final View view ) {
        load_puzzle( current_puzzle );
    }

    public void On_check_button_clicked( final View view) {
        if ( DEVMODE ) {
            game_model.LogGraph();
        } else {
            if ( current_puzzle.Check_correctness( game_model.Get_graph() ) ) {
                Render_toast( "You are correct!" );
            } else {
                Render_toast( "You are incorrect" );
            }
        }
    }

    public void On_hint_button_clicked( final View view ) {
        Render_toast("Hint");
    }

    public void On_undo_button_clicked( final View view ) {
        if ( game_model.getPastState() == null ) {
            Render_toast( "There is nothing to undo" );
        } else {
            game_model = game_model.getPastState();
            game_model.Remove_shadows();
        }
    }

    public void On_draw_button_clicked( final View view ) {
        draw_enabled = true;
    }

    public void On_erase_button_clicked( final View view ) {
        draw_enabled = false;
    }


    // Private/Protected methods
    //--------------------------

    /*
     * Method used for loading new levels, called once on game startup and again for any later
     * new level changes. Sets up GameModel, removes old data, and renders board to the screen
     *
     * @param Level level: The level that needs to be loaded
     */
    private void load_puzzle( final Puzzle puzzle ) {
        // Load up puzzle
        current_puzzle = puzzle;
        game_model.setPuzzle( puzzle );

        // Change game from world mode to level mode and vice versa
        Button left_button = ( Button ) findViewById( R.id.Left );
        Button right_button = ( Button ) findViewById( R.id.Right );

        if ( in_world_view ) {
            ( (TextView) findViewById( R.id.Title ) ).setText( "World: " + current_world );
            left_button.setVisibility( View.VISIBLE );
            right_button.setVisibility( View.VISIBLE );
        } else {
            ( (TextView) findViewById( R.id.Title ) ).setText( "Level: " + current_world + "-" + current_level );
            left_button.setVisibility( View.GONE );
            right_button.setVisibility( View.GONE );
        }
    }

    /*
    * Display a toast with the given message
    *
    * @param: String msg: The message we want to output
    */
    private void Render_toast( final String msg ) {
        Toast.makeText( this, msg, Toast.LENGTH_SHORT ).show();
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
                if ( DEVMODE ) {
                    line.Snap();
                }
                line.Snap_to_levels( game_model.Get_levels() );
                if ( draw_enabled ) {
                    if ( game_model.getLinesDrawn() < current_puzzle.Get_draw_restriction() ) {
                        game_model.action_basic( Action.Draw, line );
                    } else {
                        Render_toast( "Cannot draw any more lines " );
                    }
                }
            }

            @Override
            public void onErase( final Posn point ) {
                if( !draw_enabled ) {
                    for ( Line line : game_model.Get_graph() ) {
                        if ( line.Intersects( point ) ) {
                            if ( ( game_model.getLinesErased() < current_puzzle.Get_erase_restriction() )
                                    || ( line.Get_owner() == Owner.User ) )
                            {
                                game_model.action_basic( Action.Erase, line );
                            } else {
                                Render_toast( "Cannot erase any more lines" );
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
                if ( draw_enabled ) {
                    if ( DEVMODE ) {
                        line.Snap();
                    }
                    line.Snap_to_levels( game_model.Get_levels() );
                    game_model.Add_shadow( line );
                } else {
                    game_model.Add_shadow( point );
                }
            }

            @Override
            public void onTap( final Posn point ) {
                ArrayList<Posn> levels = game_model.Get_levels();
                int level_found = 0;
                for ( int i = 0; i < levels.size(); ++i ) {
                    if ( levels.get( i ).Approximately_equals( point ) ) {
                        level_found =  i + 1;
                    }
                }
                if ( level_found > 0 ) {
                    current_level = level_found;
                    in_world_view = false;
                    load_puzzle(puzzleDB.Fetch_level(current_world, current_level));
                } else {
                    if ( current_puzzle.Can_change_color() ) {
                        for ( Line line : game_model.Get_graph( )) {
                            if ( line.Intersects( point ) ) {
                                game_model.action_basic( Action.Change_color, line );
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public Line onDragStart( Posn point ) {
                if ( draw_enabled ) {
                    for ( Line line : game_model.Get_graph() ) {
                        if ( line.Intersects( point ) ) {
                            game_model.action_basic( Action.Drag_start, line );
                            return line.clone();
                        }
                    }
                }
                return null;
            }

            @Override
            public void onDragEnd( Line line ) {
                game_model.action_basic( Action.Drag_end, line );
            }

            @Override
            public void onEnterDobuleTouch() {
                game_model.Remove_shadows();
            }

            @Override
            public void onRotateRight() {
                if ( current_puzzle.Can_rotate() ) {
                    game_model.action_motion( Action.Rotate_right );
                }
            }

            @Override
            public void onRotateLeft() {
                if ( current_puzzle.Can_rotate() ) {
                    game_model.action_motion( Action.Rotate_left );
                }
            }

            @Override
            public void onFlipHorizontally() {
                if ( current_puzzle.Can_flip() ) {
                    game_model.action_motion( Action.Flip_horizontally );
                }
            }

            @Override
            public void onFlipVertically() {
                if ( current_puzzle.Can_flip() ) {
                    game_model.action_motion( Action.Flip_vertically );
                }
            }
        } );

        shake_detector.setOnShakeListener( new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                if ( current_puzzle.Can_shift() ) {
                    game_model.action_sensor( Action.Shift, current_puzzle.Get_boards() );
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
