package com.crush.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.SparseIntArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.request.RequestOptions
import com.crush.App
import com.crush.R

object ImageCached {
    // 使用了缓存优化的url容器
    private val thumbnails = mutableMapOf<String, String>()
    // 使用了缓存优化的尺寸容器
    private val dps = SparseIntArray()
    // 使用了缓存优化的颜色容器
    private val colors = mutableMapOf<String, Int>()

    fun dp2px(dpValue: Int): Int {
        return dp2px(dpValue, appContext)
    }

    fun dp2px(dpValue: Int, context: Context): Int { // if use viewContext can load preview
        val result = dps[dpValue]
        if (result > 0) return result
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue.toFloat(), context.resources.displayMetrics).toInt().also {
            dps.put(dpValue, it)
        }
    }

    fun getColor(color: String): Int {
        val result = colors[color]
        if (result != null) return result
        return try {
            Color.parseColor(color).also { colors[color] = it }
        } catch (e: Exception) {
            e.printStackTrace()
            Color.TRANSPARENT
        }
    }
}

/** 获得全局appContext */
val appContext: Context get() = App.applicationContext()!!
fun dpToPx(dpValue: Int) = ImageCached.dp2px(dpValue) // 全局px转化dp
// 屏幕宽度
fun screenWidth() = appContext.resources.displayMetrics.widthPixels
fun screenHeight() = appContext.resources.displayMetrics.heightPixels
object Injections{

    val mainHandler by lazy { Handler(Looper.getMainLooper()) }
    /** view动画.收缩弹一下 */
    val animScaleKick by lazy {  AnimationUtils.loadAnimation(appContext, R.anim.anims_scale_kick)!! }


    val screenWidthD2 by lazy { screenWidth() / 2 }
}

fun ViewGroup.inflate(resId: Int): View {
    return LayoutInflater.from(context).inflate(resId, this, false)
}

fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()