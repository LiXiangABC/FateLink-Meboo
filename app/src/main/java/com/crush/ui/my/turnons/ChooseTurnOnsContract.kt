package com.crush.ui.my.turnons

import androidx.recyclerview.widget.RecyclerView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class ChooseTurnOnsContract {
    interface View : BaseView {
        val turnOnsList:RecyclerView
    }

    internal interface Presenter : BasePresenter<View> {

    }
}
