package com.crush.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.crush.R
import com.zhy.autolayout.AutoLinearLayout

/**
 * 作者：
 * 日期：2022/4/4
 * 说明：背景
 */
class SleekLinearLayout(context: Context, attrs: AttributeSet) : AutoLinearLayout(context, attrs) {
    fun init(position: Int,length:Int,view: View? = null){
        if(position == 0 && length == 1) {
            view?.visibility = View.GONE
            setBackgroundResource(R.drawable.layer_white_corner_item_single)
        } else if(position == 0 && length !=1) {
            view?.visibility = View.GONE
            setBackgroundResource(R.drawable.shape_solid_white_radius_12)
        } else if(position != 0 && position == length-1) {
            view?.visibility = View.VISIBLE
            setBackgroundResource(R.drawable.layer_white_corner_item_bottomleft_bottomright_single)
        } else {
            view?.visibility = View.VISIBLE
            setBackgroundColor(Color.WHITE)
        }
    }
}