package com.crush.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StrikethroughSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.crush.R
import com.crush.view.delay.DelayClickTextView
import com.crush.util.IntentUtil
import io.rong.imkit.activity.RongWebviewActivity
import io.rong.imkit.utils.ktl.subDiscountPrice

/**
 * @Author ct
 * @Date 2024/4/11 14:52
 * 礼包购买弹窗
 */
class GiftPackBuyDialog private constructor(
    context: Context,
    theme: Int
) : Dialog(context, theme) {

    interface Callback {
        fun onBackClick()
    }

    private class Params {
        lateinit var context: Context
        var hasShadow = true
        var canCancel = true
        var callback: Callback? = null
        var privacyPolicyUrl: String? = null
        var termsUrl: String? = null
    }

    class Builder(context: Context) {
        private val p = Params()

        init {
            p.context = context
        }

        fun setCallback(callback: Callback, privacyPolicyUrl: String?, termsUrl: String?,canCancel:Boolean): Builder {
            p.callback = callback
            p.privacyPolicyUrl = privacyPolicyUrl
            p.termsUrl = termsUrl
            p.canCancel = canCancel
            return this
        }

        fun create(): GiftPackBuyDialog {
            val dialog = GiftPackBuyDialog(
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
                .inflate(R.layout.dialog_gift_pack_buy, null)

            initialViews(view, dialog, 2)

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
            dialog: GiftPackBuyDialog,
            size: Int
        ) {
            val txtGiftPackBuyBtn = view.findViewById<DelayClickTextView>(R.id.txtGiftPackBuyBtn)
            val imgMyGiftPackBuyClose = view.findViewById<ImageView>(R.id.imgMyGiftPackBuyClose)
            val txtGiftPackBuyPrivacy = view.findViewById<TextView>(R.id.txtGiftPackBuyPrivacy)
            val conGiftPackBuyOne = view.findViewById<ConstraintLayout>(R.id.conGiftPackBuyOne)
            val conGiftPackBuyTwo = view.findViewById<ConstraintLayout>(R.id.conGiftPackBuyTwo)
            val conGiftPackBuyThree = view.findViewById<ConstraintLayout>(R.id.conGiftPackBuyThree)
            setPrivacyTextData(txtGiftPackBuyPrivacy)
            setBuyPrice(txtGiftPackBuyBtn,"111","99","111")

            imgMyGiftPackBuyClose.setOnClickListener {
                dialog.dismiss()
            }
            txtGiftPackBuyBtn.setOnClickListener {
                //购买成功或者失败
                p.callback?.onBackClick()
                dialog.dismiss()
            }
            //因为后台会更改配置礼包数量，所以这边就要动态修改下约束条件
            when (size) {
                1 -> {
                    conGiftPackBuyOne.visibility = View.VISIBLE
                    conGiftPackBuyTwo.visibility = View.GONE
                    conGiftPackBuyThree.visibility = View.GONE
                    val layoutParams =
                        conGiftPackBuyOne.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.dimensionRatio = "h,153:109"
                    layoutParams.matchConstraintPercentWidth = 0.456f
                    conGiftPackBuyOne.layoutParams = layoutParams
                }

                2 -> {
                    conGiftPackBuyOne.visibility = View.VISIBLE
                    conGiftPackBuyTwo.visibility = View.GONE
                    conGiftPackBuyThree.visibility = View.VISIBLE

                    val layoutParams =
                        conGiftPackBuyOne.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.dimensionRatio = "h,153:109"
                    layoutParams.matchConstraintPercentWidth = 0.456f
                    conGiftPackBuyOne.layoutParams = layoutParams

                    val layoutParamsTwo =
                        conGiftPackBuyThree.layoutParams as ConstraintLayout.LayoutParams
                    layoutParamsTwo.dimensionRatio = "h,153:109"
                    layoutParamsTwo.matchConstraintPercentWidth = 0.456f
                    conGiftPackBuyThree.layoutParams = layoutParamsTwo
                }
            }
        }

        //设置购买礼包按钮显示
        @SuppressLint("SetTextI18n")
        private fun setBuyPrice(txtGiftPackBuyBtn:DelayClickTextView, price:String, promotionPrice:String, priceOriginal: String){
            if (!TextUtils.isEmpty(price)) {
                val buyText =
                    "$${price} ${p.context.getString(R.string.dollar_sign)}${
                        subDiscountPrice(promotionPrice)
                    } ${
                        p.context.getString(R.string.txt_continue_now)
                    }"
                val spannableString = SpannableString(buyText)
                val strikethroughSpan = StrikethroughSpan()
                spannableString.setSpan(
                    strikethroughSpan,
                    0,
                    priceOriginal.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                txtGiftPackBuyBtn.text = spannableString
            } else {
                txtGiftPackBuyBtn.text =
                    "${p.context.getString(R.string.dollar_sign)}${price} ${
                        p.context.getString(R.string.txt_continue_now)
                    }"
            }
        }

        //设置协议文本
        private fun setPrivacyTextData(txtGiftPackBuyPrivacy: TextView) {
            val style = SpannableStringBuilder()
            style.append(p.context.getString(io.rong.imkit.R.string.buy_member_unitary_tips))
            style.setSpan(object : ClickableSpan() {
                override fun onClick(p0: View) {
                    val b = Bundle()
                    b.putString("url", p.privacyPolicyUrl)
                    b.putString(
                        "title",
                        p.context.resources.getString(io.rong.imkit.R.string.privacy_policy)
                    )
                    IntentUtil.startActivity(RongWebviewActivity::class.java, b)
                }
                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(
                        p.context,
                        R.color.white
                    )
                    ds.isUnderlineText = true
                    ds.clearShadowLayer()
                }
            }, 32, 46, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            style.setSpan(object : ClickableSpan() {
                override fun onClick(p0: View) {
                    val b = Bundle()
                    b.putString("url", p.termsUrl)
                    b.putString(
                        "title",
                        p.context.resources.getString(io.rong.imkit.R.string.terms)
                    )
                    IntentUtil.startActivity(RongWebviewActivity::class.java, b)
                }
                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(
                        p.context,
                        R.color.white
                    )
                    ds.isUnderlineText = true
                    ds.clearShadowLayer()
                }
            }, 51, 56, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            txtGiftPackBuyPrivacy.text = style
            txtGiftPackBuyPrivacy.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}