package io.rong.imkit.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.sunday.eventbus.SDBaseEvent
import com.sunday.eventbus.SDEventManager
import com.sunday.eventbus.SDEventObserver
import io.rong.imkit.API
import io.rong.imkit.R
import io.rong.imkit.SpName
import io.rong.imkit.activity.RongWebviewActivity
import io.rong.imkit.entity.AgreementEntity
import io.rong.imkit.entity.BuyMemberPageEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.pay.PayOrderLogUtils
import razerdp.basepopup.BasePopupWindow

class MemberUnitaryBuyDialog(
    var ctx: Context,
    var productCategory: Int,
    var listener: MemberUnitaryBuyListener
) : BasePopupWindow(ctx),SDEventObserver {
    init {
        setContentView(R.layout.dialog_member_buy_unitary)
        initView()
    }

    private fun initView() {
        val dialogPurchaseNow = findViewById<TextView>(R.id.dialog_purchase_now)
        val dialogCancel = findViewById<ImageView>(R.id.dialog_close)
        val imgTop = findViewById<ImageView>(R.id.img_top)
        val itemTitle = findViewById<TextView>(R.id.item_title)
        val itemContent = findViewById<TextView>(R.id.item_content)

        val quarterContainer = findViewById<ConstraintLayout>(R.id.quarter_container)
        val itemQuarterTitle = findViewById<TextView>(R.id.item_quarter_title)
        val itemQuarterNumber = findViewById<TextView>(R.id.item_quarter_number)
        val itemQuarterName = findViewById<TextView>(R.id.item_quarter_name)
        val itemQuarterDollarSign = findViewById<TextView>(R.id.item_quarter_dollar_sign)
        val itemQuarterDiscountedPrice = findViewById<TextView>(R.id.item_quarter_discounted_price)
        val itemQuarterOriginalPrice = findViewById<TextView>(R.id.item_quarter_original_price)
        val itemQuarterDiscount = findViewById<TextView>(R.id.item_quarter_discount)

        val monthlyContainer = findViewById<ConstraintLayout>(R.id.monthly_container)
        val itemMonthlyTitle = findViewById<TextView>(R.id.item_monthly_title)
        val itemMonthlyNumber = findViewById<TextView>(R.id.item_monthly_number)
        val itemMonthlyName = findViewById<TextView>(R.id.item_monthly_name)
        val itemMonthlyDollarSign = findViewById<TextView>(R.id.item_monthly_dollar_sign)
        val itemMonthlyDiscountedPrice = findViewById<TextView>(R.id.item_monthly_discounted_price)
        val itemMonthlyOriginalPrice = findViewById<TextView>(R.id.item_monthly_original_price)
        val itemMonthlyDiscount = findViewById<TextView>(R.id.item_monthly_discount)

        val weeksContainer = findViewById<ConstraintLayout>(R.id.weeks_container)
        val itemWeeksNumber = findViewById<TextView>(R.id.item_weeks_number)
        val itemWeeksName = findViewById<TextView>(R.id.item_weeks_name)
        val itemWeeksDollarSign = findViewById<TextView>(R.id.item_weeks_dollar_sign)
        val itemWeeksDiscountedPrice = findViewById<TextView>(R.id.item_weeks_discounted_price)

        val txtBuySingleTips = findViewById<TextView>(R.id.txt_buy_single_tips)

        val memberDialog = findViewById<ConstraintLayout>(R.id.father_member_dialog)
        val conMemberBuyUnitaryBox = findViewById<ConstraintLayout>(R.id.conMemberBuyUnitaryBox)

        var model: BuyMemberPageEntity.ProductExt? = null

        conMemberBuyUnitaryBox.visibility = View.GONE
        val layoutParams = memberDialog.layoutParams
        layoutParams.height = dipToPx(500f).toInt()
        memberDialog.layoutParams = layoutParams
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_member_product_category_url)
                requestBody.add("productCategory", productCategory)
            }

        }, object : SDOkHttpResoutCallBack<BuyMemberPageEntity>() {
            override fun onSuccess(entity: BuyMemberPageEntity) {
                conMemberBuyUnitaryBox.visibility = View.VISIBLE
                dialogPurchaseNow.visibility = View.VISIBLE
                val layoutParams2 = memberDialog.layoutParams
                layoutParams2.height = WRAP_CONTENT
                memberDialog.layoutParams = layoutParams2

                itemTitle.text = entity.data.subscriptions[0].tip
                itemContent.text = entity.data.subscriptions[0].content

                when (productCategory) {
                    2 -> {
                        setViewInfo(
                            imgTop,
                            memberDialog,
                            dialogPurchaseNow,
                            quarterContainer,
                            entity,
                            itemQuarterTitle,
                            monthlyContainer,
                            itemMonthlyTitle,
                            weeksContainer,
                            itemQuarterNumber,
                            itemQuarterName,
                            itemQuarterDollarSign,
                            itemQuarterDiscountedPrice,
                            itemQuarterOriginalPrice,
                            itemQuarterDiscount,
                            itemMonthlyNumber,
                            itemMonthlyName,
                            itemMonthlyDollarSign,
                            itemMonthlyDiscountedPrice,
                            itemMonthlyOriginalPrice,
                            itemMonthlyDiscount,
                            itemWeeksNumber,
                            itemWeeksName,
                            itemWeeksDollarSign,
                            itemWeeksDiscountedPrice,
                            txtBuySingleTips,
                            R.drawable.icon_single_flash_chat,
                            R.drawable.shape_buy_member_dialog_bg_fl,
                            R.drawable.selector_buy_fc_btn,
                            R.drawable.shape_buy_member_item_bg_fl_select,
                            R.drawable.shape_buy_member_item_bg_fl_unselect,
                            R.drawable.shape_solid_radius_6_fl_select,
                            R.drawable.shape_solid_radius_6_fl_unselect,
                            Color.parseColor("#E6745A"),
                            Color.parseColor("#7F7F7F"),
                            Color.parseColor("#F1B3AA"),
                        )
                    }

                    3 -> {
                        setViewInfo(
                            imgTop,
                            memberDialog,
                            dialogPurchaseNow,
                            quarterContainer,
                            entity,
                            itemQuarterTitle,
                            monthlyContainer,
                            itemMonthlyTitle,
                            weeksContainer,
                            itemQuarterNumber,
                            itemQuarterName,
                            itemQuarterDollarSign,
                            itemQuarterDiscountedPrice,
                            itemQuarterOriginalPrice,
                            itemQuarterDiscount,
                            itemMonthlyNumber,
                            itemMonthlyName,
                            itemMonthlyDollarSign,
                            itemMonthlyDiscountedPrice,
                            itemMonthlyOriginalPrice,
                            itemMonthlyDiscount,
                            itemWeeksNumber,
                            itemWeeksName,
                            itemWeeksDollarSign,
                            itemWeeksDiscountedPrice,
                            txtBuySingleTips,
                            R.drawable.icon_single_pp,
                            R.drawable.shape_buy_member_dialog_bg_pp,
                            R.drawable.selector_buy_pp_btn,
                            R.drawable.shape_buy_member_item_bg_pp_select,
                            R.drawable.shape_buy_member_item_bg_pp_unselect,
                            R.drawable.shape_solid_radius_6_pp_select,
                            R.drawable.shape_solid_radius_6_pp_unselect,
                            Color.parseColor("#C773F7"),
                            Color.parseColor("#7F7F7F"),
                            Color.parseColor("#D997FF")
                        )
                    }

                    4 -> {
                        setViewInfo(
                            imgTop,
                            memberDialog,
                            dialogPurchaseNow,
                            quarterContainer,
                            entity,
                            itemQuarterTitle,
                            monthlyContainer,
                            itemMonthlyTitle,
                            weeksContainer,
                            itemQuarterNumber,
                            itemQuarterName,
                            itemQuarterDollarSign,
                            itemQuarterDiscountedPrice,
                            itemQuarterOriginalPrice,
                            itemQuarterDiscount,
                            itemMonthlyNumber,
                            itemMonthlyName,
                            itemMonthlyDollarSign,
                            itemMonthlyDiscountedPrice,
                            itemMonthlyOriginalPrice,
                            itemMonthlyDiscount,
                            itemWeeksNumber,
                            itemWeeksName,
                            itemWeeksDollarSign,
                            itemWeeksDiscountedPrice,
                            txtBuySingleTips,
                            R.drawable.icon_single_pv,
                            R.drawable.shape_buy_member_dialog_bg_pv,
                            R.drawable.selector_buy_pv_btn,
                            R.drawable.shape_buy_member_item_bg_pv_select,
                            R.drawable.shape_buy_member_item_bg_pv_unselect,
                            R.drawable.shape_solid_radius_6_pv_select,
                            R.drawable.shape_solid_radius_6_pv_unselect,
                            Color.parseColor("#EDA421"),
                            Color.parseColor("#7F7F7F"),
                            Color.parseColor("#F7C976")

                        )
                    }
                }
                for (i in 0 until entity.data.productDescriptions.size) {
                    if (entity.data.productDescriptions[i].check == true) {
                        model = entity.data.productDescriptions[i]
                    }
                }

                itemQuarterTitle.text = entity.data.productDescriptions[2].tip
                itemQuarterNumber.text = entity.data.productDescriptions[2].benefitNum
                itemQuarterName.text = entity.data.productDescriptions[2].benefitUnit
                itemQuarterDiscountedPrice.text = entity.data.productDescriptions[2].price
                itemQuarterOriginalPrice.text = entity.data.productDescriptions[2].priceOriginal
                itemQuarterDiscount.text = entity.data.productDescriptions[2].saving

                itemMonthlyTitle.text = entity.data.productDescriptions[1].tip
                itemMonthlyNumber.text = entity.data.productDescriptions[1].benefitNum
                itemMonthlyName.text = entity.data.productDescriptions[1].benefitUnit
                itemMonthlyDiscountedPrice.text = entity.data.productDescriptions[1].price
                itemMonthlyOriginalPrice.text =entity.data.productDescriptions[1].priceOriginal
                itemMonthlyDiscount.text = entity.data.productDescriptions[1].saving

                itemWeeksNumber.text = entity.data.productDescriptions[0].benefitNum
                itemWeeksName.text = entity.data.productDescriptions[0].benefitUnit
                itemWeeksDiscountedPrice.text = entity.data.productDescriptions[0].price



                quarterContainer.setOnClickListener {
                    entity.data.productDescriptions[2].check = true
                    model = entity.data.productDescriptions[2]
                    entity.data.productDescriptions[1].check = false
                    entity.data.productDescriptions[0].check = false
                    when (productCategory) {
                        2 -> {
                            setViewInfo(
                                imgTop,
                                memberDialog,
                                dialogPurchaseNow,
                                quarterContainer,
                                entity,
                                itemQuarterTitle,
                                monthlyContainer,
                                itemMonthlyTitle,
                                weeksContainer,
                                itemQuarterNumber,
                                itemQuarterName,
                                itemQuarterDollarSign,
                                itemQuarterDiscountedPrice,
                                itemQuarterOriginalPrice,
                                itemQuarterDiscount,
                                itemMonthlyNumber,
                                itemMonthlyName,
                                itemMonthlyDollarSign,
                                itemMonthlyDiscountedPrice,
                                itemMonthlyOriginalPrice,
                                itemMonthlyDiscount,
                                itemWeeksNumber,
                                itemWeeksName,
                                itemWeeksDollarSign,
                                itemWeeksDiscountedPrice,
                                txtBuySingleTips,
                                R.drawable.icon_single_flash_chat,
                                R.drawable.shape_buy_member_dialog_bg_fl,
                                R.drawable.selector_buy_fc_btn,
                                R.drawable.shape_buy_member_item_bg_fl_select,
                                R.drawable.shape_buy_member_item_bg_fl_unselect,
                                R.drawable.shape_solid_radius_6_fl_select,
                                R.drawable.shape_solid_radius_6_fl_unselect,
                                Color.parseColor("#E6745A"),
                                Color.parseColor("#7F7F7F"),
                                Color.parseColor("#F1B3AA"),
                            )
                        }

                        3 -> {
                            setViewInfo(
                                imgTop,
                                memberDialog,
                                dialogPurchaseNow,
                                quarterContainer,
                                entity,
                                itemQuarterTitle,
                                monthlyContainer,
                                itemMonthlyTitle,
                                weeksContainer,
                                itemQuarterNumber,
                                itemQuarterName,
                                itemQuarterDollarSign,
                                itemQuarterDiscountedPrice,
                                itemQuarterOriginalPrice,
                                itemQuarterDiscount,
                                itemMonthlyNumber,
                                itemMonthlyName,
                                itemMonthlyDollarSign,
                                itemMonthlyDiscountedPrice,
                                itemMonthlyOriginalPrice,
                                itemMonthlyDiscount,
                                itemWeeksNumber,
                                itemWeeksName,
                                itemWeeksDollarSign,
                                itemWeeksDiscountedPrice,
                                txtBuySingleTips,
                                R.drawable.icon_single_pp,
                                R.drawable.shape_buy_member_dialog_bg_pp,
                                R.drawable.selector_buy_pp_btn,
                                R.drawable.shape_buy_member_item_bg_pp_select,
                                R.drawable.shape_buy_member_item_bg_pp_unselect,
                                R.drawable.shape_solid_radius_6_pp_select,
                                R.drawable.shape_solid_radius_6_pp_unselect,
                                Color.parseColor("#C773F7"),
                                Color.parseColor("#7F7F7F"),
                                Color.parseColor("#D997FF")
                            )
                        }

                        4 -> {
                            setViewInfo(
                                imgTop,
                                memberDialog,
                                dialogPurchaseNow,
                                quarterContainer,
                                entity,
                                itemQuarterTitle,
                                monthlyContainer,
                                itemMonthlyTitle,
                                weeksContainer,
                                itemQuarterNumber,
                                itemQuarterName,
                                itemQuarterDollarSign,
                                itemQuarterDiscountedPrice,
                                itemQuarterOriginalPrice,
                                itemQuarterDiscount,
                                itemMonthlyNumber,
                                itemMonthlyName,
                                itemMonthlyDollarSign,
                                itemMonthlyDiscountedPrice,
                                itemMonthlyOriginalPrice,
                                itemMonthlyDiscount,
                                itemWeeksNumber,
                                itemWeeksName,
                                itemWeeksDollarSign,
                                itemWeeksDiscountedPrice,
                                txtBuySingleTips,
                                R.drawable.icon_single_pv,
                                R.drawable.shape_buy_member_dialog_bg_pv,
                                R.drawable.selector_buy_pv_btn,
                                R.drawable.shape_buy_member_item_bg_pv_select,
                                R.drawable.shape_buy_member_item_bg_pv_unselect,
                                R.drawable.shape_solid_radius_6_pv_select,
                                R.drawable.shape_solid_radius_6_pv_unselect,
                                Color.parseColor("#EDA421"),
                                Color.parseColor("#7F7F7F"),
                                Color.parseColor("#F7C976")

                            )
                        }
                    }


                }

                monthlyContainer.setOnClickListener {
                    entity.data.productDescriptions[2].check = false
                    entity.data.productDescriptions[1].check = true
                    model = entity.data.productDescriptions[1]
                    entity.data.productDescriptions[0].check = false
                    when (productCategory) {
                        2 -> {
                            setViewInfo(
                                imgTop,
                                memberDialog,
                                dialogPurchaseNow,
                                quarterContainer,
                                entity,
                                itemQuarterTitle,
                                monthlyContainer,
                                itemMonthlyTitle,
                                weeksContainer,
                                itemQuarterNumber,
                                itemQuarterName,
                                itemQuarterDollarSign,
                                itemQuarterDiscountedPrice,
                                itemQuarterOriginalPrice,
                                itemQuarterDiscount,
                                itemMonthlyNumber,
                                itemMonthlyName,
                                itemMonthlyDollarSign,
                                itemMonthlyDiscountedPrice,
                                itemMonthlyOriginalPrice,
                                itemMonthlyDiscount,
                                itemWeeksNumber,
                                itemWeeksName,
                                itemWeeksDollarSign,
                                itemWeeksDiscountedPrice,
                                txtBuySingleTips,
                                R.drawable.icon_single_flash_chat,
                                R.drawable.shape_buy_member_dialog_bg_fl,
                                R.drawable.selector_buy_fc_btn,
                                R.drawable.shape_buy_member_item_bg_fl_select,
                                R.drawable.shape_buy_member_item_bg_fl_unselect,
                                R.drawable.shape_solid_radius_6_fl_select,
                                R.drawable.shape_solid_radius_6_fl_unselect,
                                Color.parseColor("#E6745A"),
                                Color.parseColor("#7F7F7F"),
                                Color.parseColor("#F1B3AA"),
                            )
                        }

                        3 -> {
                            setViewInfo(
                                imgTop,
                                memberDialog,
                                dialogPurchaseNow,
                                quarterContainer,
                                entity,
                                itemQuarterTitle,
                                monthlyContainer,
                                itemMonthlyTitle,
                                weeksContainer,
                                itemQuarterNumber,
                                itemQuarterName,
                                itemQuarterDollarSign,
                                itemQuarterDiscountedPrice,
                                itemQuarterOriginalPrice,
                                itemQuarterDiscount,
                                itemMonthlyNumber,
                                itemMonthlyName,
                                itemMonthlyDollarSign,
                                itemMonthlyDiscountedPrice,
                                itemMonthlyOriginalPrice,
                                itemMonthlyDiscount,
                                itemWeeksNumber,
                                itemWeeksName,
                                itemWeeksDollarSign,
                                itemWeeksDiscountedPrice,
                                txtBuySingleTips,
                                R.drawable.icon_single_pp,
                                R.drawable.shape_buy_member_dialog_bg_pp,
                                R.drawable.selector_buy_pp_btn,
                                R.drawable.shape_buy_member_item_bg_pp_select,
                                R.drawable.shape_buy_member_item_bg_pp_unselect,
                                R.drawable.shape_solid_radius_6_pp_select,
                                R.drawable.shape_solid_radius_6_pp_unselect,
                                Color.parseColor("#C773F7"),
                                Color.parseColor("#7F7F7F"),
                                Color.parseColor("#D997FF")
                            )
                        }

                        4 -> {
                            setViewInfo(
                                imgTop,
                                memberDialog,
                                dialogPurchaseNow,
                                quarterContainer,
                                entity,
                                itemQuarterTitle,
                                monthlyContainer,
                                itemMonthlyTitle,
                                weeksContainer,
                                itemQuarterNumber,
                                itemQuarterName,
                                itemQuarterDollarSign,
                                itemQuarterDiscountedPrice,
                                itemQuarterOriginalPrice,
                                itemQuarterDiscount,
                                itemMonthlyNumber,
                                itemMonthlyName,
                                itemMonthlyDollarSign,
                                itemMonthlyDiscountedPrice,
                                itemMonthlyOriginalPrice,
                                itemMonthlyDiscount,
                                itemWeeksNumber,
                                itemWeeksName,
                                itemWeeksDollarSign,
                                itemWeeksDiscountedPrice,
                                txtBuySingleTips,
                                R.drawable.icon_single_pv,
                                R.drawable.shape_buy_member_dialog_bg_pv,
                                R.drawable.selector_buy_pv_btn,
                                R.drawable.shape_buy_member_item_bg_pv_select,
                                R.drawable.shape_buy_member_item_bg_pv_unselect,
                                R.drawable.shape_solid_radius_6_pv_select,
                                R.drawable.shape_solid_radius_6_pv_unselect,
                                Color.parseColor("#EDA421"),
                                Color.parseColor("#7F7F7F"),
                                Color.parseColor("#F7C976")

                            )
                        }
                    }


                }

                weeksContainer.setOnClickListener {
                    entity.data.productDescriptions[2].check = false
                    entity.data.productDescriptions[1].check = false
                    entity.data.productDescriptions[0].check = true
                    model = entity.data.productDescriptions[0]
                    when (productCategory) {
                        2 -> {
                            setViewInfo(
                                imgTop,
                                memberDialog,
                                dialogPurchaseNow,
                                quarterContainer,
                                entity,
                                itemQuarterTitle,
                                monthlyContainer,
                                itemMonthlyTitle,
                                weeksContainer,
                                itemQuarterNumber,
                                itemQuarterName,
                                itemQuarterDollarSign,
                                itemQuarterDiscountedPrice,
                                itemQuarterOriginalPrice,
                                itemQuarterDiscount,
                                itemMonthlyNumber,
                                itemMonthlyName,
                                itemMonthlyDollarSign,
                                itemMonthlyDiscountedPrice,
                                itemMonthlyOriginalPrice,
                                itemMonthlyDiscount,
                                itemWeeksNumber,
                                itemWeeksName,
                                itemWeeksDollarSign,
                                itemWeeksDiscountedPrice,
                                txtBuySingleTips,
                                R.drawable.icon_single_flash_chat,
                                R.drawable.shape_buy_member_dialog_bg_fl,
                                R.drawable.selector_buy_fc_btn,
                                R.drawable.shape_buy_member_item_bg_fl_select,
                                R.drawable.shape_buy_member_item_bg_fl_unselect,
                                R.drawable.shape_solid_radius_6_fl_select,
                                R.drawable.shape_solid_radius_6_fl_unselect,
                                Color.parseColor("#E6745A"),
                                Color.parseColor("#7F7F7F"),
                                Color.parseColor("#F1B3AA"),
                            )
                        }

                        3 -> {
                            setViewInfo(
                                imgTop,
                                memberDialog,
                                dialogPurchaseNow,
                                quarterContainer,
                                entity,
                                itemQuarterTitle,
                                monthlyContainer,
                                itemMonthlyTitle,
                                weeksContainer,
                                itemQuarterNumber,
                                itemQuarterName,
                                itemQuarterDollarSign,
                                itemQuarterDiscountedPrice,
                                itemQuarterOriginalPrice,
                                itemQuarterDiscount,
                                itemMonthlyNumber,
                                itemMonthlyName,
                                itemMonthlyDollarSign,
                                itemMonthlyDiscountedPrice,
                                itemMonthlyOriginalPrice,
                                itemMonthlyDiscount,
                                itemWeeksNumber,
                                itemWeeksName,
                                itemWeeksDollarSign,
                                itemWeeksDiscountedPrice,
                                txtBuySingleTips,
                                R.drawable.icon_single_pp,
                                R.drawable.shape_buy_member_dialog_bg_pp,
                                R.drawable.selector_buy_pp_btn,
                                R.drawable.shape_buy_member_item_bg_pp_select,
                                R.drawable.shape_buy_member_item_bg_pp_unselect,
                                R.drawable.shape_solid_radius_6_pp_select,
                                R.drawable.shape_solid_radius_6_pp_unselect,
                                Color.parseColor("#C773F7"),
                                Color.parseColor("#7F7F7F"),
                                Color.parseColor("#D997FF")
                            )
                        }

                        4 -> {
                            setViewInfo(
                                imgTop,
                                memberDialog,
                                dialogPurchaseNow,
                                quarterContainer,
                                entity,
                                itemQuarterTitle,
                                monthlyContainer,
                                itemMonthlyTitle,
                                weeksContainer,
                                itemQuarterNumber,
                                itemQuarterName,
                                itemQuarterDollarSign,
                                itemQuarterDiscountedPrice,
                                itemQuarterOriginalPrice,
                                itemQuarterDiscount,
                                itemMonthlyNumber,
                                itemMonthlyName,
                                itemMonthlyDollarSign,
                                itemMonthlyDiscountedPrice,
                                itemMonthlyOriginalPrice,
                                itemMonthlyDiscount,
                                itemWeeksNumber,
                                itemWeeksName,
                                itemWeeksDollarSign,
                                itemWeeksDiscountedPrice,
                                txtBuySingleTips,
                                R.drawable.icon_single_pv,
                                R.drawable.shape_buy_member_dialog_bg_pv,
                                R.drawable.selector_buy_pv_btn,
                                R.drawable.shape_buy_member_item_bg_pv_select,
                                R.drawable.shape_buy_member_item_bg_pv_unselect,
                                R.drawable.shape_solid_radius_6_pv_select,
                                R.drawable.shape_solid_radius_6_pv_unselect,
                                Color.parseColor("#EDA421"),
                                Color.parseColor("#7F7F7F"),
                                Color.parseColor("#F7C976")

                            )
                        }
                    }


                }


            }
        })

        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_config_url)
                requestBody.add("code", 2)
            }
        }, object : SDOkHttpResoutCallBack<AgreementEntity>() {
            override fun onSuccess(entity: AgreementEntity) {
                val style = SpannableStringBuilder()
                style.append(ctx.getString(R.string.buy_member_unitary_tips))

                style.setSpan(object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        val intent = Intent(context,RongWebviewActivity::class.java)
                        val b = Bundle()
                        b.putString("url", entity.data.privacyPolicy)
                        b.putString(
                            "title",
                            ctx.resources.getString(R.string.privacy_policy)
                        )
                        b.apply {
                            intent.putExtras(this)
                        }
                        context.startActivity(intent)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = true
                        ds.clearShadowLayer()
                    }
                }, 32, 46, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                style.setSpan(object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        val intent = Intent(context,RongWebviewActivity::class.java)
                        val b = Bundle()
                        b.putString("url", entity.data.terms)
                        b.putString(
                            "title",
                            ctx.resources.getString(R.string.terms)
                        )
                        b.apply {
                            intent.putExtras(this)
                        }
                        context.startActivity(intent)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = true
                        ds.clearShadowLayer()
                    }
                }, 51, 56, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                txtBuySingleTips.text = style
                txtBuySingleTips.movementMethod = LinkMovementMethod.getInstance()
            }
        })

        showPopupWindow()
        dialogPurchaseNow.setOnClickListener {
            if(dialogPurchaseNow.isEnabled){
                dialogPurchaseNow.isEnabled=false
                PayOrderLogUtils.requestOrderLog("","","",PayOrderLogUtils.CLICK_BUY_BUTTON)
                model?.let { it1 -> listener.onListener(it1) }
                Handler(Looper.getMainLooper()).postDelayed({
                    dialogPurchaseNow.isEnabled  = true
                }, 2000)
            }
            //dismiss()
        }
        dialogCancel.setOnClickListener {
            dismiss()
        }


        setOutSideDismiss(true)
        PayOrderLogUtils.requestOrderLog("","","", PayOrderLogUtils.TO_PRODUCT_DETAIL)
    }

    /**
     * 设置控件颜色和背景
     */
    private fun setViewInfo(
        imgTop: ImageView,
        memberDialog: ConstraintLayout,
        dialogPurchaseNow: TextView,
        quarterContainer: ConstraintLayout,
        entity: BuyMemberPageEntity,
        itemQuarterTitle: TextView,
        monthlyContainer: ConstraintLayout,
        itemMonthlyTitle: TextView,
        weeksContainer: ConstraintLayout,
        itemQuarterNumber: TextView,
        itemQuarterName: TextView,
        itemQuarterDollarSign: TextView,
        itemQuarterDiscountedPrice: TextView,
        itemQuarterOriginalPrice: TextView,
        itemQuarterDiscount: TextView,
        itemMonthlyNumber: TextView,
        itemMonthlyName: TextView,
        itemMonthlyDollarSign: TextView,
        itemMonthlyDiscountedPrice: TextView,
        itemMonthlyOriginalPrice: TextView,
        itemMonthlyDiscount: TextView,
        itemWeeksNumber: TextView,
        itemWeeksName: TextView,
        itemWeeksDollarSign: TextView,
        itemWeeksDiscountedPrice: TextView,
        txtBuySingleTips: TextView,
        topImg: Int,
        dialogBg: Int,
        dialogTitleBg: Int,
        selectBg: Int,
        unSelectBg: Int,
        selectTitleBg: Int,
        unSelectTitleBg: Int,
        selectColor: Int,
        unSelectColor: Int,
        linkColor: Int
        ) {
        imgTop.setBackgroundResource(topImg)
        memberDialog.setBackgroundResource(dialogBg)
        dialogPurchaseNow.setBackgroundResource(dialogTitleBg)
        txtBuySingleTips.setTextColor(linkColor)
        quarterContainer.setBackgroundResource(if (entity.data.productDescriptions[2].check == true) selectBg else unSelectBg)
        itemQuarterTitle.setBackgroundResource(if (entity.data.productDescriptions[2].check == true) selectTitleBg else unSelectTitleBg)
        monthlyContainer.setBackgroundResource(if (entity.data.productDescriptions[1].check == true) selectBg else unSelectBg)
        itemMonthlyTitle.setBackgroundResource(if (entity.data.productDescriptions[1].check == true) selectTitleBg else unSelectTitleBg)
        weeksContainer.setBackgroundResource(if (entity.data.productDescriptions[0].check == true) selectBg else unSelectBg)

        itemQuarterTitle.setTextColor(
            if (entity.data.productDescriptions[2].check == true) Color.WHITE else selectColor
        )
        itemQuarterNumber.setTextColor(
            if (entity.data.productDescriptions[2].check == true) Color.BLACK else unSelectColor
        )
        itemQuarterName.setTextColor(
            if (entity.data.productDescriptions[2].check == true) Color.BLACK else unSelectColor
        )
        itemQuarterDollarSign.setTextColor(
            if (entity.data.productDescriptions[2].check == true) selectColor else unSelectColor
        )
        itemQuarterDiscountedPrice.setTextColor(
            if (entity.data.productDescriptions[2].check == true) selectColor else unSelectColor
        )
        itemQuarterOriginalPrice.setTextColor(
            if (entity.data.productDescriptions[2].check == true) selectColor else unSelectColor
        )
        itemQuarterDiscount.setTextColor(
            if (entity.data.productDescriptions[2].check == true) Color.WHITE else selectColor
        )


        itemMonthlyTitle.setTextColor(
            if (entity.data.productDescriptions[1].check == true) Color.WHITE else selectColor
        )
        itemMonthlyNumber.setTextColor(
            if (entity.data.productDescriptions[1].check == true) Color.BLACK else unSelectColor
        )
        itemMonthlyName.setTextColor(
            if (entity.data.productDescriptions[1].check == true) Color.BLACK else unSelectColor
        )
        itemMonthlyDollarSign.setTextColor(
            if (entity.data.productDescriptions[1].check == true) selectColor else unSelectColor
        )
        itemMonthlyDiscountedPrice.setTextColor(
            if (entity.data.productDescriptions[1].check == true) selectColor else unSelectColor
        )
        itemMonthlyOriginalPrice.setTextColor(
            if (entity.data.productDescriptions[1].check == true) selectColor else unSelectColor
        )
        itemMonthlyDiscount.setTextColor(
            if (entity.data.productDescriptions[1].check == true) Color.WHITE else selectColor
        )

        itemWeeksNumber.setTextColor(
            if (entity.data.productDescriptions[0].check == true) Color.BLACK else unSelectColor
        )
        itemWeeksName.setTextColor(
            if (entity.data.productDescriptions[0].check == true) Color.BLACK else unSelectColor
        )
        itemWeeksDollarSign.setTextColor(
            if (entity.data.productDescriptions[0].check == true) selectColor else unSelectColor
        )
        itemWeeksDiscountedPrice.setTextColor(
            if (entity.data.productDescriptions[0].check == true) selectColor else unSelectColor
        )
    }

    interface MemberUnitaryBuyListener {
        fun onListener(bean: BuyMemberPageEntity.ProductExt)
    }

    override fun onShowing() {
        super.onShowing()
        SDEventManager.register(this)
    }
    override fun onDismiss() {
        super.onDismiss()
        BaseConfig.getInstance.setString(SpName.orderEventId,"")
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