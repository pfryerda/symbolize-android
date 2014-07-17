package symbolize.app.Game;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GameShakeDetector implements SensorEventListener {
    // Static Fields
    //---------------

    public static final int SHAKETHRESHOLD = 8;
    public static final double SHAKEIDLETHRESHOLD = 1.15;
    public static final int SHAKESEPARATIONTIME = 500;


    // Fields
    //-------

    private OnShakeListener shake_listener;
    private boolean shaking;
    private long last_update;


    // Interface
    //-----------

    public interface OnShakeListener {
        public void onShake();
    }


    // Public methods
    //----------------

    public void setOnShakeListener( final OnShakeListener shake_listener ) {
        this.shake_listener = shake_listener;
        this.last_update = System.currentTimeMillis();
        this.shaking = false;
    }

    @Override
    public void onAccuracyChanged( final Sensor sensor, final int accuracy ) {}


    @Override
    public void onSensorChanged( final SensorEvent event ) {
        if ( event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
            get_accelerometer( event );
        }
    }

    // Private Method
    //-----------------

    private void get_accelerometer( final SensorEvent event ) {
        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float acceleration_square_root = ( ( x * x + y * y + z * z ) /
                ( SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH ) );
        long current_time = event.timestamp;

        if ( ( acceleration_square_root >= SHAKETHRESHOLD ) && !shaking ) {
            if ( ( current_time - last_update ) < SHAKESEPARATIONTIME ) {
                return;
            }
            shaking = true;
            last_update = current_time;
        } else if ( ( acceleration_square_root <= SHAKEIDLETHRESHOLD ) && shaking )  {
            shaking = false;
            shake_listener.onShake();
        }
    }
}
