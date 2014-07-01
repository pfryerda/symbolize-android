package symbolize.app.Game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.util.ArrayList;

import symbolize.app.Common.Enum.Action;
import symbolize.app.Common.Level;
import symbolize.app.Common.Line;
import symbolize.app.Common.Enum.Owner;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Puzzle;
import symbolize.app.Common.PuzzleDB;
import symbolize.app.Common.World;
import symbolize.app.R;


public class GameActivity extends Activity  {
    // Static fields
    //---------------

    public static final boolean DEVMODE = false;
    public static final int SCALING = 1000;
    public static Point SCREENSIZE;
    public static int BARHEIGHT;
    public static final String LUKE = "Awesome";


    // Fields
    //---------

    private int currWorld = 1;
    private Puzzle currPuzzle;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        puzzleDB = new PuzzleDB( getResources() );

        sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shake_detector =  new ShakeDetector();


        // Set up linerlayouts and bitamps
        final Display DISPLAY = getWindowManager().getDefaultDisplay();
        SCREENSIZE = new Point();
        DISPLAY.getSize( SCREENSIZE );

        LinearLayout foreground = (LinearLayout) findViewById(R.id.canvas);
        foreground.getLayoutParams().height = SCREENSIZE.x;
        foreground.getLayoutParams().width = SCREENSIZE.x;

        LinearLayout background = (LinearLayout) findViewById(R.id.background);
        background.getLayoutParams().height = SCREENSIZE.x;
        background.getLayoutParams().width = SCREENSIZE.x;

        BARHEIGHT = ( SCREENSIZE.y - SCREENSIZE.x ) / 3;
        findViewById( R.id.topbar ).getLayoutParams().height = BARHEIGHT;
        findViewById( R.id.buttons ).getLayoutParams().height = BARHEIGHT;
        findViewById( R.id.adspace ).getLayoutParams().height = BARHEIGHT;

        Bitmap bitMap_fg = Bitmap.createScaledBitmap(
                Bitmap.createBitmap( SCREENSIZE.x, SCREENSIZE.x, Bitmap.Config.ARGB_8888 ),
                SCALING, SCALING, true );
        Bitmap bitMap_bg = Bitmap.createScaledBitmap(
                Bitmap.createBitmap( SCREENSIZE.x, SCREENSIZE.x, Bitmap.Config.ARGB_8888 ),
                SCALING, SCALING, true );


        // Set up Game
        game_model = new GameModel( this, foreground, background, bitMap_fg, bitMap_bg );
        currPuzzle = null;
        draw_enabled = true;
        Level level = puzzleDB.Fetch_level( currWorld, 1 );
        ArrayList<Posn> levels = new ArrayList<Posn>();
        levels.add( new Posn( 500, 500 ) );
        levels.add( new Posn( 500, 750 ) );
        levels.add( new Posn( 750, 500 ) );
        World world = new World( "hint", true, true, true, levels, null );
        load_puzzle( world );  // Load level 1-1
        Set_up_listeners( foreground );
    }


    // Button methods
    // ---------------

    public void On_levels_button_clicked( final View view ) {
        Render_toast( "Levels!" );
    }

    public void On_settings_button_clicked( final View view ) {
        Render_toast("Settings!");
    }

    public void On_reset_button_clicked( final View view ) {
        load_puzzle( currPuzzle );
    }

    public void On_check_button_clicked( final View view) {
        if ( DEVMODE ) {
            game_model.LogGraph();
        } else {
            if ( currPuzzle.Check_correctness( game_model.Get_graph() ) ) {
                Render_toast("You are correct!");
            } else {
                Render_toast("You are incorrect");
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
        currPuzzle = puzzle;
        game_model.setPuzzle( puzzle );
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
                    if ( game_model.getLinesDrawn() < currPuzzle.Get_draw_restriction() ) {
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
                            if ( ( game_model.getLinesErased() < currPuzzle.Get_erase_restriction() )
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
                    if ( point.Approximately_equals( levels.get( i ) ) ) {
                        level_found =  i + 1;
                    }
                }
                if ( level_found > 0 ) {
                    load_puzzle( puzzleDB.Fetch_level( currWorld, level_found ) );
                } else {
                    if ( currPuzzle.Can_change_color() ) {
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
            public void onEnterDobuleTouch() {
                game_model.Remove_shadows();
            }

            @Override
            public void onRotateRight() {
                if ( currPuzzle.Can_rotate() ) {
                    game_model.action_motion( Action.Rotate_right );
                }
            }

            @Override
            public void onRotateLeft() {
                if ( currPuzzle.Can_rotate() ) {
                    game_model.action_motion( Action.Rotate_left );
                }
            }

            @Override
            public void onFlipHorizontally() {
                if ( currPuzzle.Can_flip() ) {
                    game_model.action_motion( Action.Flip_horizontally );
                }
            }

            @Override
            public void onFlipVertically() {
                if ( currPuzzle.Can_flip() ) {
                    game_model.action_motion( Action.Flip_vertically );
                }
            }
        } );

        shake_detector.setOnShakeListener( new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                if ( currPuzzle.Can_shift() ) {
                    game_model.action_sensor( Action.Shift, currPuzzle.Get_boards() );
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
