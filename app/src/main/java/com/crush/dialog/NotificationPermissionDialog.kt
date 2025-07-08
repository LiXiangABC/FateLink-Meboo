package com.crush.dialog

import android.Manifest
import android.app.Activity
import android.os.Build
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.crush.R
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.util.PermissionUtils
import com.custom.base.config.BaseConfig
import io.rong.imkit.SpName
import razerdp.basepopup.BasePopupWindow

/**
 * 权限请求
 */
class NotificationPermissionDialog(var ctx: Activity, var nickName:String,var listener:OnListener) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_notification_permission)
        initView()
        showAnimation = AnimationUtils.loadAnimation(ctx, R.anim.dialog_anim_enter)
        dismissAnimation=AnimationUtils.loadAnimation(ctx, R.anim.dialog_anim_exit)
        setOutSideDismiss(true)
    }

    private fun initView() {
        BaseConfig.getInstance.setBoolean(SpName.firstShowNotification+nickName,true)
        val dialogTitle = findViewById<TextView>(R.id.dialog_title)
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)

        if (nickName!= ""){
            dialogTitle.text="${ctx.getString(R.string.want_to_stay_in_touch_with)} $nickName ?"
//            HttpRequest.commonNotify(501,"")
            DotLogUtil.setEventName(DotLogEventName.NOTIFICATION_PAGE_WITH_EFFECTIVE_CHAT).commit(ctx)

        }else{
//            HttpRequest.commonNotify(500,"")
            DotLogUtil.setEventName(DotLogEventName.FIRST_NOTIFICATION_PAGE).commit(ctx)
        }
        val dialogCancel = findViewById<ImageView>(R.id.dialog_cancel)
        dialogCancel.setOnClickListener {
            dismiss()
        }
        dialogConfirm.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionUtils.requestPermission(ctx,
                    {
                        listener.onGrantedListener()
//                        HttpRequest.commonNotify(503,if (nickName!= "")"501" else "500")
                        DotLogUtil.setEventName(DotLogEventName.NOTIFICATION_PAGE_GRANTED).setRemark(if (nickName!= "")"501" else "500").commit(ctx)

                    }, {
                        listener.onDeniedListener()
//                        HttpRequest.commonNotify(503,if (nickName!= "")"501" else "500")
                        DotLogUtil.setEventName(DotLogEventName.NOTIFICATION_PAGE_GRANTED).setRemark(if (nickName!= "")"501" else "500").commit(ctx)
                    },Manifest.permission.POST_NOTIFICATIONS)
            }
            dismiss()
        }
        setOutSideDismiss(true)
    }

    interface OnListener{
        fun onGrantedListener()
        fun onDeniedListener()
    }
}