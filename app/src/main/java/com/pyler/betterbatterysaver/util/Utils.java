package com.pyler.betterbatterysaver.util;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import eu.chainfire.libsuperuser.Shell;

public class Utils {
    private Context mContext;
    private SharedPreferences mPrefs;

    public Utils() {
    }

    public Utils(Context context) {
        this.mContext = context;
        this.mPrefs = (context != null) ? context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_WORLD_READABLE) : null;
    }

    public void setPrefsFileWorldReadable() {
        if (mContext == null) return;
        File prefsDir = new File(mContext.getApplicationInfo().dataDir, "shared_prefs");
        File prefsFile = new File(prefsDir, Constants.PREFS_NAME + ".xml");
        if (prefsFile.exists()) {
            prefsFile.setReadable(true, false);
        }
    }

    public boolean hasRoot() {
        return Shell.SU.available();
    }

    public boolean getBatterBatterySaver() {
        if (mPrefs == null) return false;
        return mPrefs.getBoolean("better_battery_saver", false);
    }

    public void setBetterBatterySaver(boolean mode) {
        if (mPrefs == null) return;
        mPrefs.edit().putBoolean("better_battery_saver", mode).apply();
    }

    public void setChargingMode(boolean mode) {
        if (mPrefs == null) return;
        mPrefs.edit().putBoolean("charning", mode).apply();
    }

    public void setBatteryLevel(int level) {
        if (mPrefs == null) return;
        mPrefs.edit().putInt("battery_level", level).apply();
    }

    public int getBatteryLevelThreshold() {
        if (mPrefs == null) return -1;
        return mPrefs.getInt("battery_level_threshold", 15);
    }

    public int getBatteryLevelThreshold(String packageName) {
        if (mPrefs == null) return -1;
        return mPrefs.getInt(getKeyForPackage(packageName, "battery_level_threshold"), 15);
    }

    public Set<String> getStartMode() {
        if (mPrefs == null) new HashSet<String>();
        return mPrefs.getStringSet("start_mode", new HashSet<String>());
    }

    public Set<String> getExitMode() {
        if (mPrefs == null) new HashSet<String>();
        return mPrefs.getStringSet("exit_mode", new HashSet<String>());
    }

    public boolean areHeadsUpNotificationsEnabled() {
        if (mPrefs == null) return false;
        return mPrefs.getBoolean("headsup_notification", false);
    }

    public boolean areInfoNotificationsEnabled() {
        if (mPrefs == null) return false;
        return mPrefs.getBoolean("info_notification", false);
    }

    public boolean getBooleanPreference(String key) {
        if (mPrefs == null) return false;
        return mPrefs.getBoolean(key, false);
    }

    public int getIntPreference(String key) {
        if (mPrefs == null) return -1;
        return mPrefs.getInt(key, -1);
    }

    public boolean canWriteSystemSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return android.provider.Settings.System.canWrite(mContext);
        } else {
            return true;
        }
    }

    public String getKeyForPackage(String packageName, String key) {
        return packageName + "_" + key;
    }

    public String getCurrentPackageName() {
        String packageName = AndroidAppHelper.currentPackageName();
        if (packageName == null || packageName.isEmpty()) {
            packageName = "android";
        }
        return packageName;
    }

    public boolean shouldHook(XSharedPreferences prefs, String packageName, String key) {
        if (prefs == null) return false;
        prefs.reload();
        ;
        String packageKey = getKeyForPackage(packageName, key);
        int currentBatteryLevel = prefs.getInt("battery_level", -1);
        boolean isCharging = prefs.getBoolean("charging", false);
        int batteryLevelThreshold = prefs.getInt(getKeyForPackage(packageName, "battery_level_threshold"), 15);
        if (currentBatteryLevel == -1 || isCharging) return false;
        if (currentBatteryLevel > batteryLevelThreshold) return false;
        boolean enabled = prefs.getBoolean(packageKey, false);
        return enabled;
    }

    public boolean shouldHook(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam, String key) {
        return shouldHook(prefs, lpparam.packageName, key);
    }

}
