package com.crush.util

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.crush.R
import com.crush.view.glide.listener.ProgressListener
import com.crush.view.glide.util.ProgressInterceptor
import com.custom.base.manager.SDActivityManager
import io.rong.imkit.activity.Activities
import io.rong.imkit.utils.BlurTransformation
import io.rong.imkit.utils.RongUtils


object GlideUtil {
    private const val TAG = "GlideUtil"

    @SuppressLint("CheckResult")
    fun loadImageViewWithCallBack(
        url: String?,
        imageView: ImageView,
        activity: Activity? = null,
        callback:(Boolean)->Unit
    ){
        val mActivity: Activity = activity ?: SDActivityManager.instance.lastActivity
        if (mActivity.isDestroyed){
            return
        }
        val options = RequestOptions()
        options.error(R.drawable.image_error)
        options.placeholder(R.mipmap.img_newbie_giftpack_bg_default)
        options.diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(imageView).load(url)
            .listener(object:RequestListener<Drawable>{
                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: Target<Drawable>?,
                    p3: Boolean
                ): Boolean {
                    callback.invoke(false)
                    return false
                }

                override fun onResourceReady(
                    p0: Drawable?,
                    p1: Any?,
                    p2: Target<Drawable>?,
                    p3: DataSource?,
                    p4: Boolean
                ): Boolean {
                    callback.invoke(true)
                    return false
                }
            })
            .apply(options).thumbnail(1f).into(imageView)
    }

    /**
     * @param url
     * @param imageView
     * @param vagueness 是否模糊图片
     * @param radius 模糊半径
     * @param sampling 取样
     * @param errorImageId 加载失败图片
     * @param placeholderImageId 占位图片
     * @param cache 是否缓存
     */
    fun setImageView(
        url: String?,
        imageView: ImageView,
        vagueness: Boolean = false,
        radius: Int = 5,
        sampling: Int = 5,
        thumbnail: Float = 1f,
        errorImageId: Int = R.drawable.image_error,
        placeholderImageId: Int = R.mipmap.icon_wlm_loading,
        cache: Boolean = true,
    ) {
        val options = RequestOptions()
        options.error(errorImageId)
        if (placeholderImageId != 0) options.placeholder(placeholderImageId)

        options.diskCacheStrategy(if (cache) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE)
        Activities.get().top?.let {
            if (vagueness) {
                Glide.with(it).load(url).apply(
                    RequestOptions.bitmapTransform(
                        BlurTransformation(radius, sampling)
                    )
                ).into(imageView)
            } else {
                Glide.with(it).load(url).apply(options).thumbnail(thumbnail).into(imageView)
            }
        }
    }

    fun setImageView(
        id: Int?,
        imageView: ImageView,
        vagueness: Boolean = false,
        radius: Int = 5,
        sampling: Int = 5,
        errorImageId: Int = R.drawable.ease_default_image,
        placeholderImageId: Int = R.drawable.icon_occupation_map,
        cache: Boolean = true,
    ) {
        val options = RequestOptions()
        options.error(errorImageId)
        options.placeholder(placeholderImageId)
        options.diskCacheStrategy(if (cache) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE)
        Activities.get().top?.let {
            if (vagueness) Glide.with(it).load(id).apply(
                RequestOptions.bitmapTransform(
                    BlurTransformation(radius, sampling)
                )
            ).into(imageView)
            else Glide.with(it).load(id).apply(options).into(imageView)
        }
    }

    /**
     * 获取url BitMap
     */
    fun getImageBitMap(
        url: String,
        listener: OnGetBitMapListener? = null,
        activity: Activity? = null
    ) {
        val mActivity: Activity = activity ?: SDActivityManager.instance.lastActivity
        Glide.with(mActivity).asBitmap().load(url).into(object : SimpleTarget<Bitmap?>() {
            override fun onResourceReady(bitMap: Bitmap, p1: Transition<in Bitmap?>?) {
                listener?.onBitMap(bitMap)
            }
        })
    }

    fun setImageView(url: String, imageView: ImageView, listener: ProgressListener) {
        listener(url, listener)
        val simpleTarge = object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                imageView.setImageDrawable(resource)
                Log.d(TAG, "onResourceReady: ")
                ProgressInterceptor.removeListener(url)
            }

            override fun onStart() {
                super.onStart()
                Log.d(TAG, "onStart: ")
            }
        }
        val options = RequestOptions()
        options.error(R.drawable.ease_default_image)
        options.placeholder(R.drawable.icon_occupation_map)
        options.diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(SDActivityManager.instance.lastActivity).load(url).apply(options)
            .into<SimpleTarget<Drawable>>(simpleTarge)
    }


    private fun listener(url: String, listener: ProgressListener) {
        ProgressInterceptor.addListener(url, listener)
    }

    interface OnGetBitMapListener {
        fun onBitMap(bitMap: Bitmap)
    }
}
