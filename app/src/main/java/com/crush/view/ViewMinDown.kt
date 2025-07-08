package com.crush.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.crush.R
import com.crush.ui.index.helper.IndexHelper
import com.crush.util.MyCountDownTimer

class ViewMinDown(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var view: View? = null
    private var txtLimitedSpecial: TextView? = null
    private var dowanTime: MyCountDownTimer? = null

    init {
        view = LayoutInflater.from(context).inflate(R.layout.view_min_down_time, this)
        initView()
    }

    fun initView() {
        txtLimitedSpecial = view?.findViewById(R.id.txt_down_time)
    }

    fun setDownTime(activity: Activity?, time: Long) {
        this.isVisible = time > 0
        if (time < 1) {
            return
        }
        dowanTime = IndexHelper.convertDownTime(time, 1000, onTick = { hour, minute, second ->
            if (activity == null || activity.isDestroyed) {
                dowanTime?.cancel()
                return@convertDownTime
            }
            txtLimitedSpecial?.text = IndexHelper.convertDownTimeStr(hour, minute, second)
        }, onFinish = {
            //倒计时结束
            this@ViewMinDown.isVisible = false
        })
        dowanTime?.start()
    }

    fun cancelTime() {
        dowanTime?.cancel()
        dowanTime = null
    }


}