package app.nehc.batterytool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.BatteryManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.nehc.batterytool.utils.DBUtil;

public class BatteryStats extends AppCompatActivity implements View.OnClickListener {

    private TextView switch_1;
    private TextView switch_2;
    private StatisticsView statisticsView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DBUtil.parseToStatsDataList().size() == 0){
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            DBUtil.insertData(new String[]{String.valueOf(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))});
        }
        setContentView(R.layout.battery_stats);
        switch_1 = findViewById(R.id.switch_tv_1);
        switch_2 = findViewById(R.id.switch_tv_2);
        switch_1.setOnClickListener(this);
        switch_2.setOnClickListener(this);
        switch_1.setBackground(getDrawable(R.drawable.batterystats_tv_selected_bg));
        statisticsView = findViewById(R.id.statisticView);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        statisticsView.setHeight((int) (displayMetrics.heightPixels * 0.50));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_tv_1:
                switch_2.setBackground(getDrawable(R.drawable.batterystats_tv_bg));
                switch_1.setBackground(getDrawable(R.drawable.batterystats_tv_selected_bg));
                statisticsView.setShowViewType(StatisticsView.TYPE_01);
                break;
            case R.id.switch_tv_2:
                switch_1.setBackground(getDrawable(R.drawable.batterystats_tv_bg));
                switch_2.setBackground(getDrawable(R.drawable.batterystats_tv_selected_bg));
                statisticsView.setShowViewType(StatisticsView.TYPE_02);
                break;
        }
    }
}