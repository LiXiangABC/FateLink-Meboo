package com.crush.mvp

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.crush.util.IntentUtil
import com.gyf.immersionbar.ImmersionBar
import com.custom.base.R
import com.custom.base.base.BaseActivity
import com.custom.base.type.AnimationActivityType
import io.rong.imkit.activity.Activities
import java.lang.reflect.ParameterizedType


/**
 * 作者：
 * 时间：2020-12-21
 * 描述：
 */

abstract class MVPBaseActivity<V : BaseView, T : BasePresenterImpl<V>> : BaseActivity(),
    BaseView {
    var mPresenter: T? = null
    var parms:Bundle? = null

    override fun initParms(parms: Bundle) {
        this.parms = parms
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Activities.get().add(this)
        mPresenter = getInstance<T>(this, 1)
        mPresenter?.attachView(this as V,mActivity)
        if(parms!=null){
            mPresenter?.initParms(parms!!)
        }
        initView()
        if(parms!=null){
            initViewBundle(parms!!)
        }
        mPresenter?.init()
        if(parms!=null){
            mPresenter?.initBundle(parms!!)
        }
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
        Activities.get().remove(this)
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
}
