package com.pyler.betterbatterysaver.util;

import android.util.Log;


public class Logger {
    public static void i(String tag, String message) {
        Log.i(Constants.APP_NAME, tag + ": " + message);
    }
}
