package com.busecarik.platformer.input;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;

import com.busecarik.platformer.GameActivity;
import com.busecarik.platformer.utils.Utils;

public class Accelerometer extends InputManager {

    private static final int AXIS_COUNT = 3; //azimuth (z), pitch (x), roll (y)
    public static final float MAX_ANGLE = 30; //TODO: play with
    private float[] mLastAccels = new float[AXIS_COUNT];
    private float[] mLastMagFields = new float[AXIS_COUNT];
    private GameActivity _activity = null;
    private static final float DEGREES_PER_RADIAN = 57.2957795f;
    private float[] mRotationMatrix = new float[4*4];
    private float[] mOrientation = new float[AXIS_COUNT];
    private int _rotation;
    private static final float SHAKE_THRESHOLD = 3.25f; // m/S^2
    private static final long COOLDOWN = 300;//ms
    private long mLastShake = 0;

    public Accelerometer(GameActivity activity) {
        _activity = activity;
        _rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    }

    private float getHorizontalAxis() {
        if (SensorManager.getRotationMatrix(mRotationMatrix, null, mLastAccels, mLastMagFields)) {
            if (_rotation == Surface.ROTATION_0) {
                SensorManager.remapCoordinateSystem(mRotationMatrix,
                        SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mRotationMatrix);
                SensorManager.getOrientation(mRotationMatrix, mOrientation);
                return mOrientation[1] * DEGREES_PER_RADIAN;
            }
            else {
                SensorManager.getOrientation(mRotationMatrix, mOrientation);
                return -mOrientation[1] * DEGREES_PER_RADIAN;
            }
        }
        else {
            // Case for devices that DO NOT have magnetic sensors
            if (_rotation == Surface.ROTATION_0) {
                return -mLastAccels[0] * 5;
            }
            else {
                return -mLastAccels[1] * -5;
            }
        }
    }

    private boolean isJumping(){
        if((System.currentTimeMillis()-mLastShake) < COOLDOWN){
            return false;
        }
        float x = mLastAccels[0];
        float y = mLastAccels[1];
        float z = mLastAccels[2];
        float acceleration = (float) Math.sqrt(x*x + y*y + z*z)
                - SensorManager.GRAVITY_EARTH;
        if(acceleration > SHAKE_THRESHOLD){
            mLastShake = System.currentTimeMillis();
            return true;
        }
        return false;
    }


    @Override
    public void update(float dt) {
        _horizontalFactor = getHorizontalAxis() / MAX_ANGLE;
        _horizontalFactor = Utils.clamp(_horizontalFactor, -1.0f, 1.0f);
        _verticalFactor = 0.0f;
    }

    private SensorEventListener mMagneticListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            System.arraycopy(event.values, 0, mLastMagFields, 0, AXIS_COUNT);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private SensorEventListener mAccelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            System.arraycopy(event.values, 0, mLastAccels, 0, AXIS_COUNT);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void registerListeners() {
        SensorManager sm = (SensorManager) _activity
                .getSystemService(Activity.SENSOR_SERVICE);
        sm.registerListener(mAccelerometerListener,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(mMagneticListener,
                sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
    }

    private void unregisterListeners() {
        SensorManager sm = (SensorManager) _activity
                .getSystemService(Activity.SENSOR_SERVICE);
        sm.unregisterListener(mAccelerometerListener);
        sm.unregisterListener(mMagneticListener);
    }

    @Override
    public void onStart() {
        registerListeners();
    }

    @Override
    public void onStop() {
        unregisterListeners();
    }

    @Override
    public void onResume() {
        registerListeners();
    }

    @Override
    public void onPause() {
        unregisterListeners();
    }
}
