package app.nehc.batterytool.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import app.nehc.batterytool.broadcastreceiver.BatteryChangedReceiver;

public class MonitoringService extends Service {

    private IntentFilter filter;
    private BatteryChangedReceiver batteryChangedReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        batteryChangedReceiver = new BatteryChangedReceiver();
        registerReceiver(batteryChangedReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryChangedReceiver);
    }
}