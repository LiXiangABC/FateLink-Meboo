package com.crush.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class ScrollableCustomViewPager(
        context: Context,
        attrs: AttributeSet?
) : ViewPager(context, attrs) {
    var isScrollable = true


    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return isScrollable && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            return isScrollable && super.onInterceptTouchEvent(ev)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

}
