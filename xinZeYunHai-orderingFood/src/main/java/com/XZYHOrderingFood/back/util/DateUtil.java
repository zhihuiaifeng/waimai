package com.XZYHOrderingFood.back.util;

import org.apache.commons.lang3.time.FastDateFormat;

import com.XZYHOrderingFood.back.pojo.DayCompare;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 线程安全的时间转换工具类
 */
public class DateUtil {
    //格式(2000-01)
    private static final FastDateFormat formatYm = FastDateFormat.getInstance("yyyy-MM");

    //格式(2000-01-01)
    private static final FastDateFormat formatYmd = FastDateFormat.getInstance("yyyy-MM-dd");

    //格式(2000-01-01 00:00:00)
    private static final FastDateFormat formatHms = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    
  //格式(2000-01-01 00:00 )
    private static final FastDateFormat formatH = FastDateFormat.getInstance("yyyy-MM-dd HH");

    //获取时分秒  HH:mm:ss
    private static final FastDateFormat formatHMS = FastDateFormat.getInstance("HH:mm:ss");

    //String转换Date : 年月日格式
    public static Date getDateYmd(String date) throws ParseException {
        return formatYmd.parse(date);
    }

    //String转换Date : 年月日 时分秒格式
    public static Date getDateHms(String date) throws ParseException {
        return formatHms.parse(date);
    }


    //将字符串转化为 HH:mm:ss 形式的 Date

    public static Date getHms(String date) throws ParseException {
        return formatHMS.parse(date);
    }

    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 返回分钟数
     *
     * @param date
     * @return
     */
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 返回周几
     * @param date
     * @return
     */
    public static int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 返回秒数
     *
     * @param date
     * @return
     */
    public static int getSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 获取时分秒  HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getStrHMS(Date date) {
        return formatHMS.format(date);
    }

    //Date转换String : 年月日格式
    public static String getStrYmd(Date date) {
        return formatYmd.format(date);
    }

    //Date转换String : 年月日格式
    public static String getStrYm(Date date) {
        return formatYm.format(date);
    }

    //Date转换String : 年月日 时分秒格式
    public static String getStrHms(Date date) {
        return formatHms.format(date);
    }
    
    //Date转换String : 年月日 时格式
    public static String getStrH(Date date) {
        return formatH.format(date);
    }

