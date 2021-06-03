package app.nehc.batterytool.broadcastreceiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.nehc.batterytool.BatteryStatsDBHelper;
import app.nehc.batterytool.MainActivity;
import app.nehc.batterytool.R;
import app.nehc.batterytool.bean.BatteryStatsBean;
import app.nehc.batterytool.utils.DBUtil;

public class BatteryChangedReceiver extends BroadcastReceiver {

    private SharedPreferences.Editor editor;
    private BatteryManager batteryManager;
    private static Context context;
    private static int stopFirst = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        if (stopFirst == 0) {
            stopFirst++;
        } else {
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
                    //insertData
//                    DBUtil.insertData(new String[]{String.valueOf(cBattery)});
                    RemoteDBUtil.insertData(new String[]{String.valueOf(cBattery)});
                    //organizeData
//                    if ((DBUtil.getBottomStats() - DBUtil.getTopStats()) > 43200000) {
//                        DBUtil.organizeData();
//                    }
                    if ((RemoteDBUtil.getBottomStats() - RemoteDBUtil.getTopStats()) > 43200000) {
                        RemoteDBUtil.organizeData();
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
                    editor.putBoolean("has_notified", false);
                    editor.apply();
                    break;
            }
        }

    }


    static class RemoteDBUtil {
        private static SQLiteDatabase db;
        private static Context context;
        private final static String TABLE_NAME = "battery_stats";

        static {
            context = BatteryChangedReceiver.context.getApplicationContext();
            if (!new File("/data/data/" + context.getPackageName() + "/databases/batterytool.db").exists()) {
                new BatteryStatsDBHelper(context, "batterytool.db", null, 1).getWritableDatabase();
            }
        }


        public static void insertData(String[] content) {
            db = getDB();
            ContentValues cv = new ContentValues();
            cv.put("time_stamp", System.currentTimeMillis());
            cv.put("capacity", content[0]);
            db.insert("battery_stats", null, cv);
            db.close();
        }

        public static void closeDB() {
            if (db != null) {
                db.close();
            }
        }

        public static void initDBUtil() {
        }

        private static SQLiteDatabase getDB() {
            SQLiteDatabase.OpenParams openParams = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
                openParams = new SQLiteDatabase.OpenParams.Builder().build();
                db = SQLiteDatabase.openDatabase(new File("/data/data/app.nehc.batterytool/databases/batterytool.db"), openParams);
            }
            return db;
        }

        public static List<BatteryStatsBean> parseToStatsDataList() {
            List<BatteryStatsBean> result = new ArrayList<>();
            db = getDB();
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                BatteryStatsBean batteryStatsBean = new BatteryStatsBean();
                batteryStatsBean.setTimeStamp(cursor.getLong(cursor.getColumnIndex("time_stamp")));
                batteryStatsBean.setCapacity(cursor.getInt(cursor.getColumnIndex("capacity")));
                result.add(batteryStatsBean);
            }
            cursor.close();
            db.close();
            return result;
        }

        public static void organizeData() {
            db = getDB();
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "time_stamp desc");
            cursor.moveToNext();
            Long latestTimeStamp = cursor.getLong(cursor.getColumnIndex("time_stamp"));
            db.delete(TABLE_NAME, "time_stamp <= ? ", new String[]{String.valueOf(latestTimeStamp - 7200000)});
            cursor.close();
            db.close();
        }

        public static Long getTopStats() {
            db = getDB();
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, "1");
            cursor.moveToNext();
            Long earliestTimeStamp = cursor.getLong(cursor.getColumnIndex("time_stamp"));
            cursor.close();
            db.close();
            return earliestTimeStamp;
        }

        public static Long getBottomStats() {
            db = getDB();
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "time_stamp desc");
            cursor.moveToNext();
            Long latestTimeStamp = cursor.getLong(cursor.getColumnIndex("time_stamp"));
            cursor.close();
            db.close();
            return latestTimeStamp;
        }
    }
}