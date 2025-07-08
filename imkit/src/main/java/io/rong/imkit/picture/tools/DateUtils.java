package io.rong.imkit.picture.tools;

import android.util.Log;

import io.rong.common.RLog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();
    private SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd_HHmmssSS");

    private DateUtils() {
        // default implementation ignored
    }

    private static class SingletonHolder {
        static DateUtils sInstance = new DateUtils();
    }

    public static DateUtils getInstance() {
        return SingletonHolder.sInstance;
    }

    /**
     * 判断两个时间戳相差多少秒
     *
     * @param d
     * @return
     */
    public int dateDiffer(long d) {
        try {
            long l1 = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(0, 10));
            long interval = l1 - d;
            return (int) Math.abs(interval);
        } catch (Exception e) {
            RLog.e(TAG, e.getMessage());
            return -1;
        }
    }

    /**
     * 时间戳转换成时间格式
     *
     * @param duration
     * @return
     */
    public String formatDurationTime(long duration) {
        return String.format(
                Locale.getDefault(),
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    /**
     * 根据时间戳创建文件名
     *
     * @param prefix 前缀名
     * @return
     */
    public String getCreateFileName(String prefix) {
        long millis = System.currentTimeMillis();
        return prefix + sf.format(millis);
    }

    /**
     * 计算两个时间间隔
     *
     * @param sTime
     * @param eTime
     * @return
     */
    public String cdTime(long sTime, long eTime) {
        long diff = eTime - sTime;
        return diff > 1000 ? diff / 1000 + "s" : diff + "ms";
    }
    public long cdTimes(long eTime, long sTime) {
        long diff = eTime - sTime;
        return diff;
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

}
