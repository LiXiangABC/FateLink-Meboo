package com.crush.ui.my.profile.info.fragment

import androidx.recyclerview.widget.RecyclerView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class TurnOnsContract {
    interface View : BaseView {

        val turnOnsList:RecyclerView?
    }

    internal interface Presenter : BasePresenter<View> {
    }

}