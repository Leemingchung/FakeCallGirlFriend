package com.example.fakecall;

import android.content.Context;
import android.content.Intent;
//import android.support.v4.content.WakefulBroadcastReceiver;

import androidx.legacy.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {
    public void onReceive(Context arg0, Intent arg1) {
        WakeLocker.acquire(arg0);
        Intent i = new Intent();
        i.setClassName(arg0, "com.example.fakecall.CallActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        arg0.startActivity(i);

    }
}
