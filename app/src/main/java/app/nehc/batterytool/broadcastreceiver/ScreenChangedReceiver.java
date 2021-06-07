package app.nehc.batterytool.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import app.nehc.batterytool.service.MonitoringService;

public class ScreenChangedReceiver extends BroadcastReceiver {
    public static long screenOnTime;
    private long lastTime;
    private SharedPreferences sharedPreferences;

    static {
        SharedPreferences sharedPreferences = MonitoringService.context.getSharedPreferences("screen_on_time", Context.MODE_PRIVATE);
        screenOnTime = sharedPreferences.getLong("screenOnTime",System.currentTimeMillis());
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("screen_on_time",Context.MODE_PRIVATE);
        }
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_ON:
                screenOnTime = System.currentTimeMillis();
                sharedPreferences.edit().putLong("screenOnTime",screenOnTime).commit();
                break;
            case Intent.ACTION_SCREEN_OFF:
                lastTime = context.getSharedPreferences("screen_on_time", Context.MODE_PRIVATE).getLong("on_time", 0);
                Long writeTime = lastTime + (System.currentTimeMillis() - screenOnTime);
                context.getSharedPreferences("screen_on_time", Context.MODE_PRIVATE).edit().putLong("on_time", writeTime).commit();
                break;
        }
    }
}
