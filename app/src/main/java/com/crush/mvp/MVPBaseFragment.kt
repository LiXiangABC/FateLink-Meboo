package com.crush.mvp

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.crush.util.IntentUtil
import com.gyf.immersionbar.ImmersionBar
import com.custom.base.R
import com.custom.base.base.BaseFragment
import com.custom.base.type.AnimationActivityType
import java.lang.reflect.ParameterizedType

/**
 * 作者：
 * 时间：2020-12-21
 * 描述：
 */
abstract class MVPBaseFragment<V : BaseView, T : BasePresenterImpl<V>> : BaseFragment(),
    BaseView {
    var mPresenter: T? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isAdded || activity == null) {
            return
        }
        mPresenter = getInstance<T>(this, 1)
        mPresenter?.attachView(this as V, mActivity)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPresenter?.init()
        arguments?.apply {
            mPresenter?.initBundle(this)
        }
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }

    fun <T> getInstance(o: Any, i: Int): T? {
        try {
            return ((o.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[i] as Class<T>).newInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 页面跳转
     */
    fun startActivity(
        clz: Class<*>,
        bundle: Bundle? = null,
        view: View? = null,
        requestCode: Int = 0,
        flags: List<Int> = listOf(),
        animationStartType: AnimationActivityType = AnimationActivityType.NO,
        animationEndType:Int = R.anim.animo_no,
        activity: Activity? = null
    ) {
        IntentUtil.startActivity(clz, bundle, requestCode, flags, view,activity = activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ImmersionBar.destroy(this)
    }
}