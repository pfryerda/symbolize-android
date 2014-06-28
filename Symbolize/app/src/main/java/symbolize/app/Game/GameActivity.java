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

import symbolize.app.Common.Level;
import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
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


    // Main fields
    //--------------

    private int currWorld = 1;
    private PuzzleDB puzzleDB;

    private LinearLayout foreground;
    private LinearLayout background;
    private GameController gameController;

    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;


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
        Log.d( "ScreenSize", "X:" + SCREENSIZE.x + " Y:" + SCREENSIZE.y );

        foreground = (LinearLayout) findViewById(R.id.canvas);
        foreground.getLayoutParams().height = SCREENSIZE.x;
        foreground.getLayoutParams().width = SCREENSIZE.x;

        background = (LinearLayout) findViewById(R.id.background);
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

        gameController = new GameController( this, foreground, background, bitMap_fg, bitMap_bg );
        Level level = puzzleDB.fetch_level( currWorld, 1 );
        ArrayList<Posn> levels = new ArrayList<Posn>();
        levels.add( new Posn( 500, 500 ) );
        levels.add( new Posn( 500, 750 ) );
        levels.add( new Posn( 750, 500 ) );
        World world = new World( "hint", true, true, true, levels, null );
        gameController.loadPuzzle( world );  // Load level 1-1
        setUpListeners();
    }

    /*
     * Method called to set up event/gesture listeners for game
     */
    public void setUpListeners(){
        foreground.setOnTouchListener( new GameTouchListener() {
            @Override
            public void onDraw( Line line ) {
                gameController.snapToLevels( line );
                if ( gameController.isInDrawMode() ) {
                    gameController.drawLine( line );
                }
            }

            @Override
            public void onErase( Posn point ) {
                if( gameController.isInEraseMode() ) {
                    gameController.drawShadowPoint( point );
                    gameController.tryToErase( point );
                }
            }

            @Override
            public void onFingerUp() {
                gameController.removeShadows();
            }

            @Override
            public void onFingerMove( Line line, Posn point ) {
                if ( gameController.isInDrawMode() ) {
                    gameController.snapToLevels( line );
                    gameController.drawShadowLine( line );
                } else {
                    gameController.snapToLevels( point );
                    gameController.drawShadowPoint( point );
                }
            }

            @Override
            public void onTap( Posn point ) {
                int level_found = gameController.tryToMatchLevel( point );
                if ( level_found > 0 ) {
                    gameController.loadPuzzle( puzzleDB.fetch_level( currWorld, level_found ) );
                } else {
                    gameController.tryToChangeColor( point );
                }
            }

            @Override
            public void onEnterDobuleTouch() {
                gameController.removeShadows();
            }

            @Override
            public void onRotateRight() {
                gameController.rotateRight();
            }

            @Override
            public void onRotateLeft() {
                gameController.rotateLeft();
            }

            @Override
            public void onFlipHorizontally() {
                gameController.flipHorizontally();
            }

            @Override
            public void onFlipVertically() {
                gameController.flipVertically();
            }
        } );

        shakeDetector.setOnShakeListener( new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                gameController.shift();
            }
        } );
    }


    // Button methods
    // ---------------

    public void onLevelsButtonClicked( View view ) {
        Toast.makeText( this, "Levels!", Toast.LENGTH_SHORT ).show();
    }

    public void onSettingsButtonClicked( View view ) {
        Toast.makeText( this, "Settings!", Toast.LENGTH_SHORT ).show();
    }

    public void onResetButtonClicked( View view ) {
        gameController.reset();
    }

    public void onCheckButtonClicked(View view) {
        if ( DEVMODE ) {
            gameController.LogModel();
        } else {
            if (gameController.checkSolution()) {
                Toast.makeText(this, "You are correct!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You are incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onHintButtonClicked( View view ) {
        Toast.makeText( this, "Hint", Toast.LENGTH_SHORT ).show();
    }

    public void onUndoButtonClicked( View view ) {
        gameController.undo();
    }

    public void onDrawButtonClicked( View view ) {
        gameController.enterDrawMode();
    }

    public void onEraseButtonClicked( View view ) {
        gameController.enterEraseMode();
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
