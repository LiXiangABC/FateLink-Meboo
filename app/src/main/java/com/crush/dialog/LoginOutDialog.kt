package com.crush.dialog

import android.content.Context
import android.widget.TextView
import com.crush.R
import razerdp.basepopup.BasePopupWindow

class LoginOutDialog(var ctx: Context,var callback:OnCallBack) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_log_out)
        initView()
    }

    private fun initView() {
        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        dialogCancel.setOnClickListener {
            dismiss()
        }
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setOnClickListener {
            callback.callBack()
            dismiss()
        }
        setOutSideDismiss(true)
    }
    interface OnCallBack{
        fun callBack()
    }
}