package app.symbolize.Game;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Map;

public class GameShakeHandler {
    // Constants
    //-----------

    public static final byte SHAKE_THRESHOLD = 23;
    public static final double SHAKE_IDLE_THRESHOLD = 0.55f;
    public static final short SHAKE_SEPARATION_TIME = 2000;


    // Fields
    //-------

    private OnShakeListener shake_listener;
    private boolean shaking;
    private long last_update;


    // Interface
    //-----------

    public interface OnShakeListener extends SensorEventListener {
        public void onShake();
    }


    // Singleton setup
    //-----------------

    private static GameShakeHandler instance = new GameShakeHandler();

    public static GameShakeHandler Get_instance() {
        return instance;
    }


    // Constructor
    //---------------

    private GameShakeHandler() {}


    // Setter methods
    //----------------

    public void Set_listener( final OnShakeListener shake_listener ) {
        this.shake_listener = shake_listener;
        this.last_update = System.currentTimeMillis();
        this.shaking = false;
    }


    // Main Method
    //--------------

    public void handle_shake( final SensorEvent event ) {
        if( event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            //float z = values[2];
            //float acceleration_square_root = ( ( x * x + y * y + z * z ) /
            //        ( SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH ) );
            long current_time = event.timestamp;

            if ( Math.abs( x ) >= SHAKE_THRESHOLD && !shaking ) {
                if ((current_time - last_update) < SHAKE_SEPARATION_TIME) {
                    return;
                }
                shaking = true;
                last_update = current_time;
            } else if ( ( Math.abs( x ) <= SHAKE_IDLE_THRESHOLD ) && shaking ) {
                shaking = false;
                shake_listener.onShake();
            }
        }
    }
}
