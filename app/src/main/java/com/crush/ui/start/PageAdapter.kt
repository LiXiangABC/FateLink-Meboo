package com.crush.ui.start

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.custom.base.util.SDViewUtil
import com.youth.banner.adapter.BannerAdapter

class PageAdapter(
    val mActivity: Activity,
    val data: List<Int>
) : BannerAdapter<Int, PageAdapter.BannerViewHolder>(data) {
    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = SDViewUtil.getRId(parent.context, R.layout.item_start_page)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return BannerViewHolder(view)
    }

    override fun onBindView(holder: BannerViewHolder?, id: Int, position: Int, size: Int) {
        holder?.apply {
            val iv = view.findViewById<ImageView>(R.id.item_start_page_iv)
            iv.setImageResource(id)
        }
    }

    inner class BannerViewHolder(var view: View) : RecyclerView.ViewHolder(
        view
    )
}