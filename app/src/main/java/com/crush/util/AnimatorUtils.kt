package com.crush.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.BounceInterpolator

object AnimatorUtils {
    fun playBounceAnimX(target: View, startX:Float,endX:Float,duration: Long,delay:Long){
        val drop = ObjectAnimator.ofFloat(target,"X", startX,endX)
        drop.duration = duration
        drop.interpolator = BounceInterpolator()
        val set = AnimatorSet()
        set.startDelay=delay
        set.playSequentially(drop)
        set.start()
    }
}