package com.crush.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.crush.R
import com.crush.view.delay.DelayClickTextView


/**
 * @Author ct
 * @Date 2024/4/11 19:50
 * 购买礼包成功弹窗
 */
class GiftPackBuySuccessDialog private constructor(
    context: Context,
    theme: Int
) : Dialog(context, theme) {

    interface Callback {
        fun onBackClick()
    }

    private class Params {
        lateinit var context: Context
        lateinit var lottieGiftPackBuySuccess:LottieAnimationView
        var hasShadow = true
        var canCancel = true
        var callback: Callback? = null
    }

    class Builder(context: Context) {
        private val p = Params()

        init {
            p.context = context
        }

        fun setCallback(callback: Callback): Builder {
            p.callback = callback
            return this
        }

        fun create(): GiftPackBuySuccessDialog {
            val dialog = GiftPackBuySuccessDialog(
                p.context,
                if (p.hasShadow) R.style.Theme_Light_NoTitle_Dialog else R.style.Theme_Light_NoTitle_NoShadow_Dialog
            )

            val window = dialog.window
            if (window != null) {
                window.setWindowAnimations(R.style.Animation_Bottom_Rising)
                window.decorView.setPadding(0, 0, 0, 0)
                val lp = window.attributes
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                window.attributes = lp
                window.setGravity(Gravity.CENTER)
            }

            @SuppressLint("InflateParams") val view = LayoutInflater.from(p.context)
                .inflate(R.layout.dialog_gift_pack_buy_success, null)

            initialViews(view, dialog)

            dialog.setContentView(view)
            dialog.setCanceledOnTouchOutside(p.canCancel)
            dialog.setCancelable(p.canCancel)
            return dialog
        }

        fun show() {
            create().show()
        }

        private fun initialViews(
            view: View,
            dialog: GiftPackBuySuccessDialog
        ) {
            p.lottieGiftPackBuySuccess = view.findViewById<LottieAnimationView>(R.id.lottieGiftPackBuySuccess)
            val imgMyGiftPackBuySuccessClose = view.findViewById<ImageView>(R.id.imgMyGiftPackBuySuccessClose)
            val txtGiftPackBuySuccessSubTitle = view.findViewById<TextView>(R.id.txtGiftPackBuySuccessSubTitle)
            val txtGiftPackBuySuccessBtn = view.findViewById<DelayClickTextView>(R.id.txtGiftPackBuySuccessBtn)
            p.lottieGiftPackBuySuccess.repeatCount = 0
            p.lottieGiftPackBuySuccess.playAnimation()

            imgMyGiftPackBuySuccessClose.setOnClickListener {
                p.lottieGiftPackBuySuccess.cancelAnimation()
                dialog.dismiss()
            }
            txtGiftPackBuySuccessBtn.setOnClickListener {
                p.lottieGiftPackBuySuccess.cancelAnimation()
                dialog.dismiss()
            }
            val text: String = txtGiftPackBuySuccessSubTitle.text.toString()
            val spannableString = SpannableString(text)
            // 找到特定字符串"10,000+"的开始和结束位置
            val target = "10,000+"
            val start: Int = text.indexOf(target)
            val end = start + target.length
            spannableString.setSpan(
                AbsoluteSizeSpan(20, true),
                start,
                end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            txtGiftPackBuySuccessSubTitle.text = spannableString
        }


    }
}