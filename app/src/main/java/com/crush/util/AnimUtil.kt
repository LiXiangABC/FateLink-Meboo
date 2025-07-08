package com.crush.util

import android.os.Vibrator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.crush.App
import com.crush.R

class AnimUtil {
    fun shake(view: View) {
        Thread {
            val shake: Animation = AnimationUtils.loadAnimation(App.appInterface, R.anim.shake) //加载动画资源文件
            val vibrator = App.appInterface?.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(100)
            view.startAnimation(shake)
        }.start()
    }

}