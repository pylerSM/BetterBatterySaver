package com.pyler.betterbatterysaver.util;


import android.os.Build;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class SystemSettingsEditor {
    public static final String SECURE = "secure";
    public static final String GLOBAL = "global";
    public static final String LOW_POWER = "low_power";
    public static final String MOBILE_DATA = "mobile_data";
    public static final String AIRPLANE_MODE = "airplane_mode_on";
    private static final String TAG = "SystemSettingsEditor";

    public static void setBoolean(String namespace, String key, boolean state) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) return;
        if (!Shell.SU.available()) {
            Logger.i(TAG, "Cant set system settings, no root");
            return;
        }
        int value = (state) ? 1 : 0;
        String command = String.format("settings put %s %s %d", namespace, key, value);
        Shell.SU.run(command);
    }

    public static void setString(String namespace, String key, String value) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) return;
        if (!Shell.SU.available()) {
            Logger.i(TAG, "Cant set system settings, no root");
            return;
        }
        String command = String.format("settings put %s %s %s", namespace, key, value);
        Shell.SU.run(command);
    }

    public static String get(String namespace, String key) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) return null;
        if (!Shell.SU.available()) {
            Logger.i(TAG, "Cant get system settings, no root");
            return null;
        }
        String command = String.format("settings get %s %s", namespace, key);
        List<String> out = Shell.SU.run(command);
        if (out == null) return null;
        return out.toString();
    }

    public static void clear(String namespace, String key) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) return;
        if (!Shell.SU.available()) {
            Logger.i(TAG, "Cant clear system settings, no root");
            return;
        }
        String command;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            command = String.format("settings delete %s %s", namespace, key);
        } else {
            command = String.format("settings put %s %s %s", namespace, key, "");
        }
        Shell.SU.run(command);
    }
}
