package com.crush.view.delay;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.crush.view.delay.delegate.ClickDelegate;


public class DelayClickCardView extends CardView {
  public DelayClickCardView(Context context) {
    super(context);
  }

  public DelayClickCardView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DelayClickCardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override public void setOnClickListener(@Nullable OnClickListener l) {
    super.setOnClickListener(l instanceof ClickDelegate ? l : ClickDelegate.delay(l, 1000));
  }

  public void setOnClickListener(@Nullable OnClickListener l, int delay) {
    super.setOnClickListener(ClickDelegate.delay(l, delay));
  }
}
