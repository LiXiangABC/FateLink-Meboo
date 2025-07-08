package com.crush.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import com.crush.R
import razerdp.basepopup.BasePopupWindow

class ChangeMembershipPackageDialog(ctx: Context) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_change_membership_package)
        initView()
    }

    private fun initView() {
        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        dialogCancel.setOnClickListener {
            dismiss()
        }
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setOnClickListener {
            dismiss()
        }
        setOutSideDismiss(true)
    }

    interface ChangeMembershipListener{
        fun onListener()
    }

}