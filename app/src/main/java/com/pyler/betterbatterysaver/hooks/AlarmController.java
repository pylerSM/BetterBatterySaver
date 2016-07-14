package com.pyler.betterbatterysaver.hooks;

import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AlarmController {
    public static final String TAG = "AlarmController";
    public static final String KEY = "disable_alarms";
    public static final String SERVICE = "com.android.server.AlarmManagerService";

    public static void init(final XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!"android".equals(lpparam.packageName)) return;


        try {
            XposedBridge.hookAllMethods(XposedHelpers.findClass(SERVICE, lpparam.classLoader), "triggerAlarmsLocked", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param)
                        throws Throwable {
                    Utils utils = new Utils();
                    String packageName = utils.getCurrentPackageName();
                    if (utils.shouldHook(prefs, packageName, KEY)) {
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}

