package app.nehc.batterytool.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.nehc.batterytool.BatteryStatsDBHelper;
import app.nehc.batterytool.MainActivity;
import app.nehc.batterytool.bean.BatteryStatsBean;

public class DBUtil {

    private static SQLiteDatabase db;
    private static Context context;
    private final static String TABLE_NAME = "battery_stats";
    private final static String DB_NAME = "batterytool.db";

    static {
        context = MainActivity.getContext();
        if (!new File("/data/data/" + context.getPackageName() + "/databases/" + DB_NAME).exists()) {
            new BatteryStatsDBHelper(context, DB_NAME, null, 1).getWritableDatabase();
        }
    }


    public static void insertData(BatteryStatsBean bean) {
        db = getDB();
        ContentValues cv = new ContentValues();
        cv.put("time_stamp", System.currentTimeMillis());
        cv.put("capacity", bean.getCapacity());
        cv.put("isCharging", bean.isCharging());
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

//    public static void closeDB() {
//        if (db != null) {
//            db.close();
//        }
//    }

    public static void initDBUtil() {
    }

    private static SQLiteDatabase getDB() {
//        SQLiteDatabase.OpenParams openParams = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
//            openParams = new SQLiteDatabase.OpenParams.Builder().build();
//            db = SQLiteDatabase.openDatabase(new File("/data/data/app.nehc.batterytool/databases/batterytool.db"), openParams);
//        }

        db = new BatteryStatsDBHelper(context, DB_NAME, null, 1).getWritableDatabase();
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
            if (cursor.getInt(cursor.getColumnIndex("isCharging")) == 0){
                batteryStatsBean.setCharging(false);
            }else {
                batteryStatsBean.setCharging(true);
            }
            result.add(batteryStatsBean);
        }
        cursor.close();
        db.close();
        return result;
    }

//    public static void organizeData() {
//        db = getDB();
//        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "time_stamp desc");
//        cursor.moveToNext();
//        Long latestTimeStamp = cursor.getLong(cursor.getColumnIndex("time_stamp"));
//        db.delete(TABLE_NAME, "time_stamp <= ? ", new String[]{String.valueOf(latestTimeStamp - 7200000)});
//        cursor.close();
//        db.close();
//    }

//    public static Long getTopStats() {
//        db = getDB();
//        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, "1");
//        cursor.moveToNext();
//        Long earliestTimeStamp = cursor.getLong(cursor.getColumnIndex("time_stamp"));
//        cursor.close();
//        db.close();
//        return earliestTimeStamp;
//    }

//    public static Long getBottomStats() {
//        db = getDB();
//        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "time_stamp desc");
//        cursor.moveToNext();
//        Long latestTimeStamp = cursor.getLong(cursor.getColumnIndex("time_stamp"));
//        cursor.close();
//        db.close();
//        return latestTimeStamp;
//    }
}
