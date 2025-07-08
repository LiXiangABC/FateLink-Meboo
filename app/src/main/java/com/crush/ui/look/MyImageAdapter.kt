package com.crush.ui.look

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.crush.util.GlideUtil
import io.rong.imkit.picture.photoview.PhotoView


/**
 * 作者：
 * 日期：2022/5/11
 * 说明：
 */
class MyImageAdapter(
    private val imageUrls: List<String>,
    private val activity: Activity
) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val url = imageUrls[position]
        val photoView = PhotoView(activity)
        GlideUtil.setImageView(url,photoView)
        container.addView(photoView)
        photoView.setOnClickListener {
            activity.finish()
        }
        return photoView
    }

    override fun getCount(): Int {
        return imageUrls.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    companion object {
        val TAG = MyImageAdapter::class.java.simpleName
    }

}