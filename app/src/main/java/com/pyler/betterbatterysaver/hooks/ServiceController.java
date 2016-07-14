package com.pyler.betterbatterysaver.hooks;

import android.os.Build;

import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ServiceController {
    public static final String TAG = "ServiceController";
    public static final String KEY = "disable_services";
    public static final String SERVICE = "com.android.server.am.ActiveServices";
    public static final String SERVICE_OLD = "com.android.server.am.ActivityManagerService";

    public static void init(final XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!"android".equals(lpparam.packageName)) return;

        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param)
                    throws Throwable {
                Utils utils = new Utils();
                String packageName = utils.getCurrentPackageName();
                if (utils.shouldHook(prefs, packageName, KEY)) {
                    param.setResult(null);
                }
            }
        };


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                XposedBridge.hookAllMethods(XposedHelpers.findClass(SERVICE, lpparam.classLoader), "startServiceLocked", hook);

            } else {
                XposedBridge.hookAllMethods(XposedHelpers.findClass(SERVICE_OLD, lpparam.classLoader), "startServiceLocked", hook);
            }
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}