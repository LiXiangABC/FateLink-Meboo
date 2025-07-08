package com.crush.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.crush.R;

import io.rong.imkit.widget.refresh.api.RefreshHeader;
import io.rong.imkit.widget.refresh.api.RefreshKernel;
import io.rong.imkit.widget.refresh.api.RefreshLayout;
import io.rong.imkit.widget.refresh.constant.RefreshState;
import io.rong.imkit.widget.refresh.constant.SpinnerStyle;

public class MyClassicsHeader extends LinearLayout implements RefreshHeader {
    private LottieAnimationView animationView;
    public MyClassicsHeader(Context context) {
        super(context);
        initView(context);
    }
    public MyClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context);
    }
    public MyClassicsHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context);
    }
    private void initView(Context context) {
        setGravity(Gravity.CENTER);
        View inflate = View.inflate(context, R.layout.layout_refresh_header, null);
        animationView =  inflate.findViewById(R.id.animation_view);
        addView(inflate);
    }
    @NonNull
    public View getView() {
        return this;//真实的视图就是自己，不能返回null
    }
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }
    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int maxDragHeight) {
        animationView.playAnimation();//开始动画
    }
    @Override
    public int onFinish(RefreshLayout layout, boolean success) {

        animationView.cancelAnimation();//停止动画
        return 500;//延迟500毫秒之后再弹回
    }
    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case PullDownToRefresh: //下拉过程
                break;
            case ReleaseToRefresh: //松开刷新
                break;
            case Refreshing: //loading中
                break;
        }
    }
    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
    @Override
    public void onInitialized(RefreshKernel kernel, int height, int maxDragHeight) {
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }
    @Override
    public void setPrimaryColors(@ColorInt int ... colors){
    }
}