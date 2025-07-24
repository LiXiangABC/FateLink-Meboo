package io.rong.imkit.utils

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
import com.custom.base.manager.SDActivityManager
import io.rong.imkit.R
import io.rong.imkit.activity.Activities
import io.rong.imkit.utils.BlurTransformation
import io.rong.imkit.utils.RongUtils


object GlideUtil {
    private const val TAG = "GlideUtil"

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
        cache: Boolean = true,
    ) {
        val options = RequestOptions()
        options.error(errorImageId)
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

    interface OnGetBitMapListener {
        fun onBitMap(bitMap: Bitmap)
    }
}
