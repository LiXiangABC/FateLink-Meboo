package com.crush.ui.my.profile.info

import androidx.viewpager.widget.ViewPager
import com.crush.mvp.BaseView
import com.flyco.tablayout.SlidingTabLayout

class ProfileInfoContact {
    interface View : BaseView {
        val profileTabLayout: SlidingTabLayout
        val profileViewPager: ViewPager
    }
}