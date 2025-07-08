package io.rong.imkit.dialog

import android.content.Context
import android.widget.TextView
import com.custom.base.config.BaseConfig
import io.rong.imkit.R
import razerdp.basepopup.BasePopupWindow

class PaymentFailedDialog(var ctx: Context, var type:Int, var listener: FirstClickPrivateListener) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_payment_failed)
        initView()
    }

    private fun initView() {
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        val dialogContent = findViewById<TextView>(R.id.dialog_content)

        when(type){
            1->{
                dialogContent.text="This is a private photo that requires unlocking to view. After all, it may contain highly sensitive content. If you're certain you want to see it, unlock it now."
            }
            2->{
                dialogContent.text="To view this private video, unlocking is required. Aren't you curious about its content? Unlock now to find out."
            }
        }
        dialogConfirm.setOnClickListener {
            listener.onListener(type)
            dismiss()
        }

        setOutSideDismiss(true)
    }

    interface FirstClickPrivateListener{
        fun onListener(type: Int)
    }

}