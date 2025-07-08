package com.crush.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import com.crush.R
import razerdp.basepopup.BasePopupWindow

class UpdateApkDialog(ctx: Context, var content:String, var force:Int) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_update_apk)
        initView()
    }

    private fun initView() {
        val dialogContent = findViewById<TextView>(R.id.dialog_content)
        dialogContent.text=content

        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        dialogCancel.visibility=if (force==1) View.GONE else View.VISIBLE
        dialogCancel.setOnClickListener {
            dismiss()
        }
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        dialogConfirm.setOnClickListener {
            openAppsMarket("",context)
        }
        setOutSideDismiss(false)
    }
    private fun openAppsMarket(url: String,context: Context) {
        //goto google play detail page
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=" + context.packageName)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
//                ToastUtil.showToast("Please visit $url")
//                startActivity(WebViewActivity.callIntent(this, "", url))
            }
        }
    }
}