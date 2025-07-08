package com.crush.dialog

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crush.Constant
import com.crush.R
import com.crush.adapter.ReportTextAdapter
import com.crush.entity.MatchIndexEntity
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.utils.FirebaseEventUtils
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.util.ToastUtil
import io.rong.imkit.utils.AnimatorUtils
import razerdp.basepopup.BasePopupWindow

class UserProfileOperationDialog(var ctx: Activity,var bundle: Bundle,var listener:UserActionListener) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_user_profile_operation)
        initView()
        showAnimation = AnimatorUtils.popupWindowShow()
        dismissAnimation= AnimatorUtils.popupWindowDismiss()
    }

    private fun initView() {
        val outsideContainer = findViewById<View>(R.id.outside_container)
        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)

        outsideContainer.setOnClickListener {
            dismiss()
        }
        dialogCancel.setOnClickListener {
            dismiss()
        }
        val dialogReport = findViewById<TextView>(R.id.dialog_report)
        val dialogBlock = findViewById<TextView>(R.id.dialog_block)
        dialogReport.setOnClickListener {
            dismiss()
            listener.userReport()
        }
        dialogBlock.setOnClickListener {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_add_action_url)
                    requestBody.add("opType", 1)
                    requestBody.add("userCodeFriend", bundle.getString("userCodeFriend",""))
                }
            }, object : SDOkHttpResoutCallBack<MatchIndexEntity>() {
                override fun onSuccess(entity: MatchIndexEntity) {
                    ToastUtil.toast(ctx.getString(R.string.black_toast))
                    dismiss()
                    listener.userBlack()
                }


                override fun onFailure(code: Int, msg: String) {
                }
            }, isShowDialog = false)
        }
        setOutSideDismiss(true)
        isOutSideTouchable = true
    }

    interface UserActionListener{
        fun userReport()
        fun userBlack()
    }
}