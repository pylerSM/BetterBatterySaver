package com.pyler.betterbatterysaver.hooks;

import android.app.Notification;

import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class NotificationLightController {
    public static final String TAG = "NotificationLightController";
    public static final String KEY = "disable_notification_lights";

    public static void init(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!new Utils().shouldHook(prefs, lpparam, KEY)) return;

        try {
            XposedBridge.hookAllMethods(Notification.Builder.class, "setLights", XC_MethodReplacement.returnConstant(null));
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}
