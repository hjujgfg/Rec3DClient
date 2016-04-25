package lapidus.edu.rec3dclient;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;


public class ImageCapture extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetic;
    private Sensor gravity;
    private TextView xView, yView, zView, xRot, yRot, zRot;
    private float[] mGravity = null;
    private float[] mGeo = null;
    private float[] anotherGravity = new float[3];
    private float[] location = new float[3];
    private long[] millis = new long[3];
    private final static String TAG = "ImageCapture";
    private int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        xView = (TextView)findViewById(R.id.Xcoord);
        yView = (TextView)findViewById(R.id.Ycoord);
        zView = (TextView)findViewById(R.id.Zcoord);
        xRot = (TextView)findViewById(R.id.Xrot);
        yRot = (TextView)findViewById(R.id.Yrot);
        zRot = (TextView)findViewById(R.id.Zrot);
        for (int i =0; i < 3; i ++) {
            location[i] = 0;
            millis[i] = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_capture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        counter ++;
        if (counter < 10) {
            Log.d(TAG, "Counter " + counter);
            return;
        } else {
            counter = 0;
        }
        float threshold = .5f;
        long tempTime = System.currentTimeMillis();
        float timeDiffX = (tempTime - millis[0]) / 1000;
        float timeDiffY = (tempTime - millis[1]) / 1000;
        float timeDiffZ = (tempTime - millis[2]) / 1000;
        Sensor s = event.sensor;
        if (s.getType() == Sensor.TYPE_ACCELEROMETER) {
            /*float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];*/
            //http://developer.android.com/intl/ru/reference/android/hardware/SensorEvent.html#values
            final float alpha = 0.8f;

            for (int i = 0; i < 3; i ++) {
                anotherGravity[i] = alpha * anotherGravity[i] + (1 - alpha) * event.values[i];
            }
            float x = event.values[0] - anotherGravity[0];
            float y = event.values[1] - anotherGravity[1];
            float z = event.values[2] - anotherGravity[2];
            boolean changedLocation = false;
            if (Math.abs(x) >= threshold) {
                xView.setText("X: " + x);
                float shift = x * timeDiffX * timeDiffX / 2;
                location[0] += shift;
                millis[0] = tempTime;
                changedLocation = true;
            }
            if (Math.abs(y) >= threshold) {
                yView.setText("Y: " + y);
                float shift = y * timeDiffY * timeDiffY / 2;
                location[1] += shift;
                millis[1] = tempTime;
                changedLocation = true;
            }
            if (Math.abs(z) >= threshold) {
                zView.setText("Z: " + z);
                float shift = z * timeDiffZ * timeDiffZ / 2;
                location[2] += shift;
                millis[2] = tempTime;
                changedLocation = true;
            }
            if (changedLocation) {
                Log.d(TAG, location[0] + " : " + location[1] + " : " + location[2]);
                xRot.setText("X: " + location[0]);
                yRot.setText("Y: " + location[1]);
                zRot.setText("Z: " + location[2]);
            }
        }
        /*if (s.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeo = event.values;
        }
        if (s.getType() == Sensor.TYPE_GRAVITY) {
            mGravity = event.values;
        }
        if (mGravity != null && mGeo != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = sensorManager.getRotationMatrix(R, I, mGravity, mGeo);
            if (success) {
                float [] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                xRot.setText("Pitch: " + Math.toDegrees(orientation[0]));
                yRot.setText("Roll: " + Math.toDegrees(orientation[1]));
                zRot.setText("Azimuth: " + Math.toDegrees(orientation[2]));
            }

        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
