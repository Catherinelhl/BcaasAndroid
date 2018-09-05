package io.bcaas.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/01/01
 */

public class DateFormatTool {
    @SuppressWarnings("unused")
    private static final String TAG = "DateFormatTool";

    private final static String DATETIMEFORMAT_TZ = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    // private final static String DATETIMEFORMAT_TZ = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    // private final static String DATETIMEFORMAT_TZ = "MM/dd/yyyy KK:mm:ss a Z";

    private final static String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";

    // Greenwich Mean Time
    private final static String TIMEZONE_GMT = "GMT";
    // Coordinated Universal Time
    private final static String TIMEZONE_UTC = "UTC";

    // UTC -> Current TimeZone 更改日期時區
    public static String dateFormat(String strDate) throws Exception {

        SimpleDateFormat simpleDateFormat_GMT = new SimpleDateFormat(DATETIMEFORMAT_TZ);
        simpleDateFormat_GMT.setTimeZone(TimeZone.getTimeZone(TIMEZONE_GMT));
        Date date = simpleDateFormat_GMT.parse(strDate);

        SimpleDateFormat simpleDateFormat_SystemDefault = new SimpleDateFormat(DATETIMEFORMAT_TZ);
        simpleDateFormat_SystemDefault.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));
        strDate = simpleDateFormat_SystemDefault.format(date);

        strDate = strDate.substring(0, strDate.indexOf("T")) + " "
                + strDate.substring(strDate.indexOf("T") + 1, strDate.lastIndexOf(":"));

        return strDate;
    }

    // Current TimeZone -> UTC 更改日期時區
    public static String dateFormatUTC(Date date) throws Exception {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATETIMEFORMAT_TZ);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));

        String strUTCDate = simpleDateFormat.format(date);
        System.out.println(strUTCDate);

        return strUTCDate;
    }

    // Current TimeZone -> UTC String to Date
    public static Date stringFormatUTCDate(String dateString) throws Exception {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATETIMEFORMAT_TZ);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));

        Date dateUTC = simpleDateFormat.parse(dateString);

        return dateUTC;
    }

    // 取出日期(年,月,日)
    public static Calendar getCalendar(String strDate) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATETIMEFORMAT_TZ);
        Date date = simpleDateFormat.parse(strDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    // 取得使用者當下的時區時間
    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATETIMEFORMAT);
        Date date = new Date();
        String strDate = simpleDateFormat.format(date);

        strDate = strDate.substring(0, strDate.lastIndexOf(":"));

        return strDate;
    }

    public static String dateConvertTZFormat(String strDate) {

        strDate = strDate.substring(0, strDate.indexOf("T")) + " "
                + strDate.substring(strDate.indexOf("T") + 1, strDate.indexOf("Z"));

        return strDate;
    }

    // Get UTC TimeStamp
    public static String getUTCTimeStamp() {
//		Instant instant = Instant.now();
//		long timeStampMillis = instant.toEpochMilli();
        return String.valueOf(new Date().getTime());
    }

}
