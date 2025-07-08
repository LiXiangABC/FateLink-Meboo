package io.rong.imkit.dialog

import android.app.Activity
import android.content.Context
import android.text.Html
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.sunday.eventbus.SDBaseEvent
import com.sunday.eventbus.SDEventManager
import com.sunday.eventbus.SDEventObserver
import de.greenrobot.event.EventBus
import io.rong.imkit.API
import io.rong.imkit.R
import io.rong.imkit.SpName
import io.rong.imkit.entity.DiscountInfoEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.event.TriggerDiscountEvent
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayOrderLogUtils
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.picture.tools.ToastUtils
import io.rong.imkit.widget.SnapUpCountDownTimerView
import razerdp.basepopup.BasePopupWindow

class DiscountOfferDialog(var ctx: Context, val data: DiscountInfoEntity) : BasePopupWindow(ctx),
    SDEventObserver {
    init {
        setContentView(R.layout.dialog_discount_offer)
        initView()
    }

    //是否存在倒计时并且倒计时已完成
    private var beDownTime: Boolean = false
    private fun initView() {
        val dialogTitle = findViewById<TextView>(R.id.dialog_title)
        val dialogContent = findViewById<TextView>(R.id.dialog_content)
        val dialogCountdown = findViewById<SnapUpCountDownTimerView>(R.id.dialog_countdown)
        val dialogDiscountAmount = findViewById<TextView>(R.id.dialog_discount_amount)
        val dialogDiscountCycle = findViewById<TextView>(R.id.dialog_discount_cycle)
        val dialogDiscountTip = findViewById<TextView>(R.id.dialog_discount_tip)
        val txtDiscountSaving = findViewById<TextView>(R.id.txt_discount_saving)
        val dialogTips = findViewById<TextView>(R.id.dialog_tips)
        val dialogPurchaseNow = findViewById<TextView>(R.id.dialog_purchase_now)
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)
        val dialogTipTitle = findViewById<TextView>(R.id.dialog_tip_title)
        val dialogTipContent = findViewById<TextView>(R.id.dialog_tip_content)
        val discountInfo = data.discountInfoResponse
        if (discountInfo == null) {
            this.dismiss()
            return
        }
        dialogTitle.text = discountInfo.popTitle
        dialogContent.text = discountInfo.popContent
        dialogCountdown.setDownTime(data.popOverTime)
        dialogCountdown.start()
        dialogCountdown.setTimeFinish {
            beDownTime = true
        }
        try {
            dialogDiscountAmount.text =
                discountInfo.promotionPrice.substring(0, discountInfo.promotionPrice.indexOf("/"))
            dialogDiscountCycle.text = discountInfo.promotionPrice.substring(
                discountInfo.promotionPrice.indexOf("/"),
                discountInfo.promotionPrice.length
            )
        } catch (e: Exception) {

        }
        val spannedText = Html.fromHtml(discountInfo.promotionDelPrice)
        dialogDiscountTip.text = spannedText

        dialogTips.text = discountInfo.promotionNote
        dialogPurchaseNow.text = discountInfo.popButton
        txtDiscountSaving.text = discountInfo.popTag + "%"
        dialogTipTitle.text = discountInfo.popButtonTitle
        dialogTipContent.text = discountInfo.popButtonContent

        dialogClose.setOnClickListener {
            dismissPop()
        }

        setOutSideDismiss(true)
        dialogPurchaseNow.setOnClickListener {
            if (beDownTime) {
                ToastUtils.s(ctx, ctx.getString(R.string.special_has_ended))
                dismiss()
                return@setOnClickListener
            } else {
                PayOrderLogUtils.requestOrderLog(
                    "",
                    "",
                    "",
                    PayOrderLogUtils.CLICK_BUY_BUTTON
                )
                OkHttpManager.instance.requestInterface(object :
                    OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(API.user_create_order_url)
                        requestBody.add("productCode", data.discountInfoResponse.productCode)
                        requestBody.add(
                            "productCategory",
                            data.discountInfoResponse.productCategory
                        )
                    }

                }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                    override fun onSuccess(entity: OrderCreateEntity) {
                        //dismiss()
                        PayUtils.instance.start(
                            entity,
                            ctx,
                            object : EmptySuccessCallBack {
                                override fun OnSuccessListener() {
//                                    FirebaseEventUtils.logEvent(
//                                        FirebaseEventTag.Me_PV_Buysuccess.name
//                                    )
                                    dismiss()
                                }

                            })
                    }
                })
            }
        }

        PayOrderLogUtils.requestOrderLog(
            "",
            "", "", PayOrderLogUtils.TO_PRODUCT_DETAIL
        )
    }

    private fun dismissPop() {
        SDEventManager.post(EnumEventTag.CLOSE_DISCOUNT_POP.ordinal)
        dismiss()
    }

    interface FirstClickPrivateListener {
        fun onListener(type: Int)
    }

    override fun onShowing() {
        super.onShowing()
        SDEventManager.register(this)
    }

    override fun onDismiss() {
        super.onDismiss()
        BaseConfig.getInstance.setString(SpName.orderEventId, "")
        SDEventManager.unregister(this)
    }

    //EventBus事件监听
    override fun onEvent(p0: SDBaseEvent?) = Unit
    override fun onEventMainThread(event: SDBaseEvent?) {
        Log.i("MemberBuyDialog", "onEventMainThread")
        event?.let {
            when (EnumEventTag.valueOf(it.tagInt)) {
                EnumEventTag.CLOSE_PAY_BUY_DIALOG -> {
                    dismiss()
                }

                else -> Unit
            }
        }
    }

    override fun onEventBackgroundThread(p0: SDBaseEvent?) = Unit
    override fun onEventAsync(p0: SDBaseEvent?) = Unit

}