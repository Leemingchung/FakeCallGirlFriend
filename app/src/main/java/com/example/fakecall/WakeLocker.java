package com.example.fakecall;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public abstract class WakeLocker {
    private static WakeLock wakeLock;

    @SuppressLint("InvalidWakeLockTag")
    public static void acquire(Context ctx) {
        if (wakeLock != null) {
            wakeLock.release();
        }
        wakeLock = ((PowerManager) ctx.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.FULL_WAKE_LOCK, "com.techx.fakecallprank");
        ((KeyguardManager) ctx.getSystemService(Context.KEYGUARD_SERVICE)).newKeyguardLock("com.techx.fakecallprank").disableKeyguard();
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null) {
            wakeLock.release();
        }
        wakeLock = null;
    }
}