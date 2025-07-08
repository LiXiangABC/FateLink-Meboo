package com.crush.util

import android.os.CountDownTimer

class MyCountDownTimer(
    millisInFuture: Long,
    countDownInterval: Long,
    val downTime: (millisUntilFinished : Long,isFinish: Boolean) -> Unit
) : CountDownTimer(millisInFuture, countDownInterval) {
    override fun onTick(millisUntilFinished: Long) {
        downTime.invoke(millisUntilFinished,false)
    }

    override fun onFinish() {
        downTime.invoke(0,true)
    }
}