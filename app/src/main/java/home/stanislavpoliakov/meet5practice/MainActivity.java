package home.stanislavpoliakov.meet5practice;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextViewsCallback {
    private final String LOG_TAG = "meet5_logs";

    private TextView textView11, textView12, textView13; // Linear Layout (Horizontal) #1
    private TextView textView21; // Relative Layout #2
    private TextView textView31, textView32, textView33; // Linear Layout (Vertical) #3
    private TextView textView41; // Constraint Layout #4

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initItems();
        startService(MyService.newIntent(MainActivity.this));
    }

    private void initItems() {
        Button bind_button = findViewById(R.id.bind_button);
        broadcastReceiver = new MyReceiver(this);
        intentFilter = new IntentFilter("StopWatch_count");
        textView13 = findViewById(R.id.textView13);
        textView12 = findViewById(R.id.textView12);
        textView11 = findViewById(R.id.textView11);
        textView33 = findViewById(R.id.textView33);
        textView32 = findViewById(R.id.textView32);
        textView31 = findViewById(R.id.textView31);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void changeViews(String stopWatch_count) {
       //Log.d(LOG_TAG, "Main Activity: change views: " + stopWatch_count);

       textView13.setText(stopWatch_count.substring(stopWatch_count.length() - 1));
       textView33.setText(stopWatch_count.substring(stopWatch_count.length() - 1));

       if (Integer.parseInt(stopWatch_count) >= 10) {
           textView12.setText(stopWatch_count.substring(stopWatch_count.length() - 2, stopWatch_count.length() - 1));
           textView32.setText(stopWatch_count.substring(stopWatch_count.length() - 2, stopWatch_count.length() - 1));
       }
        if (Integer.parseInt(stopWatch_count) >= 100) {
            textView11.setText(stopWatch_count.substring(0, stopWatch_count.length() - 2));
            textView31.setText(stopWatch_count.substring(0, stopWatch_count.length() - 2));
        }

//        textView31.setText(String.valueOf(count_small));
//
//        textView12.setText(String.valueOf(count_medium));
//        textView32.setText(String.valueOf(count_medium));
//
//        textView13.setText(String.valueOf(count_big));
//        textView33.setText(String.valueOf(count_big));

    }
}
