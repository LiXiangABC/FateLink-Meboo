package com.crush.dialog

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.R
import razerdp.basepopup.BasePopupWindow


/**
 * 弹幕弹窗
 */
class DanmuDialog(var ctx: Context) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_danmu)
        initView()
        isOutSideTouchable = true

    }
    private lateinit var danmuContainer: ViewGroup
    private var danmuList = mutableListOf<String>()
    private var danmuTextViewList = mutableListOf<TextView>()


    private fun initView() {


        var danmuContainer = findViewById<ConstraintLayout>(R.id.view_group)
        danmuTextViewList = mutableListOf<TextView>()
        danmuList = mutableListOf<String>()
        // 添加弹幕信息
        danmuList.add("这是第一条弹幕")
        danmuList.add("这是第二条弹幕")
        danmuList.add("这是第三条弹幕")
        danmuList.add("这是第四条弹幕")
        danmuList.add("这是第五条弹幕")
        danmuList.add("这是第六条弹幕")
        danmuList.add("这是第七条弹幕")
        danmuList.add("这是第八条弹幕")
        danmuList.add("这是第九条弹幕")
        danmuList.add("这是第十条弹幕")

        // 获取屏幕宽度
        val screenWidth = getScreenWidth()

        // 创建并添加弹幕视图
        for (i in 0 until danmuList.size) {
            val danmuTextView = createDanmuTextView(danmuList[i])
            danmuContainer.addView(danmuTextView)
            danmuTextViewList.add(danmuTextView)

            // 创建弹幕动画
            val danmuAnimator = ValueAnimator.ofFloat(screenWidth.toFloat(), -danmuTextView.width.toFloat())
            danmuAnimator.duration = 5000 // 弹幕从屏幕右侧滚动到左侧的时间
            danmuAnimator.addUpdateListener { animation ->
                val x = animation.animatedValue as Float
                danmuTextView.translationX = x
            }
            danmuAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    // 动画结束后移除弹幕视图
                    removeDanmuView(danmuTextView)
                }
            })

            // 启动弹幕动画
            danmuAnimator.start()
        }

        setBackgroundColor(Color.TRANSPARENT)

    }

    private fun createDanmuTextView(text: String): TextView {
        val textView = TextView(ctx)
        textView.text = text
        textView.setTextColor(Color.WHITE)
        textView.setBackgroundColor(Color.BLACK)
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.setPadding(16, 8, 16, 8)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.layoutParams = layoutParams
        return textView
    }

    private fun removeDanmuView(danmuTextView: TextView) {
        danmuContainer.removeView(danmuTextView)
        danmuTextViewList.remove(danmuTextView)
    }

    private fun getScreenWidth(): Int {
        val windowManager = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val screenWidth = display.width
        return screenWidth
    }

}