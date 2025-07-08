package com.crush.ui.look

import android.os.Bundle
import android.view.View
import com.crush.mvp.BasePresenterImpl


/**
 * 作者：
 * 时间：2021-02-07
 * 描述：查看图片
 */
class LookImagesPresenter : BasePresenterImpl<LookImagesContract.View>(),
    LookImagesContract.Presenter {
    private var images: List<String> = arrayListOf()
    private var index: Int = 0

    override fun initParms(parms: Bundle) {
        images = parms.getStringArrayList("images") as List<String>
        mView?.apply {
            viewPager.visibility = View.VISIBLE
            val adapter = MyImageAdapter(images, mActivity)
            viewPager.adapter = adapter
            viewPager.setCurrentItem(index, false)

        }

    }
}