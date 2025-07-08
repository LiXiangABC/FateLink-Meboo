package com.crush.ui.index

import android.app.Activity
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.crush.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.luck.picture.lib.utils.ActivityCompatHelper


fun SimpleDraweeView.loadImage(activity: Activity,imageUrl :String,position:Int = -1, result:(isSuccess:Boolean)->Unit) {
    var isLoadSuccess = false
    if (ActivityCompatHelper.isDestroy(activity)){
        return
    }
    Glide.with(this.context)
        .load(imageUrl)
        .timeout(300)
        .error(R.drawable.image_error)
        .dontAnimate()
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                Handler(Looper.getMainLooper()).post {
//                    this@loadImage.loadImage(imageUrl) {
                        result.invoke(true)
//                    }
//                }
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                isLoadSuccess = true
                result.invoke(true)
//                Log.d("aaaaaaa加载","正常加载成功   $position")
                return false
            }

        })
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this@loadImage)
    this.postDelayed({
        if (isLoadSuccess) {
            return@postDelayed
        }
        if (activity.isDestroyed){
            return@postDelayed
        }
        Glide.with(this.context).clear(this@loadImage)
//        Log.d("aaaaaaa加载","开始超时加载   $position")
        this@loadImage.controller = Fresco.newDraweeControllerBuilder()
            .setUri(Uri.parse(imageUrl))
            .setAutoPlayAnimations(false) // 如果是动画图片，是否自动播放
            .setControllerListener(getControllerListener{
                if (it){
//                    Log.d("aaaaaaa加载","开始超时加载成功   $position :  $imageUrl")
                }
                result.invoke(it)
            }) // 设置监听器
            .build()
    }, 310)
}
private fun getControllerListener(result:(isSuccess:Boolean)->Unit): ControllerListener<ImageInfo?> {
    return object : BaseControllerListener<ImageInfo?>() {
        override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
            // 图片加载完成时的回调
            // 可以在这里执行一些操作，比如显示图片尺寸等信息
            result.invoke(true)
        }

        override fun onFailure(id: String, throwable: Throwable) {
            // 图片加载失败时的回调
            // 可以在这里处理加载失败的情况
            result.invoke(false)
        }
    }
}