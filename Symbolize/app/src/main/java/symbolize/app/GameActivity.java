package symbolize.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;


public class GameActivity extends Activity  {
    // Static fields
    //---------------

    public static final boolean DEVMODE = true;
    public static final int SCALING = 1000;
    public static Point SCREENSIZE;
    public static int BARHEIGHT;
    public static final String LUKE = "Awesome";


    // Main fields
    //--------------

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
        // Build level 1
        LinkedList<Line> puzzle1 = new LinkedList<Line>();
        puzzle1.addLast(new Line(new Posn(100, 100), new Posn(500, 500)));
        puzzle1.addLast(new Line(new Posn(500, 500), new Posn(100, 900)));

        LinkedList<LinkedList<Line>> solns = new LinkedList<LinkedList<Line>>();
        LinkedList<Line> soln = new LinkedList<Line>();
        soln.addLast(new Line(new Posn(100, 100), new Posn(500, 500)));
        soln.addLast(new Line(new Posn(500, 500), new Posn(100, 900)));
        soln.addLast(new Line(new Posn(500, 500), new Posn(900, 500)));
        solns.addLast(soln);

        LinkedList<Line> puzzle2 = new LinkedList<Line>();
        for(Line l : soln) puzzle2.addLast(l.clone());

        ArrayList<LinkedList<Line>> boards = new ArrayList<LinkedList<Line>>();
        boards.add(puzzle1);
        boards.add(puzzle2);

        Level level = new Level(1, 1, "Place hint here", 30000, 30000, true, true, true, boards, solns );
        //Serializer serializer = new Persister();
        //File result = new File(  "C:\\Users\\Luke\\Desktop\\example.xml" );
        /*try {
            serializer.write( level, result );
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        gameController.loadLevel( level );  // Load level 1-1

        setUpListeners();
    }

    /*
     * Method called to set up event/gesture listeners for game
     */
    public void setUpListeners(){
        foreground.setOnTouchListener( new GameTouchListener() {
            @Override
            public void onDraw( Line line ) {
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
                    gameController.drawShadowLine( line );
                } else {
                    gameController.drawShadowPoint( point );
                }
            }

            @Override
            public void onTap( Posn point ) {
                gameController.tryToChangeColor( point );
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
        Toast.makeText( this, "Hint!", Toast.LENGTH_SHORT ).show();
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
