package com.pyler.betterbatterysaver.hooks;

import android.content.ContentResolver;

import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AutoSyncController {
    public static final String TAG = "AutoSyncController";
    public static final String KEY = "disable_auto_sync_control";

    public static void init(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!new Utils().shouldHook(prefs, lpparam, KEY)) return;

        try {
            XposedBridge.hookAllMethods(ContentResolver.class, "setMasterSyncAutomatically", XC_MethodReplacement.returnConstant(null));
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}
