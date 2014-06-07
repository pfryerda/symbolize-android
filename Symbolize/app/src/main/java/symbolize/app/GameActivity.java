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
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import static symbolize.app.Constants.*;

import java.util.ArrayList;
import java.util.LinkedList;


public class GameActivity extends Activity implements SensorEventListener {
    // Main fields
    //--------------

    private LinearLayout foreground;
    private LinearLayout background;
    private GameController gameController;

    private SensorManager sensorManager;
    private boolean shaking;
    private long lastUpdate;


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
        shaking = false;
        lastUpdate = System.currentTimeMillis();


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

        findViewById( R.id.topbar ).getLayoutParams().height = ( SCREENSIZE.y - SCREENSIZE.x ) / 3;
        findViewById( R.id.buttons ).getLayoutParams().height = ( SCREENSIZE.y - SCREENSIZE.x ) / 3;
        findViewById( R.id.adspace ).getLayoutParams().height = ( SCREENSIZE.y - SCREENSIZE.x ) / 3;

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

        ArrayList<LinkedList<Line>> sg = new ArrayList<LinkedList<Line>>();
        sg.add(puzzle1);
        sg.add(puzzle2);

        Level level = new Level(1, 1, "Place hint here", puzzle1, solns, 30000, 30000, true, true, true, sg);

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
                    gameController.tryToErase( point );
                }
            }

            @Override
            public void onTap( Posn point ) {
                gameController.tryToChangeColor( point );
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
    }



    // Sensor methods
    //----------------

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        // Movement
        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;

        if ( ( accelationSquareRoot >= SHAKETHRESHOLD ) && !shaking ) {
            if ( actualTime - lastUpdate < SHAKESEPARATIONTIME ) {
                return;
            }
            shaking = true;
            lastUpdate = actualTime;
        } else if ( ( accelationSquareRoot <= SHAKEIDLETHRESHOLD ) && shaking )  {
            shaking = false;
            gameController.shift();
        }
    }


    // Button methods
    // ---------------

    public void onToggleButtonClicked( View view ) {
        Button toggleButton = (Button) findViewById(R.id.Toggle);
        if(toggleButton.getText() == "Draw") {
            toggleButton.setText("Erase");
        } else {
            toggleButton.setText("Draw");
        }
        gameController.toogleModes();
    }

    public void onCheckButtonClicked(View view) {
        if ( gameController.checkSolution() ) {
            Toast.makeText( this, "You are correct!", Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText( this, "You are incorrect", Toast.LENGTH_SHORT ).show();
        }
    }

    public void onHintButtonClicked( View view ) {
        //gameController.rotateRight();
        //gameController.rotateLeft();
        //gameController.flipHorizontally();
        //gameController.flipVertically();
        gameController.shift();
        /*Display hint box here;*/
    }

    public void onResetButtonClicked( View view ) {
        gameController.reset();
    }

    public void onUndoButtonClicked( View view ) {
        gameController.undo();
    }


}
