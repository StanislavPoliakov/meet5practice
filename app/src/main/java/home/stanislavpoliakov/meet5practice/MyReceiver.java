package home.stanislavpoliakov.meet5practice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    private TextViewsCallback mTextViewsCallback;

    public MyReceiver(TextViewsCallback textViewsCallback) {
        this.mTextViewsCallback = textViewsCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mTextViewsCallback.changeViews(intent.getStringExtra("Count"));
    }
}
