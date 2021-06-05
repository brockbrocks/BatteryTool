package app.nehc.batterytool.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import app.nehc.batterytool.broadcastreceiver.BatteryChangedReceiver;
import app.nehc.batterytool.broadcastreceiver.ScreenChangedReceiver;

public class MonitoringService extends Service {

    private IntentFilter filter;
    private BatteryChangedReceiver batteryChangedReceiver;
    private IntentFilter filter2;
    private ScreenChangedReceiver screenChangedReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //电量变化广播
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        batteryChangedReceiver = new BatteryChangedReceiver();
        registerReceiver(batteryChangedReceiver, filter);
        //屏幕变化广播
        filter2 = new IntentFilter();
        filter2.addAction(Intent.ACTION_SCREEN_ON);
        filter2.addAction(Intent.ACTION_SCREEN_OFF);
        screenChangedReceiver = new ScreenChangedReceiver();
        registerReceiver(screenChangedReceiver, filter2);
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
        unregisterReceiver(screenChangedReceiver);
    }
}