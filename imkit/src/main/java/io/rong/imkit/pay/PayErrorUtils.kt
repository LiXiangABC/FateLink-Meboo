package io.rong.imkit.pay

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingResult
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import com.custom.base.util.ToastUtil
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.API
import io.rong.imkit.R
import io.rong.imkit.SpName
import io.rong.imkit.activity.OrderConfirmActivity
import io.rong.imkit.dialog.PayFailedTipOldDialog
import io.rong.imkit.entity.AgreementEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.widget.Loading.RunnablePost
import razerdp.basepopup.BasePopupWindow.OnDismissListener

//支付报错工具类
object PayErrorUtils {

    private var firstShow: Boolean = true//防止出现多个报错信息，只显示第一个报错信息
    private var firstOldPayFailShow: Boolean = true

    //显示老的支付报错弹窗
    fun showPayFailOldDialog(context: Context?, entity: OrderCreateEntity) {
        if (firstOldPayFailShow) {
            firstOldPayFailShow = false
            if (context == null) return
            if (SDActivityManager.instance.lastActivity != null && !SDActivityManager.instance.lastActivity.isFinishing &&
                !SDActivityManager.instance.lastActivity.isDestroyed
            ) {
                SDEventManager.post(EnumEventTag.CLOSE_PAY_BUY_DIALOG.ordinal)
                val activity = SDActivityManager.instance.lastActivity
                RunnablePost.post {
                    val dialog =
                        PayFailedTipOldDialog(
                            context,
                            entity,
                            object : PayFailedTipOldDialog.CallBack {
                                override fun sendEmail() {
                                    sendEmail(activity)
                                }
                            })
                    dialog.onDismissListener = object : OnDismissListener() {
                        override fun onDismiss() {
                            firstOldPayFailShow = true
                        }
                    }
                    dialog.showPopupWindow()
                }
            }
        }
    }


    //显示订单确认-根据开关控制
    fun showOrderConfirmDialog(callBack: (Boolean) -> Unit) {
        if (SDActivityManager.instance.lastActivity != null && !SDActivityManager.instance.lastActivity.isFinishing &&
            !SDActivityManager.instance.lastActivity.isDestroyed
        ) {
            val activity = SDActivityManager.instance.lastActivity
            requestOrderConfirmSwitch {
                callBack.invoke(it)
            }
        }

    }

    //获取订单确认开关
    private fun requestOrderConfirmSwitch(callBack: (Boolean) -> Unit) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_config_url)
                requestBody.add("code", 7)
            }
        }, object : SDOkHttpResoutCallBack<AgreementEntity>() {
            override fun onSuccess(entity: AgreementEntity) {
                callBack.invoke(entity.data.orderSnapshotAddSwitch)
            }

            override fun onFailure(code: Int, msg: String) {
                callBack.invoke(false)
            }
        })
    }

    //显示支付报错弹窗
    fun show(
        payErrorCode: Int,
        billingResult: BillingResult,
        orderEntity: OrderCreateEntity,
        context: Context?
    ) {
        BaseConfig.getInstance.setString(SpName.orderEventId, "")
        if (payErrorCode == BillingResponseCode.OK) {
            SDEventManager.post(EnumEventTag.CLOSE_PAY_BUY_DIALOG.ordinal)
            return
        }
        if (payErrorCode == BillingResponseCode.ITEM_ALREADY_OWNED
            || payErrorCode == BillingResponseCode.FEATURE_NOT_SUPPORTED ||
            payErrorCode == BillingResponseCode.DEVELOPER_ERROR ||
            payErrorCode == BillingResponseCode.ITEM_NOT_OWNED
        ) {
            return
        }
        if (firstShow) {
            firstShow = false
            showPayErrorDialog(billingResult.toString(), payErrorCode, orderEntity, context)
        }

//        when (payErrorCode) {
//            BillingResponseCode.BILLING_UNAVAILABLE -> {
//
//            }
//
//            BillingResponseCode.USER_CANCELED -> {
//                showPayErrorDialog(billingResult.toString())
//            }
//
//            else -> {
//                showPayErrorDialog(billingResult.toString())
//            }
//        }
    }

    //显示支付报错提示弹窗
    private fun showPayErrorDialog(
        errorStr: String,
        code: Int,
        orderEntity: OrderCreateEntity,
        context: Context?
    ) {
        Log.i("PayErrorUtils", "showPayErrorDialog error=$errorStr  code=$code")
        if (context == null) return
        if (SDActivityManager.instance.lastActivity != null && !SDActivityManager.instance.lastActivity.isFinishing &&
            !SDActivityManager.instance.lastActivity.isDestroyed
        ) {
            Log.i(
                "PayErrorUtils",
                "showPayErrorDialog SDActivityManager.instance.lastActivity=${SDActivityManager.instance.lastActivity}"
            )
            Log.i(
                "PayErrorUtils",
                "showPayErrorDialog current thread=${Thread.currentThread()} context=$context"
            )
            SDEventManager.post(EnumEventTag.CLOSE_PAY_BUY_DIALOG.ordinal)
            val activity = SDActivityManager.instance.lastActivity
            RunnablePost.post {
                val dialog =
                    PayErrorTipDialog(context, code, errorStr, object : PayErrorTipDialog.Callback {
                        override fun onBackClick(type: Int) {
                            if (type == 1) {
                                sendEmail(activity)
                            } else if (type == 2) {
                                goToGooglePlay()
                            } else {
                                //try it again
                                PayUtils.instance.start(
                                    orderEntity,
                                    context,
                                    object : EmptySuccessCallBack {
                                        override fun OnSuccessListener() {
                                        }

                                    })
                            }
                        }
                    })
                dialog.onDismissListener = object : OnDismissListener() {
                    override fun onDismiss() {
                        firstShow = true
                        //弹窗开关开启情况：显示购买弹窗，取消支付，显示报错提示弹窗，应该是先关闭报错弹窗提示再显示促销
                        if (code == BillingResponseCode.USER_CANCELED) {
                            if (orderEntity.data.purchaseTypeExt == 2) {
                                return
                            }
                            SDEventManager.post(3, EnumEventTag.CLOSE_MEMBER_POP.ordinal)
                        }
                    }
                }
                dialog.showPopupWindow()
            }
        }
    }

    //发送邮件
    private fun sendEmail(activity: Activity) {
        val emailTitle = String.format(
            activity.resources.getString(R.string.send_email_default_title),
            BaseConfig.getInstance.getString(SpName.userCode, ""), getVersionName(activity)
        )
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))/*不带附件发送邮件*/
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailTitle)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(activity.getString(R.string.email_address)))
            emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                activity.resources.getString(R.string.send_email_default_txt)
            ) //发送的内容
            activity.startActivity(emailIntent)
        } catch (e: Exception) {
            ToastUtil.toast("Please set up an email account and send us feedback")
        }
    }

    //获取版本号
    private fun getVersionName(activity: Activity): String {
        val packageManager = activity.packageManager
        val packageName = activity.packageName
        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionName
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun goToGooglePlay() {
        // 跳转到 Google Play 商店 Google Play 服务页面
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=com.google.android.gms")
            setPackage("com.android.vending")
        }
        if (SDActivityManager.instance.lastActivity != null && !SDActivityManager.instance.lastActivity.isFinishing &&
            !SDActivityManager.instance.lastActivity.isDestroyed
        ) {
            val activity = SDActivityManager.instance.lastActivity
            // 检查是否有能够处理此 Intent 的活动
            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(intent)
            } else {
                // 如果没有 Google Play 商店，使用浏览器打开
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms")
                )
                activity.startActivity(webIntent)
            }
        }

    }

}