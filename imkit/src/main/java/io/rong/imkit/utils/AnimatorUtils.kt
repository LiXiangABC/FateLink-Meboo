package io.rong.imkit.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.TranslateAnimation

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

    fun popupWindowShow(): TranslateAnimation {
        val animation = TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, 0f)
        animation.interpolator = AccelerateInterpolator()
        animation.duration = 200
        return animation
    }
    fun popupWindowDismiss(): TranslateAnimation {
        val animation = TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 1f)
        animation.interpolator = AccelerateInterpolator()
        animation.duration = 200
        return animation
    }
}