package com.crush.view.delay;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.crush.view.delay.delegate.ClickDelegate;

public class DelayClickConstraintLayout extends ConstraintLayout {
    public DelayClickConstraintLayout(@NonNull Context context) {
        super(context);
    }

    public DelayClickConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DelayClickConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l instanceof ClickDelegate ? l : ClickDelegate.delay(l, 1000));
    }

    public void setOnClickListener(@Nullable OnClickListener l, int delay) {
        super.setOnClickListener(ClickDelegate.delay(l, delay));
    }

}
