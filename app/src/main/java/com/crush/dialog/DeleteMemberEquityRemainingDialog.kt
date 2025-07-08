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
import io.rong.imkit.SpName
import com.crush.entity.QueryBenefitsEntity
import com.custom.base.config.BaseConfig
import razerdp.basepopup.BasePopupWindow

class DeleteMemberEquityRemainingDialog(var ctx: Context, var entity: QueryBenefitsEntity, var listener:OnListener) : BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_delete_member_equity_remaining)
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
        if (BaseConfig.getInstance.getBoolean(SpName.privatePhotoShowUnableFlag,false) || BaseConfig.getInstance.getInt(SpName.trafficSource,0)==1) {
            style.append("Unused Boost services (flash chat * ${entity.data[0].maxUses}) still available. Deleting your account forfeits these services.Confirm deletion?")
            style.setSpan(object : ClickableSpan() {
                override fun onClick(p0: View) {
                }
                override fun updateDrawState(ds: TextPaint) {
                    ds.color = Color.parseColor("#FF465D")
                    ds.isUnderlineText = false
                    ds.isFakeBoldText=true
                    ds.clearShadowLayer()
                }
            }, 22, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        } else {
            style.append("Unused Boost services (flash chat * ${entity.data[0].maxUses}, private photo * ${entity.data[1].maxUses}, private video * ${entity.data[2].maxUses}) still available. Deleting your account forfeits these services.Confirm deletion?")
            style.setSpan(object : ClickableSpan() {
                override fun onClick(p0: View) {
                }
                override fun updateDrawState(ds: TextPaint) {
                    ds.color = Color.parseColor("#FF465D")
                    ds.isUnderlineText = false
                    ds.isFakeBoldText=true
                    ds.clearShadowLayer()
                }
            }, 22, 77, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }






        dialogContent.text = style
        dialogContent.movementMethod = LinkMovementMethod.getInstance()
    }

    interface OnListener{
        fun onListener()
    }

}