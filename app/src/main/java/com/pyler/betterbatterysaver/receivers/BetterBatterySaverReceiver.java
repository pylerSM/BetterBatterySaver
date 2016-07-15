package com.pyler.betterbatterysaver.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;

import com.pyler.betterbatterysaver.PreferencesActivity;
import com.pyler.betterbatterysaver.R;
import com.pyler.betterbatterysaver.services.BatteryMonitorService;
import com.pyler.betterbatterysaver.util.Constants;
import com.pyler.betterbatterysaver.util.DeviceController;
import com.pyler.betterbatterysaver.util.Logger;
import com.pyler.betterbatterysaver.util.Utils;

import java.util.Set;

public class BetterBatterySaverReceiver extends BroadcastReceiver {
    private static final String TAG = "BetterBatterySaverReceiver";
    private static final int CONFIRM_NOTIFICATION_ID = 0;
    private static final int INFO_CONFIRMATION_ID = 1;
    private Utils mUtils;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mUtils = new Utils(context);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            context.startService(new Intent(context, BatteryMonitorService.class));
            Logger.i(TAG, "BatteryMonitorService has started");
            return;
        }

        if (PowerManager.ACTION_POWER_SAVE_MODE_CHANGED.equals(intent.getAction())) {
            Set<String> startMode = mUtils.getStartMode();
            if (!startMode.contains("android_saver")) return;
            if (new DeviceController(context).isPowerSaveMode()) {
                setBetterBatterySaver(true);
                Logger.i(TAG, "Bettery Battery Saver started since Android Battery Saver was turned on");
            } else {
                setBetterBatterySaver(false);
            }
            return;
        }

        if (Constants.INTENT_BETTER_BATTERY_SAVER_START.equals(intent.getAction())) {
            cancelConfirmStartNotification();
            manageBetterBatterySaver(true);
            Logger.i(TAG, "INTENT_BETTER_BATTERY_SAVE_START: Turning on");
            return;
        }

        if (Constants.INTENT_BETTER_BATTERY_SAVER_STOP.equals(intent.getAction())) {
            cancelConfirmStartNotification();
            manageBetterBatterySaver(false);
            Logger.i(TAG, "INTENT_BETTER_BATTERY_SAVE_START: Turning off");
            return;
        }


        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();

            if (bundle == null) {
                Logger.i(TAG, "ACTION_BATTERY_CHANGED intent has no extras");
                return;
            }
            //*****
            int plugged = intent.getIntExtra("plugged", -1);
            boolean isCharging = isCharging(plugged);
            mUtils.setChargingMode(isCharging);
            int level = intent.getIntExtra("level", -1);
            mUtils.setBatteryLevel(level);
            Logger.i(TAG, "Battery level " + level + ", charging " + isCharging);

            ///
            int batteryLevelThreshold = mUtils.getBatteryLevelThreshold();
            if (level == batteryLevelThreshold) {
                setBetterBatterySaver(!isCharging(plugged));

            } else if (level > batteryLevelThreshold) {
                // 50 / 12 / 6
                if (mUtils.getBatterBatterySaver()) {
                    setBetterBatterySaver(false);
                }
            } else if (level < batteryLevelThreshold) {
                // 7 / 12 / 52
                if (!mUtils.getBatterBatterySaver()) {
                    setBetterBatterySaver(true);
                }
            }


            //Toast.makeText(context, "Battery Receiver: " + level + "\tPlugged" + isCharging(plugged) + " Start: " + batteryLevelThreshold + "Raw: " + level, Toast.LENGTH_LONG).show();
        }

    }

    private boolean isCharging(int plugged) {
        return (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB);
    }

    public void setBetterBatterySaver(boolean mode) {
        Set<String> runMode;
        if (mode) {
            runMode = mUtils.getStartMode();
        } else {
            runMode = mUtils.getExitMode();
        }
        if (runMode.contains("auto")) {
            manageBetterBatterySaver(mode);
        } else if (runMode.contains("notification")) {
            showConfirmStartNotification(mode);
        } else {
            // nothing
        }
    }

    private void manageBetterBatterySaver(boolean mode) {
        mUtils.setBetterBatterySaver(mode);
        DeviceController device = new DeviceController(mContext);
        if (mode) {
            //turn off
            if (mUtils.getBooleanPreference("turn_wifi_off")) {
                device.setWiFiMode(false);
                Logger.i(TAG, "Wi-Fi turned off");
            }

            if (mUtils.getBooleanPreference("turn_bluetooth_off")) {
                device.setBluetoothMode(false);
                Logger.i(TAG, "Bluetooth turned off");
            }

            if (mUtils.getBooleanPreference("turn_auto_sync_off")) {
                device.setAutoSyncMode(false);
                Logger.i(TAG, "Auto Sync turned off");
            }

            if (mUtils.getBooleanPreference("set_screen_timeout_off")) {
                device.setScreenTimeout(false);
                Logger.i(TAG, "Screen timeout turned off");
            }

            if (mUtils.getBooleanPreference("turn_brightness_off")) {
                device.setBrightnessMode(false);
                Logger.i(TAG, "Brightness turned off");
            }

            if (mUtils.getBooleanPreference("turn_wifi-ap_off")) {
                device.setWiFiApMode(false);
                Logger.i(TAG, "Wi-Fi AP turned off");
            }

            // Root features
            if (mUtils.hasRoot()) {
                if (mUtils.getBooleanPreference("turn_gps_off")) {
                    device.setGPSMode(false);
                    Logger.i(TAG, "GPS turned off");
                }
                if (mUtils.getBooleanPreference("turn_nfc_off")) {
                    device.setNFCMode(false);
                    Logger.i(TAG, "NFC turned off");
                }
                if (mUtils.getBooleanPreference("turn_device_off")) {
                    device.turnDeviceOff();
                    Logger.i(TAG, "Device turned off");
                }

                if (mUtils.getBooleanPreference("turn_screen_off")) {
                    device.setScreenMode(false);
                    Logger.i(TAG, "Screen turned off");
                }

                if (mUtils.getBooleanPreference("turn_mobile_data_off")) {
                    device.setMobileDataMode(false);
                    Logger.i(TAG, "Mobile data turned off");
                }

                if (mUtils.getBooleanPreference("turn_android_saver_on")) {
                    device.setPowerSaveMode(true);
                    Logger.i(TAG, "Android Battery Saver turned on");
                }

                if (mUtils.getBooleanPreference("turn_airplane_mode_on")) {
                    device.setAirplaneMode(true);
                    Logger.i(TAG, "Airplane mode turned on");
                }

                if (mUtils.getBooleanPreference("turn_doze_on")) {
                    device.setDozeMode(true);
                    Logger.i(TAG, "Doze turned on");
                }
            }
        } else {
            if (mUtils.getBooleanPreference("turn_wifi_on")) {
                device.setWiFiMode(true);
                Logger.i(TAG, "Wi-Fi turned on");
            }

            if (mUtils.getBooleanPreference("turn_bluetooth_on")) {
                device.setBluetoothMode(true);
                Logger.i(TAG, "Bluetooth turned on");
            }

            if (mUtils.getBooleanPreference("turn_auto_sync_on")) {
                device.setAutoSyncMode(true);
                Logger.i(TAG, "Auto Sync turned on");
            }

            if (mUtils.getBooleanPreference("set_screen_timeout_on")) {
                device.setScreenTimeout(true);
                Logger.i(TAG, "Screen timeout turned on");
            }

            if (mUtils.getBooleanPreference("turn_brightness_on")) {
                device.setBrightnessMode(true);
                Logger.i(TAG, "Brightness turned on");
            }

            if (mUtils.getBooleanPreference("turn_wifi_ap_on")) {
                device.setWiFiApMode(true);
                Logger.i(TAG, "Wi-Fi AP turned on");
            }

            // Root
            if (mUtils.hasRoot()) {
                if (mUtils.getBooleanPreference("turn_gps_on")) {
                    device.setGPSMode(true);
                    Logger.i(TAG, "GPS turned on");
                }
                if (mUtils.getBooleanPreference("turn_nfc_on")) {
                    device.setNFCMode(true);
                    Logger.i(TAG, "NFC turned on");
                }
                if (mUtils.getBooleanPreference("turn_screen_on")) {
                    device.setScreenMode(true);
                    Logger.i(TAG, "Screen turned on");
                }

                if (mUtils.getBooleanPreference("turn_mobile_data_on")) {
                    device.setMobileDataMode(true);
                    Logger.i(TAG, "Mobile data turned on");
                }

                if (mUtils.getBooleanPreference("turn_android_saver_off")) {
                    device.setPowerSaveMode(false);
                    Logger.i(TAG, "Android Battery Saver turned off");
                }
                if (mUtils.getBooleanPreference("turn_airplane_mode_off")) {
                    device.setAirplaneMode(false);
                    Logger.i(TAG, "Airplane mode turned off");
                }

                if (mUtils.getBooleanPreference("turn_doze_off")) {
                    device.setDozeMode(false);
                    Logger.i(TAG, "Doze turned off");
                }
            }
        }
        showInfoNotification(mode);
    }

    private void showConfirmStartNotification(boolean mode) {
        Intent start = new Intent(Constants.INTENT_BETTER_BATTERY_SAVER_START);
        Intent stop = new Intent(Constants.INTENT_BETTER_BATTERY_SAVER_STOP);
        Intent app = new Intent(mContext, PreferencesActivity.class);

        app.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent openApp = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), app, 0);
        PendingIntent turnOn = PendingIntent.getBroadcast(mContext, (int) System.currentTimeMillis(), start, 0);
        PendingIntent turnOff = PendingIntent.getBroadcast(mContext, (int) System.currentTimeMillis(), stop, 0);

        String title, action;
        PendingIntent run;
        if (mode) {
            title = mContext.getString(R.string.turn_on_message);
            action = mContext.getString(R.string.turn_on);
            run = turnOn;
        } else {
            title = mContext.getString(R.string.turn_off_message);
            action = mContext.getString(R.string.turn_off);
            run = turnOff;
        }

        Notification.Builder builder = new Notification.Builder(mContext)
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(openApp)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Notification.Action runAction = new Notification.Action.Builder(
                    Icon.createWithResource(mContext, R.drawable.ic_notification_turn_on),
                    action,
                    run).build();
            builder.addAction(runAction);
        } else {
            builder.addAction(R.drawable.ic_notification_turn_on, action, run);
        }

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M) {
            builder.setContentTitle(mContext.getString(R.string.app_name));
        }

        if (mUtils.areHeadsUpNotificationsEnabled()) {
            builder.setPriority(Notification.PRIORITY_HIGH)
                    .setVibrate(new long[0]);
        }


        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(CONFIRM_NOTIFICATION_ID, builder.build());
    }

    private void showInfoNotification(boolean mode) {
        if (!mUtils.areInfoNotificationsEnabled()) return;
        String title;
        if (mode) {
            title = mContext.getString(R.string.battery_saver_enabled);
        } else {
            title = mContext.getString(R.string.battery_saver_disabled);
        }
        Notification.Builder builder = new Notification.Builder(mContext)
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true);

        if (mUtils.areHeadsUpNotificationsEnabled()) {
            builder.setPriority(Notification.PRIORITY_HIGH)
                    .setVibrate(new long[0]);
        }

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M) {
            builder.setContentTitle(mContext.getString(R.string.app_name));
        }

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(INFO_CONFIRMATION_ID, builder.build());
    }

    public void cancelConfirmStartNotification() {
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(CONFIRM_NOTIFICATION_ID);
    }
}
