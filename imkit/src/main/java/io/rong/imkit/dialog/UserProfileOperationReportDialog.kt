package io.rong.imkit.dialog

import android.app.Activity
import android.view.View
import android.widget.TextView
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


class UserProfileOperationReportDialog(var ctx: Activity, var targetId: String,var isWlm:Boolean,var listener:OnListener) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_user_profile_operation_report)
        initView()
        showAnimation = AnimatorUtils.popupWindowShow()
        dismissAnimation=AnimatorUtils.popupWindowDismiss()
    }

    private fun initView() {
        val outsideContainer = findViewById<View>(R.id.outside_container)
        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        val reportList = findViewById<RecyclerView>(R.id.report_list)

        val reportTextList= arrayListOf(ctx.getString(R.string.report_one),ctx.getString(R.string.report_two),ctx.getString(R.string.report_three),ctx.getString(R.string.report_four),ctx.getString(R.string.report_five),ctx.getString(R.string.report_six),ctx.getString(R.string.report_seven))
        val reportTextAdapter = ReportTextAdapter(reportTextList, ctx,object :ReportTextAdapter.OnCallBack{
            override fun callBack(text: String) {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(API.user_add_action_url)
                        requestBody.add("opType", 4)
                        requestBody.add("reason",text)
                        requestBody.add("userCodeFriend", targetId)
                    }
                }, object : SDOkHttpResoutCallBack<BaseEntity>() {
                    override fun onSuccess(entity: BaseEntity) {
                        ToastUtil.toast(ctx.getString(R.string.report_toast))
                        dismiss()
                        listener.userReport()
                    }
                    override fun onFailure(code: Int, msg: String) {
                    }
                }, isShowDialog = false)
            }

        })
        reportList.layoutManager=LinearLayoutManager(ctx)
        reportList.adapter=reportTextAdapter
        outsideContainer.setOnClickListener {
            dismiss()
        }
        dialogCancel.setOnClickListener {
            dismiss()
        }
        setOutSideDismiss(true)
        isOutSideTouchable = true
    }

    interface OnListener{
        fun userReport()
    }
}