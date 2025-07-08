package com.crush.ui.my.profile.info.fragment

import androidx.recyclerview.widget.RecyclerView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class PhotoContract {
    interface View : BaseView {

        val photoList:RecyclerView?
    }

    internal interface Presenter : BasePresenter<View> {
    }

}