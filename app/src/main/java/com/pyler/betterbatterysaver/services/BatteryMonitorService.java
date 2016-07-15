package com.pyler.betterbatterysaver.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;

import com.pyler.betterbatterysaver.receivers.BetterBatterySaverReceiver;
import com.pyler.betterbatterysaver.util.Constants;


public class BatteryMonitorService extends Service {
    private BroadcastReceiver receiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
        filter.addAction(Constants.INTENT_BETTER_BATTERY_SAVER_START);
        filter.addAction(Constants.INTENT_BETTER_BATTERY_SAVER_STOP);
        receiver = new BetterBatterySaverReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}

