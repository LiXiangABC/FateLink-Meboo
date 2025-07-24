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
import com.crush.util.PermissionUtil
import com.custom.base.config.BaseConfig
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import razerdp.basepopup.BasePopupWindow

/**
 * 权限请求
 */
class NotificationPermissionDialog(var activity: Activity, var nickName:String,var listener:OnListener) :  BasePopupWindow(activity) {
    init {
        setContentView(R.layout.dialog_notification_permission)
        initView()
        showAnimation = AnimationUtils.loadAnimation(activity, R.anim.dialog_anim_enter)
        dismissAnimation=AnimationUtils.loadAnimation(activity, R.anim.dialog_anim_exit)
        setOutSideDismiss(true)
    }

    private fun initView() {
        BaseConfig.getInstance.setBoolean(SpName.firstShowNotification+nickName,true)
        val dialogTitle = findViewById<TextView>(R.id.dialog_title)
        val dialogConfirm = findViewById<TextView>(R.id.dialog_confirm)

        if (nickName!= ""){
            dialogTitle.text="${activity.getString(R.string.want_to_stay_in_touch_with)} $nickName ?"
//            HttpRequest.commonNotify(501,"")
            DotLogUtil.setEventName(DotLogEventName.NOTIFICATION_PAGE_WITH_EFFECTIVE_CHAT).commit(activity)

        }else{
//            HttpRequest.commonNotify(500,"")
            DotLogUtil.setEventName(DotLogEventName.FIRST_NOTIFICATION_PAGE).commit(activity)
        }
        val dialogCancel = findViewById<ImageView>(R.id.dialog_cancel)
        dialogCancel.setOnClickListener {
            dismiss()
        }
        dialogConfirm.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Activities.get().top?.let { it1 ->
                    PermissionUtil.requestPermissionCallBack(Manifest.permission.POST_NOTIFICATIONS, activity = it1)
                    {
                        if(it) {
                            listener.onGrantedListener()
                            DotLogUtil.setEventName(DotLogEventName.NOTIFICATION_PAGE_GRANTED).setRemark(if (nickName != "") "501" else "500").commit(activity)
                        }else {
                            listener.onDeniedListener()
                            DotLogUtil.setEventName(DotLogEventName.NOTIFICATION_PAGE_GRANTED).setRemark(if (nickName != "") "501" else "500").commit(activity)
                        }
                    }
                }
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