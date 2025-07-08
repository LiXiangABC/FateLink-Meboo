package com.crush.dot

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.analytics.ktx.logEvent


class AFDotLogUtil {
    fun addPurchaseEvent(
        context: Context?,
        amount: String,
        productCode: String,
        orderNo: String,
        currencyType: String,
        pushAfSwitch: Boolean,
        pushFBSwitch: Boolean
    ) {
        if (pushFBSwitch) {
            addFirebasePurchase(amount.toDouble(), productCode, orderNo, currencyType)
        }
    }

    fun addFirebasePurchase(amount: Double, productCode: String,orderNo: String,currencyType:String) {
        // 获取 FirebaseAnalytics 实例
        val mFirebaseAnalytics = Firebase.analytics
        // 记录支付事件
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
            param(FirebaseAnalytics.Param.CURRENCY, currencyType?:"USD") // 货币代码
            param(FirebaseAnalytics.Param.VALUE, amount)    // 支付金额
            param(FirebaseAnalytics.Param.PRICE, amount)    // 支付金额
            param(FirebaseAnalytics.Param.TRANSACTION_ID,orderNo) // 交易ID，可选
            param(FirebaseAnalytics.Param.ITEM_NAME, productCode)// 商品名称，可选
        }
    }

    fun addFirebaseSighUp(method:String){
        val firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP){
            param(FirebaseAnalytics.Param.METHOD, method)
        }

    }

}