package app.nehc.batterytool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import app.nehc.batterytool.utils.TimeUtil;

public class BatteryStats extends AppCompatActivity {

    private StatisticsView statisticsView;
    private TextView screenOnTimeDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_stats);
        statisticsView = findViewById(R.id.statisticView);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        statisticsView.setHeight((int) (displayMetrics.heightPixels * 0.382));
        screenOnTimeDetail = findViewById(R.id.screenOn_timeDetail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            Long screenOnTime = getSharedPreferences("screen_on_time", MODE_MULTI_PROCESS).getLong("on_time", 0);
            runOnUiThread(() -> screenOnTimeDetail.setText(TimeUtil.timeToStr(screenOnTime)));

        }).start();
    }

}