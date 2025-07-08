package com.crush.dialog

import android.content.Context
import android.view.View.OnClickListener
import android.widget.TextView
import com.crush.R
import razerdp.basepopup.BasePopupWindow

class CommonDialog(var ctx: Context) : BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_common)
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

    fun setTitle(title: String): CommonDialog {
        val dialogTitle = findViewById<TextView>(R.id.dialog_title)
        dialogTitle.text = title
        return this
    }

    fun setContent(content: String): CommonDialog {
        val dialogTitle = findViewById<TextView>(R.id.dialog_content)
        dialogTitle.text = content
        return this
    }

    fun setConfirmText(confirmText: String): CommonDialog {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.text = confirmText
        return this
    }

    fun setConfirmTextBackground(resId: Int): CommonDialog {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setBackgroundResource(resId)
        return this
    }

    fun setConfirmTextColor(colorId: Int): CommonDialog {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setTextColor(colorId)
        return this
    }

    fun setCancelText(cancelText: String): CommonDialog {
        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        dialogCancel.text = cancelText
        return this
    }

    fun setConfirmListener(listener: OnClickListener): CommonDialog {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setOnClickListener(listener)
        return this
    }
}