package com.crush.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.custom.base.util.SDViewUtil
import com.crush.R

/**
 * 作者：
 * 日期：2022/3/6
 * 说明：星星
 */
class StarStar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    var length = 0.0
    var maxLength = 0
    var minLength = 0
    var ivWidth = 0
    var isClick = false
    var listener:OnStarStarListener? = null
    var messages  = arrayListOf<String>()
    var textSize = 0f
    var textColor = 0
    var textLeftMargin = 0
    var rightMargin = 0
    init {
        orientation = HORIZONTAL
        val attrsArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.StarStar)
        length = attrsArray.getInteger(R.styleable.StarStar_StarStar_length,0).toDouble()
        maxLength = attrsArray.getInteger(R.styleable.StarStar_StarStar_maxLength,5)
        minLength = attrsArray.getInteger(R.styleable.StarStar_StarStar_minLength,0)
        ivWidth = attrsArray.getInteger(R.styleable.StarStar_StarStar_width,20)
        isClick = attrsArray.getBoolean(R.styleable.StarStar_StarStar_isClick,false)
        val message = attrsArray.getString(R.styleable.StarStar_StarStar_message)
        textSize = attrsArray.getDimension(R.styleable.StarStar_StarStar_message_text_size,12f)
        textColor = attrsArray.getColor(R.styleable.StarStar_StarStar_message_text_color, Color.BLACK)
        textLeftMargin = attrsArray.getInteger(R.styleable.StarStar_StarStar_message_text_leftMargin, 0)
        rightMargin = attrsArray.getInteger(R.styleable.StarStar_StarStar_rightMargin, 5)

        if(length>maxLength) length = maxLength.toDouble()

        if(message!=null&&message!=""&&message!="null"){
            for (s in message.split(",")) {
                messages.add(s)
            }
        }

        setView()
    }

    private fun setView(){
        for(i in 0 until maxLength){
            val ll = LinearLayout(context)
            val llLp = SDViewUtil.layoutParamsLinearLayoutWW
            val iv = ImageView(context)
            val ivLp = SDViewUtil.layoutParamsLinearLayoutWW
            ivLp.width = SDViewUtil.dp2pxInt(ivWidth)
            ivLp.rightMargin = SDViewUtil.dp2pxInt(rightMargin)
            iv.adjustViewBounds = true
            if(i < length||i < minLength){
//                iv.setImageResource(R.drawable.icon_star_y)
            }else{
//                iv.setImageResource(R.drawable.icon_star)
            }
            if(isClick)iv.setOnClickListener {
                length = (i+1).toDouble()
                listener?.onListener(length)
                removeAllViews()
                setView()
            }
            ll.gravity = Gravity.CENTER
            ll.addView(iv,ivLp)
            addView(ll,llLp)
        }
        if(messages.size>0&&length>0){
            val text = TextView(context)
            text.textSize = textSize
            text.setTextColor(textColor)
            text.text = messages[(length-1).toInt()]
            val tvLp = SDViewUtil.layoutParamsLinearLayoutWW
            tvLp.leftMargin = SDViewUtil.dp2pxInt(textLeftMargin)
            addView(text,tvLp)
        }

    }

    /**
     * 设置星星选中几颗
     */
    fun setGrain(length:Double){
        this.length = length
        removeAllViews()
        setView()
    }

    fun setOnStarStarListener(listener:OnStarStarListener){
        this.listener = listener
    }

    interface OnStarStarListener{
        fun onListener(length: Double)
    }
}