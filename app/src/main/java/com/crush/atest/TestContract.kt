package com.crush.atest

import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class TestContract {
    interface View : BaseView {
    }

    internal interface Presenter : BasePresenter<View> {

    }
}
