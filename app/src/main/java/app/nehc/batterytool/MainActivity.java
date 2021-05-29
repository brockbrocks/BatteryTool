package app.nehc.batterytool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.nehc.batterytool.adapter.FunctionListAdapter;
import app.nehc.batterytool.bean.FuncItem;
import app.nehc.batterytool.service.MonitoringService;
import app.nehc.batterytool.utils.ConfigUtil;

public class MainActivity extends AppCompatActivity {
    //电量视图
    private CirclePercentView circlePercentView;
    private BatteryManager batteryManager;
    private BroadcastReceiver refreshCirclePercentReceiver;
    //状态面板
    private Timer refreshStatusTimer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            //
            if (atTop == 1) {
                registerReceiver(refreshStatusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                unregisterReceiver(refreshStatusReceiver);
            }
        }
    };

    private TextView batteryTemp;
    private TextView batteryCurrent;
    private TextView batteryVoltage;
    private TextView batteryRemain;
    private BroadcastReceiver refreshStatusReceiver;
    //
    private int atTop = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化设置列表(RecyclerView)
        initFunctionList();
        //根据配置文件加载服务，后续需实现检测服务是否启动功能
        initServiceByConfigFile();
        //
        initRefreshCirclePercentReceiver();
        //
        initRefreshStatusPanel();
    }


    private void initRefreshStatusPanel() {
        //刷新状态面板广播接收器
        if (refreshStatusReceiver == null) {
            refreshStatusReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (batteryTemp == null)
                        batteryTemp = findViewById(R.id.batteryTemp);
                    if (batteryCurrent == null)
                        batteryCurrent = findViewById(R.id.batteryCurrent);
                    if (batteryManager == null)
                        batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                    if (batteryVoltage == null)
                        batteryVoltage = findViewById(R.id.batteryVoltage);
                    if (batteryRemain == null)
                        batteryRemain = findViewById(R.id.batteryRemain);
                    int current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                    int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                    int remain = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                    //电池温度
                    batteryTemp.setText("电池温度：\n" + (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0f) + " °C");
                    //电池电压
                    batteryVoltage.setText("电压：\n" + voltage / 1000.0f + " V");
                    //电池电流
                    batteryCurrent.setText("电流：\n" + current / 1000.0f + " mA");
                    //电池剩余
                    batteryRemain.setText("当前容量：\n" + remain / 1000.0f + " mAh");
                }
            };
        }
        refreshStatusTimer.schedule(task, 0, 2000);
    }

    private void initRefreshCirclePercentReceiver() {
        if (refreshCirclePercentReceiver == null) {
            refreshCirclePercentReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    refreshCirclePercentView();
                }
            };
        }
        registerReceiver(refreshCirclePercentReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void initServiceByConfigFile() {
        ConfigUtil.setPackageName(getPackageName());
        for (FuncItem item : ConfigUtil.getFuncItemList()) {
            if (item.isEnable()) {
                switch (item.getFuncId()) {
                    case 10000:
                        //充电80%提醒功能
                        startService(new Intent(this, MonitoringService.class));
                        break;
                }
            }
        }
    }

    private void initFunctionList() {
        ConfigUtil.setPackageName(getPackageName());
        //初始化配置文件
        ConfigUtil.initSettingConfigFile(getApplicationContext());
        List<FuncItem> list = new ArrayList<>(ConfigUtil.getFuncItemList());
        FunctionListAdapter adapter = new FunctionListAdapter(this, list);
        RecyclerView recyclerView = findViewById(R.id.funcList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void refreshCirclePercentView() {
        circlePercentView = findViewById(R.id.circlePercentView);
        batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        circlePercentView.setPercent(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
    }

    @Override
    protected void onResume() {
        super.onResume();
        atTop = 1;
        if (overTimer != null) {
            overTimer.cancel();
            overTimer = null;
        }
    }

    private Timer overTimer;

    @Override
    protected void onStop() {
        super.onStop();
        atTop = 0;
        overTimer = new Timer();
        overTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshStatusTimer.cancel();
                finish();
                Toast.makeText(getApplicationContext(), "超时，退出程序", Toast.LENGTH_SHORT).show();
            }
        }, 1000 * 60 * 3);
    }
}