    //时间计算(秒)
    public static Date reckonSeconds(Date date, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, seconds);
        date = calendar.getTime();
        return date;
    }

    //计算时间(分钟)
    public static Date reckonMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        date = calendar.getTime();
        return date;
    }

    //计算时间(小时)
    public static Date reckonHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        date = calendar.getTime();
        return date;
    }

    //时间计算(天)
    public static Date reckonDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        date = calendar.getTime();
        return date;
    }

    //时间计算(月)
    public static Date reckonMonths(Date date, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        date = calendar.getTime();
        return date;
    }

    //时间计算(年)
    public static Date reckonYears(Date date, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, years);
        date = calendar.getTime();
        return date;
    }

    //按照hour获取日期的 年-月-日  hour-分-秒
    public static Date getHourMinSecond(Date date, int hour) throws ParseException {
        String strDate = formatYmd.format(date);

        String formatDate = new StringBuilder(strDate).append(" ").append(hour).append(":00:00").toString();

        return formatHms.parse(formatDate);
    }

    //按照hour获取日期的  年-月-日  (hour-1)-59-59
    public static Date getHourEnd(Date date, int hour) throws ParseException {
        String strDate = formatYmd.format(date);

        String formatDate = new StringBuilder(strDate).append(" ").append(hour - 1).append(":59:59").toString();

        return formatHms.parse(formatDate);
    }

    //得到今天的开始00:00:00
    public static Date getBeginDate(Date date) throws ParseException {
        String strDate = formatYmd.format(date);

        String formatDate = strDate + " 00:00:00";

        return formatHms.parse(formatDate);
    }

    //得到今天06:00:00
    public static Date getSixDate(Date date) throws ParseException {
        String strDate = formatYmd.format(date);

        String formatDate = strDate + " 06:00:00";

        return formatHms.parse(formatDate);
    }

    //获取两个日期的时间差(天)
    public static int getDay(Date date1, Date date2) throws ParseException {
        String fromDate = formatHms.format(date1);
        String toDate = formatHms.format(date2);

        long from = formatHms.parse(fromDate).getTime();

        long to = formatHms.parse(toDate).getTime();

        double hours = (double) (to - from) / (1000 * 60 * 60 * 24);

        int day = (int) Math.ceil(hours);

        return day;
    }

    //获取两个日期的时间差(小时)
    public static long getHour(Date date1, Date date2) throws ParseException {
        String fromDate = formatHms.format(date1);

        String toDate = formatHms.format(date2);

        long from = formatHms.parse(fromDate).getTime();

        long to = formatHms.parse(toDate).getTime();

        long hours = (to - from) / (1000 * 60 * 60);

        return hours;
    }

    //获取两个日期的时间差(分钟)
    public static long getMinutes(Date date1, Date date2) throws ParseException {
        String fromDate = formatHms.format(date1);

        String toDate = formatHms.format(date2);

        long from = formatHms.parse(fromDate).getTime();

        long to = formatHms.parse(toDate).getTime();

        long minutes = (to - from) / (1000 * 60);

        return minutes;
    }

    //得到今天的结束时间 23:59:59 为true则结束时间 为false则起始时间
    public static Date getEndOrStartDate(Date date, boolean flag) throws ParseException {
        String strDate = formatYmd.format(date);

        String formatDate;
        if (flag) {
            formatDate = strDate + " 23:59:59";
        } else {
            formatDate = strDate + " 00:00:00";
        }

        return formatHms.parse(formatDate);
    }

    //获取昨天日期
    public static Date yesterdayYmd() throws ParseException {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, -1);

        String yesterday = new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime());

        return getDateYmd(yesterday);
    }

    // 获取昨天的结束时间
    public static Date yesterdayEndYmd() throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime());
        String formatDate = yesterday + " 23:59:59";
        return getDateHms(formatDate);
    }

    // 获取传入时间第n天前或几天后的起始或结束日期 flag为true是前几天，false是后几天 flag1为true是开始时间，false是结束时间
    public static Date daysAgoYmd(Date date, int n, boolean flag1) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, n);
        String day = new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime());
        String newDay = null;
        if (flag1) {
            newDay = day + " 00:00:00";
        } else {
            newDay = day + " 23:59:59";
        }
        return getDateHms(newDay);
    }

    // 判断传入时间是星期几 1-星期天 2-星期一 3-星期二 4-星期三 5-星期四 6-星期五 7-星期六
    public static int todatIsWhichDay(Date date){
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        int week  = cal.get(Calendar.DAY_OF_WEEK);

        return week;
    }

    // 传入周几返回日期
    public static String getDateByWeek (Date date, int weekDay) throws ParseException {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        int week  = cal.get(Calendar.DAY_OF_WEEK);

        if (weekDay < week) {
            cal.add(Calendar.DATE, weekDay-week);
        } else {
            cal.add(Calendar.DATE, -7-(week-weekDay));
        }
        return formatYmd.format(cal.getTime());
    }

    // 返回传入时间属于的月份
    public static int getWhichMonthbyDate (Date date){
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        int month = cal.get(Calendar.MONTH);

        month++;

        return month;
    }

    // 返回传入时间属于的年份
    public static int getWhichYearbyDate (Date date){
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        int year = cal.get(Calendar.YEAR);

        return year;
    }

    public static long get_D_Plaus_1(Calendar c) {
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
        return c.getTimeInMillis();
    }

    // yyyymmdd 转yyyy-mm-dd
    public static String strToDateFormat(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        formatter.setLenient(false);
        Date newDate = formatter.parse(date);
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(newDate);
    }


    public static String dateToString(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }
    
    
    /**
     * 计算2个日期之间相差的  以年、月、日为单位，各自计算结果是多少
     * 比如：2011-02-02 到  2017-03-02
     *                                以年为单位相差为：6年
     *                                以月为单位相差为：73个月
     *                                以日为单位相差为：2220天
     * @param fromDate
     * @param toDate
     * @return
     */
    public static DayCompare dayCompare(Date fromDate,Date toDate){
        Calendar  from  =  Calendar.getInstance();
        from.setTime(fromDate);
        Calendar  to  =  Calendar.getInstance();
        to.setTime(toDate);
        //只要年月
        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);

        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);

        int year = toYear  -  fromYear;
        int month = toYear *  12  + toMonth  -  (fromYear  *  12  +  fromMonth);
        int day = (int) ((to.getTimeInMillis()  -  from.getTimeInMillis())  /  (24  *  3600  *  1000));
        return DayCompare.builder().day(day).month(month).year(year).build();
    }
    
	
	/*public static void main(String[] args) throws ParseException{
		System.out.println(getDateYmd("2018-03-12"));
		System.out.println(strToDateFormat("20111111"));
		System.out.println(getStrHMS(new Date()));
		System.out.println(getStrYmd(new Date()));
	}*/
}
