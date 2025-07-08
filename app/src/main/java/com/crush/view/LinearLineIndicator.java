package com.crush.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.crush.R;
import com.youth.banner.indicator.BaseIndicator;
import com.youth.banner.util.BannerUtils;

public class LinearLineIndicator extends BaseIndicator {
    private int indicatorViewGravity = 0;
    private boolean indicatorChangeColor = false;
    private boolean indicatorLastChangeColor = false;
    public LinearLineIndicator(Context context) {
        this(context, (AttributeSet)null);
    }

    public LinearLineIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLineIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    @Override
    public View getIndicatorView() {
        if (this.config.isAttachToBanner()) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            switch (this.config.getGravity()) {
                case 0:
                    layoutParams.gravity = 8388691;
                    break;
                case 1:
                    if (indicatorViewGravity == 1){
                        layoutParams.gravity = Gravity.TOP | Gravity.CENTER;
                    }else {
                        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
                    }
                    break;
                case 2:
                    layoutParams.gravity = 8388693;
            }
            layoutParams.leftMargin = this.config.getMargins().leftMargin;
            layoutParams.rightMargin = this.config.getMargins().rightMargin;
            layoutParams.topMargin = this.config.getMargins().topMargin;
            layoutParams.bottomMargin = this.config.getMargins().bottomMargin;
            this.setLayoutParams(layoutParams);
        }

        return this;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = this.config.getIndicatorSize();
        if (count > 1) {
            int width = (int) ((count - 1) * this.config.getIndicatorSpace() + BannerUtils.dp2px(16) + BannerUtils.dp2px(16)* (count - 1));
            this.setMeasuredDimension(width, (int) BannerUtils.dp2px(4));
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = this.config.getIndicatorSize();
        if (count > 1) {
            float left = 0.0F;
            for(int i = 0; i < count; ++i) {
                if (indicatorChangeColor){
                    this.mPaint.setColor(this.config.getCurrentPosition() == i ?( this.config.getCurrentPosition()==0 || ( this.config.getCurrentPosition()==1 && indicatorLastChangeColor))?Color.BLACK: Color.WHITE :( this.config.getCurrentPosition()==0 || ( this.config.getCurrentPosition()==1 && indicatorLastChangeColor))? ContextCompat.getColor(getContext(), R.color.color_80000000) : ContextCompat.getColor(getContext(), R.color.color_80FFFFFF));
                }else {
                    this.mPaint.setColor(this.config.getCurrentPosition() == i ? Color.BLACK : this.config.getNormalColor());
                }
                float indicatorWidth = BannerUtils.dp2px(16);
                float radius = BannerUtils.dp2px(2);
                RectF rect = new RectF(left, 0, left + indicatorWidth, BannerUtils.dp2px(4));
                canvas.drawRoundRect(rect, radius, radius, mPaint);
                left = rect.right + this.config.getIndicatorSpace();


            }
        }

    }

    public void setIndicatorViewGravity(int indicatorViewGravity) {
        this.indicatorViewGravity = indicatorViewGravity;
    }

    public void setIndicatorChangeColor(boolean indicatorChangeColor) {
        this.indicatorChangeColor = indicatorChangeColor;
    }

    public void setIndicatorLastChangeColor(boolean indicatorLastChangeColor) {
        this.indicatorLastChangeColor = indicatorLastChangeColor;
    }
}