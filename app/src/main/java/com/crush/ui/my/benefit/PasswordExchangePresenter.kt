package com.crush.ui.my.benefit

import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.View
import com.crush.util.DensityUtil
import com.crush.util.SoftInputUtils
import com.crush.mvp.BasePresenterImpl
import com.kenny.separatededittext.SeparatedEditText

/**
 * @Author ct
 * @Date 2024/4/12 11:51
 */
class PasswordExchangePresenter : BasePresenterImpl<PasswordExchangeContact.View>() {
    override fun initBundle(bundle: Bundle) {

        mView?.apply {
            val decorView: View = mActivity.window.decorView
            decorView.viewTreeObserver.addOnGlobalLayoutListener {
                val r = Rect()
                mActivity.window.decorView.getWindowVisibleDisplayFrame(r)
                val defaultDisplay: Display = mActivity.windowManager.defaultDisplay
                val point = Point()
                defaultDisplay.getSize(point)
                val height = point.y
                val heightDifference = height - (r.bottom - r.top) // 实际高度减去可视图高度即是键盘高度
                conPwdExchangeContainers.setPadding(
                    DensityUtil.dip2px(mActivity, 0f),
                    DensityUtil.dip2px(mActivity, 0f),
                    DensityUtil.dip2px(mActivity, 0f), DensityUtil.dip2px(mActivity, 50f)
                )
                //Log.i("PasswordExchangePresenter", "heightDifference != 0   $heightDifference")
                if (heightDifference == 0){
                    Log.i("PasswordExchangePresenter","heightDifference == 0")
                    conPwdExchangeContainers.setPadding(
                        DensityUtil.dip2px(mActivity,0f),
                        DensityUtil.dip2px(mActivity,0f),
                        DensityUtil.dip2px(mActivity,0f),
                        DensityUtil.dip2px(mActivity,20f))
                }
            }

            editPwdExchanges.setTextChangedListener(object : SeparatedEditText.TextChangedListener {
                override fun textChanged(changeText: CharSequence?) {
                    Log.i("PasswordExchangePresenter", "textChanged ${changeText.toString()}")
                }

                override fun textCompleted(text: CharSequence?) {
                    Log.i("PasswordExchangePresenter", "textCompleted ${text.toString()}")
                    if (text?.isNotEmpty() == true && text.length == 6) {
                        SoftInputUtils.hideSoftInput(editPwdExchanges)
                    }
                }

            })
        }
    }
}