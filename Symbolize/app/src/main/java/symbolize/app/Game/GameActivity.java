package symbolize.app.Game;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Action;
import symbolize.app.Common.Level;
import symbolize.app.Common.Line;
import symbolize.app.Common.Owner;
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

    private GameModel gameModel;
    private GameView gameView;

    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;

    private boolean drawnEnabled;


    /*
     * Main method called once app is ready
     *
     * @parama Bundle savedInstanceState: A mapping from String values to various Parcelable types
     */
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        puzzleDB = new PuzzleDB( getResources() );

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeDetector =  new ShakeDetector();


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

        Bitmap bitMap_fg = Bitmap.createScaledBitmap( Bitmap.createBitmap( SCREENSIZE.x, SCREENSIZE.x, Bitmap.Config.ARGB_8888 ),
                SCALING, SCALING, true );
        Bitmap bitMap_bg = Bitmap.createScaledBitmap( Bitmap.createBitmap( SCREENSIZE.x, SCREENSIZE.x, Bitmap.Config.ARGB_8888 ),
                SCALING, SCALING, true );


        // Set up Game
        gameModel = new GameModel();
        gameView = new GameView( this, foreground, background, bitMap_fg, bitMap_bg, gameModel );
        currPuzzle = null;
        drawnEnabled = true;
        Level level = puzzleDB.fetch_level( currWorld, 1 );
        ArrayList<Posn> levels = new ArrayList<Posn>();
        levels.add( new Posn( 500, 500 ) );
        levels.add( new Posn( 500, 750 ) );
        levels.add( new Posn( 750, 500 ) );
        World world = new World( "hint", true, true, true, levels, null );
        loadPuzzle( world );  // Load level 1-1
        setUpListeners( foreground );
    }

    /*
     * Method used for loading new levels, called once on game startup and again for any later
     * new level changes. Sets up GameModel, removes old data, and renders board to the screen
     *
     * @param Level level: The level that needs to be loaded
     */
    public void loadPuzzle( Puzzle puzzle ) {
        currPuzzle = puzzle;
        gameModel.setPuzzle( puzzle );
        gameView.renderGraph();
    }

    /*
     * Method called to set up event/gesture listeners for game
     *
     * @param: Linearlayout foreground: The Linearlayout to apply the event listeners to
     */
    public void setUpListeners( LinearLayout foreground ){
        foreground.setOnTouchListener( new GameTouchListener() {
            @Override
            public void onDraw( Line line ) {
                line.snapToLevels( gameModel.getLevels() );
                if ( drawnEnabled ) {
                    if ( gameModel.getLinesDrawn() < currPuzzle.getDrawRestirction() ) {
                        gameModel.action_basic(Action.Draw, line);
                        gameView.renderGraph();
                    } else {
                        gameView.renderToast( "Cannot draw any more lines " );
                    }
                }
            }

            @Override
            public void onErase( Posn point ) {
                if( !drawnEnabled ) {
                    gameView.renderShadow( point );
                    for ( Line line : gameModel.getGraph() ) {
                        if ( line.intersect( point ) ) {
                            if ( ( gameModel.getLinesErased() < currPuzzle.getEraseRestirction() ) || ( line.getOwner() == Owner.User ) ) {
                                gameModel.action_basic( Action.Erase, line );
                                gameView.renderGraph();
                            } else {
                                gameView.renderToast( "Cannot erase any more lines" );
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFingerUp() {
                gameView.renderGraph();
            }

            @Override
            public void onFingerMove( Line line, Posn point ) {
                if ( drawnEnabled ) {
                    line.snapToLevels( gameModel.getLevels() );
                    gameView.renderShadow( line );
                } else {
                    gameView.renderShadow( point );
                }
            }

            @Override
            public void onTap( Posn point ) {
                ArrayList<Posn> levels = gameModel.getLevels();
                int level_found = 0;
                for ( int i = 0; i < levels.size(); ++i ) {
                    if ( point.eq( levels.get( i ) ) ) {
                        level_found =  i + 1;
                    }
                }
                if ( level_found > 0 ) {
                    loadPuzzle( puzzleDB.fetch_level( currWorld, level_found ) );
                } else {
                    if ( currPuzzle.canChangeColur() ) {
                        for ( Line line : gameModel.getGraph( )) {
                            if ( line.intersect( point ) ) {
                                gameModel.action_basic( Action.Change_color, line );
                                gameView.renderLine( line );
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onEnterDobuleTouch() {
                gameView.renderGraph();
            }

            @Override
            public void onRotateRight() {
                if ( currPuzzle.canRotate() ) {
                    gameModel.action_motion( Action.Rotate_right );
                    gameView.renderRotateR();
                }
            }

            @Override
            public void onRotateLeft() {
                if ( currPuzzle.canRotate() ) {
                    gameModel.action_motion( Action.Rotate_left );
                    gameView.renderRotateL();
                }
            }

            @Override
            public void onFlipHorizontally() {
                if ( currPuzzle.canFlip() ) {
                    gameModel.action_motion( Action.Flip_horizontally );
                    gameView.renderFlipH();
                }
            }

            @Override
            public void onFlipVertically() {
                if ( currPuzzle.canFlip() ) {
                    gameModel.action_motion( Action.Flip_vertically );
                    gameView.renderFlipV();
                }
            }
        } );

        shakeDetector.setOnShakeListener( new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                if ( currPuzzle.canShift() ) {
                    gameModel.action_sensor( Action.Shift, currPuzzle.getBoards() );
                    gameView.renderShift();
                }
            }
        } );
    }




    // Button methods
    // ---------------

    public void onLevelsButtonClicked( View view ) {
        gameView.renderToast( "Levels!" );
    }

    public void onSettingsButtonClicked( View view ) {
        gameView.renderToast("Settings!");
    }

    public void onResetButtonClicked( View view ) {
        loadPuzzle( currPuzzle );
    }

    public void onCheckButtonClicked(View view) {
        if ( DEVMODE ) {
            gameModel.LogGraph();
        } else {
            if ( currPuzzle.checkCorrectness( gameModel.getGraph() ) ) {
                gameView.renderToast("You are correct!");
            } else {
                gameView.renderToast("You are incorrect");
            }
        }
    }

    public void onHintButtonClicked( View view ) {
        gameView.renderToast("Hint");
    }

    public void onUndoButtonClicked( View view ) {
        if ( gameModel.getPastState() == null ) {
            gameView.renderToast( "There is nothing to undo" );
        } else {
            gameModel = gameModel.getPastState();
            gameView.renderUndo();
        }
    }

    public void onDrawButtonClicked( View view ) {
        drawnEnabled = true;
    }

    public void onEraseButtonClicked( View view ) {
        drawnEnabled = false;
    }


    // Methods used to stop sensors on pause ( save resources )
    //---------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener( shakeDetector,
                sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),
                SensorManager.SENSOR_DELAY_NORMAL );
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener( shakeDetector );
    }
}
