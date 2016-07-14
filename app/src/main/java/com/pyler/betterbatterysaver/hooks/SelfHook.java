package com.pyler.betterbatterysaver.hooks;

import com.pyler.betterbatterysaver.util.Constants;
import com.pyler.betterbatterysaver.util.Logger;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class SelfHook {
    public static final String TAG = "SelfHook";

    public static void init(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!Constants.PACKAGE_NAME.equals(lpparam.packageName)) {
            return;
        }
        try {
            XposedHelpers.findAndHookMethod(Constants.PACKAGE_NAME + ".PreferencesActivity", lpparam.classLoader, "isXposedModuleEnabled", XC_MethodReplacement.returnConstant(true));
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}
