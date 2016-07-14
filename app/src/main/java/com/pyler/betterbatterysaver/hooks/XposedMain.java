package com.pyler.betterbatterysaver.hooks;

import com.pyler.betterbatterysaver.util.Constants;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedMain implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private static XSharedPreferences prefs;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        prefs = new XSharedPreferences(Constants.PACKAGE_NAME);
        prefs.makeWorldReadable();

    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!prefs.getBoolean("app_battery_saving", false)) return;

        /*ActivityController.init(prefs, lpparam);
        AppActivityController.init(prefs, lpparam);
        AppBrightnessController.init(prefs, lpparam);
        AlarmController.init(prefs, lpparam);
        AppThemeController.init(prefs, lpparam);
        BluetoothController.init(prefs, lpparam);
        CameraController.init(prefs, lpparam);
        ConnectionController.init(prefs, lpparam);*/
        NotificationController.init(prefs, lpparam);
        /*NotificationLightController.init(prefs, lpparam)
        SelfHook.init(prefs, lpparam);
        ServiceController.init(prefs, lpparam);
        VibrationController.init(prefs, lpparam);
        WakelockController.init(prefs, lpparam);
        WifiController.init(prefs, lpparam);
        WifiApController.init(prefs, lpparam);*/
    }
}

