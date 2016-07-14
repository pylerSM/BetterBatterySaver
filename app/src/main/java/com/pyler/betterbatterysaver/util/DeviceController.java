package com.pyler.betterbatterysaver.util;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Dávid Bolvanský on 10.7.2016.
 */
public class DeviceController {
    private Context mContext;
    private Utils mUtils;

    public DeviceController(Context context) {
        this.mContext = context;
        this.mUtils = new Utils(mContext);
    }

    public void setMobileDataMode(boolean mode) {
        SystemSettingsEditor.setBoolean(SystemSettingsEditor.GLOBAL, SystemSettingsEditor.MOBILE_DATA, mode);
    }

    public boolean isPowerSaveMode() {
        if (mContext == null) return false;
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return pm.isPowerSaveMode();
        } else {
            return false;
        }
    }

    public void setPowerSaveMode(boolean mode) {
        SystemSettingsEditor.setBoolean(SystemSettingsEditor.GLOBAL, SystemSettingsEditor.LOW_POWER, mode);
    }

    public void setWiFiMode(boolean mode) {
        if (mContext == null) return;
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(mode);
    }

    public void setGPSMode(boolean mode) {
        if (mContext == null) return;
        String oldSetting = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        String newSetting;
        if (oldSetting.isEmpty()) {
            newSetting = "gps";
        } else {
            newSetting = oldSetting + ",gps";
        }
        if (mode) {
            SystemSettingsEditor.setString(SystemSettingsEditor.SECURE, SystemSettingsEditor.MOBILE_DATA, newSetting);
        } else {
            SystemSettingsEditor.clear(SystemSettingsEditor.SECURE, SystemSettingsEditor.MOBILE_DATA);
        }
    }

    public void setBluetoothMode(boolean mode) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) return;
        if (mode) {
            btAdapter.enable();
        } else {
            btAdapter.disable();
        }
    }

    public void setNFCMode(boolean mode) {
        if (!Shell.SU.available()) return;
        if (mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Shell.SU.run("service call nfc 6");
            } else {
                Shell.SU.run("service call nfc 5");
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Shell.SU.run("service call nfc 5");
            } else {
                Shell.SU.run("service call nfc 4");
            }
        }
    }

    public void setAutoSyncMode(boolean mode) {
        ContentResolver.setMasterSyncAutomatically(mode);
    }

    public void setBrightnessMode(boolean mode) {
        if (mContext == null) return;
        if (!mUtils.canWriteSystemSettings()) return;
        if (mode) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        } else {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, 200);
        }
    }

    public void turnDeviceOff() {
        if (!Shell.SU.available()) return;
        Shell.SU.run("am start -a android.intent.action.ACTION_REQUEST_SHUTDOWN"); //reboot -p
    }

    public void setScreenMode(boolean mode) {
        if (mContext == null) return;
        if (!Shell.SU.available()) return;
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = powerManager.isScreenOn();
        if (mode) {
            if (!isScreenOn) {
                Shell.SU.run("input keyevent 26");
            }
        } else {
            if (isScreenOn) {
                Shell.SU.run("input keyevent 26");
            }
        }
    }

    public void setScreenTimeout(boolean mode) {
        if (mContext == null) return;
        if (!mUtils.canWriteSystemSettings()) return;
        int screenTimeout;
        if (mode) {
            screenTimeout = mUtils.getIntPreference("screen_timeout_on");
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, screenTimeout);
        } else {
            screenTimeout = mUtils.getIntPreference("screen_timeout_off");
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, screenTimeout);
        }
    }

    public void setAirplaneMode(boolean mode) {
        SystemSettingsEditor.setBoolean(SystemSettingsEditor.GLOBAL, SystemSettingsEditor.AIRPLANE_MODE, mode);
    }

    public void setServiceMode(String name, boolean mode) {
        if (!Shell.SU.available()) return;
        if (mode) {
            Shell.SU.run("pm enable " + name);
        } else {
            Shell.SU.run("pm disable " + name);
        }
    }

    public void setDozeMode(boolean mode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
        if (!Shell.SU.available()) return;
        boolean enabled = "1".equals(Shell.SU.run("dumpsys deviceidle enabled").toString());
        if (mode) {
            Shell.SU.run("dumpsys deviceidle force-idle");
        } else {
            if (enabled) {
                Shell.SU.run("dumpsys deviceidle disable");
            }
        }
    }

}
