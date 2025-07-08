package com.crush.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @program: binary-option
 * @description
 * @author: binary
 * @create: 2020-06-05 17:59
 **/
public class DateUtils {

    public static String parseDate(long time, String formatStr) {
        if (time <= 0) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = new Date(time);
        return format.format(date);
    }

    public static String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        return format.format(date);
    }
    public static String getTimes(Date date) {//可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.valueOf(format.format(date));
    }

    /**
     * 增加秒
     *
     * @param date
     * @param second
     * @return
     */
    public static Date addDate(Date date, int second) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getMillis(date) + (long) second * 1000L);
        return c.getTime();
    }

    public static long getMillis(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    /**
     * 是否在某一时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean isTimeIn(Date nowTime, Date beginTime, Date endTime) {
        return nowTime.getTime() >= beginTime.getTime()
                && nowTime.getTime() <= endTime.getTime();
    }

    public static boolean isTimeIn(Date time, String begin, String end) {
        return isTimeIn(time, getDate(begin, "HH:mm:ss"), getDate(end, "HH:mm:ss"));
    }

    /**
     * 把传入的日期字符串，转换成指定格式的日期对象
     *
     * @param dateString 日期字符串
     * @param pattern    指定转换格式
     * @return 日期对象
     */
    public static Date getDate(String dateString, String pattern) {
        SimpleDateFormat df = null;
        Date date = null;
        try {
            df = new SimpleDateFormat(pattern);
            date = df.parse(dateString);
            Log.e("~~~", "getDate: "+df.format(date) );

        } catch (Exception e) {

        }
        return date;
    }
    public static String getDateString(String dateString, String pattern) {
        SimpleDateFormat df = null;
        Date date = null;
        try {
            df = new SimpleDateFormat(pattern);
            date = df.parse(dateString);

        } catch (Exception e) {

        }
        return df.format(date);
    }


    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime,
                                         Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInTime(String openingTime, String closingTime) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");// 设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(openingTime);
            endTime = df.parse(closingTime);
        } catch (Exception e) {

        }
        if (!DateUtils.belongCalendar(now, beginTime, endTime)) {
            return false;
        }
        return true;
    }


    public static boolean isWeekend(Date date) {

        Calendar cal = Calendar.getInstance();
//        TimeZone tz = TimeZone.getTimeZone("Asia/Kolkata");
//        cal.setTimeZone(tz);
        cal.setTime(date);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        } else {
            return false;
        }
    }

    public static Date getLastWeekend(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);//将date - 7
        c.add(Calendar.DATE, -7);
        Date d = c.getTime();
        return d;
    }

    public static Date getNextWeekend(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);//将date + 7
        c.add(Calendar.DATE, +7);
        Date d = c.getTime();
        return d;
    }

    public static void main(String[] args) {
        TimeZone tz = TimeZone.getTimeZone("Asia/Kolkata");
        System.out.println(tz);
        System.out.println(isWeekend(new Date()));
    }

    //将时间戳转化为对应的时间  日-时-分-秒
    public static String timeConversion(long time) {
        long day = 0;
        long hour = 0;
        long minutes = 0;
        long sencond = 0;
        long dayTimp = time % (3600*24);
        long hourTimp = time % 3600;

        if(time >= 86400){
            day = time / (3600*24);
            if(dayTimp != 0){
                time = time-(day * 24 * 60 * 60);
                if(time  >= 3600 && time < 86400){
                    hour = time / 3600;
                    if (hourTimp != 0) {
                        if (hourTimp  >= 60) {
                            minutes = hourTimp / 60;
                            if (hourTimp % 60 != 0) {
                                sencond = hourTimp % 60;
                            }
                        } else if (hourTimp < 60){
                            sencond = hourTimp;
                        }
                    }
                } else if(time < 3600){
                    minutes = time / 60;
                    if (time % 60 != 0) {
                        sencond = time % 60;
                    }
                }
            }
        } else if (time  >= 3600 && time < 86400) {
            hour = time / 3600;
            if (hourTimp != 0) {
                if (hourTimp  >= 60) {
                    minutes = hourTimp / 60;
                    if (hourTimp % 60 != 0) {
                        sencond = hourTimp % 60;
                    }
                } else if (hourTimp < 60){
                    sencond = hourTimp;
                }
            }
        } else if(time < 3600){
            minutes = time / 60;
            if (time % 60 != 0) {
                sencond = time % 60;
            }
        }
        return (minutes<10?("0"+minutes):minutes) + ":" + (sencond<10?("0"+sencond):sencond);
    }
    public static String timeHourConversion(long time) {
        long day = 0;
        long hour = 0;
        long minutes = 0;
        long sencond = 0;
        long dayTimp = time % (3600*24);
        long hourTimp = time % 3600;

        if(time >= 86400){
            day = time / (3600*24);
            if(dayTimp != 0){
                time = time-(day * 24 * 60 * 60);
                if(time  >= 3600 && time < 86400){
                    hour = time / 3600;
                    if (hourTimp != 0) {
                        if (hourTimp  >= 60) {
                            minutes = hourTimp / 60;
                            if (hourTimp % 60 != 0) {
                                sencond = hourTimp % 60;
                            }
                        } else if (hourTimp < 60){
                            sencond = hourTimp;
                        }
                    }
                } else if(time < 3600){
                    minutes = time / 60;
                    if (time % 60 != 0) {
                        sencond = time % 60;
                    }
                }
            }
        } else if (time  >= 3600 && time < 86400) {
            hour = time / 3600;
            if (hourTimp != 0) {
                if (hourTimp  >= 60) {
                    minutes = hourTimp / 60;
                    if (hourTimp % 60 != 0) {
                        sencond = hourTimp % 60;
                    }
                } else if (hourTimp < 60){
                    sencond = hourTimp;
                }
            }
        } else if(time < 3600){
            minutes = time / 60;
            if (time % 60 != 0) {
                sencond = time % 60;
            }
        }
        return (hour>0?hour>9?"0"+hour+":":hour+":":"")+(minutes<10?("0"+minutes):minutes) + ":" + (sencond<10?("0"+sencond):sencond);
    }
}
