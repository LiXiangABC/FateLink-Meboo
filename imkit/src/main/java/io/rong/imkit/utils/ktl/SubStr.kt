package io.rong.imkit.utils.ktl

import android.widget.TextView
import java.lang.Exception

fun TextView.subDiscountPice(pice: String?) {
    if (pice.isNullOrEmpty()) {
        this.text = ""
        return
    }
    var discountPice = ""
    try {
        discountPice = pice.substring(1, pice.indexOf("/"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    this.text = " " + discountPice
}

fun subDiscountPrice(price: String?):String {
    if (price.isNullOrEmpty()) {
        return ""
    }
    var discountPice = ""
    try {
        discountPice = price.substring(0, price.indexOf("/"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return " " + discountPice
}
