package com.crush.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.crush.R
import com.crush.adapter.NewBieGiftPackVpAdapter
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.entity.NewBieGiftPackEntity
import com.crush.util.GlideUtil
import com.crush.view.cardstackview.internal.DisplayUtil.dpToPx
import com.crush.view.delay.DelayClickTextView
import com.custom.base.config.BaseConfig
import com.youth.banner.Banner
import com.youth.banner.listener.OnPageChangeListener
import io.rong.imkit.SpName.userCode

/**
 * @Author ct
 * @Date 2024/5/10 11:41
 * 新手礼包弹窗
 */
class NewBieGiftPackDialog private constructor(
    context: Context,
    theme: Int
) : Dialog(context, theme) {

    interface Callback {
        fun onBackClick()
        fun onBuyClick()
    }

    private class Params {
        lateinit var context: Context
        lateinit var lottieNewBieGiftPackBg: LottieAnimationView
        var hasShadow = true
        var canCancel = true
        var isLoadImg = false
        var callback: Callback? = null
        var data: NewBieGiftPackEntity.Data? = null
        var mDotImages = mutableListOf<ImageView>()
    }

    class Builder(context: Context) {
        private val p = Params()

        init {
            p.context = context
        }

        fun setCallback(
            callback: Callback,
            data: NewBieGiftPackEntity.Data?,
            canCancel: Boolean
        ): Builder {
            p.callback = callback
            p.canCancel = canCancel
            p.data = data
            return this
        }

        fun create(): NewBieGiftPackDialog {
            val dialog = NewBieGiftPackDialog(
                p.context,
                if (p.hasShadow) R.style.Theme_Light_NoTitle_Dialog else R.style.Theme_Light_NoTitle_NoShadow_Dialog
            )

            val window = dialog.window
            if (window != null) {
                window.setWindowAnimations(R.style.Animation_Bottom_Rising)
                window.decorView.setPadding(0, 0, 0, 0)
                val lp = window.attributes
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.MATCH_PARENT
                window.attributes = lp
                window.setGravity(Gravity.CENTER)
            }

            @SuppressLint("InflateParams") val view = LayoutInflater.from(p.context)
                .inflate(R.layout.dialog_newbie_giftpack, null)

            initialViews(view, dialog)

            dialog.setContentView(view)
            dialog.setCanceledOnTouchOutside(p.canCancel)
            dialog.setCancelable(p.canCancel)
            return dialog
        }

        fun show() {
            create().show()
        }

        @SuppressLint("SetTextI18n")
        private fun initialViews(
            view: View,
            dialog: NewBieGiftPackDialog
        ) {
            //外面大背景
            val conNewBieGiftPackBg = view.findViewById<ConstraintLayout>(R.id.conNewBieGiftPackBg)
            val imgNewBieGiftPackBg = view.findViewById<ImageView>(R.id.imgNewBieGiftPackBg)
            //banner
            val bannerNewBieGiftPack = view.findViewById<Banner<*, *>>(R.id.bannerNewBieGiftPack)
            var indicatorNewBieGiftPack =
                view.findViewById<LinearLayout>(R.id.indicatorNewBieGiftPack)
            //left
            val conNewBieGiftPackLeftPrice =
                view.findViewById<ConstraintLayout>(R.id.conNewBieGiftPackLeftPrice)
            val conNewBieGiftPackOffFromContainer =
                view.findViewById<ConstraintLayout>(R.id.conNewBieGiftPackOffFromContainer)
            val conNewBieGiftPackPriceContainer = view.findViewById<ConstraintLayout>(R.id.conNewBieGiftPackPriceContainer)
            //打折百分比
            val txtNewBieGiftPackOffFrom =
                view.findViewById<TextView>(R.id.txtNewBieGiftPackOffFrom)
            txtNewBieGiftPackOffFrom.text = p.data?.discountTag ?: ""
            //打折后的价格
            val txtNewBieGiftPackPriceNum= view.findViewById<TextView>(R.id.txtNewBieGiftPackPriceNum)
            txtNewBieGiftPackPriceNum.text = p.data?.newcomerPrice ?: ""
            //打折前的价格
            val txtNewBieGiftPackPriceTwo =
                view.findViewById<TextView>(R.id.txtNewBieGiftPackPriceTwo)
            // 创建 SpannableString 对象
            if (!TextUtils.isEmpty(p.data?.memberInfo?.remark)) {
                val insteadStr = p.data?.memberInfo?.remark!!
                val spannableString = SpannableString(insteadStr)
                // 获取要划线的字符的起始位置和结束位置
                val start = insteadStr.indexOf(p.data?.currencyType?:"")
                val end = insteadStr.length
                // 应用 StrikethroughSpan 到指定范围的字符
                spannableString.setSpan(StrikethroughSpan(), start, end, 0)
                txtNewBieGiftPackPriceTwo.text = spannableString
            }
            //right
            val conNewBieGiftPackRightInfo =
                view.findViewById<ConstraintLayout>(R.id.conNewBieGiftPackRightInfo)
            val imgNewBieGiftPackRes =
                view.findViewById<ImageView>(R.id.imgNewBieGiftPackRes)
            val txtNewBieGiftPackResNum =
                view.findViewById<TextView>(R.id.txtNewBieGiftPackResNum)
            val txtNewBieGiftPackResDesc =
                view.findViewById<TextView>(R.id.txtNewBieGiftPackResDesc)
            //bottom
            val txtNewBieGiftPackBottomNotice =
                view.findViewById<TextView>(R.id.txtNewBieGiftPackBottomNotice)
            val txtTermTipTitle =
                view.findViewById<TextView>(R.id.dialog_tip_title)
            val txtTermTipContent =
                view.findViewById<TextView>(R.id.dialog_tip_content)

            txtNewBieGiftPackBottomNotice.text = p.data?.notice ?: ""
            txtTermTipTitle.text = p.data?.payInfo?.title ?: ""
            txtTermTipContent.text = p.data?.payInfo?.content ?: ""

            //购买按钮文案
            val txtNewBieGiftPackBuy = view.findViewById<DelayClickTextView>(R.id.txtNewBieGiftPackBuy)
            if (!TextUtils.isEmpty(p.data?.normalPrice) && !TextUtils.isEmpty(p.data?.newcomerPrice)) {
                setBuyPrice(
                    txtNewBieGiftPackBuy,
                    p.data?.normalPrice!!,
                    p.data?.newcomerPrice!!,
                    p.data?.normalPrice!!
                )
            }
            val imgNewBieGiftPackClose =
                view.findViewById<ImageView>(R.id.imgNewBieGiftPackClose)
            p.lottieNewBieGiftPackBg =
                view.findViewById(R.id.lottieNewBieGiftPackBg)

            //设置banner数据
            //bannerNewBieGiftPack.setStartPosition(0)
            p.data?.let {
                if (!it.carousel.isNullOrEmpty()) {
                    //设置小点
                    it.carousel.forEachIndexed { index, carousel ->
                        val imgDot = ImageView(p.context)
                        //设置圆点的尺寸
                        val params = LinearLayout.LayoutParams(
                            dpToPx(p.context, 6f),
                            dpToPx(p.context, 6f)
                        )
                        params.leftMargin = dpToPx(p.context, 3f)
                        params.rightMargin = dpToPx(p.context, 3f)
                        imgDot.layoutParams = params
                        p.mDotImages.add(imgDot)
                        if (index == 0) {
                            imgDot.setImageResource(R.drawable.shape_white_circle)
                        } else {
                            imgDot.setImageResource(R.drawable.shape_99ffffff_circle)
                        }
                        indicatorNewBieGiftPack.addView(imgDot)
                    }
                    bannerNewBieGiftPack.currentItem = 0
                    val adapter = NewBieGiftPackVpAdapter(p.context, it.carousel)
                    bannerNewBieGiftPack.adapter = adapter
                    bannerNewBieGiftPack.addOnPageChangeListener(onPageListener)
                }
            }

            //针对小屏幕和大屏
//            if(DensityUtil.getScresHeight(p.context)<=2200){
//                val layoutParamsIndicatorNewBieGiftPack =
//                    indicatorNewBieGiftPack.layoutParams as ConstraintLayout.LayoutParams
//                layoutParamsIndicatorNewBieGiftPack.topMargin =  dpToPx(p.context, 6f)
//                indicatorNewBieGiftPack.layoutParams = layoutParamsIndicatorNewBieGiftPack
//            }else{
//                //动态设置垂直距离
//                val layoutParamsBanner =
//                    bannerNewBieGiftPack.layoutParams as ConstraintLayout.LayoutParams
//                layoutParamsBanner.matchConstraintPercentHeight = 0.2f
//                bannerNewBieGiftPack.layoutParams = layoutParamsBanner
//
//                val layoutParamsIndicatorNewBieGiftPack =
//                    indicatorNewBieGiftPack.layoutParams as ConstraintLayout.LayoutParams
//                layoutParamsIndicatorNewBieGiftPack.topMargin =  dpToPx(p.context, 6f)
//                indicatorNewBieGiftPack.layoutParams = layoutParamsIndicatorNewBieGiftPack
//            }
            bannerNewBieGiftPack.visibility =View.GONE
            indicatorNewBieGiftPack.visibility =View.GONE
            conNewBieGiftPackPriceContainer.visibility =View.GONE
            conNewBieGiftPackOffFromContainer.visibility =View.GONE
            txtNewBieGiftPackBuy.visibility =View.GONE
            txtNewBieGiftPackBottomNotice.visibility =View.GONE
            p.lottieNewBieGiftPackBg.visibility =View.GONE

            p.data?.let {
                if (!TextUtils.isEmpty(it.backgroundImg)) {
                    GlideUtil.loadImageViewWithCallBack(
                        it.backgroundImg,
                        imgNewBieGiftPackBg,
                    ){
                        //Log.i("新手礼包", "用户加载弹窗背景图回调")
                        //设置本地图片
                        if(!it){
                            imgNewBieGiftPackBg.setImageResource(R.mipmap.img_newbie_giftpack_bg)
                        }
                        bannerNewBieGiftPack.visibility =View.VISIBLE
                        indicatorNewBieGiftPack.visibility =View.VISIBLE
                        conNewBieGiftPackPriceContainer.visibility =View.VISIBLE
                        conNewBieGiftPackOffFromContainer.visibility =View.VISIBLE
                        txtNewBieGiftPackBuy.visibility =View.VISIBLE
                        txtNewBieGiftPackBottomNotice.visibility =View.VISIBLE

                        if(!p.isLoadImg){
                            //Log.i("新手礼包", "用户加载弹窗背景图回调==p.isLoadImg=${p.isLoadImg}")
                            p.isLoadImg = true
                            if (BaseConfig.getInstance.getBoolean("NewBieGiftPack_${BaseConfig.getInstance.getString(userCode,"")}",false)) {
                                p.lottieNewBieGiftPackBg.visibility = View.GONE
                            }else{
                                p.lottieNewBieGiftPackBg.visibility =View.VISIBLE
                                p.lottieNewBieGiftPackBg.playAnimation()
                                BaseConfig.getInstance.setBoolean("NewBieGiftPack_${BaseConfig.getInstance.getString(userCode,"")}",true)
                                DotLogUtil.setEventName(DotLogEventName.All_Egg_Sub)
                                    .commit(p.context)
                                //Log.i("新手礼包打点","All_Egg_Sub")
                            }
                        }
                    }
                }
            }

            //关闭点击
            imgNewBieGiftPackClose.setOnClickListener {
                p.callback?.onBackClick()
                dialog.dismiss()
            }
            //购买点击
            txtNewBieGiftPackBuy.setOnClickListener {
                p.callback?.onBuyClick()
            }

            //打开动画只展示一次
            //如果当前登录的用户 NewBieGiftPack_111
//            if (BaseConfig.getInstance.getBoolean("NewBieGiftPack_${BaseConfig.getInstance.getString(userCode,"")}",false)) {
//                p.lottieNewBieGiftPackBg.visibility = View.GONE
//            } else {
//                BaseConfig.getInstance.setBoolean("NewBieGiftPack_${BaseConfig.getInstance.getString(userCode,"")}",true)
//            }

            //因为后台会更改配置礼包数量，所以这边就要动态修改下约束条件
            p.data?.let {
                if (it.benefitsInfo == null) {
                    //只有一个
                    conNewBieGiftPackRightInfo.visibility = View.GONE
                    conNewBieGiftPackLeftPrice.setBackgroundResource(R.mipmap.img_newbie_giftpack_price_one)
                    val layoutParams =
                        conNewBieGiftPackLeftPrice.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.matchConstraintPercentWidth = 0.797f
                    layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    conNewBieGiftPackLeftPrice.layoutParams = layoutParams
                    val layoutParamsTag =
                        conNewBieGiftPackOffFromContainer.layoutParams as ConstraintLayout.LayoutParams
                    layoutParamsTag.horizontalBias = 0.908f
                    conNewBieGiftPackOffFromContainer.layoutParams = layoutParamsTag
                } else {
                    if (!TextUtils.isEmpty(it.benefitsInfo.icon)) {
                        GlideUtil.setImageView(it.benefitsInfo.icon, imgNewBieGiftPackRes)
                    }
                    txtNewBieGiftPackResNum.text = "X" + it.benefitsInfo.num
                    txtNewBieGiftPackResDesc.text = it.benefitsInfo.name ?: ""
                }
            }
        }

        //设置购买礼包按钮显示
        @SuppressLint("SetTextI18n")
        private fun setBuyPrice(
            txtGiftPackBuyBtn: TextView,
            price: String,
            promotionPrice: String,
            priceOriginal: String
        ) {
            if (!TextUtils.isEmpty(price)) {
                val buyText =
                    "$price $promotionPrice ${
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
                spannableString.setSpan(AbsoluteSizeSpan(14, true), 0, priceOriginal.length, 0)
                txtGiftPackBuyBtn.text = spannableString
            } else {
                txtGiftPackBuyBtn.text =
                    "$price ${
                        p.context.getString(R.string.txt_continue_now)
                    }"
            }
        }

        //监听banner选中
        private val onPageListener = object : OnPageChangeListener {
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(position: Int) {
                p.data?.let {
                    if (!it.carousel.isNullOrEmpty()) {
                        //设置小点
                        it.carousel.forEachIndexed { index, carousel ->
                            if (index == position) {
                                p.mDotImages[index].setImageResource(R.drawable.shape_white_circle)
                            }else{
                                p.mDotImages[index].setImageResource(R.drawable.shape_99ffffff_circle)
                            }
                        }
                    }
                }
            }

            override fun onPageScrollStateChanged(p0: Int) {

            }

        }
    }
}