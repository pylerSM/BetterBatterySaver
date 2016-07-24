package com.pyler.betterbatterysaver.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.lang.reflect.Constructor;

import eu.chainfire.libsuperuser.Shell;

public class RootSharedPreferences {
    private SharedPreferences.Editor sharedPreferencesEditor;
    private SharedPreferences sharedPreferences;
    private File sharedPreferencesFile;

    public RootSharedPreferences(Context context, String packageName, String prefFile) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
            File prefsDir = new File(ai.dataDir, "shared_prefs");
            sharedPreferencesFile = new File(prefsDir, prefFile);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
    }

    public RootSharedPreferences(Context context, String packageName) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
            File prefsDir = new File(ai.dataDir, "shared_prefs");
            sharedPreferencesFile = new File(prefsDir, packageName + "_preferences.xml");
        } catch (PackageManager.NameNotFoundException ignored) {
        }
    }

    public RootSharedPreferences(String prefFile) {

        sharedPreferencesFile = new File(prefFile);
    }

    public RootSharedPreferences(File prefFile) {
        sharedPreferencesFile = prefFile;
    }

    public boolean init() {
        if (sharedPreferencesFile == null) return false;
        if (!sharedPreferencesFile.canRead() || !sharedPreferencesFile.canWrite()) {
            if (!Shell.SU.available()) {
                return false;
            }

            Shell.SU.run("chmod 777 " + sharedPreferencesFile);
        }

        try {
            Class sharedPreferencesImpl = Class.forName("android.app.SharedPreferencesImpl");
            Constructor prefsConstructor = sharedPreferencesImpl.getDeclaredConstructor(File.class, int.class);
            prefsConstructor.setAccessible(true);

            sharedPreferences = (SharedPreferences) prefsConstructor.newInstance(sharedPreferencesFile, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
            sharedPreferencesEditor = (SharedPreferences.Editor) sharedPreferencesImpl.getMethod("edit").invoke(sharedPreferences);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public SharedPreferences.Editor getEditor() {
        return sharedPreferencesEditor;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
