package com.pyler.betterbatterysaver.hooks;

import android.app.Activity;

import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AppActivityController {
    public static final String TAG = "AppActivityController";
    public static final String KEY = "disable_app_launch";

    public static void init(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!new Utils().shouldHook(prefs, lpparam, KEY)) return;

        try {
            XposedBridge.hookAllMethods(Activity.class, "onStart", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param)
                        throws Throwable {
                    Activity activity = (Activity) param.thisObject;
                    activity.finish();
                }
            });
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}
