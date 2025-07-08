package com.crush.view.delay;

import android.content.Context;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;

public class BaseLottieAnimationView extends LottieAnimationView {

    public BaseLottieAnimationView(Context context) {
        this(context, null);
    }

    public BaseLottieAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        analyzeAttributeSet(context, attrs);
    }

    public BaseLottieAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        analyzeAttributeSet(context, attrs);
    }

    private void analyzeAttributeSet(Context context, AttributeSet attrs) {

    }
}
