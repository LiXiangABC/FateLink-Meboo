package com.crush.dialog

import android.content.Context
import android.widget.TextView
import com.crush.R
import razerdp.basepopup.BasePopupWindow


class GoogleNoRemindDialog(var ctx: Context,val callBack:()->Unit) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_google_no_remind)
        initView()
        isOutSideTouchable = true

    }

    private fun initView() {
        findViewById<TextView>(R.id.dialog_cancel).setOnClickListener {
            dismiss()
        }
        findViewById<TextView>(R.id.dialog_submit).setOnClickListener {
            callBack.invoke()
            dismiss()
        }


    }

}