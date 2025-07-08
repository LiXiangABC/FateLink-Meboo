package com.crush.view.delay;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.crush.view.delay.delegate.ClickDelegate;


public class DelayClickTextView extends BaseTextView {
  public DelayClickTextView(Context context) {
    super(context);
  }

  public DelayClickTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DelayClickTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override public void setOnClickListener(@Nullable OnClickListener l) {
    super.setOnClickListener(l instanceof ClickDelegate ? l : ClickDelegate.delay(l, 600));
  }

  public void setOnClickListener(@Nullable OnClickListener l, int delay) {
    super.setOnClickListener(ClickDelegate.delay(l, delay));
  }
}
