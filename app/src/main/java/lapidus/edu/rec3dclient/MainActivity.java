package lapidus.edu.rec3dclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.kircherelectronics.gyroscopeexplorer.activity.filter.GyroscopeOrientation;

public class MainActivity extends Activity {

    private GyroscopeOrientation gyroscopeOrientation;
    private Runnable orientationThread;
    private Handler handler;
    private final static String TAG = MainActivity.class.getSimpleName();
    private float [] vOrientation = new float[3];
    private TextView X, Y, Z;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        X = (TextView) findViewById(R.id.X);
        Y = (TextView) findViewById(R.id.Y);
        Z = (TextView) findViewById(R.id.Z);
    }

    public void onResume()
    {
        super.onResume();

        //readPrefs();
        reset();

        gyroscopeOrientation.onResume();

        handler.post(orientationThread);
    }

    private void reset() {
        gyroscopeOrientation = new GyroscopeOrientation(this);
        handler = new Handler();
        orientationThread = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 100);
                vOrientation = gyroscopeOrientation.getOrientation();
                updateText();
            }
        };
    }

    private void updateText() {
        X.setText(Math.toDegrees(vOrientation[0]) + "");
        Y.setText(Math.toDegrees(vOrientation[1]) + "");
        Z.setText(Math.toDegrees(vOrientation[2]) + "");
    }

    public void onPause()
    {
        super.onPause();

        gyroscopeOrientation.onPause();

        handler.removeCallbacks(orientationThread);
    }
}
