package io.rong.imkit.dialog

import android.content.Context
import android.widget.TextView
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.util.ToastUtil
import io.rong.imkit.API
import io.rong.imkit.R
import io.rong.imkit.entity.BaseEntity
import io.rong.imlib.RongIMClient
import razerdp.basepopup.BasePopupWindow

class UnblockUserDialog(var ctx: Context,var userId:String) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.popup_unblock_user)
        initView()
    }

    private fun initView() {
        val popupConfirm = findViewById<TextView>(R.id.popup_confirm)
        val popupCancel = findViewById<TextView>(R.id.popup_cancel)

        popupConfirm.setOnClickListener {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(API.user_add_action_url)
                    requestBody.add("opType", 2)
                    requestBody.add("userCodeFriend",userId)
                }
            }, object : SDOkHttpResoutCallBack<BaseEntity>() {
                override fun onSuccess(entity: BaseEntity) {
                    ToastUtil.toast("Unblock successful. You can now interact with this user.")
                }
                override fun onFailure(code: Int, msg: String) {
                }
            })
            dismiss()
        }
        popupCancel.setOnClickListener {
            dismiss()
        }

        setOutSideDismiss(true)
    }

}