package com.crush.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.crush.entity.NewBieGiftPackEntity
import com.crush.util.GlideUtil
import com.custom.base.util.SDViewUtil
import com.youth.banner.adapter.BannerAdapter

//新手礼包权限轮播显示
class NewBieGiftPackVpAdapter(
    val mActivity: Context,
    val data: List<NewBieGiftPackEntity.Data.Carousel>
) : BannerAdapter<NewBieGiftPackEntity.Data.Carousel, NewBieGiftPackVpAdapter.BannerViewHolder>(data) {
    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = SDViewUtil.getRId(parent.context, R.layout.item_newbie_giftpack_page)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return BannerViewHolder(view)
    }

    override fun onBindView(
        holder: BannerViewHolder?,
        bean: NewBieGiftPackEntity.Data.Carousel,
        position: Int,
        size: Int
    ) {
        holder?.apply {
            val imgTop = view.findViewById<ImageView>(R.id.img_top)
            val itemTitle = view.findViewById<TextView>(R.id.item_title)
            val itemContent = view.findViewById<TextView>(R.id.item_content)

            GlideUtil.setImageView(bean.img, imgTop)
            itemTitle.text = bean.title
            itemContent.text = bean.content
        }
    }

    inner class BannerViewHolder(var view: View) : RecyclerView.ViewHolder(
        view
    )
}