package com.crush.ui.look

import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

/**
 * 作者：
 * 时间：2021-02-07
 * 描述：
 */

class LookImagesContract {
    interface View : BaseView {
        val viewPager: PhotoViewPager
    }

    internal interface Presenter : BasePresenter<View>
}
