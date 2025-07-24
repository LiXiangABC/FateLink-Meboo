package io.rong.imkit.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.sunday.eventbus.SDBaseEvent
import com.sunday.eventbus.SDEventManager
import com.sunday.eventbus.SDEventObserver
import com.youth.banner.Banner
import com.youth.banner.listener.OnPageChangeListener
import io.rong.common.CollectionUtils
import io.rong.imkit.API
import io.rong.imkit.R
import io.rong.imkit.SpName
import io.rong.imkit.adapter.MemberSubscriptionAdapter
import io.rong.imkit.adapter.PageBuyMemberAdapter
import io.rong.imkit.entity.MemberSubscribeEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.pay.PayOrderLogUtils
import io.rong.imkit.picture.tools.ToastUtils
import io.rong.imkit.utils.DensityUtil
import io.rong.imkit.utils.TextUtils
import io.rong.imkit.utils.ktl.subDiscountPice
import io.rong.imkit.widget.SnapUpCountDownTimerView
import razerdp.basepopup.BasePopupWindow

class MemberBuyDialog(
    var ctx: Context,
    var currentItem: Int,
    var listener: ChangeMembershipListener
) : BasePopupWindow(ctx), SDEventObserver {
    init {
        setContentView(R.layout.dialog_member_buy)
        initView()
        setOverlayMask(true)
    }
    //是否存在倒计时并且倒计时已完成
    private var beDownTime: Boolean = false
    private var refreshTime: Long = 0
    private fun initView() {
        val dialogCycleBanner = findViewById<Banner<*, *>>(R.id.dialog_cycle_banner)
        val dialogPurchaseNow = findViewById<TextView>(R.id.dialog_purchase_now)
        val dialogCancel = findViewById<ImageView>(R.id.dialog_member_close)
        val quarterContainer = findViewById<ConstraintLayout>(R.id.quarter_container)
        val itemQuarterTitle = findViewById<TextView>(R.id.item_quarter_title)
        val itemQuarterNumber = findViewById<TextView>(R.id.item_quarter_number)
        val itemQuarterName = findViewById<TextView>(R.id.item_quarter_name)
        val itemQuarterDollarSign = findViewById<TextView>(R.id.item_quarter_dollar_sign)
        val itemQuarterDiscountedPrice = findViewById<TextView>(R.id.item_quarter_discounted_price)
        val itemQuarterOriginalPrice = findViewById<TextView>(R.id.item_quarter_original_price)

        val monthlyContainer = findViewById<ConstraintLayout>(R.id.monthly_container)
        val itemMonthlyTitle = findViewById<TextView>(R.id.item_monthly_title)
        val itemMonthlyNumber = findViewById<TextView>(R.id.item_monthly_number)
        val itemMonthlyName = findViewById<TextView>(R.id.item_monthly_name)
        val itemMonthlyDollarSign = findViewById<TextView>(R.id.item_monthly_dollar_sign)
        val itemMonthlyDiscountedPrice = findViewById<TextView>(R.id.item_monthly_discounted_price)
        val itemMonthlyOriginalPrice = findViewById<TextView>(R.id.item_monthly_original_price)

        val weeksContainer = findViewById<ConstraintLayout>(R.id.weeks_container)
        val itemWeeksNumber = findViewById<TextView>(R.id.item_weeks_number)
        val itemWeeksName = findViewById<TextView>(R.id.item_weeks_name)
        val itemWeeksDollarSign = findViewById<TextView>(R.id.item_weeks_dollar_sign)
        val itemWeeksDiscountedPrice = findViewById<TextView>(R.id.item_weeks_discounted_price)
        val itemWeeksOriginalPrice = findViewById<TextView>(R.id.item_weeks_original_price)
        val itemIndicatorRg = findViewById<RadioGroup>(R.id.item_member_indicator_rg)

        val memberIntroduceContainer =
            findViewById<ConstraintLayout>(R.id.member_introduce_container)
        val memberScrollView = findViewById<NestedScrollView>(R.id.member_scroll_view)
        val memberIntroduceContainerBg = findViewById<ImageView>(R.id.member_introduce_container_bg)


        val offFromContainer = findViewById<ConstraintLayout>(R.id.off_from_container)
        val txtofffrom = findViewById<TextView>(R.id.txt_off_from)
        val dialogValidTime = findViewById<TextView>(R.id.dialog_valid_time)
        val discountContainer = findViewById<ConstraintLayout>(R.id.discount_container)
        val conBuyMemberBenefits = findViewById<ConstraintLayout>(R.id.conBuyMemberBenefits)
        val txtBuyMemberBenefitNum = findViewById<TextView>(R.id.txtBuyMemberBenefitNum)
        val dialogDiscountTime = findViewById<SnapUpCountDownTimerView>(R.id.dialog_discount_time)

//        val dialogDiscountTitle = findViewById<TextView>(R.id.dialog_discount_title)
//        val dialog_discount_tip = findViewById<TextView>(R.id.dialog_discount_tip)
        val tipContent = findViewById<TextView>(R.id.tip_content)
        val tipTitle = findViewById<TextView>(R.id.tip_title)

        val memberEquityList = findViewById<RecyclerView>(R.id.member_equity_list)
        val conBuyMemberWai = findViewById<ConstraintLayout>(R.id.conBuyMemberWai)
        conBuyMemberWai.visibility = View.GONE
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_member_product_category_url)
                requestBody.add("productCategory", 1)
            }

        }, object : SDOkHttpResoutCallBack<MemberSubscribeEntity>() {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(entity: MemberSubscribeEntity) {
                conBuyMemberWai.visibility = View.VISIBLE
                dialogPurchaseNow.visibility = View.VISIBLE
                val subscriptions = entity.data.subscriptions
//                if (!CollectionUtils.checkNullOrEmptyOrContainsNull(entity.data.subscriptions)) {
//                    if (subscriptions.size == 7) {
//                        subscriptions.removeLast()
//                    }
//                }

                subscriptions.forEachIndexed { index, item ->
                    val radioButton = RadioButton(context).apply {
                        id = View.generateViewId() // Generate a unique ID for each RadioButton
                        layoutParams = RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            width = DensityUtil.dp2px(context,6f)
                            height = DensityUtil.dp2px(context,6f)
                            marginStart = DensityUtil.dp2px(context,6f)
                        }
                        setBackgroundResource(R.drawable.selector_buy_member_in)
                    }

                    // Add the RadioButton to the RadioGroup
                    itemIndicatorRg.addView(radioButton)
                }

//                if (subscriptions.size != itemIndicatorRg.childCount) {
//                    itemIndicatorRg.removeViews(
//                        subscriptions.size,
//                        itemIndicatorRg.childCount - subscriptions.size
//                    )
//                }
                dialogCycleBanner.setStartPosition(currentItem + 1)
                val pageBuyMemberAdapter = PageBuyMemberAdapter(ctx, subscriptions)
                dialogCycleBanner.adapter = pageBuyMemberAdapter

                memberEquityList.layoutManager = LinearLayoutManager(ctx)
                memberEquityList.adapter = MemberSubscriptionAdapter(subscriptions, ctx)


                if (itemIndicatorRg.getChildAt(currentItem) != null) {
                    (itemIndicatorRg.getChildAt(currentItem) as RadioButton).isChecked = true
                }
                dialogPurchaseNow.setOnClickListener {
                    if(dialogPurchaseNow.isEnabled) {
                        dialogPurchaseNow.isEnabled = false
                        if (beDownTime) {
                            ToastUtils.s(ctx, ctx.getString(R.string.special_has_ended))
                            initView()
                        } else {
                            var bean: MemberSubscribeEntity.Data.ProductDescriptions? = null
                            for (i in 0 until entity.data.productDescriptions.size) {
                                if (entity.data.productDescriptions[i].check) {
                                    bean = entity.data.productDescriptions[i]
                                    break
                                }
                            }
                            bean?.let { it1 -> listener.onListener(it1)
                                PayOrderLogUtils.requestOrderLog("","","",PayOrderLogUtils.CLICK_BUY_BUTTON)
                            }
                            //dismiss()
                        }
                        Handler(Looper.getMainLooper()).postDelayed({
                            dialogPurchaseNow.isEnabled  = true
                        }, 2000)
                    }
                }

                val bean = entity.data
                itemQuarterTitle.text = bean.productDescriptions[2].tip
                itemQuarterNumber.text = bean.productDescriptions[2].benefitNum
                itemQuarterName.text = bean.productDescriptions[2].benefitUnit
                itemQuarterOriginalPrice.text = bean.productDescriptions[2].priceOriginal
                itemQuarterOriginalPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                TextUtils.setSymbolText(itemQuarterDiscountedPrice,"${entity.data.productDescriptions[2].currencyType} ${entity.data.productDescriptions[2].price}",entity.data.productDescriptions[2].currencyType)



                itemMonthlyTitle.text = bean.productDescriptions[1].tip
                itemMonthlyNumber.text = bean.productDescriptions[1].benefitNum
                itemMonthlyName.text = bean.productDescriptions[1].benefitUnit
                itemMonthlyOriginalPrice.text = bean.productDescriptions[1].priceOriginal
                itemMonthlyOriginalPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                TextUtils.setSymbolText(itemMonthlyDiscountedPrice,"${entity.data.productDescriptions[1].currencyType} ${entity.data.productDescriptions[1].price}",entity.data.productDescriptions[1].currencyType)


                itemWeeksNumber.text = bean.productDescriptions[0].benefitNum
                itemWeeksName.text = bean.productDescriptions[0].benefitUnit
                itemWeeksOriginalPrice.text = bean.productDescriptions[0].priceOriginal
                itemWeeksOriginalPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                TextUtils.setSymbolText(itemWeeksDiscountedPrice,"${entity.data.productDescriptions[0].currencyType} ${entity.data.productDescriptions[0].price}",entity.data.productDescriptions[0].currencyType)


                if (BaseConfig.getInstance.getInt(SpName.buyType, -1) == -1) {
                    BaseConfig.getInstance.setInt(SpName.buyType, 2)//购买页面 1周 2月 3季
                }
                bean.productDescriptions[0].popButtonTitle?.let {
                    tipTitle.text = it
                }
                bean.productDescriptions[0].popButtonContent?.let {
                    tipContent.text = it
                }
                setViewInfo(
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
                    itemMonthlyNumber,
                    itemMonthlyName,
                    itemMonthlyDollarSign,
                    itemMonthlyDiscountedPrice,
                    itemMonthlyOriginalPrice,
                    itemWeeksNumber,
                    itemWeeksName,
                    itemWeeksDollarSign,
                    itemWeeksDiscountedPrice,
                    itemWeeksOriginalPrice,
                    R.drawable.shape_member_select_bg,
                    R.drawable.shape_member_unselect_bg,
                    R.drawable.shape_member_select_title_bg,
                    R.drawable.shape_solid_purple_e1a7ff_radius_6,
                    Color.parseColor("#E71EA8"),
                    Color.parseColor("#7F7F7F"),
                )


                if (entity.data.popOverTime != null && entity.data.popOverTime > 0) {
                    dialogValidTime.visibility = View.VISIBLE
                    discountContainer.visibility = View.VISIBLE
                    conBuyMemberBenefits.visibility = View.GONE
                    itemQuarterOriginalPrice.visibility = View.VISIBLE
                    itemMonthlyOriginalPrice.visibility = View.VISIBLE
                    itemWeeksOriginalPrice.visibility = View.VISIBLE
                    bean.productDescriptions[1].popTag?.let {
                        offFromContainer.visibility = View.VISIBLE
                        txtofffrom.text = bean.productDescriptions[1].popTag + "%"
                    }

//                    if (entity.data.havaDiscount == 1){
                        TextUtils.setSymbolText(itemQuarterDiscountedPrice,"${entity.data.productDescriptions[2].currencyType} ${TextUtils.subText(entity.data.productDescriptions[2].promotionPrice?:"")}",entity.data.productDescriptions[2].currencyType)
                        TextUtils.setSymbolText(itemMonthlyDiscountedPrice,"${entity.data.productDescriptions[1].currencyType} ${TextUtils.subText(entity.data.productDescriptions[1].promotionPrice?:"")}",entity.data.productDescriptions[1].currencyType)
                        TextUtils.setSymbolText(itemWeeksDiscountedPrice,"${entity.data.productDescriptions[0].currencyType} ${TextUtils.subText(entity.data.productDescriptions[0].promotionPrice?:"")}",entity.data.productDescriptions[0].currencyType)
                        itemMonthlyOriginalPrice.text ="${entity.data.productDescriptions[1].currencyType}${entity.data.productDescriptions[1].price}"
                        itemWeeksOriginalPrice.text = "${entity.data.productDescriptions[0].currencyType}${entity.data.productDescriptions[0].price}"
                        itemQuarterOriginalPrice.text ="${entity.data.productDescriptions[2].currencyType}${entity.data.productDescriptions[2].price}"
//                    }

//                    itemMonthlyDiscountedPrice.subDiscountPice(bean.productDescriptions[1].promotionPrice)
//                    itemQuarterDiscountedPrice.subDiscountPice(bean.productDescriptions[2].promotionPrice)
//                    itemWeeksDiscountedPrice.subDiscountPice(bean.productDescriptions[0].promotionPrice)
//                    itemQuarterOriginalPrice.text = "$" + bean.productDescriptions[2].price
//                    itemMonthlyOriginalPrice.text = "$" + bean.productDescriptions[1].price
//                    itemWeeksOriginalPrice.text = "$" + bean.productDescriptions[0].price

                    bean.productDescriptions[1].promotionNote?.let {
                        dialogValidTime.text = it
                    }
                    val hours: Long = entity.data.popOverTime / 60 / 60
                    val min = (entity.data.popOverTime - hours * 60 * 60) / 60
                    val s = entity.data.popOverTime - hours * 60 * 60 - min * 60
                    dialogDiscountTime.setTime(
                        hours.toInt(),
                        min.toInt(),
                        s.toInt()
                    )
//                    if (hours<=0){
//                        dialogDiscountTime.setHourHide()
//                    }
                    dialogDiscountTime.start()
                    dialogDiscountTime.setTimeFinish {
                        beDownTime = true
//                        offFromContainer.visibility=View.GONE
//                        dialogValidTime.visibility=View.GONE
//                        discountContainer.visibility=View.GONE
//                        itemQuarterOriginalPrice.visibility=View.GONE
//                        itemMonthlyOriginalPrice.visibility=View.GONE
//                        itemWeeksOriginalPrice.visibility=View.GONE
//                        itemQuarterDiscountedPrice.text= bean.productDescriptions[2].price
//                        itemMonthlyDiscountedPrice.text= bean.productDescriptions[1].price
//                        itemWeeksDiscountedPrice.text= bean.productDescriptions[0].price

                    }

                } else {
                    beDownTime = false
                    offFromContainer.visibility = View.GONE
                    dialogValidTime.visibility = View.GONE
                    discountContainer.visibility = View.GONE
                    conBuyMemberBenefits.visibility = View.VISIBLE
                    itemQuarterOriginalPrice.visibility = View.GONE
                    itemMonthlyOriginalPrice.visibility = View.GONE
                    itemWeeksOriginalPrice.visibility = View.GONE
                    TextUtils.setSymbolText(itemQuarterDiscountedPrice,"${entity.data.productDescriptions[2].currencyType} ${entity.data.productDescriptions[2].price}",entity.data.productDescriptions[2].currencyType)
                    TextUtils.setSymbolText(itemMonthlyDiscountedPrice,"${entity.data.productDescriptions[1].currencyType} ${entity.data.productDescriptions[1].price}",entity.data.productDescriptions[1].currencyType)
                    TextUtils.setSymbolText(itemWeeksDiscountedPrice,"${entity.data.productDescriptions[0].currencyType} ${entity.data.productDescriptions[0].price}",entity.data.productDescriptions[0].currencyType)
                    txtBuyMemberBenefitNum.text = "Enjoy ${subscriptions.size} Premium Benefits"

                    //因为倒计时隐藏了，所以要动态设置banner约束在下面
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(conBuyMemberWai)
                    constraintSet.connect(
                        dialogCycleBanner.id,
                        ConstraintSet.TOP,
                        conBuyMemberBenefits.id,
                        ConstraintSet.BOTTOM,
                        10
                    )
                    constraintSet.applyTo(conBuyMemberWai)
                }

                quarterContainer.setOnClickListener {
                    bean.productDescriptions[2].check = true
                    bean.productDescriptions[1].check = false
                    bean.productDescriptions[0].check = false
                    bean.productDescriptions[2].popTag?.let {
                        txtofffrom.text = it + "%"
                    }
                    bean.productDescriptions[2].promotionNote?.let {
                        dialogValidTime.text = it
                    }
                    BaseConfig.getInstance.setInt(SpName.buyType, 3)
                    setViewInfo(
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
                        itemMonthlyNumber,
                        itemMonthlyName,
                        itemMonthlyDollarSign,
                        itemMonthlyDiscountedPrice,
                        itemMonthlyOriginalPrice,
                        itemWeeksNumber,
                        itemWeeksName,
                        itemWeeksDollarSign,
                        itemWeeksDiscountedPrice,
                        itemWeeksOriginalPrice,
                        R.drawable.shape_member_select_bg,
                        R.drawable.shape_member_unselect_bg,
                        R.drawable.shape_member_select_title_bg,
                        R.drawable.shape_solid_purple_e1a7ff_radius_6,
                        Color.parseColor("#E71EA8"),
                        Color.parseColor("#7F7F7F"),
                    )
                }

                monthlyContainer.setOnClickListener {
                    bean.productDescriptions[2].check = false
                    bean.productDescriptions[1].check = true
                    bean.productDescriptions[0].check = false
                    bean.productDescriptions[1].popTag?.let {
                        txtofffrom.text = it + "%"
                    }
                    bean.productDescriptions[1].promotionNote?.let {
                        dialogValidTime.text = it
                    }
                    BaseConfig.getInstance.setInt(SpName.buyType, 2)
                    setViewInfo(
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
                        itemMonthlyNumber,
                        itemMonthlyName,
                        itemMonthlyDollarSign,
                        itemMonthlyDiscountedPrice,
                        itemMonthlyOriginalPrice,
                        itemWeeksNumber,
                        itemWeeksName,
                        itemWeeksDollarSign,
                        itemWeeksDiscountedPrice,
                        itemWeeksOriginalPrice,
                        R.drawable.shape_member_select_bg,
                        R.drawable.shape_member_unselect_bg,
                        R.drawable.shape_member_select_title_bg,
                        R.drawable.shape_solid_purple_e1a7ff_radius_6,
                        Color.parseColor("#E71EA8"),
                        Color.parseColor("#7F7F7F"),
                    )
                }

                weeksContainer.setOnClickListener {
                    bean.productDescriptions[2].check = false
                    bean.productDescriptions[1].check = false
                    bean.productDescriptions[0].check = true
                    bean.productDescriptions[0].popTag?.let {
                        txtofffrom.text = it + "%"
                    }
                    bean.productDescriptions[0].promotionNote?.let {
                        dialogValidTime.text = it
                    }
                    BaseConfig.getInstance.setInt(SpName.buyType, 1)
                    setViewInfo(
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
                        itemMonthlyNumber,
                        itemMonthlyName,
                        itemMonthlyDollarSign,
                        itemMonthlyDiscountedPrice,
                        itemMonthlyOriginalPrice,
                        itemWeeksNumber,
                        itemWeeksName,
                        itemWeeksDollarSign,
                        itemWeeksDiscountedPrice,
                        itemWeeksOriginalPrice,
                        R.drawable.shape_member_select_bg,
                        R.drawable.shape_member_unselect_bg,
                        R.drawable.shape_member_select_title_bg,
                        R.drawable.shape_solid_purple_e1a7ff_radius_6,
                        Color.parseColor("#E71EA8"),
                        Color.parseColor("#7F7F7F"),
                    )
                }
                dialogCycleBanner.addOnPageChangeListener(object : OnPageChangeListener {
                    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

                    }

                    override fun onPageSelected(p0: Int) {
                        for (i in 0 until itemIndicatorRg.childCount) {
                            (itemIndicatorRg.getChildAt(i) as RadioButton).isChecked = p0 == i
                        }
                    }

                    override fun onPageScrollStateChanged(p0: Int) {

                    }

                })
                entity.data.refreshTime?.let {
                    this@MemberBuyDialog.refreshTime = it
                }
            }

        })
        showPopupWindow()

        dialogCancel?.setOnClickListener {
            SDEventManager.post(1, EnumEventTag.CLOSE_MEMBER_POP.ordinal)
            listener.closeListener(refreshTime)
            dismiss()
        }

        setOutSideDismiss(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            memberScrollView.setOnScrollChangeListener { _, _, _, _, _ ->
                memberIntroduceContainerBg.setImageResource(R.drawable.icon_member_dialog_tip_bg)
            }
        }


        onDismissListener = object : OnDismissListener() {
            override fun onDismiss() {
                dialogCycleBanner.destroy()
            }

        }
        PayOrderLogUtils.requestOrderLog("","","",PayOrderLogUtils.TO_PRODUCT_DETAIL)
    }

    /**
     * 设置控件颜色和背景
     */
    private fun setViewInfo(
        quarterContainer: ConstraintLayout,
        entity: MemberSubscribeEntity,
        itemQuarterTitle: TextView,
        monthlyContainer: ConstraintLayout,
        itemMonthlyTitle: TextView,
        weeksContainer: ConstraintLayout,
        itemQuarterNumber: TextView,
        itemQuarterName: TextView,
        itemQuarterDollarSign: TextView,
        itemQuarterDiscountedPrice: TextView,
        itemQuarterOriginalPrice: TextView,
        itemMonthlyNumber: TextView,
        itemMonthlyName: TextView,
        itemMonthlyDollarSign: TextView,
        itemMonthlyDiscountedPrice: TextView,
        itemMonthlyOriginalPrice: TextView,
        itemWeeksNumber: TextView,
        itemWeeksName: TextView,
        itemWeeksDollarSign: TextView,
        itemWeeksDiscountedPrice: TextView,
        itemWeeksOriginalPrice: TextView,
        selectBg: Int,
        unSelectBg: Int,
        selectTitleBg: Int,
        unSelectTitleBg: Int,
        selectColor: Int,
        unSelectColor: Int,
    ) {
        quarterContainer.setBackgroundResource(if (entity.data.productDescriptions[2].check) selectBg else unSelectBg)
        itemQuarterTitle.setBackgroundResource(if (entity.data.productDescriptions[2].check) selectTitleBg else unSelectTitleBg)
        monthlyContainer.setBackgroundResource(if (entity.data.productDescriptions[1].check) selectBg else unSelectBg)
        itemMonthlyTitle.setBackgroundResource(if (entity.data.productDescriptions[1].check) selectTitleBg else unSelectTitleBg)
        weeksContainer.setBackgroundResource(if (entity.data.productDescriptions[0].check) selectBg else unSelectBg)

        itemQuarterNumber.setTextColor(
            if (entity.data.productDescriptions[2].check) Color.BLACK else unSelectColor
        )
        itemQuarterName.setTextColor(
            if (entity.data.productDescriptions[2].check) Color.BLACK else unSelectColor
        )
        itemQuarterDollarSign.setTextColor(
            if (entity.data.productDescriptions[2].check) selectColor else unSelectColor
        )
        itemQuarterDiscountedPrice.setTextColor(
            if (entity.data.productDescriptions[2].check) selectColor else unSelectColor
        )
        itemQuarterOriginalPrice.setTextColor(
            if (entity.data.productDescriptions[2].check) selectColor else unSelectColor
        )

        itemMonthlyNumber.setTextColor(
            if (entity.data.productDescriptions[1].check) Color.BLACK else unSelectColor
        )
        itemMonthlyName.setTextColor(
            if (entity.data.productDescriptions[1].check) Color.BLACK else unSelectColor
        )
        itemMonthlyDollarSign.setTextColor(
            if (entity.data.productDescriptions[1].check) selectColor else unSelectColor
        )
        itemMonthlyDiscountedPrice.setTextColor(
            if (entity.data.productDescriptions[1].check) selectColor else unSelectColor
        )
        itemMonthlyOriginalPrice.setTextColor(
            if (entity.data.productDescriptions[1].check) selectColor else unSelectColor
        )


        itemWeeksNumber.setTextColor(
            if (entity.data.productDescriptions[0].check) Color.BLACK else unSelectColor
        )
        itemWeeksName.setTextColor(
            if (entity.data.productDescriptions[0].check) Color.BLACK else unSelectColor
        )
        itemWeeksDollarSign.setTextColor(
            if (entity.data.productDescriptions[0].check) selectColor else unSelectColor
        )
        itemWeeksDiscountedPrice.setTextColor(
            if (entity.data.productDescriptions[0].check) selectColor else unSelectColor
        )
        itemWeeksOriginalPrice.setTextColor(
            if (entity.data.productDescriptions[0].check) selectColor else unSelectColor
        )
    }


    interface ChangeMembershipListener {
        fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions)
        fun closeListener(refreshTime: Long)

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