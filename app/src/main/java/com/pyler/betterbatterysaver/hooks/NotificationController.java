package com.pyler.betterbatterysaver.hooks;

import android.app.NotificationManager;

import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class NotificationController {
    public static final String TAG = "NotificationController";
    public static final String KEY = "disable_notifications";

    public static void init(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!new Utils(null).shouldHook(prefs, lpparam, KEY)) return;

        try {
            XposedBridge.hookAllMethods(NotificationManager.class, "notify", XC_MethodReplacement.returnConstant(null));
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}
