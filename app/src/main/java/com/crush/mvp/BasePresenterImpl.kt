package com.crush.mvp

import android.app.Activity

/**
 * 作者：
 * 时间：2020-12-21
 * 描述：
 */

open class BasePresenterImpl<V : BaseView> :
    BasePresenter<V> {
    protected var mView: V? = null
    protected lateinit var mActivity: Activity

    override fun attachView(view: V, activity: Activity) {
        mView = view
        mActivity = activity
    }

    override fun detachView() {
        mView = null
    }
}
