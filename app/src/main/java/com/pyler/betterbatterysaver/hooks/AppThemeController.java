package com.pyler.betterbatterysaver.hooks;

import android.app.Activity;

import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AppThemeController {
    public static final String TAG = "AppThemeController";
    public static final String KEY = "set_dark_app_theme";

    public static void init(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!new Utils().shouldHook(prefs, lpparam, KEY)) return;

        try {
            XposedBridge.hookAllMethods(Activity.class, "onCreate", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param)
                        throws Throwable {
                    Activity activity = (Activity) param.thisObject;
                    activity.setTheme(android.R.style.Theme_DeviceDefault);
                }
            });
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}
