package com.crush.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.crush.R
import com.crush.util.Injections.animScaleKick
import com.custom.base.config.BaseConfig
import io.rong.imkit.SpName

class NavigationLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    val inflate = LayoutInflater.from(context).inflate(R.layout.layout_navigation, this)


    private val itemViews by lazy { arrayListOf<ImageView>(inflate.findViewById(R.id.tabHomeIndex), inflate.findViewById(R.id.tabHomeWlm),inflate.findViewById(R.id.tabHomeChat), inflate.findViewById(R.id.tabHomeMe)) }
    private val clickViews by lazy { arrayListOf<android.view.View>(inflate.findViewById(R.id.vTabFirst), inflate.findViewById(R.id.vTabSecond), inflate.findViewById(R.id.vTabThird), inflate.findViewById(R.id.vTabFour)) }
    private var currentIndex = -1
    private var pager: ViewPager? = null
    private var callback: Callback? = null

    interface Callback {
        fun onItemClick(position: Int): Boolean
        fun onItemClickAgain(position: Int)
    }

    init {
        clickViews.forEachIndexed { index, view ->
            view.setOnClickListener { select(index) }
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        pager?.currentItem?.let(::select)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    fun select(target: Int) {
        if (currentIndex == target) {
            val animId = when (currentIndex) {
                0 -> if (BaseConfig.getInstance.getInt(
                        SpName.trafficSource,
                        0
                    ) != 1
                ) R.mipmap.tab_index_select else R.mipmap.tab_index_white_select
                1 -> if (BaseConfig.getInstance.getInt(
                        SpName.trafficSource,
                        0
                    ) != 1
                ) R.mipmap.tab_wlm_select else R.mipmap.tab_wlm_white_select
                2 -> if (BaseConfig.getInstance.getInt(
                        SpName.trafficSource,
                        0
                    ) != 1
                ) R.mipmap.tab_im_select else R.mipmap.tab_im_white_select
                else -> R.mipmap.tab_my_select
            }
            select(itemViews[target], animId,target)
            callback?.onItemClickAgain(target)
            return
        }
        if (callback?.onItemClick(target) != true) {
            currentIndex = target
            itemViews.forEachIndexed { index, view ->
                if (target == index) {
                    val animId = when (index) {
                        0 -> if (BaseConfig.getInstance.getInt(
                                SpName.trafficSource,
                                0
                            ) != 1
                        ) R.mipmap.tab_index_select else R.mipmap.tab_index_white_select
                        1 -> if (BaseConfig.getInstance.getInt(
                                SpName.trafficSource,
                                0
                            ) != 1
                        ) R.mipmap.tab_wlm_select else R.mipmap.tab_wlm_white_select
                        2 -> if (BaseConfig.getInstance.getInt(
                                SpName.trafficSource,
                                0
                            ) != 1
                        ) R.mipmap.tab_im_select else R.mipmap.tab_im_white_select
                        else -> R.mipmap.tab_my_select
                    }
                    select(view, animId,index)
                } else {
                    val restId = when (index) {
                        0 ->  if (BaseConfig.getInstance.getInt(
                                SpName.trafficSource,
                                0
                            ) != 1
                        ) R.mipmap.tab_index_unselect else R.mipmap.tab_index_white_unselect
                        1 -> if (BaseConfig.getInstance.getInt(
                                SpName.trafficSource,
                                0
                            ) != 1
                        ) R.mipmap.tab_wlm_unselect else R.mipmap.tab_wlm_white_unselect
                        2 -> if (BaseConfig.getInstance.getInt(
                                SpName.trafficSource,
                                0
                            ) != 1
                        ) R.mipmap.tab_im_unselect else R.mipmap.tab_im_white_unselect
                        else -> R.mipmap.tab_my_unselect
                    }
                    reset(view, restId,index)
                }
            }
        }


    }


    fun attach(viewPager: ViewPager) {
        pager = viewPager
        viewPager.currentItem.let(::select)
    }

    fun setListener(listener: Callback?) {
        callback = listener
    }

    private fun select(imageView: ImageView, animationId: Int, index: Int) {
        if (index != 3){
            imageView.setImageResource(animationId)
            itemViews[3].setBackgroundResource(R.drawable.shape_me_unselect_bg)
        }else {
            itemViews[3].setBackgroundResource(R.drawable.shape_me_select_bg)
        }
        imageView.startAnimation(animScaleKick)
    }

    private fun reset(imageView: ImageView, resId: Int, index: Int) {
        if (index != 3){
            imageView.setImageResource(resId)
        }else{
            imageView.background=null
        }
        imageView.clearAnimation()
    }

    /**
     * 设置未读消息
     */
    fun setUnreadNum(number:Int){
        val txtUnreadNum = inflate.findViewById<TextView>(R.id.txt_unread_num)
        txtUnreadNum.isVisible=number>0
        txtUnreadNum.text=if (number > 100) "99+" else "$number"
    }

    fun setAvatar(url:String){
        Glide.with(this)
            .load(url)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(inflate.findViewById<ImageView>(R.id.tabHomeMe))
    }

    /**
     * 设置wlm次数
     */
    fun setWLMNum(number:Int){
        val txtWlmNum = inflate.findViewById<TextView>(R.id.txt_wlm_num)
        txtWlmNum.isVisible=number>0
        txtWlmNum.text=if (number > 100) "99+" else "$number"
    }

}