package io.rong.imkit.dialog

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import io.rong.imkit.IMCenter
import io.rong.imkit.R
import io.rong.imkit.http.HttpRequest.showBuyMember
import razerdp.basepopup.BasePopupWindow

class PictureMessageLimitDialog(var ctx: Context, var targetId:String) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_picture_message_limit)
        initView()
        setOverlayMask(true)
    }

    private fun initView() {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)

        dialogConfirm.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                showBuyMember(ctx, 3, targetId)
            },300)
            dismiss()
        }

        dialogClose.setOnClickListener {
            dismiss()
        }

        setOutSideDismiss(true)
    }

    interface FirstClickPrivateListener{
        fun onListener(type: Int)
    }

}