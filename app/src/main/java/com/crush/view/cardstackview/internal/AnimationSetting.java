package com.crush.view.cardstackview.internal;

import android.view.animation.Interpolator;

import com.crush.view.cardstackview.Direction;

public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
