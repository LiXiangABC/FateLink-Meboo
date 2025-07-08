package io.rong.imkit.dialog

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.util.ToastUtil
import io.rong.imkit.API
import io.rong.imkit.R
import io.rong.imkit.adapter.ReportTextAdapter
import io.rong.imkit.entity.BaseEntity
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.utils.AnimatorUtils
import io.rong.imkit.utils.FirebaseEventUtils
import razerdp.basepopup.BasePopupWindow

class UserProfileOperationDialog(var ctx: Activity,var targetId: String,var listener:UserActionListener) :  BasePopupWindow(ctx) {
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
            listener.userReport()
            dismiss()
        }
        dialogBlock.setOnClickListener {
            FirebaseEventUtils.logEvent(FirebaseEventTag.Chat_More_Block.name)
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(API.user_add_action_url)
                    requestBody.add("opType", 1)
                    requestBody.add("userCodeFriend",targetId)
                }
            }, object : SDOkHttpResoutCallBack<BaseEntity>() {
                override fun onSuccess(entity: BaseEntity) {
                    ToastUtil.toast(ctx.getString(R.string.black_toast))
                    FirebaseEventUtils.logEvent(FirebaseEventTag.Chat_More_Block.name)

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