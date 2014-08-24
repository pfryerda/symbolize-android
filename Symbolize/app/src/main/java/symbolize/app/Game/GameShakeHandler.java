package symbolize.app.Game;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GameShakeHandler {
    // Static Fields
    //---------------

    public static final byte SHAKETHRESHOLD = 8;
    public static final double SHAKEIDLETHRESHOLD = 1.15;
    public static final short SHAKESEPARATIONTIME = 500;


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
            float z = values[2];

            float acceleration_square_root = ((x * x + y * y + z * z) /
                    (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH));
            long current_time = event.timestamp;

            if ((acceleration_square_root >= SHAKETHRESHOLD) && !shaking) {
                if ((current_time - last_update) < SHAKESEPARATIONTIME) {
                    return;
                }
                shaking = true;
                last_update = current_time;
            } else if ((acceleration_square_root <= SHAKEIDLETHRESHOLD) && shaking) {
                shaking = false;
                shake_listener.onShake();
            }
        }
    }
}
