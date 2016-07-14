package com.pyler.betterbatterysaver.hooks;

import android.net.wifi.WifiManager;

import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WifiController {
    public static final String TAG = "WifiController";
    public static final String KEY = "disable_wifi_control";

    public static void init(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!new Utils(null).shouldHook(prefs, lpparam, KEY)) return;


        try {
            XposedBridge.hookAllMethods(WifiManager.class, "setWifiEnabled", XC_MethodReplacement.returnConstant(false));
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}
