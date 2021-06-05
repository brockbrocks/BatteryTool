package app.nehc.batterytool.utils;

public class TimeUtil {
    private static final long oneDayMillis = 86400000;
    private static final long oneHourMillis = 3600000;
    private static final long oneMinuteMillis = 60000;
    private static final long oneSecondMillis = 1000;

    public static String timeToStr(Long timeStamp) {

        long day = timeStamp / oneDayMillis;
        if (day > 0) {
            timeStamp -= oneDayMillis * day;
        }
        long hour = timeStamp / oneHourMillis;
        if (hour > 0) {
            timeStamp -= oneHourMillis * hour;
        }
        long minute = timeStamp / oneMinuteMillis;
        if (minute > 0) {
            timeStamp -= oneMinuteMillis * minute;
        }
        long second = timeStamp / oneSecondMillis;

        String date;
        if (day > 0) {
            date = String.format("%d天 %02d小时 %02d分 %02d秒", day, hour, minute, second);
        } else {
            date = String.format("%02d时 %02d分 %02d秒", hour, minute, second);
        }
        return date;
    }
}
