package symbolize.app;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static symbolize.app.Constants.SHAKEIDLETHRESHOLD;
import static symbolize.app.Constants.SHAKESEPARATIONTIME;
import static symbolize.app.Constants.SHAKETHRESHOLD;

public class ShakeDetector implements SensorEventListener {
    private OnShakeListener shakeListener;
    private boolean shaking;
    private long lastUpdate;

    public void setOnShakeListener( OnShakeListener shakeListener ) {
        this.shakeListener = shakeListener;
        this.lastUpdate = System.currentTimeMillis();
        this.shaking = false;
    }

    public interface OnShakeListener {
        public void onShake();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


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
            shakeListener.onShake();
            //gameController.shift();
        }
    }
}
