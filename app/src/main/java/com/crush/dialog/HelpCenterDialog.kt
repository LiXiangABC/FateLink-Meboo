package com.crush.dialog

import android.app.Activity
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import com.crush.BuildConfig
import com.crush.R
import com.crush.util.SystemAppCallUtil
import com.custom.base.config.BaseConfig
import io.rong.imkit.SpName
import razerdp.basepopup.BasePopupWindow

class HelpCenterDialog(var ctx: Activity) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_help_center)
        initView()
    }

    private fun initView() {
        val dialogContent = findViewById<TextView>(R.id.dialog_content)
        val spannableStringBuilder = SpannableStringBuilder(ctx.getString(R.string.help_tip))
        spannableStringBuilder.setSpan(ForegroundColorSpan(Color.parseColor("#44F3C4")), 14, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        dialogContent.text = spannableStringBuilder
        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        val imgDialogCancel = findViewById<ImageView>(R.id.img_dialog_cancel)
        imgDialogCancel.setOnClickListener {
            dismiss()
        }
        dialogCancel.setOnClickListener {
            dismiss()
        }
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setOnClickListener {
            val emailTitle = String.format(ctx.getString(R.string.send_email_default_title),
                BaseConfig.getInstance.getString(SpName.userCode,""), BuildConfig.VERSION_NAME)
            SystemAppCallUtil().sendEmail(ctx,emailTitle,ctx.getString(R.string.send_email_default_txt))
            dismiss()
        }
        setOutSideDismiss(true)
    }
}