package edu.bupt.beibei.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

    private static final SimpleDateFormat IntDayPattern = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat StringDayPattern = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DateTimePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat TimeOnlyPattern = new SimpleDateFormat("HH:mm:ss");



    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int getWeekday() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public static String getTimeOnly() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        return TimeOnlyPattern.format(calendar.getTime());
    }

    public static String getTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        return DateTimePattern.format(calendar.getTime());
    }

    public static Date parseDateTime(String s) throws ParseException {
        return DateTimePattern.parse(s);
    }

    public static int getIntDay() {
        return getIntDay(0);
    }

    public static String getStringDay(int offsetDay) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.add(Calendar.DAY_OF_YEAR, offsetDay);
        return StringDayPattern.format(calendar.getTime());
    }

    public static int getIntDay(int offsetDay) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.add(Calendar.DAY_OF_YEAR, offsetDay);
        String day = IntDayPattern.format(calendar.getTime());
        return Integer.parseInt(day);
    }

    /**
     * yyyy-MM-dd转yyyyMMdd
     *
     * @param stringDay
     * @return
     * @throws ParseException
     */
    public static int getIntDayOfStringDay(String stringDay) throws ParseException {
        return Integer.parseInt(IntDayPattern.format(StringDayPattern.parse(stringDay)));
    }

    public static String getStringDayOfIntDay(int intDay) throws ParseException {
        return StringDayPattern.format(IntDayPattern.parse(String.valueOf(intDay)));
    }

    public static int getIntDay(int offsetDay, Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, offsetDay);
        String day = IntDayPattern.format(calendar.getTime());
        return Integer.parseInt(day);
    }

    public static Date getDate(int intDay) {
        try {
            return IntDayPattern.parse(String.valueOf(intDay));
        } catch (Exception e) {

        }
        return Calendar.getInstance().getTime();
    }
}
