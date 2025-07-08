package com.crush.ui.member

import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.custom.base.config.BaseConfig
import com.crush.mvp.MVPBaseActivity
import com.sunday.eventbus.SDBaseEvent
import io.rong.imkit.SpName
import io.rong.imkit.event.EnumEventTag

/**
 * 会员订阅
 */
class MemberSubscriptionActivity : MVPBaseActivity<MemberSubscriptionContract.View, MemberSubscriptionPresenter>(), MemberSubscriptionContract.View {

    override fun setFullScreen(): Boolean {
        return true
    }
    override fun bindLayout(): Int {
        return R.layout.act_member_subscription
    }

    override fun initView() {
        memberBackLayout.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseConfig.getInstance.setString(SpName.orderEventId,"")
    }

    override fun onEventMainThread(event: SDBaseEvent) {
        when (EnumEventTag.valueOf(event.tagInt)) {
            EnumEventTag.CLOSE_DISCOUNT_POP->{
               mPresenter?.getData()
            }

            else -> {

            }
        }

    }

    override val toolbarTitle: Toolbar
        get() =  findViewById(R.id.toolbar_title)

    override val outsideTopContainer: ConstraintLayout
        get() =  findViewById(R.id.outside_top_container)
    override val outView: CoordinatorLayout
        get() =  findViewById(R.id.out_view)
    override val topContainer: ConstraintLayout
        get() =  findViewById(R.id.top_container)
    override val memberBackLayout: LinearLayout
        get() =  findViewById(R.id.member_back_layout)
    override val memberTitle: TextView
        get() =  findViewById(R.id.member_title)
    override val memberTip: TextView
        get() =  findViewById(R.id.member_tip)
    override val txtMemberTitle: TextView
        get() =  findViewById(R.id.txt_member_title)
    override val txtMemberContent: TextView
        get() = findViewById(R.id. txt_member_content)
    override val txtMemberDate: TextView
        get() =  findViewById(R.id.txt_member_date)
    override val quarterContainer: ConstraintLayout
        get() =  findViewById(R.id.quarter_container)
    override val itemQuarterTitle: TextView
        get() =  findViewById(R.id.item_quarter_title)
    override val itemQuarterNumber: TextView
        get() =  findViewById(R.id.item_quarter_number)
    override val itemQuarterName: TextView
        get() =  findViewById(R.id.item_quarter_name)
    override val itemQuarterDiscountedPrice: TextView
        get() =  findViewById(R.id.item_quarter_discounted_price)
    override val itemQuarterOriginalPrice: TextView
        get() =  findViewById(R.id.item_quarter_original_price)
    override val itemQuarterDollarSign: TextView
        get() =  findViewById(R.id.item_quarter_dollar_sign)
    override val monthlyContainer: ConstraintLayout
        get() =  findViewById(R.id.monthly_container)
    override val itemMonthlyTitle: TextView
        get() =  findViewById(R.id.item_monthly_title)
    override val itemMonthlyNumber: TextView
        get() =  findViewById(R.id.item_monthly_number)
    override val itemMonthlyName: TextView
        get() =  findViewById(R.id.item_monthly_name)
    override val itemMonthlyDiscountedPrice: TextView
        get() =  findViewById(R.id.item_monthly_discounted_price)
    override val itemMonthlyOriginalPrice: TextView
        get() =  findViewById(R.id.item_monthly_original_price)
    override val itemMonthlyDollarSign: TextView
        get() =  findViewById(R.id.item_monthly_dollar_sign)
    override val weeksContainer: ConstraintLayout
        get() =  findViewById(R.id.weeks_container)
    override val itemWeeksNumber: TextView
        get() =  findViewById(R.id.item_weeks_number)
    override val itemWeeksName: TextView
        get() =  findViewById(R.id.item_weeks_name)
    override val itemWeeksDiscountedPrice: TextView
        get() =  findViewById(R.id.item_weeks_discounted_price)
    override val itemWeeksDollarSign: TextView
        get() =  findViewById(R.id.item_weeks_dollar_sign)
    override val itemWeeksOriginalPrice: TextView
        get() =  findViewById(R.id.item_weeks_original_price)
    override val memberEquityList: RecyclerView
        get() =  findViewById(R.id.member_equity_list)
    override val memberAction: TextView
        get() =  findViewById(R.id.member_action)
    override val txtPaymentProtocol: TextView
        get() =  findViewById(R.id.txt_payment_protocol)
    override val bottomContainer: ConstraintLayout
        get() =  findViewById(R.id.bottom_container)
    override val memberActionBuy: TextView
        get() =  findViewById(R.id.member_action_buy)
    override val txtDiscountTag: TextView
        get() =  findViewById(R.id.txt_discount_tag)
    override val txtGetPremium: TextView
        get() =  findViewById(R.id.txt_get_premium)
    override val txtValidTime: TextView
        get() =  findViewById(R.id.txt_valid_time)

}