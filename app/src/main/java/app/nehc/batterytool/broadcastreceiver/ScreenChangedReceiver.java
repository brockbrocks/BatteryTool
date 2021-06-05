package app.nehc.batterytool.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenChangedReceiver extends BroadcastReceiver {
    private static long screenOnTime;
    private long lastTime;

    static {
        screenOnTime = System.currentTimeMillis();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_ON:
                screenOnTime = System.currentTimeMillis();
                break;
            case Intent.ACTION_SCREEN_OFF:
                lastTime = context.getSharedPreferences("screen_on_time", Context.MODE_PRIVATE).getLong("on_time", 0);
                Long writeTime = lastTime + (System.currentTimeMillis() - screenOnTime);
                context.getSharedPreferences("screen_on_time", Context.MODE_PRIVATE).edit().putLong("on_time", writeTime).apply();
                break;
        }
    }
}
