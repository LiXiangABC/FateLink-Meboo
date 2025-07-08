package io.rong.imkit.pay

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingClient
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import io.rong.imkit.API
import io.rong.imkit.R
import io.rong.imkit.entity.BaseEntity
import io.rong.imkit.entity.PayErrorBean
import razerdp.basepopup.BasePopupWindow

/**
 * 支付报错提示弹窗
 */
class PayErrorTipDialog(
    var context: Context,
    var code: Int,
    var errorStr: String,
    var callback: Callback
) : BasePopupWindow(context) {

    interface Callback {
        fun onBackClick(type: Int)
    }

    init {
        Log.i("PayErrorUtils", "showPayErrorDialog init error=$errorStr  code=$code")
        setContentView(R.layout.dialog_pay_error_tip)
        initialViews()
    }

    override fun onShowing() {
        super.onShowing()
    }

    override fun onDismiss() {
        super.onDismiss()
    }


    private var adapter: PayErrorTipAdapter? = null
    private fun initialViews() {
        //标题
        val txtPayErrorTipTitle = findViewById<TextView>(R.id.txtPayErrorTipTitle)
        //副标题
        val txtPayErrorTipSubTitle = findViewById<TextView>(R.id.txtPayErrorTipSubTitle)
        //内容
        val rvPayErrorTip = findViewById<RecyclerView>(R.id.rvPayErrorTip)
        //联系我们提示
        val txtPayErrorTipContact = findViewById<TextView>(R.id.txtPayErrorTipContact)
        //邮箱
        val txtPayErrorTipContactEmail = findViewById<TextView>(R.id.txtPayErrorTipContactEmail)
        //按钮
        val txtPayErrorTipBtn = findViewById<TextView>(R.id.txtPayErrorTipBtn)
        val txtPayErrorTipAbandonBtn = findViewById<TextView>(R.id.txtPayErrorTipAbandonBtn)
        val conPayErrorBox = findViewById<ConstraintLayout>(R.id.conPayErrorBox)

        //Log.i("支付报错弹窗信息", "错误信息=${errorStr}")
        errorStr?.let {
            //设置标题和副标题
            if (code == BillingClient.BillingResponseCode.USER_CANCELED) {
                txtPayErrorTipTitle.text =
                    context.resources.getString(R.string.txt_pay_error_tip_cancel_title)
                txtPayErrorTipSubTitle.text =
                    context.resources.getString(R.string.txt_pay_error_tip_subtitle_cancel)

                txtPayErrorTipTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
                txtPayErrorTipSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    txtPayErrorTipSubTitle.typeface =
                        context.resources.getFont(R.font.cabin_medium)
                }
            } else {
                txtPayErrorTipTitle.text =
                    context.resources.getString(R.string.txt_pay_error_tip_title)
                txtPayErrorTipSubTitle.text =
                    context.resources.getString(R.string.txt_pay_error_tip_subtitle)
            }
        }
        //设置联系邮箱
        txtPayErrorTipContact.text =
            context.resources.getString(R.string.txt_pay_error_tip_contact)
        txtPayErrorTipContactEmail.text =
            context.resources.getString(R.string.txt_pay_error_tip_contact_email)

        rvPayErrorTip.layoutManager = LinearLayoutManager(context)

        //点击ok按钮
        txtPayErrorTipBtn.setOnClickListener {
            if (txtPayErrorTipBtn.text.toString() == context.resources.getString(R.string.txt_pay_error_tip_btn_one)) {
                callback.onBackClick(2)
                dismiss()
            } else if (txtPayErrorTipBtn.text.toString() == context.resources.getString(R.string.txt_pay_error_tip_btn_three)) {
                callback.onBackClick(3)
                dismiss()
            } else {
                dismiss()
            }
        }

        conPayErrorBox.setOnClickListener {
            dismiss()
        }

        //点击邮箱
        txtPayErrorTipContactEmail.setOnClickListener {
            callback.onBackClick(1)
        }
        //如果是取消的报错原因就隐藏列表
        if (code == BillingClient.BillingResponseCode.USER_CANCELED) {
            rvPayErrorTip.visibility = View.GONE
            txtPayErrorTipBtn.text =
                context.resources.getString(R.string.txt_pay_error_tip_btn_two)
            return
        }
        if (SDActivityManager.instance.lastActivity != null && !SDActivityManager.instance.lastActivity.isFinishing &&
            !SDActivityManager.instance.lastActivity.isDestroyed
        ) {
            if (code == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                if (!TextUtils.isEmpty(errorStr)) {
                    if (errorStr.contains(context.resources.getString(R.string.txt_pay_error_billing_unavailable_one))) {
                        //BILLING_UNAVAILABLE, Debug Message: Google Play In-app Billing API version is less than 3
                        txtPayErrorTipBtn.text =
                            context.resources.getString(R.string.txt_pay_error_tip_btn_one)
                        val list = arrayListOf<PayErrorBean>().apply {
                            add(
                                PayErrorBean(
                                    icon = R.mipmap.img_pay_error_one,
                                    title = null,
                                    content = context.resources.getString(R.string.txt_pay_error_tip_content_one)
                                )
                            )
                        }
                        adapter =
                            PayErrorTipAdapter(list, SDActivityManager.instance.lastActivity)
                    } else if (errorStr.contains(context.resources.getString(R.string.txt_pay_error_billing_unavailable_two))) {
                        //BILLING_UNAVAILABLE, Debug Message: Billing service unavailable on device.
                        txtPayErrorTipBtn.text =
                            context.resources.getString(R.string.txt_pay_error_tip_btn_two)
                        val list = arrayListOf<PayErrorBean>().apply {
                            add(
                                PayErrorBean(
                                    icon = R.mipmap.img_pay_error_two,
                                    title = null,
                                    content = context.resources.getString(R.string.txt_pay_error_tip_content_two)
                                )
                            )
                            add(
                                PayErrorBean(
                                    icon = R.mipmap.img_pay_error_three,
                                    title = null,
                                    content = context.resources.getString(R.string.txt_pay_error_tip_content_three)
                                )
                            )
                        }
                        adapter =
                            PayErrorTipAdapter(list, SDActivityManager.instance.lastActivity)
                    } else {
                        //BILLING_UNAVAILABLE, Debug Message:
                        //BILLING_UNAVAILABLE, Debug Message: Billing Unavailable
                        txtPayErrorTipBtn.text =
                            context.resources.getString(R.string.txt_pay_error_tip_btn_two)
                        val list = arrayListOf<PayErrorBean>().apply {
                            add(
                                PayErrorBean(
                                    icon = R.mipmap.img_pay_error_four,
                                    title = null,
                                    content = context.resources.getString(R.string.txt_pay_error_tip_content_four)
                                )
                            )
                            add(
                                PayErrorBean(
                                    icon = R.mipmap.img_pay_error_five,
                                    title = null,
                                    content = context.resources.getString(R.string.txt_pay_error_tip_content_five)
                                )
                            )
                            add(
                                PayErrorBean(
                                    icon = R.mipmap.img_pay_error_six,
                                    title = null,
                                    content = context.resources.getString(R.string.txt_pay_error_tip_content_six)
                                )
                            )
                        }
                        adapter =
                            PayErrorTipAdapter(list, SDActivityManager.instance.lastActivity)
                    }
                }
            } else {
                txtPayErrorTipBtn.text =
                    context.resources.getString(R.string.txt_pay_error_tip_btn_three)
                //通用报错提示
                txtPayErrorTipAbandonBtn.visibility = View.VISIBLE
                txtPayErrorTipAbandonBtn.setOnClickListener {
                    dismiss()
                }
                val list = arrayListOf<PayErrorBean>().apply {
                    add(
                        PayErrorBean(
                            icon = R.mipmap.img_pay_error_six,
                            title = context.resources.getString(R.string.txt_pay_error_tip_content_seven_title),
                            content = context.resources.getString(R.string.txt_pay_error_tip_content_seven)
                        )
                    )
                    add(
                        PayErrorBean(
                            icon = R.mipmap.img_pay_error_two,
                            title = null,
                            content = context.resources.getString(R.string.txt_pay_error_tip_content_two)
                        )
                    )
                    add(
                        PayErrorBean(
                            icon = R.mipmap.img_pay_error_three,
                            title = null,
                            content = context.resources.getString(R.string.txt_pay_error_tip_content_three)
                        )
                    )
                    add(
                        PayErrorBean(
                            icon = R.mipmap.img_pay_error_four,
                            title = null,
                            content = context.resources.getString(R.string.txt_pay_error_tip_content_four)
                        )
                    )
                }
                adapter =
                    PayErrorTipAdapter(list, SDActivityManager.instance.lastActivity)
            }
            rvPayErrorTip.adapter = adapter
        }

        //新的弹窗报错如果走了这个支付结果报错code就请求服务端然后下发一条系统通知
        if (code == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
            payFail()
        }
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