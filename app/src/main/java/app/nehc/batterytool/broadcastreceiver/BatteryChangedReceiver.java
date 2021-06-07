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

import androidx.annotation.RequiresApi;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import app.nehc.batterytool.BatteryStatsDBHelper;
import app.nehc.batterytool.R;
import app.nehc.batterytool.bean.BatteryStatsBean;
import app.nehc.batterytool.bean.FuncItem;

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
                    boolean isCharging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) != 0;
                    RemoteConfigUtil.setPackageName(context.getPackageName());
                    if (RemoteConfigUtil.getFuncItemList().get(0).isEnable()) {
                        boolean charge_notice = context.getSharedPreferences("notice_status", Context.MODE_PRIVATE).getBoolean("charge_notice", false);
                        boolean has_notified = context.getSharedPreferences("notice_status", Context.MODE_PRIVATE).getBoolean("has_notified", false);
                        int noticeValue = context.getSharedPreferences("charge_notice_value", Context.MODE_PRIVATE).getInt("notice_value", 80);
                        if (cBattery == noticeValue && charge_notice && !has_notified) {
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
                            editor.commit();
                        }
                    }
                    //insertData
                    BatteryStatsBean bean = new BatteryStatsBean();
                    bean.setCapacity(cBattery);
                    bean.setCharging(isCharging);
                    RemoteDBUtil.insertData(bean);
                    //organizeData
                    if ((RemoteDBUtil.getBottomStats() - RemoteDBUtil.getTopStats()) > 43200000) {
                        RemoteDBUtil.organizeData();
                    }
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    editor = context.getSharedPreferences("notice_status", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("charge_notice", false);
                    editor.commit();
                    //重置亮屏时间
                    SharedPreferences.Editor editor2 = context.getSharedPreferences("screen_on_time", Context.MODE_PRIVATE).edit();
                    editor2.putLong("on_time", 0);
                    editor2.putLong("screenOnTime",System.currentTimeMillis());
                    editor2.putInt("lastCapacity", batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)).commit();
                    break;
                case Intent.ACTION_POWER_CONNECTED:
                    editor = context.getSharedPreferences("notice_status", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("charge_notice", true);
                    editor.putBoolean("has_notified", false);
                    editor.commit();
                    break;
            }
        }

    }


    static class RemoteDBUtil {
        private static SQLiteDatabase db;
        private static Context context;
        private final static String TABLE_NAME = "battery_stats";
        private final static String DB_NAME = "batterytool.db";

        static {
            context = BatteryChangedReceiver.context.getApplicationContext();
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

//        public static void closeDB() {
//            if (db != null) {
//                db.close();
//            }
//        }

//        public static void initDBUtil() {
//        }

        private static SQLiteDatabase getDB() {
            db = new BatteryStatsDBHelper(context, DB_NAME, null, 1).getWritableDatabase();
            return db;
        }

//        public static List<BatteryStatsBean> parseToStatsDataList() {
//            List<BatteryStatsBean> result = new ArrayList<>();
//            db = getDB();
//            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
//            while (cursor.moveToNext()) {
//                BatteryStatsBean batteryStatsBean = new BatteryStatsBean();
//                batteryStatsBean.setTimeStamp(cursor.getLong(cursor.getColumnIndex("time_stamp")));
//                batteryStatsBean.setCapacity(cursor.getInt(cursor.getColumnIndex("capacity")));
//                result.add(batteryStatsBean);
//            }
//            cursor.close();
//            db.close();
//            return result;
//        }

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

    static class RemoteConfigUtil {
        private static String PACKAGE_NAME;
        private static String CONFIG_FILE_PATH;
        private static final String CONFIG_FILE_NAME = "setting_config.xml";

        /**
         * 初始化配置文件
         */
//        public static void initSettingConfigFile(Context context) {
//            try {
//                BufferedReader br = null;
//                File configFile = new File(CONFIG_FILE_PATH);
//                if (!configFile.exists()) {
//                    br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.default_setting)));
//                    String tmp;
//                    StringBuilder builder = new StringBuilder();
//                    while ((tmp = br.readLine()) != null) {
//                        builder.append(tmp);
//                        builder.append('\n');
//                    }
//                    //开始写入文件
//                    context.openFileOutput(CONFIG_FILE_NAME, context.MODE_PRIVATE).write(builder.toString().getBytes());
//                }
//                try {
//                    //关闭流
//                    if (br != null) {
//                        br.close();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        public static void setPackageName(String packageName) {
            PACKAGE_NAME = packageName;
            CONFIG_FILE_PATH = "/data/data/" + packageName + "/files/setting_config.xml";
        }

        /**
         * 解析setting-config.xml成FuncItem对象列表
         *
         * @return
         */
        public static List<FuncItem> getFuncItemList() {
            List<FuncItem> funcItemList = new ArrayList<>();
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new File(CONFIG_FILE_PATH));
                NodeList nodeList = document.getDocumentElement().getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    switch (nodeList.item(i).getNodeName()) {
                        case "setting-item":
                            FuncItem funcItem = new FuncItem();
                            Node node = nodeList.item(i);
                            funcItem.setFuncId(Integer.parseInt(node.getAttributes().item(0).getNodeValue()));
                            NodeList childNodes = node.getChildNodes();
                            //此for循环解析成单个FuncItem对象
                            for (int i1 = 0; i1 < childNodes.getLength(); i1++) {
                                Node cNode = childNodes.item(i1);
                                if (cNode.getNodeType() == Node.ELEMENT_NODE) {
                                    switch (cNode.getNodeName()) {
                                        case "name":
                                            funcItem.setFuncName(cNode.getFirstChild().getNodeValue() + "");
                                            break;
                                        case "isSwitch":
                                            if (cNode.getFirstChild().getNodeValue().equals("1")) {
                                                funcItem.setSwitchItem(true);
                                            } else {
                                                funcItem.setSwitchItem(false);
                                            }
                                            break;
                                        case "isEnable":
                                            if (cNode.getFirstChild().getNodeValue().equals("1")) {
                                                funcItem.setEnable(true);
                                            } else {
                                                funcItem.setEnable(false);
                                            }
                                            break;
                                    }
                                }
                            }
                            funcItemList.add(funcItem);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return funcItemList;
        }

        /**
         * 写入设置
         *
         * @param id
         * @param isEnable
         */
//        public static void writeSetting(int id, boolean isEnable) {
//            String isEnableToStr;
//            if (isEnable)
//                isEnableToStr = "1";
//            else
//                isEnableToStr = "0";
//            //
//            try {
//                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                DocumentBuilder builder = factory.newDocumentBuilder();
//                Document document = builder.parse(new File(CONFIG_FILE_PATH));
//                Element element = document.getElementById("" + id);
//                //设置开关状态
//                element.getChildNodes().item(5).getFirstChild().setNodeValue(isEnableToStr);
//                //保存至文件
//                TransformerFactory transformerFactory = TransformerFactory.newInstance();
//                Transformer transformer = transformerFactory.newTransformer();
//                Source source = new DOMSource(document);
//                Result result = new StreamResult(new File(CONFIG_FILE_PATH));
//                transformer.transform(source, result);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}