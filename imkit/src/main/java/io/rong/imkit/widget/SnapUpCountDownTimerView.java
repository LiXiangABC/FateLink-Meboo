package io.rong.imkit.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Timer;
import java.util.TimerTask;

import io.rong.imkit.R;

@SuppressLint("HandlerLeak")
public class SnapUpCountDownTimerView extends LinearLayout {

    private TextView tv_hour_decade;
    private TextView tv_hour_unit;
    private TextView tv_min_decade;
    private TextView tv_min_unit;
    private TextView tv_sec_decade;
    private TextView tv_sec_unit;
    private LinearLayout hour_container;
    private TextView hour_separation;
    private Context context;

    private int hour_decade;
    private int hour_unit;
    private int min_decade;
    private int min_unit;
    private int sec_decade;
    private int sec_unit;

    private Timer timer;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            countDown();
        }
    };

    private OnTimeFinish onTimeFinish;
    public SnapUpCountDownTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_snap_count_down_time, this);

        hour_container = (LinearLayout) view.findViewById(R.id.hour_container);
        hour_separation = (TextView) view.findViewById(R.id.hour_separation);
        tv_hour_decade = (TextView) view.findViewById(R.id.tv_hour_decade);
        tv_hour_unit = (TextView) view.findViewById(R.id.tv_hour_unit);
        tv_min_decade = (TextView) view.findViewById(R.id.tv_min_decade);
        tv_min_unit = (TextView) view.findViewById(R.id.tv_min_unit);
        tv_sec_decade = (TextView) view.findViewById(R.id.tv_sec_decade);
        tv_sec_unit = (TextView) view.findViewById(R.id.tv_sec_unit);

    }


    public void start() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, 0, 1000);
        }
    }


    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setDownTime(long time){
        int hours = (int) (time / 60 / 60);
        int min = (int) ((time - hours * 60 * 60) / 60);
        int s = (int) (time - hours * 60 * 60 - min * 60);
        setTime(hours,min,s);
    }

    public void setTime(int hour, int min, int sec) {

        if (hour >= 60 || min >= 60 || sec >= 60 || hour < 0 || min < 0
                || sec < 0) {
            throw new RuntimeException("");
        }

        hour_decade = hour / 10;
        hour_unit = hour - hour_decade * 10;

        min_decade = min / 10;
        min_unit = min - min_decade * 10;

        sec_decade = sec / 10;
        sec_unit = sec - sec_decade * 10;

        tv_hour_decade.setText(hour_decade + "");
        tv_hour_unit.setText(hour_unit + "");
        tv_min_decade.setText(min_decade + "");
        tv_min_unit.setText(min_unit + "");
        tv_sec_decade.setText(sec_decade + "");
        tv_sec_unit.setText(sec_unit + "");
    }


    private void countDown() {
        if (isCarry4Unit(tv_sec_unit)) {
            if (isCarry4Decade(tv_sec_decade)) {
                if (isCarry4Unit(tv_min_unit)) {
                    if (isCarry4Decade(tv_min_decade)) {
                        if (isCarry4Unit(tv_hour_unit)) {
                            if (isCarry4Decade(tv_hour_decade)) {
                                stop();
                                setTime(0, 0, 0);//重置为0
                                if (onTimeFinish!= null){
                                    onTimeFinish.onTimeFinish();
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private boolean isCarry4Decade(TextView tv) {

        int time = Integer.valueOf(tv.getText().toString());
        time = time - 1;
        if (time < 0) {
            time = 5;
            tv.setText(time + "");
            return true;
        } else {
            tv.setText(time + "");
            return false;
        }
    }


    private boolean isCarry4Unit(TextView tv) {

        int time = Integer.valueOf(tv.getText().toString());
        time = time - 1;
        if (time < 0) {
            time = 9;
            tv.setText(time + "");
            return true;
        } else {
            tv.setText(time + "");
            return false;
        }
    }

    public void setTimeFinish(OnTimeFinish onTimeFinish){
        this.onTimeFinish = onTimeFinish;
    }

    public void setHourHide(){
        hour_container.setVisibility(View.GONE);
        hour_separation.setVisibility(View.GONE);
    }

    public void setBackground(int resId){
        tv_hour_decade.setBackgroundResource(resId);
        tv_hour_unit.setBackgroundResource(resId);
        tv_min_decade.setBackgroundResource(resId);
        tv_min_unit.setBackgroundResource(resId);
        tv_sec_decade.setBackgroundResource(resId);
        tv_sec_unit.setBackgroundResource(resId);
    }
   public interface OnTimeFinish{
       void onTimeFinish();
    }
}