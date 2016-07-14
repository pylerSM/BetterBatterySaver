package com.pyler.betterbatterysaver.hooks;

import android.app.Activity;
import android.view.WindowManager;

import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Dávid Bolvanský on 13.7.2016.
 */
public class AppBrightnessController {
    public static final String TAG = "AppBrightnessController";
    public static final String KEY = "set_lowest_app_brightness";

    public static void init(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!new Utils().shouldHook(prefs, lpparam, KEY)) return;


        try {
            XposedBridge.hookAllMethods(Activity.class, "onResume", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param)
                        throws Throwable {
                    Activity activity = (Activity) param.thisObject;
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.screenBrightness = 0f;
                    activity.getWindow().setAttributes(lp);
                }
            });
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}
