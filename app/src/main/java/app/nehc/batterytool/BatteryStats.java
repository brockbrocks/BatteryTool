package app.nehc.batterytool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import app.nehc.batterytool.utils.TimeUtil;

public class BatteryStats extends AppCompatActivity {

    private StatisticsView statisticsView;
    private TextView screenOnTimeDetail;
    private TextView lastCapacity;
    private TextView lastCapacityTitle;
    private Long screenOnTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_stats);
        statisticsView = findViewById(R.id.statisticView);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        statisticsView.setHeight((int) (displayMetrics.heightPixels * 0.382));
        screenOnTimeDetail = findViewById(R.id.screenOn_timeDetail);
        lastCapacity = findViewById(R.id.lastCapacity);
        lastCapacityTitle = findViewById(R.id.lastCapacityTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int lastCapacityValue = getSharedPreferences("screen_on_time", MODE_PRIVATE).getInt("lastCapacity", -1);
        if (lastCapacityValue == -1) {
            lastCapacityTitle.setText("未查询到上次拔掉电源 剩余电量");
            lastCapacity.setVisibility(View.GONE);
        } else {
            lastCapacity.setText(lastCapacityValue + "%");
        }
        screenOnTime = getSharedPreferences("screen_on_time", MODE_MULTI_PROCESS).getLong("on_time", 0);
        screenOnTimeDetail.setText(TimeUtil.timeToStr(screenOnTime));
    }

}