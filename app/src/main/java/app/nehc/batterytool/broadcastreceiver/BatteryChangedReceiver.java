package app.nehc.batterytool.broadcastreceiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import app.nehc.batterytool.R;

public class BatteryChangedReceiver extends BroadcastReceiver {

    private SharedPreferences.Editor editor;
    private BatteryManager batteryManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        switch (intent.getAction()) {
            case Intent.ACTION_BATTERY_CHANGED:
                int cBattery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                boolean charge_notice = context.getSharedPreferences("notice_status", Context.MODE_PRIVATE).getBoolean("charge_notice", false);
                boolean has_notified = context.getSharedPreferences("notice_status", Context.MODE_PRIVATE).getBoolean("has_notified", false);
                if (cBattery == 80 && charge_notice && !has_notified) {
                    NotificationChannel notificationChannel = new NotificationChannel("charge notice", "充电提醒", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = new Notification.Builder(context)
                            .setChannelId("charge notice")
                            .setContentText("电池已充到指定值")
                            .setContentTitle("充电提醒")
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .build();
                    notificationManager.createNotificationChannel(notificationChannel);
                    notificationManager.notify(1, notification);
                    editor = context.getSharedPreferences("notice_status", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("has_notified", true);
                    editor.apply();
                }
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                editor = context.getSharedPreferences("notice_status", Context.MODE_PRIVATE).edit();
                editor.putBoolean("charge_notice", false);
                editor.apply();
                break;
            case Intent.ACTION_POWER_CONNECTED:
                editor = context.getSharedPreferences("notice_status", Context.MODE_PRIVATE).edit();
                editor.putBoolean("charge_notice", true);
                editor.putBoolean("has_notified",false);
                editor.apply();
                break;
        }
    }
}