package com.crush.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.crush.R
import com.crush.bean.Avatar
import com.crush.ui.index.loadImage
import com.custom.base.util.SDViewUtil
import com.facebook.drawee.view.SimpleDraweeView
import com.luck.picture.lib.utils.ActivityCompatHelper
import com.youth.banner.adapter.BannerAdapter

class PageLoaderAdapter(
    val mActivity: Activity,
    val data: List<Avatar>,
) : BannerAdapter<Avatar, PageLoaderAdapter.BannerViewHolder>(data) {
    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder

    val load = arrayListOf<Int>()
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = SDViewUtil.getRId(parent.context, R.layout.item_start_page)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return BannerViewHolder(view)
    }

    override fun onBindView(holder: BannerViewHolder?, bean: Avatar, position: Int, size: Int) {
        holder?.apply {
            val iv = view.findViewById<SimpleDraweeView>(R.id.item_start_page_iv)
            val imageLoading = view.findViewById<LottieAnimationView>(R.id.image_loading)
            imageLoading.visibility=View.GONE
            if (load.indexOf(position) == -1){
                imageLoading.visibility = View.VISIBLE
                imageLoading.playAnimation()
                if (!ActivityCompatHelper.isDestroy(mActivity)) {
                    iv.loadImage(mActivity, bean.imageUrl, position) {
                        imageLoading.visibility = View.GONE
                        imageLoading.cancelAnimation()
                        load.add(position)
                    }
                }
            }else{

                if (!ActivityCompatHelper.isDestroy(mActivity)){
                    Glide.with(mActivity)
                        .load(bean.imageUrl)
                        .error(R.drawable.image_error)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv)
                }

            }

        }
    }

    inner class BannerViewHolder(var view: View) : RecyclerView.ViewHolder(
        view
    )
}