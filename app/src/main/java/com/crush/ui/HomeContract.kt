package com.crush.ui

import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.crush.view.LayoutNewBieMiniView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView
import com.crush.view.NavigationLayout
import com.crush.view.ScrollableCustomViewPager

/**
 * 作者：
 * 时间：
 * 描述：
 */

class HomeContract {
    interface View : BaseView {
        val fragmentId: FrameLayout
        val bottomContainer : LinearLayout
        val layoutNewBieMiniViews: LayoutNewBieMiniView
        val navigation: NavigationLayout
        val viewPager: ScrollableCustomViewPager
    }

    internal interface Presenter : BasePresenter<View> {
    }
}
