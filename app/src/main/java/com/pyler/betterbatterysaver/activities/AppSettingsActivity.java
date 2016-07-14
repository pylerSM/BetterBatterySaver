package com.pyler.betterbatterysaver.activities;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.pyler.betterbatterysaver.R;
import com.pyler.betterbatterysaver.util.Utils;

import java.util.ArrayList;

public class AppSettingsActivity extends PreferenceActivity {
    public static Context mContext;
    public static SharedPreferences mPrefs;
    public static Utils mUtils;
    private static String packageName;
    private static String appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mUtils = new Utils(this);
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();


        Intent intent = getIntent();
        if (intent == null) return;
        packageName = intent.getStringExtra("package");
        appName = intent.getStringExtra("app");

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(appName);
        }
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Settings()).commit();
    }

    @SuppressWarnings("deprecation")
    public static class Settings extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager()
                    .setSharedPreferencesMode(MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.app_settings);
            mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            final PreferenceScreen appSettings = (PreferenceScreen) findPreference("app_settings");

            ArrayList<Preference> list = getPreferenceList(appSettings, new ArrayList<Preference>());
            for (Preference p : list) {
                String newKey = getKeyForPackage(p.getKey());
                p.setKey(newKey);
                if (p instanceof CheckBoxPreference) {
                    ((CheckBoxPreference) p).setChecked(mPrefs.getBoolean(newKey, false));
                }
            }

            Preference batterySaving = (Preference) findPreference(getKeyForPackage("battery_saving"));
            final Preference batteryLevelThreshold = (Preference) findPreference(getKeyForPackage("battery_level_threshold"));
            final PreferenceCategory batterySavingSettings = (PreferenceCategory) findPreference("battery_saving_settings");
            boolean batterySavingValue = mUtils.getBooleanPreference(getKeyForPackage("battery_saving"));
            batteryLevelThreshold.setEnabled(batterySavingValue);
            if (!batterySavingValue) {
                appSettings.removePreference(batterySavingSettings);
            }
            batterySaving.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(
                        Preference preference, Object newValue) {
                    boolean set = (boolean) newValue;
                    batteryLevelThreshold.setEnabled(set);
                    if (set) {
                        appSettings.addPreference(batterySavingSettings);
                    } else {
                        appSettings.removePreference(batterySavingSettings);
                    }
                    return true;
                }
            });

            int batteryLevel = mUtils.getBatteryLevelThreshold(packageName);
            String batteryLevelThresholdTitle = getString(R.string.battery_level_threshold, batteryLevel);
            batteryLevelThreshold.setTitle(batteryLevelThresholdTitle);
            batteryLevelThreshold.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(
                        Preference preference, Object newValue) {
                    int batteryLevel = (int) newValue;
                    String title = getString(R.string.battery_level_threshold, batteryLevel);
                    preference.setTitle(title);
                    return true;
                }
            });

            // category

        }


        @Override
        public void onPause() {
            super.onPause();

            mUtils.setPrefsFileWorldReadable();
        }

        private String getKeyForPackage(String key) {
            return mUtils.getKeyForPackage(packageName, key);
        }

        private ArrayList<Preference> getPreferenceList(Preference p, ArrayList<Preference> list) {
            if (p instanceof PreferenceCategory || p instanceof PreferenceScreen) {
                PreferenceGroup pGroup = (PreferenceGroup) p;
                int pCount = pGroup.getPreferenceCount();
                for (int i = 0; i < pCount; i++) {
                    getPreferenceList(pGroup.getPreference(i), list); // recursive call
                }
            } else {
                list.add(p);
            }
            return list;
        }
    }
}
