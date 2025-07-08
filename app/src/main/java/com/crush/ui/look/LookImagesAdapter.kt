package com.crush.ui.look

import android.app.Activity
import android.graphics.Matrix
import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.crush.util.GlideUtil
import com.custom.base.util.SDViewUtil
import com.youth.banner.adapter.BannerAdapter


class LookImagesAdapter(
    mDatas: List<String>,
    var listener: OnFinish? = null,
    var isShare: Boolean,
    var mActivity: Activity
) : BannerAdapter<String, LookImagesAdapter.BannerViewHolder>(mDatas) {

    class BannerViewHolder(var mView: View,var listener: OnFinish? = null) : RecyclerView.ViewHolder(mView), View.OnTouchListener {
        // 縮放控制
        private val matrix: Matrix = Matrix()
        private val savedMatrix: Matrix = Matrix()

        // 不同状态的表示：
        private val NONE = 0
        private val DRAG = 1
        private val ZOOM = 2
        private var mode = NONE

        // 定义第一个按下的点，两只接触点的重点，以及出事的两指按下的距离：
        private val startPoint = PointF()
        private var midPoint = PointF()
        private var oriDis = 1f

        private var lastClickTime: Long = 0 //点击时间 hwb001
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val view = v as ImageView
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP ->                 //点击整个页面都会让内容框获得焦点，且弹出软键盘
                    if (isFastDoubleThreeClick()) {
                        listener?.onFinish()
                    } else {
                        mode = NONE
                    }
                MotionEvent.ACTION_DOWN -> {
                    matrix.set(view.imageMatrix)
                    savedMatrix.set(matrix)
                    startPoint.set(event.x, event.y)
                    mode = DRAG
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    oriDis = distance(event)
                    if (oriDis > 10f) {
                        savedMatrix.set(matrix)
                        midPoint = middle(event)
                        mode = ZOOM
                    }
                }
                MotionEvent.ACTION_POINTER_UP -> mode = NONE
                MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                    // 是一个手指拖动
                    matrix.set(savedMatrix)
                    matrix.postTranslate(event.x - startPoint.x, event.y - startPoint.y)
                } else if (mode == ZOOM) {
                    // 两个手指滑动
                    val newDist = distance(event)
                    if (newDist > 10f) {
                        matrix.set(savedMatrix)
                        val scale: Float = newDist / oriDis
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y)
                    }
                }
            }
            // 设置ImageView的Matrix
            // 设置ImageView的Matrix
            view.imageMatrix = matrix
            return true
        }

        // 计算两个触摸点之间的距离
        private fun distance(event: MotionEvent): Float {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            return java.lang.Float.valueOf(Math.sqrt(x * x + y * y.toDouble()).toString())
        }

        // 计算两个触摸点的中点
        private fun middle(event: MotionEvent): PointF {
            val x = event.getX(0) + event.getX(1)
            val y = event.getY(0) + event.getY(1)
            return PointF(x / 2, y / 2)
        }

        /**
         * 防止过快点击(200毫秒)
         * @return
         */
        private fun isFastDoubleThreeClick(): Boolean {
            val time = System.currentTimeMillis()
            val timeD = time - lastClickTime
            if (timeD in 1..199) {    //这样所有按钮在200毫秒内不能同时起效。
                return true
            }
            lastClickTime = time
            return false
        }
    }

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerViewHolder {
        val view = SDViewUtil.getRId(mActivity, R.layout.add_lookimages)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return BannerViewHolder(view,listener = listener)
    }

    override fun onBindView(holder: BannerViewHolder?, data: String, position: Int, size: Int) {
        holder?.apply {
            val iv = mView.findViewById<ImageView>(R.id.add_lookimage_iv)
            GlideUtil.setImageView(
                data,
                iv,
                placeholderImageId = R.drawable.icon_occupation_map
            )
            iv.setOnTouchListener(this)
//            rl.setOnClickListener { listener?.onFinish() }
        }
    }
    interface OnFinish{
        fun onFinish()
    }
}