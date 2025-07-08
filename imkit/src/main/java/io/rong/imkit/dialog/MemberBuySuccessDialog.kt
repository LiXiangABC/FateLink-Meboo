package io.rong.imkit.dialog

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import io.rong.imkit.R
import razerdp.basepopup.BasePopupWindow

class MemberBuySuccessDialog(var ctx: Context, var listener :ChangeMembershipListener) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_member_buy_success)
        initView()
    }

    private fun initView() {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setOnClickListener {
            listener.onListener()
            dismiss()
        }

        setOutSideDismiss(true)
    }

    interface ChangeMembershipListener{
        fun onListener()
    }

}