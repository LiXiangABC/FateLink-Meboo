package io.rong.imkit.dialog

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.rong.imkit.R
import io.rong.imkit.http.HttpRequest
import razerdp.basepopup.BasePopupWindow

class GoogleEvaluateDialog(var ctx: Context,var num: String, var callback: OnCallBack) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_google_evaluate)
        initView()
    }

    private fun initView() {
        HttpRequest.commonNotify(801,"EVALUATE_POP")
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)
        dialogClose.setOnClickListener {
            HttpRequest.commonNotify(803,"EVALUATE_DISLIKE")
            dismiss()
        }
        val dialogContent = findViewById<TextView>(R.id.dialog_content)
        dialogContent.text="We truly value your feedback on the Play Store! \uD83D\uDC49Kindly leave us a positive review (with more than 10 characters) and rate us with 5 stars.⭐️⭐️⭐️⭐️⭐️\n You'll receive your gifts immediately!"
        val dialogFlashChatNum = findViewById<TextView>(R.id.dialog_flash_chat_num)


        dialogFlashChatNum.text="Flash chat * $num"
        val drawable = ContextCompat.getDrawable(ctx, R.drawable.icon_boots_flash_chat)
        val drawableWidth = 40
        val drawableHeight = 40
        drawable?.setBounds(0, 0, drawableWidth, drawableHeight)
        dialogFlashChatNum.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setOnClickListener {
            HttpRequest.commonNotify(802,num)
            callback.callBack()
            dismiss()
        }
        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        dialogCancel.setOnClickListener {
            HttpRequest.commonNotify(803,"EVALUATE_DISLIKE")
            dismiss()
        }
        setOutSideDismiss(true)
    }
    interface OnCallBack{
        fun callBack()
    }
}