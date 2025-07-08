package com.crush.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.media.Image
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
import com.crush.bean.UserPhotoV2Bean
import com.custom.base.util.SDViewUtil
import com.luck.picture.lib.photoview.PhotoView
import com.youth.banner.adapter.BannerAdapter

class PageLoaderStringAdapter(
    val mActivity: Activity,
    val data: List<UserPhotoV2Bean>
) : BannerAdapter<UserPhotoV2Bean, PageLoaderStringAdapter.BannerViewHolder>(data) {
    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = SDViewUtil.getRId(parent.context, R.layout.item_user_profile)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return BannerViewHolder(view)
    }

    override fun onBindView(
        holder: BannerViewHolder?,
        model: UserPhotoV2Bean,
        position: Int,
        size: Int
    ) {
        holder?.apply {
            val imageLoading = view.findViewById<LottieAnimationView>(R.id.image_loading)
            val iv = view.findViewById<ImageView>(R.id.item_start_page_iv)

            Glide.with(iv)
                .load(model.imageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        imageLoading.visibility = View.GONE
                        imageLoading.cancelAnimation()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        imageLoading.visibility = View.GONE
                        imageLoading.cancelAnimation()
                        return false
                    }
                })
                .error(R.drawable.image_error_black)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv)

        }
    }

    inner class BannerViewHolder(var view: View) : RecyclerView.ViewHolder(
        view
    )
}