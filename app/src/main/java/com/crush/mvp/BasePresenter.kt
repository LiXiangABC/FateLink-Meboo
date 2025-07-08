package com.crush.mvp

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.crush.util.IntentUtil
import com.custom.base.R
import com.custom.base.manager.SDActivityManager
import com.custom.base.type.AnimationActivityType
import io.rong.imkit.activity.Activities

/**
 * 作者：
 * 时间：2020-12-21
 * 描述：
 */

interface BasePresenter<V : BaseView>{
    fun attachView(view: V, mActivity: Activity)

    fun detachView()

    fun init(){}

    fun initBundle(bundle: Bundle){}

    fun initParms(bundle: Bundle){}

    /**
     * 简化Toast
     */
    fun showToast(message: String) {
        Activities.get().top?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 结束最后一个activity 或者指定一个activity
     */
    fun finish(view: View? = null,cls: Class<*>? = null){
        if(view == null){
            if(cls == null) Activities.get().top?.finish()
            else Activities.get().finish(cls)
        }else{
            if(cls == null) view.setOnClickListener { Activities.get().top?.finish() }
            else view.setOnClickListener { Activities.get().finish(cls)  }
        }

    }

    /**
     * 页面跳转
     */
    fun startActivity(
        clz: Class<*>,
        bundle: Bundle? = null,
        view:View? = null,
        requestCode: Int = 0,
        flags: List<Int> = listOf(),
        animationStartType: AnimationActivityType = AnimationActivityType.RIGHT,
        animationEndType:Int = R.anim.animo_no
    ) {
        IntentUtil.startActivity(clz, bundle, requestCode, flags)
    }
}
