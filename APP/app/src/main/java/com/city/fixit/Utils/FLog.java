package com.city.fixit.Utils;

import android.util.Log;

public class FLog {
    private static final String APP = "Fixit";
    public static void d(String TAG, String message) {
        Log.d(APP, TAG + ": " + message);
    }
    public static void e(String TAG, String message) {
        Log.e(APP, TAG + ": " + message);
    }
}
