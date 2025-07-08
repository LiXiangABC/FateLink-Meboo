package com.crush.ui.member

import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StrikethroughSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.crush.Constant
import com.crush.R
import com.crush.adapter.MemberSubscriptionAdapter
import com.crush.dialog.CommonDialog
import com.crush.entity.AgreementEntity
import com.crush.ui.index.helper.IndexHelper
import com.crush.util.CollectionUtils
import com.crush.view.Loading.LoadingDialog
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import io.rong.imkit.utils.TextUtils
import io.rong.imkit.SpName
import io.rong.imkit.activity.RongWebviewActivity
import io.rong.imkit.entity.MemberSubscribeEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayOrderLogUtils
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.picture.tools.ToastUtils
import io.rong.imkit.utils.FirebaseEventUtils
import io.rong.imkit.utils.ktl.subDiscountPice
import io.rong.imkit.utils.ktl.subDiscountPrice


class MemberSubscriptionPresenter : BasePresenterImpl<MemberSubscriptionContract.View>(),
    MemberSubscriptionContract.Presenter {


    private var countDownTimer: CountDownTimer?=null
    var alreadyBuyProductCode:String=""
    var beDownTime = false
    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            PayOrderLogUtils.requestOrderLog("","","",PayOrderLogUtils.TO_PRODUCT_DETAIL)
            getData()
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_config_url)
                    requestBody.add("code", 2)
                }
            }, object : SDOkHttpResoutCallBack<AgreementEntity>() {
                override fun onSuccess(entity: AgreementEntity) {
                    val style = SpannableStringBuilder()
                    style.append(mActivity.getString(R.string.agree_to_the_payment_agreement))

                    style.setSpan(object : ClickableSpan() {
                        override fun onClick(p0: View) {
                            val b = Bundle()
                            b.putString("url", entity.data.paymentAgreement)
                            b.putString(
                                "title",
                                mActivity.resources.getString(R.string.payment_agreement)
                            )
                            startActivity(RongWebviewActivity::class.java, b)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.color = ContextCompat.getColor(
                                mActivity,
                                R.color.color_202323
                            )
                            ds.isUnderlineText = true
                            ds.clearShadowLayer()
                        }
                    }, 13, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    txtPaymentProtocol.text = style
                    txtPaymentProtocol.movementMethod = LinkMovementMethod.getInstance()
                }
            })
        }
    }

    fun getData() {
        mView?.apply {
            LoadingDialog.showLoading(mActivity)
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_member_product_subscription_url)
                }

            }, object : SDOkHttpResoutCallBack<MemberSubscribeEntity>() {
                override fun onSuccess(entity: MemberSubscribeEntity) {
                    LoadingDialog.dismissLoading(mActivity)
                    FirebaseEventUtils.logEvent(if (entity.data.memberCode != null)FirebaseEventTag.Me_Member_Premium.name  else FirebaseEventTag.Me_Nonmember_Premium.name)

                    txtMemberTitle.visibility = View.VISIBLE
                    txtMemberContent.visibility = View.VISIBLE
                    txtMemberTitle.text = entity.data.memberDescription.tip
                    txtMemberContent.text = entity.data.memberDescription.content
                    if (entity.data.memberDescription.header != null && entity.data.memberDescription.header!="") {
                        memberTip.visibility = View.VISIBLE
                        memberTip.text = entity.data.memberDescription.header
                    } else {
                        memberTip.visibility = View.GONE
                    }
                    if (entity.data.memberCode != null)
                        txtMemberDate.text =
                            if (entity.data.autoRenew == 1) mActivity.getString(R.string.auto_subscription) else "Until: ${entity.data.expiryDate}"
                    txtMemberDate.visibility =
                        if (entity.data.memberCode != null) View.VISIBLE else View.GONE
                    countDownTimer?.cancel()
                    memberAction.visibility =if (entity.data.autoRenew == 2)View.GONE else{ if (entity.data.memberCode != null)
                        View.VISIBLE else View.GONE}
                    txtGetPremium.visibility = if (entity.data.memberCode != null) View.GONE else View.VISIBLE
                    bottomContainer.visibility =
                        if (entity.data.memberCode == null) View.VISIBLE else{ if (entity.data.autoRenew == 2) View.VISIBLE else View.GONE}
                    var productCode = ""//产品码
                    if (CollectionUtils.isNotEmpty(entity.data.productDescriptions)) {
                        for (i in 0 until entity.data.productDescriptions.size) {
                            if (entity.data.productDescriptions[i].check) {
                                setPurchaseText(entity, i)
                                productCode = entity.data.productDescriptions[i].productCode
                                alreadyBuyProductCode = entity.data.productDescriptions[i].productCode
                                BaseConfig.getInstance.setInt(SpName.buyType,i+1)

                                break
                            }
                        }

                        quarterContainer.setBackgroundResource(if (entity.data.productDescriptions[2].check) R.drawable.shape_member_select_bg else R.drawable.shape_member_unselect_bg)
                        itemQuarterTitle.setBackgroundResource(if (entity.data.productDescriptions[2].check) R.drawable.shape_member_select_title_bg else R.drawable.shape_solid_purple_e1a7ff_radius_6)
                        itemQuarterTitle.text = entity.data.productDescriptions[2].tip
                        itemQuarterNumber.text = entity.data.productDescriptions[2].benefitNum
                        itemQuarterName.text = entity.data.productDescriptions[2].benefitUnit
                        itemQuarterName.setTextColor(if (entity.data.productDescriptions[2].check) ContextCompat.getColor(mActivity,R.color.color_202323) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
//                        itemQuarterDiscountedPrice.text = entity.data.productDescriptions[2].price
                        TextUtils.setSymbolText(itemQuarterDiscountedPrice,"${entity.data.productDescriptions[2].currencyType} ${entity.data.productDescriptions[2].price}",entity.data.productDescriptions[2].currencyType)
                        itemQuarterDiscountedPrice.setTextColor(if (entity.data.productDescriptions[2].check) ContextCompat.getColor(mActivity,R.color.color_E71EA8) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                        itemQuarterOriginalPrice.setTextColor(if (entity.data.productDescriptions[2].check) ContextCompat.getColor(mActivity,R.color.color_E71EA8) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                        itemQuarterDollarSign.setTextColor(if (entity.data.productDescriptions[2].check) ContextCompat.getColor(mActivity,R.color.color_E71EA8) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
//                        itemQuarterDollarSign.text= entity.data.productDescriptions[2].currencyType
                        itemQuarterOriginalPrice.text =
                            entity.data.productDescriptions[2].priceOriginal
                        itemQuarterOriginalPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG

                        monthlyContainer.setBackgroundResource(if (entity.data.productDescriptions[1].check) R.drawable.shape_member_select_bg else R.drawable.shape_member_unselect_bg)
                        itemMonthlyTitle.setBackgroundResource(if (entity.data.productDescriptions[1].check) R.drawable.shape_member_select_title_bg else R.drawable.shape_solid_purple_e1a7ff_radius_6)
                        itemMonthlyTitle.text = entity.data.productDescriptions[1].tip
                        itemMonthlyNumber.text = entity.data.productDescriptions[1].benefitNum
                        itemMonthlyName.text = entity.data.productDescriptions[1].benefitUnit
                        itemMonthlyName.setTextColor(if (entity.data.productDescriptions[1].check) ContextCompat.getColor(mActivity,R.color.color_202323) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                        TextUtils.setSymbolText(itemMonthlyDiscountedPrice,"${entity.data.productDescriptions[1].currencyType} ${entity.data.productDescriptions[1].price}",entity.data.productDescriptions[1].currencyType)

                        itemMonthlyDiscountedPrice.setTextColor(if (entity.data.productDescriptions[1].check) ContextCompat.getColor(mActivity,R.color.color_E71EA8) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                        itemMonthlyOriginalPrice.setTextColor(if (entity.data.productDescriptions[1].check) ContextCompat.getColor(mActivity,R.color.color_E71EA8) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                        itemMonthlyDollarSign.setTextColor(if (entity.data.productDescriptions[1].check) ContextCompat.getColor(mActivity,R.color.color_E71EA8) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))

                        itemMonthlyOriginalPrice.text = entity.data.productDescriptions[1].priceOriginal
                        itemMonthlyOriginalPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG


                        weeksContainer.setBackgroundResource(if (entity.data.productDescriptions[0].check) R.drawable.shape_member_select_bg else R.drawable.shape_member_unselect_bg)
                        itemWeeksNumber.text = entity.data.productDescriptions[0].benefitNum
                        itemWeeksName.text = entity.data.productDescriptions[0].benefitUnit
                        itemWeeksName.setTextColor(if (entity.data.productDescriptions[0].check) ContextCompat.getColor(mActivity,R.color.color_202323) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                        TextUtils.setSymbolText(itemWeeksDiscountedPrice,"${entity.data.productDescriptions[0].currencyType} ${entity.data.productDescriptions[0].price}",entity.data.productDescriptions[0].currencyType)
                        itemWeeksDiscountedPrice.setTextColor(if (entity.data.productDescriptions[0].check) ContextCompat.getColor(mActivity,R.color.color_E71EA8) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                        itemWeeksDollarSign.setTextColor(if (entity.data.productDescriptions[0].check) ContextCompat.getColor(mActivity,R.color.color_E71EA8) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                        itemWeeksOriginalPrice.setTextColor(if (entity.data.productDescriptions[0].check) ContextCompat.getColor(mActivity,R.color.color_E71EA8) else ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                        itemWeeksOriginalPrice.text = entity.data.productDescriptions[0].priceOriginal
                        itemWeeksOriginalPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG


                        if (entity.data.havaDiscount == 1){
                            TextUtils.setSymbolText(itemQuarterDiscountedPrice,"${entity.data.productDescriptions[2].currencyType} ${TextUtils.subText(entity.data.productDescriptions[2].promotionPrice?:"")}",entity.data.productDescriptions[2].currencyType)
                            TextUtils.setSymbolText(itemMonthlyDiscountedPrice,"${entity.data.productDescriptions[1].currencyType} ${TextUtils.subText(entity.data.productDescriptions[1].promotionPrice?:"")}",entity.data.productDescriptions[1].currencyType)
                            TextUtils.setSymbolText(itemWeeksDiscountedPrice,"${entity.data.productDescriptions[0].currencyType} ${TextUtils.subText(entity.data.productDescriptions[0].promotionPrice?:"")}",entity.data.productDescriptions[0].currencyType)

//                            itemQuarterDiscountedPrice.subDiscountPice(entity.data.productDescriptions[2].promotionPrice)
//                            itemMonthlyDiscountedPrice.subDiscountPice(entity.data.productDescriptions[1].promotionPrice)
//                            itemWeeksDiscountedPrice.subDiscountPice(entity.data.productDescriptions[0].promotionPrice)
                            itemMonthlyOriginalPrice.text ="${entity.data.productDescriptions[1].currencyType}${entity.data.productDescriptions[1].price}"
                            itemWeeksOriginalPrice.text = "${entity.data.productDescriptions[0].currencyType}${entity.data.productDescriptions[0].price}"
                            itemQuarterOriginalPrice.text ="${entity.data.productDescriptions[2].currencyType}${entity.data.productDescriptions[2].price}"
                        }
                        memberActionBuy.setOnClickListener {
                            if (beDownTime){
                                ToastUtils.s(mActivity, mActivity.getString(io.rong.imkit.R.string.special_has_ended))
                                getData()
                                return@setOnClickListener
                            }
                            if (entity.data.memberCode != null){
                                val dialog =
                                    CommonDialog(mActivity).setTitle(mActivity.getString(R.string.purchase_new_subscription))
                                        .setContent(mActivity.getString(R.string.purchase_new_subscription_tips))
                                        .setConfirmText(mActivity.getString(R.string.purchase))
                                        .setCancelText(mActivity.getString(R.string.cancel))
                                dialog.setConfirmListener {
                                    PayOrderLogUtils.requestOrderLog("","","",PayOrderLogUtils.CLICK_BUY_BUTTON)
                                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                        override fun addBody(requestBody: OkHttpBodyEntity) {
                                            requestBody.setPost(Constant.user_create_order_url)
                                            requestBody.add("productCode", productCode)
                                            requestBody.add("productCategory", 1)
                                        }

                                    }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                        override fun onSuccess(entity: OrderCreateEntity) {
                                            PayUtils.instance.start(entity,mActivity,object : EmptySuccessCallBack {
                                                override fun OnSuccessListener() {
                                                    FirebaseEventUtils.logEvent(FirebaseEventTag.Me_Member_Premiumsuccess.name)
                                                    getData()
                                                }

                                            })
                                        }
                                    })
                                    dialog.dismiss()
                                }
                                dialog.showPopupWindow()
                            }else {
                                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                    override fun addBody(requestBody: OkHttpBodyEntity) {
                                        requestBody.setPost(Constant.user_create_order_url)
                                        requestBody.add("productCode", productCode)
                                        requestBody.add("productCategory", 1)
                                    }

                                }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                    override fun onSuccess(entity: OrderCreateEntity) {
                                        PayUtils.instance.start(entity,mActivity,object : EmptySuccessCallBack {
                                            override fun OnSuccessListener() {
                                                FirebaseEventUtils.logEvent(FirebaseEventTag.Me_Nonmember_Premiumsuccess.name)

                                                getData()
                                            }

                                        })
                                    }
                                })
                            }
                        }

                        quarterContainer.setOnClickListener {
                            quarterContainer.setBackgroundResource(R.drawable.shape_member_select_bg)
                            itemQuarterTitle.setBackgroundResource(R.drawable.shape_member_select_title_bg)
                            itemQuarterDiscountedPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_E71EA8))
                            itemQuarterOriginalPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_E71EA8))
                            itemQuarterDollarSign.setTextColor(ContextCompat.getColor(mActivity,R.color.color_E71EA8))
                            itemQuarterName.setTextColor(ContextCompat.getColor(mActivity,R.color.color_202323))
                            BaseConfig.getInstance.setInt(SpName.buyType,3)


                            entity.data.productDescriptions[2].check = true


                            monthlyContainer.setBackgroundResource(R.drawable.shape_member_unselect_bg)
                            itemMonthlyTitle.setBackgroundResource(R.drawable.shape_solid_purple_e1a7ff_radius_6)
                            itemMonthlyOriginalPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemMonthlyDiscountedPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemMonthlyDollarSign.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemMonthlyName.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            entity.data.productDescriptions[1].check = false

                            weeksContainer.setBackgroundResource(R.drawable.shape_member_unselect_bg)
                            entity.data.productDescriptions[0].check = false

                            setPurchaseText(entity, 2)

                            itemWeeksDiscountedPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemWeeksDollarSign.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            productCode = entity.data.productDescriptions[2].productCode
                            memberAction.visibility = if (entity.data.autoRenew == 2)View.GONE else{ if (entity.data.memberCode == null) View.GONE else{ if (alreadyBuyProductCode==productCode) View.VISIBLE else View.GONE}}
                            bottomContainer.visibility =if (entity.data.autoRenew == 2)View.VISIBLE else{ if (entity.data.memberCode == null) View.VISIBLE else{ if (alreadyBuyProductCode==productCode) View.GONE else View.VISIBLE}}
                            itemWeeksName.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemWeeksOriginalPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                        }

                        monthlyContainer.setOnClickListener {
                            quarterContainer.setBackgroundResource(R.drawable.shape_member_unselect_bg)
                            itemQuarterTitle.setBackgroundResource(R.drawable.shape_solid_purple_e1a7ff_radius_6)
                            itemQuarterOriginalPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemQuarterDiscountedPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemQuarterDollarSign.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemQuarterName.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            BaseConfig.getInstance.setInt(SpName.buyType,2)

                            entity.data.productDescriptions[2].check = false



                            monthlyContainer.setBackgroundResource(R.drawable.shape_member_select_bg)
                            itemMonthlyTitle.setBackgroundResource(R.drawable.shape_member_select_title_bg)
                            itemMonthlyOriginalPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_E71EA8))
                            itemMonthlyDiscountedPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_E71EA8))
                            itemMonthlyDollarSign.setTextColor(ContextCompat.getColor(mActivity,R.color.color_E71EA8))
                            itemMonthlyName.setTextColor(ContextCompat.getColor(mActivity,R.color.color_202323))

                            entity.data.productDescriptions[1].check = true

                            weeksContainer.setBackgroundResource(R.drawable.shape_member_unselect_bg)
                            entity.data.productDescriptions[0].check = false

                            productCode = entity.data.productDescriptions[1].productCode
                            setPurchaseText(entity, 1)



                            itemWeeksDiscountedPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemWeeksDollarSign.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemWeeksName.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemWeeksOriginalPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))

                            memberAction.visibility =if (entity.data.autoRenew == 2)View.GONE else{ if (entity.data.memberCode == null) View.GONE else{ if (alreadyBuyProductCode==productCode) View.VISIBLE else View.GONE}}
                            bottomContainer.visibility =if (entity.data.autoRenew == 2)View.VISIBLE else{  if (entity.data.memberCode == null) View.VISIBLE else{  if (alreadyBuyProductCode==productCode) View.GONE else View.VISIBLE}}
                        }

                        weeksContainer.setOnClickListener {
                            quarterContainer.setBackgroundResource(R.drawable.shape_member_unselect_bg)
                            itemQuarterTitle.setBackgroundResource(R.drawable.shape_solid_purple_e1a7ff_radius_6)
                            itemQuarterDiscountedPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemQuarterOriginalPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemQuarterDollarSign.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemQuarterName.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            entity.data.productDescriptions[2].check = false
                            BaseConfig.getInstance.setInt(SpName.buyType,1)



                            monthlyContainer.setBackgroundResource(R.drawable.shape_member_unselect_bg)
                            itemMonthlyTitle.setBackgroundResource(R.drawable.shape_solid_purple_e1a7ff_radius_6)
                            itemMonthlyOriginalPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemMonthlyDiscountedPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemMonthlyDollarSign.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            itemMonthlyName.setTextColor(ContextCompat.getColor(mActivity,R.color.color_7F7F7F))
                            entity.data.productDescriptions[1].check = false


                            weeksContainer.setBackgroundResource(R.drawable.shape_member_select_bg)
                            entity.data.productDescriptions[0].check = true
                            itemWeeksDiscountedPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_E71EA8))
                            itemWeeksDollarSign.setTextColor(ContextCompat.getColor(mActivity,R.color.color_E71EA8))
                            itemWeeksOriginalPrice.setTextColor(ContextCompat.getColor(mActivity,R.color.color_E71EA8))
                            itemWeeksName.setTextColor(ContextCompat.getColor(mActivity,R.color.color_202323))


                            productCode = entity.data.productDescriptions[0].productCode
                            setPurchaseText(entity, 0)
                            memberAction.visibility =if (entity.data.autoRenew == 2)View.GONE else{ if (entity.data.memberCode == null) View.GONE else{   if (alreadyBuyProductCode==productCode) View.VISIBLE else View.GONE}}
                            bottomContainer.visibility =if (entity.data.autoRenew == 2)View.VISIBLE else{  if (entity.data.memberCode == null) View.VISIBLE else{  if (alreadyBuyProductCode==productCode) View.GONE else View.VISIBLE}}
                        }
                    }

                    if (entity.data.popOverTime != null  && entity.data.popOverTime!! >0){
                        itemQuarterOriginalPrice.visibility=View.VISIBLE
                        itemMonthlyOriginalPrice.visibility=View.VISIBLE
                        itemWeeksOriginalPrice.visibility=View.VISIBLE
                        txtDiscountTag.visibility=View.VISIBLE
                        txtValidTime.visibility=View.VISIBLE
//                        IndexHelper.convertDownTime(entity.data.popOverTime,1000, onTick = {
//                            hour, minute, second ->
//                            txtGetPremium.text =
//                                mActivity.getString(R.string.get_special) + " ${IndexHelper.convertDownTimeStr(hour, minute, second)}"
//                        }) {
//                            //倒计时结束
//                            beDownTime = true
//                        }

                        countDownTimer = object : CountDownTimer(entity.data.popOverTime*1000, 1000L) {
                            override fun onTick(millisUntilFinished: Long) {
                                val hours = millisUntilFinished / (1000 * 60 * 60)
                                val minutes = millisUntilFinished % (1000 * 60 * 60) / (1000 * 60)
                                val seconds = millisUntilFinished % (1000 * 60 * 60) % (1000 * 60) / 1000
                                txtGetPremium.text =
                                    mActivity.getString(R.string.get_special) + " ${IndexHelper.convertDownTimeStr(hours, minutes, seconds)}"
                            }

                            override fun onFinish() {
                                beDownTime = true
                            }
                        }

                        countDownTimer?.start()

                    }else{
                        itemQuarterOriginalPrice.visibility=View.GONE
                        itemMonthlyOriginalPrice.visibility=View.GONE
                        itemWeeksOriginalPrice.visibility=View.GONE
                        txtDiscountTag.visibility=View.GONE
                        txtValidTime.visibility=View.GONE
                        txtGetPremium.text = mActivity.getString(R.string.get_special)
                        beDownTime = false
                    }

                    memberEquityList.layoutManager = LinearLayoutManager(mActivity)
                    val subscriptions = entity.data.subscriptions
                    memberEquityList.adapter = MemberSubscriptionAdapter(subscriptions, mActivity)

                    memberAction.setOnClickListener {
                        val dialog =
                            CommonDialog(mActivity).setTitle(mActivity.getString(R.string.unsubscribe))
                                .setContent(mActivity.getString(R.string.unsubscribe_tip))
                                .setConfirmText(mActivity.getString(R.string.unsubscribe))
                                .setCancelText(mActivity.getString(R.string.cancel))
                        dialog.setConfirmListener {
                            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                override fun addBody(requestBody: OkHttpBodyEntity) {
                                    requestBody.setPost(Constant.user_off_auto_renew_url)
                                }
                            }, object : SDOkHttpResoutCallBack<MemberSubscribeEntity>() {
                                override fun onSuccess(entity: MemberSubscribeEntity) {
                                    FirebaseEventUtils.logEvent(FirebaseEventTag.Me_Member_Premium_Unsub.name)
                                    getData()
                                }
                            })
                            dialog.dismiss()
                        }
                        dialog.showPopupWindow()
                    }
                    outView.visibility=View.VISIBLE
                }

            })
        }

    }

    private fun MemberSubscriptionContract.View.setPurchaseText(
        entity: MemberSubscribeEntity,
        i: Int
    ) {
        if (entity.data.popOverTime != null && entity.data.popOverTime!! > 0) {
            val buyText =
                "${entity.data.productDescriptions[i].currencyType}${entity.data.productDescriptions[i].price} ${
                    subDiscountPrice(entity.data.productDescriptions[i].promotionPrice)
                } ${
                    mActivity.getString(R.string.purchase_now)
                }"
            val spannableString = SpannableString(buyText)
            val strikethroughSpan = StrikethroughSpan()
            spannableString.setSpan(
                strikethroughSpan,
                0,
                entity.data.productDescriptions[1].priceOriginal.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            memberActionBuy.text = spannableString

            txtDiscountTag.text = "${entity.data.productDescriptions[i].popTag}% off"
            txtValidTime.text=entity.data.productDescriptions[i].promotionNote?:""

        } else {
            memberActionBuy.text =
                "${entity.data.productDescriptions[i].currencyType}${entity.data.productDescriptions[i].price} ${
                    mActivity.getString(R.string.purchase_now)
                }"
        }
    }


}
