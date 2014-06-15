package symbolize.app.Game;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {
    // Static Fields
    //---------------

    public static final int SHAKETHRESHOLD = 8;
    public static final double SHAKEIDLETHRESHOLD = 1.15;
    public static final int SHAKESEPARATIONTIME = 500;


    // Fields
    //-------

    private OnShakeListener shakeListener;
    private boolean shaking;
    private long lastUpdate;


    // Methods
    //--------

    public void setOnShakeListener( OnShakeListener shakeListener ) {
        this.shakeListener = shakeListener;
        this.lastUpdate = System.currentTimeMillis();
        this.shaking = false;
    }

    public interface OnShakeListener {
        public void onShake();
    }

    @Override
    public void onAccuracyChanged( Sensor sensor, int accuracy ) {}


    @Override
    public void onSensorChanged( SensorEvent event ) {
        if ( event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
            getAccelerometer( event );
        }
    }

    private void getAccelerometer( SensorEvent event ) {
        // Movement
        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSQRT = ( x * x + y * y + z * z ) / ( SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH );
        long currTime = event.timestamp;

        if ( ( accelationSQRT >= SHAKETHRESHOLD ) && !shaking ) {
            if ( ( currTime - lastUpdate ) < SHAKESEPARATIONTIME ) {
                return;
            }
            shaking = true;
            lastUpdate = currTime;
        } else if ( ( accelationSQRT <= SHAKEIDLETHRESHOLD ) && shaking )  {
            shaking = false;
            shakeListener.onShake();
        }
    }
}
