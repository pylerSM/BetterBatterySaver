package com.pyler.betterbatterysaver.hooks;

import android.net.wifi.WifiManager;

import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WifiApController {
    public static final String TAG = "WifiApController";
    public static final String KEY = "disable_wifi_ap_control";

    public static void init(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!new Utils().shouldHook(prefs, lpparam, KEY)) return;

        try {
            XposedBridge.hookAllMethods(WifiManager.class, "setWifiApEnabled", XC_MethodReplacement.returnConstant(false));
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}
