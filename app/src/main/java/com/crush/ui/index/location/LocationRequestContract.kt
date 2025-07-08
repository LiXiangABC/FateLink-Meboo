package com.crush.ui.index.location

import android.widget.ImageView
import android.widget.LinearLayout
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class LocationRequestContract {
    interface View : BaseView {
        val  requestLocationContainer:LinearLayout
        val requestLocationClose:ImageView
        val outSideView:ImageView
    }

    internal interface Presenter : BasePresenter<View> {

    }
}
