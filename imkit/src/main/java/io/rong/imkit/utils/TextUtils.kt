package io.rong.imkit.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.widget.TextView
import io.rong.imkit.utils.ktl.subDiscountPice
import java.lang.Exception

object TextUtils {

    fun setSymbolText(textView: TextView, text: String,symbol:String) {
        val spannableString = SpannableString(text)

        // 设置"AUD"的字体大小为"88.88"的0.6倍
        val audSizeSpan = RelativeSizeSpan(0.6f)
        spannableString.setSpan(audSizeSpan, 0, symbol.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
    }

    fun subText(price:String):String{
        if (price.isNullOrEmpty()) {
            return price
        }
        var discountPice = price
        try {
            discountPice = price.substring(0, price.indexOf("/"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return discountPice
    }
}