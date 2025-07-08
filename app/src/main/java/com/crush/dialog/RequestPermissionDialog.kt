package com.crush.dialog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.TextView
import com.crush.R
import razerdp.basepopup.BasePopupWindow

/**
 * 权限请求
 */
class RequestPermissionDialog(var ctx: Activity,var type:Int) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_request_permission)
        initView()
    }

    private fun initView() {
        val dialogTitle = findViewById<TextView>(R.id.dialog_title)
        val dialogContent = findViewById<TextView>(R.id.dialog_content)
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)
        when(type){
            1->{
                dialogTitle.text=ctx.getString(R.string.camera_permission_title)
                dialogContent.text=ctx.getString(R.string.camera_permission_content)
            }
            2->{
                dialogTitle.text=ctx.getString(R.string.notification_permission_title)
                dialogContent.text=ctx.getString(R.string.notification_permission_content)
            }
            3->{
                dialogTitle.text=ctx.getString(R.string.location_permission_title)
                dialogContent.text=ctx.getString(R.string.location_permission_content)
            }
            4->{
                dialogTitle.text=ctx.getString(R.string.photo_permission_title)
                dialogContent.text=ctx.getString(R.string.photo_permission_content)
            }
        }

        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        dialogCancel.setOnClickListener {
            dismiss()
        }
        dialogConfirm.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package",ctx.packageName, null)
            intent.data = uri
            ctx.startActivityForResult(intent, 10001)
            dismiss()
        }
        setOutSideDismiss(true)
    }
}