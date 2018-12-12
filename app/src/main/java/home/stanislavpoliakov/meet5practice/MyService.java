package home.stanislavpoliakov.meet5practice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.TimeUnit;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MyService extends Service {
    private final String LOG_TAG = "meet5_logs";
    private long count;
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopWatch(); // запускаем метод с тредом на секундомер
        return START_NOT_STICKY;
    }

    /**
     * Метод секундомера. Свой Thread
     */
    private void stopWatch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        count += 1;
                        Intent broadcastIntent = new Intent("StopWatch_count");
                        broadcastIntent.putExtra("Count", String.valueOf(count));
                        sendBroadcast(broadcastIntent); // кидаем Broadcast
                        //Log.d(LOG_TAG, "Count: " + String.valueOf(count));
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {

                    }
                }
            }
        }).start();
    }

    // Аналогично Activity
    private Messenger mService = new Messenger(new IncomingHandler());
    private Messenger mClient;
    static final int MSG_GET_TIMESTAMP = 1;
    static final int MSG_TURN_THE_CLOCK = 2;

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MainActivity.MSG_REGISTER_CLIENT) mClient = msg.replyTo;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        timestampAndClock();
        //Log.d(LOG_TAG, "Service: Binded");
        return mService.getBinder(); // возвращаем в вызов ссылку на IBinder для Service
    }

    /**
     * Метод для штампа и передачи данных для "стрелки". Свой Thread
     */
    private void timestampAndClock() {
        new Thread(new Runnable() {
            private int turnTick = 0;

            @Override
            public void run() {
                while (true) {
                    try {
                        String timestamp = String.valueOf(Calendar.getInstance().getTime()); // Забираем штамп из календаря
                        Message timestampMessage = Message.obtain(null, MSG_GET_TIMESTAMP);
                        Bundle bundle = new Bundle();
                        bundle.putString("timestamp", timestamp);
                        timestampMessage.setData(bundle);
                        if (mClient != null) {
                            try {
                                mClient.send(timestampMessage);
                            } catch (RemoteException ex) {

                            }
                        }
                        turnTick += 1;
                        turnTick = (turnTick == 60) ? 0 : turnTick; // Сбрасыаем стрелку после 60
                        if ((turnTick % 3) == 0) {
                            Message cloclMessage = Message.obtain(null, MSG_TURN_THE_CLOCK);
                            cloclMessage.arg1 = turnTick; // целое передаем как аргумент, без Bundle'-а
                            try {
                                mClient.send(cloclMessage);
                            } catch (RemoteException ex) {

                            }
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {

                    }
                }
            }
        }).start();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MyService.class);
    }
}
