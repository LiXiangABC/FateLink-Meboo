package com.crush.dialog

import android.content.Context
import android.os.Handler
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.R
import com.custom.base.config.BaseConfig
import io.rong.imkit.SpName
import razerdp.basepopup.BasePopupWindow

class WlmTipsDialog(var ctx: Context, var content: String) : BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_wlm_tips)
        initView()
        if (!BaseConfig.getInstance.getBoolean(SpName.wlmTopTipDialogShow, false)) {
            Handler().postDelayed({
                dismiss()
            }, 6000)
        }
    }

    private fun initView() {
        val tipDialog = findViewById<ConstraintLayout>(R.id.tip_dialog)
        tipDialog.setOnClickListener {
            dismiss()
        }
        val dialogContent = findViewById<TextView>(R.id.dialog_content)
        if (content != "") {
            dialogContent.text = content
        }
        setOutSideDismiss(true)
    }

    interface OnCallBack {
        fun callBack()
    }
}