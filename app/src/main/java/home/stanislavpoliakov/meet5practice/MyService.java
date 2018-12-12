package home.stanislavpoliakov.meet5practice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private final String LOG_TAG = "meet5_logs";
    private long count;
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopWatch();
        return START_NOT_STICKY;
    }

    private void stopWatch() {
        /*Intent broadcastIntent = new Intent("StopWatch_count");
        broadcastIntent.putExtra("Count", String.valueOf(count));
        sendBroadcast(broadcastIntent);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        count += 1;
                        Intent broadcastIntent = new Intent("StopWatch_count");
                        broadcastIntent.putExtra("Count", String.valueOf(count));
                        sendBroadcast(broadcastIntent);
                        //Log.d(LOG_TAG, "Count: " + String.valueOf(count));
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {

                    }
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MyService.class);
    }
}
