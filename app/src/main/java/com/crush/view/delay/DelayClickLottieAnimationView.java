package com.crush.view.delay;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;

import androidx.annotation.Nullable;

import com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.crush.view.delay.delegate.ClickDelegate;

import org.jetbrains.annotations.NotNull;


public class DelayClickLottieAnimationView extends BaseLottieAnimationView {

  public DelayClickLottieAnimationView(Context context) {
    super(context);
  }

  public DelayClickLottieAnimationView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DelayClickLottieAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override public void setOnClickListener(@Nullable OnClickListener l) {
    super.setOnClickListener(l instanceof ClickDelegate ? l : ClickDelegate.delay(l, 600));
  }

  public void setOnClickListener(@Nullable OnClickListener l, int delay) {
    super.setOnClickListener(ClickDelegate.delay(l, delay));
  }

    public void addAnimatorListener(@NotNull Animator.AnimatorListener listener) {
      super.addAnimatorListener(listener);
    }
}
