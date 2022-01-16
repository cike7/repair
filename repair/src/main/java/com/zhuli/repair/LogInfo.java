package com.zhuli.repair;
import android.util.Log;

public class LogInfo {
    public static <T> void e(T msg) {
        Log.e("Repair", "" + msg);
    }
}
