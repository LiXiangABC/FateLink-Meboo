package io.rong.imkit.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import io.rong.imkit.R
import razerdp.basepopup.BasePopupWindow

class CustomDialog(var ctx: Context) : BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_common)
        initView()
    }

    fun setLayoutId(layouyIdRes: Int): CustomDialog {
        setContentView(layouyIdRes)
        return this
    }

    fun setControllerListener(controlle: (dialog: CustomDialog) -> Unit): CustomDialog {
        controlle.invoke(this)
        return this
    }

    fun show(): CustomDialog {
        showPopupWindow()
        setPopupGravity(Gravity.CENTER)
        return this
    }

    fun setOnClickListener(
        idRes: Int,
        onClickListener: (dialog: CustomDialog, view: View) -> Unit
    ): CustomDialog {
        findViewById<View>(idRes).setOnClickListener {
            onClickListener.invoke(this@CustomDialog, it)
        }
        return this
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

    fun setTitle(title: String): CustomDialog {
        val dialogTitle = findViewById<TextView>(R.id.dialog_title)
        dialogTitle.text = title
        return this
    }

    fun setContent(content: String): CustomDialog {
        val dialogTitle = findViewById<TextView>(R.id.dialog_content)
        dialogTitle.text = content
        return this
    }

    fun setConfirmText(confirmText: String): CustomDialog {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.text = confirmText
        return this
    }

    fun setConfirmTextBackground(resId: Int): CustomDialog {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setBackgroundResource(resId)
        return this
    }

    fun setConfirmTextColor(colorId: Int): CustomDialog {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setTextColor(colorId)
        return this
    }

    fun setCancelText(cancelText: String): CustomDialog {
        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        dialogCancel.text = cancelText
        return this
    }

    fun setConfirmListener(listener: OnClickListener): CustomDialog {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setOnClickListener(listener)
        return this
    }
}