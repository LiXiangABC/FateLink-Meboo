package com.crush.dialog

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.crush.R
import com.crush.util.DateUtils
import razerdp.basepopup.BasePopupWindow

class DeleteMemberExpireDateDialog(var ctx: Context,var expireDate:String,var listener:OnListener) : BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_delete_member_expire_date)
        initView()
        setOutSideDismiss(true)
    }

    private fun initView() {
        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        dialogCancel.setOnClickListener {
            dismiss()
        }
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setOnClickListener {
            listener.onListener()
            dismiss()
        }
        val dialogContent = findViewById<TextView>(R.id.dialog_content)

        val style = SpannableStringBuilder()
        val date = DateUtils.getDate(expireDate, "yyyy-MM-dd HH:mm:ss")
        style.append("Premium benefits valid until ${DateUtils.getTime(date)}. Deleting account forfeits benefits and cancels automatic subscription. Confirm deletion?")

        style.setSpan(object : ClickableSpan() {
            override fun onClick(p0: View) {
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.color = Color.parseColor("#FF465D")
                ds.isUnderlineText = false
                ds.clearShadowLayer()
            }
        }, 29, 40, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)



        dialogContent.text = style
        dialogContent.movementMethod = LinkMovementMethod.getInstance()
    }

    interface OnListener{
        fun onListener()
    }

}