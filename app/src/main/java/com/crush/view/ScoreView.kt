package com.crush.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.custom.base.util.SDViewUtil

/**
 * 作者：
 * 日期：2022/3/18
 * 说明：评分
 */
class ScoreView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    val text:TextView
    val starStar:StarStar
    init {
        orientation = HORIZONTAL
        starStar = StarStar(context,attrs)
        addView(starStar,SDViewUtil.layoutParamsFrameLayoutWW)
        text = TextView(context)
        text.setTextColor(Color.parseColor("#FF000000"))
        text.textSize = 10f
        text.text = "1"
        val view = View(context)
        val viewParams = SDViewUtil.layoutParamsLinearLayoutWW
        viewParams.width = 10
        addView(view,viewParams)
        addView(text)
    }

    /**
     * 设置评分
     */
    fun setScore(score: Double){
        text.text = score.toString()
        starStar.setGrain(score)
    }
}