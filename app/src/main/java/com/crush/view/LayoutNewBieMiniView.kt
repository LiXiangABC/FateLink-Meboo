package com.crush.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.crush.R
import com.crush.view.cardstackview.internal.DisplayUtil.dpToPx
import com.crush.view.delay.DelayClickConstraintLayout
import io.rong.imkit.utils.RongUtils.screenHeight

/**
 * @Author ct
 * @Date 2024/5/10 15:59
 * 新手最小化显示
 */
class LayoutNewBieMiniView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : DelayClickConstraintLayout(context, attrs), View.OnLongClickListener, View.OnTouchListener {
    private var view: View? = null
    private var lottieNewBieGiftPackMini: LottieAnimationView? = null

    private var xDelta: Float = 0.toFloat()
    private var yDelta: Float = 0.toFloat()
    private val screenWidth: Int

    init {
        initView()
        setOnLongClickListener(this)
        setOnTouchListener(this)

        // 获取屏幕宽度
        val displayMetrics = resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
    }

    private fun initView() {
        view = LayoutInflater.from(context).inflate(R.layout.layout_newbiegiftpack_mini_view, this)
        lottieNewBieGiftPackMini = view?.findViewById(R.id.lottieNewBieGiftPackMini)
        visibility = VISIBLE
    }

    //设置间距
    fun setMarginData(marginTopVal:Float,marginEndVal:Float){
        val layoutParams =
            view?.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topMargin = dpToPx(context, marginTopVal)
        layoutParams.marginEnd = dpToPx(context, marginEndVal)
        view?.layoutParams = layoutParams
    }

    //显示
    fun show(isShow: Boolean, index: Int) {
        Log.i("LayoutNewBieMiniView","isShow=$isShow  index=$index")
        //关闭了最小化
        visibility = if (isShow) VISIBLE else GONE
        //初始化
        if(index==0){
            val layoutParams =
                view?.layoutParams as ConstraintLayout.LayoutParams
            //
            layoutParams.topMargin = dpToPx(context, 90f)
            layoutParams.marginEnd = dpToPx(context, 5f)
            view?.layoutParams = layoutParams
        }
    }

    override fun onLongClick(v: View?): Boolean {
        // 在长按时开始拖动
        parent.requestDisallowInterceptTouchEvent(true)
        return true
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录按下时的偏移量
                xDelta = v!!.x - event.rawX
                yDelta = v.y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                // 在移动时更新控件的位置
                v!!.x = event.rawX + xDelta
                val newY = if (event.rawY + yDelta < 0) 0f else if (event.rawY + yDelta > screenHeight- dpToPx(context,120f)) (screenHeight- dpToPx(context,120f)).toFloat()  else event.rawY + yDelta
                v.y = newY
            }
            MotionEvent.ACTION_UP -> {
                // 在松开手指时将控件贴边显示
                val screenWidth = resources.displayMetrics.widthPixels
                val viewCenterX = v!!.x + v.width / 2
                if (viewCenterX < screenWidth / 2) {
                    // 悬浮球贴左边显示
                    animateViewToX(v, dpToPx(context, 5f).toFloat())
                } else {
                    // 悬浮球贴右边显示
                    animateViewToX(v, (screenWidth - v.width-dpToPx(context, 5f)).toFloat())
                }

                // 在松开手指后允许父视图拦截触摸事件
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return false

    }
    // 创建一个 ObjectAnimator 来平滑地移动视图
    private fun animateViewToX(view: View, targetX: Float) {
        val animator = ObjectAnimator.ofFloat(view, "x", targetX).apply {
            duration = 200 // 设置动画持续时间为 200 毫秒
        }
        animator.start()
    }


}