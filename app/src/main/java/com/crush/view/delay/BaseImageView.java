package com.crush.view.delay;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class BaseImageView extends AppCompatImageView {

  public BaseImageView(Context context) {
    this(context, null);
  }

  public BaseImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    analyzeAttributeSet(context, attrs);
  }

  public BaseImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    analyzeAttributeSet(context, attrs);
  }

  private void analyzeAttributeSet(Context context, AttributeSet attrs) {

  }
}
