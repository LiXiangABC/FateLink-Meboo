package com.crush.view.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

/**
 * @Author andy
 * @Date 2024/4/17 13:02
 * 头像从左往右或者从右往左自动滚动
 */
public class SocllRecyclerView extends RecyclerView {
    private Autoaaview autoview;
    private boolean running;
    private boolean canrun;
    private int x;
    private static final int Timea = 40;//控制滚动的速度，值越大速度越慢

    public SocllRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        autoview = new Autoaaview(this);
    }

    public SocllRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    private class Autoaaview implements Runnable{
        WeakReference<SocllRecyclerView> myScrViewWeakReference;
        public Autoaaview(SocllRecyclerView myScrView) {
            myScrViewWeakReference = new WeakReference<>(myScrView);
        }

        @Override
        public void run() {
            SocllRecyclerView myScrView = myScrViewWeakReference.get();
            if (myScrView.canrun&&myScrView.running){
                myScrView.scrollBy(x,2);
                myScrView.postDelayed(myScrView.autoview,Timea);
            }
        }
    }
    //开始滚动
    public void start(int xParam){
        if (running)
            stop();
        x = xParam;
        running = true;
        canrun = true;
        postDelayed(autoview,Timea);
    }
    //停止滚动
    public void stop() {
        running = false;
        removeCallbacks(autoview);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }
}
