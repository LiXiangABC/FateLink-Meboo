package com.crush.ui.member

import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class MemberSubscriptionContract {
    interface View : BaseView {
        val toolbarTitle: Toolbar
        val outsideTopContainer:ConstraintLayout
        val outView: CoordinatorLayout
        val topContainer:ConstraintLayout
        val memberBackLayout:LinearLayout
        val memberTitle:TextView
        val memberTip:TextView
        val txtMemberTitle:TextView
        val txtMemberContent:TextView
        val txtMemberDate:TextView

        val quarterContainer:ConstraintLayout
        val itemQuarterTitle :TextView
        val itemQuarterNumber :TextView
        val itemQuarterName :TextView
        val itemQuarterDiscountedPrice:TextView
        val itemQuarterOriginalPrice:TextView 
        val itemQuarterDollarSign:TextView

        val monthlyContainer:ConstraintLayout
        val itemMonthlyTitle:TextView
        val itemMonthlyNumber :TextView
        val itemMonthlyName:TextView 
        val itemMonthlyDiscountedPrice:TextView 
        val itemMonthlyOriginalPrice:TextView
        val itemMonthlyDollarSign:TextView

        val weeksContainer :ConstraintLayout
        val itemWeeksNumber:TextView 
        val itemWeeksName:TextView 
        val itemWeeksDiscountedPrice:TextView
        val itemWeeksDollarSign:TextView
        val itemWeeksOriginalPrice:TextView

        val memberEquityList:RecyclerView

        val memberAction:TextView
        val txtPaymentProtocol:TextView
        val bottomContainer:ConstraintLayout
        val memberActionBuy:TextView
        val txtDiscountTag:TextView

        val txtGetPremium:TextView
        val txtValidTime:TextView

    }

    internal interface Presenter : BasePresenter<View> {
    }
}
