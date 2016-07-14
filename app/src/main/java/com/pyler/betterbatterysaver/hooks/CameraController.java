package com.pyler.betterbatterysaver.hooks;

import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class CameraController {
    public static final String TAG = "CameraController";
    public static final String KEY = "disable_camera_control";

    public static void init(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
        if (!new Utils().shouldHook(prefs, lpparam, KEY)) return;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedBridge.hookAllMethods(CameraManager.class, "openCamera", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param)
                            throws Throwable {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            param.setThrowable(new CameraAccessException(CameraAccessException.CAMERA_DISABLED));
                        }
                    }
                });
            }
            XposedBridge.hookAllMethods(Camera.class, "open", XC_MethodReplacement.returnConstant(null));
        } catch (Throwable t) {
            Logger.i(TAG, t.getMessage());
        }
    }
}
