package app.nehc.batterytool;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BatteryStats extends AppCompatActivity implements View.OnClickListener {

    private TextView switch_1;
    private TextView switch_2;
    private StatisticsView statisticsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_stats);
        switch_1 = findViewById(R.id.switch_tv_1);
        switch_2 = findViewById(R.id.switch_tv_2);
        switch_1.setOnClickListener(this);
        switch_2.setOnClickListener(this);
        switch_1.setBackground(getDrawable(R.drawable.batterystats_tv_selected_bg));
        statisticsView = findViewById(R.id.statisticView);
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