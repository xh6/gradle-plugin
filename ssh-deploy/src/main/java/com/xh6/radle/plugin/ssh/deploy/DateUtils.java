package com.xh6.radle.plugin.ssh.deploy;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {

    public static final  String                         DATE_TIME        = "yyyy-MM-dd HH:mm:ss";

    public static final  String                         DATE             = "yyyy-MM-dd";

    public static final  String                         TIME             = "HH:mm:ss";

    public static final  DateTimeFormatter              FORMAT_DATE_TIME = DateTimeFormat.forPattern(DATE_TIME);

    public static final  DateTimeFormatter              FORMAT_DATE      = DateTimeFormat.forPattern(DATE);

    public static final  DateTimeFormatter              FORMAT_TIME      = DateTimeFormat.forPattern(TIME);

    private static final Map<String, DateTimeFormatter> map              = new ConcurrentHashMap<>();

    private DateUtils() {
    }

    static {
        map.put(DATE_TIME, FORMAT_DATE_TIME);
        map.put(DATE, FORMAT_DATE);
        map.put(TIME, FORMAT_TIME);
    }

    public static DateTimeFormatter getDateTimeFormatter(String dateFormatPattern) {
        if (map.containsKey(dateFormatPattern)) {
            return map.get(dateFormatPattern);
        }
        map.putIfAbsent(dateFormatPattern, DateTimeFormat.forPattern(dateFormatPattern));
        return map.get(dateFormatPattern);
    }

    public static String format(Date from, String pattern) {
        DateTime jodaDate = new DateTime(from);
        return jodaDate.toString(getDateTimeFormatter(pattern));
    }

    public static DateTime parse(String dateStr, String pattern) {
        return DateTime.parse(dateStr, getDateTimeFormatter(pattern));
    }

    public static String getUseTime(long start, long end) {
        StringBuilder sb = new StringBuilder();
        long usedTime = end - start;
        long count = TimeUnit.MILLISECONDS.toDays(usedTime);
        if (count > 0) {
            sb.append(count).append("天");
            usedTime -= TimeUnit.DAYS.toMillis(count);
        }

        count = TimeUnit.MILLISECONDS.toHours(usedTime);
        if (count > 0 || sb.length() > 0) {
            usedTime -= TimeUnit.HOURS.toMillis(count);
            sb.append(count).append("小时");
        }

        count = TimeUnit.MILLISECONDS.toMinutes(usedTime);
        if (count > 0) {
            usedTime -= TimeUnit.MINUTES.toMillis(count);
            sb.append(count).append("分");
        }

        count = TimeUnit.MILLISECONDS.toSeconds(usedTime);
        if (count > 0) {
            usedTime -= TimeUnit.SECONDS.toMillis(count);
            sb.append(count).append("秒");
        }
        if (StringUtils.isBlank(sb)) {
            sb.append(usedTime).append("毫秒");
        }
        return sb.toString();
    }

    /**
     * 格式化时间
     * 格式:yyyy-MM-dd HH:mm:ss
     * @param from
     * @return
     */
    public static String formatDateTime(Date from) {
        return format(from, DATE_TIME);
    }

    /**
     * 格式化时间
     * 格式:yyyy-MM-dd HH:mm:ss
     * @param timestamp
     * @return
     */
    public static String formatDateTime(long timestamp) {
        return format(new Date(timestamp), DATE_TIME);
    }

    public static String formatDate(Date from) {
        return format(from, DATE);
    }

    public static String formatTime(Date from) {
        return format(from, TIME);
    }

    public static DateTime parseDateTime(String dateStr) {
        return parse(dateStr, DATE_TIME);
    }

    public static DateTime parseDate(String dateStr) {
        return parse(dateStr, DATE);
    }

    public static DateTime parseTime(String dateStr) {
        return parse(dateStr, TIME);
    }

    public static String getUseTime(long start) {
        return getUseTime(start, System.currentTimeMillis());
    }

}
