package io.rong.imkit.dialog

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.widget.TextView
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import io.rong.imkit.API
import io.rong.imkit.R
import io.rong.imkit.entity.BaseEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import razerdp.basepopup.BasePopupWindow

/**
 * 将支付失败旧的弹窗挪过来
 * 因为业务需求没办法
 */
class PayFailedTipOldDialog(
    var ctx: Context,
    var entity: OrderCreateEntity,
    var callback: CallBack
) : BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_pay_failed_tips_old)
        initView()
    }

    interface CallBack {
        fun sendEmail()
    }

    private fun initView() {
        val dialogAbandon = findViewById<TextView>(R.id.dialog_abandon)
        dialogAbandon.setOnClickListener {
            dismiss()
        }
        val dialogTryItAgain = findViewById<TextView>(R.id.dialog_try_it_again)

        dialogTryItAgain.setOnClickListener {
            PayUtils.instance.start(
                entity,
                ctx,
                object : EmptySuccessCallBack {
                    override fun OnSuccessListener() {
                    }

                })
            dismiss()
        }
        val dialogContactEmail = findViewById<TextView>(R.id.dialog_contact_email)
        dialogContactEmail.paint.flags = Paint.UNDERLINE_TEXT_FLAG

        dialogContactEmail.setOnClickListener {
            callback.sendEmail()
//            val emailTitle = String.format(ctx.getString(R.string.send_email_default_title),
//                BaseConfig.getInstance.getString(SpName.userCode,""), BuildConfig.VERSION_NAME)
//            SystemAppCallUtil().sendEmail(ctx,emailTitle,ctx.getString(R.string.send_email_default_txt))
            dismiss()
        }
        setOutSideDismiss(true)

        payFail()
    }

    private fun payFail() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.pay_fail_url)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {

            }

        })
    }
}