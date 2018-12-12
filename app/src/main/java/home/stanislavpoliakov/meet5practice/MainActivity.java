package home.stanislavpoliakov.meet5practice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.constraint.ConstraintLayout;
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
    private Button bind_button;

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initItems();
        startService(MyService.newIntent(MainActivity.this)); // Сразу запускаем сервис (onStartCommand)
    }

    private void initItems() {
        bind_button = findViewById(R.id.bind_button);
        bind_button.setOnClickListener(this);
        broadcastReceiver = new MyReceiver(this); // Инициализируем объект приемника на основе Activity, в
        // которой надо менять UI-компоненты
        intentFilter = new IntentFilter("StopWatch_count"); // Фильтр для отлова сообщений
        textView13 = findViewById(R.id.textView13);
        textView12 = findViewById(R.id.textView12);
        textView11 = findViewById(R.id.textView11);
        textView33 = findViewById(R.id.textView33);
        textView32 = findViewById(R.id.textView32);
        textView31 = findViewById(R.id.textView31);
        textView41 = findViewById(R.id.textView41);
        textView21 = findViewById(R.id.textView21);
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

    private Messenger mService;
    private Messenger mClient = new Messenger(new IncomingHandler());
    static final int MSG_REGISTER_CLIENT = 1; // Исходящее сообщение для регистрации Activity в качестве клиента

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyService.MSG_GET_TIMESTAMP: // Входящее сообщение со штампом даты
                    textView21.setText(msg.getData().getString("timestamp"));
                    break;
                case MyService.MSG_TURN_THE_CLOCK: // Входящее сообщение для изменения положения в 4 Activity

                    // Полуаем параметры Layout для того элемента, который будем менять
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textView41.getLayoutParams();
                    layoutParams.circleAngle += 18; // 18 градусов для полного круга за 20 тиков
                    textView41.setLayoutParams(layoutParams); // применяем параметры
                    textView41.setText(String.valueOf(msg.arg1));
                    break;
            }
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Log.d(LOG_TAG, "Main Activity: Connected");
            mService = new Messenger(service);
            try { //Отправляем сообщение для регистрации в Service. Bundle'-а нет, потому что данных не передаем
                Message registerClient = Message.obtain(null, MSG_REGISTER_CLIENT);
                registerClient.replyTo = mClient;
                mService.send(registerClient);
            } catch (RemoteException ex) {

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onClick(View v) {
        //Log.d(LOG_TAG, "Click");
        bind_button.setEnabled(false);
        bindService(MyService.newIntent(MainActivity.this), mServiceConnection, Service.BIND_AUTO_CREATE);
        textView21.setVisibility(View.VISIBLE);
        textView41.setVisibility(View.VISIBLE);
    }


    /**
     * В этом методе меняем левую часть Activity согласно нашему секундомеру
     * @param stopWatch_count количество отмерянных секунд
     */
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



    }
}
