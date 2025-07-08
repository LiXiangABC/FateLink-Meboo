package com.crush.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.custom.base.util.SDViewUtil.dp2px
import com.crush.R


/**
 * 作者：
 * 日期：2022/3/29
 * 说明：圆形进度条
 */
class CircularProgressBarView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private lateinit var canvas: Canvas
    private val paint: Paint
    private val outsideStartColor: Int
    private val outsideEndColor: Int
    private val outsideRadius: Float
    private val insideColor: Int
    private val progressTextColor: Int
    private val progressTextSize: Float
    private val progressWidth: Float
    private var progress: Float
    private var maxProgress: Int
    private val direction: Int

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomCircleProgressBar)
        outsideStartColor = a.getColor(
            R.styleable.CustomCircleProgressBar_outside_start_color,
            ContextCompat.getColor(getContext(), R.color.colorPrimary)
        )
        outsideEndColor = a.getColor(
            R.styleable.CustomCircleProgressBar_outside_end_color,
            ContextCompat.getColor(getContext(), R.color.colorPrimary)
        )
        outsideRadius = a.getDimension(R.styleable.CustomCircleProgressBar_outside_radius, dp2px(60.0f))
        insideColor = a.getColor(
            R.styleable.CustomCircleProgressBar_inside_color,
            ContextCompat.getColor(getContext(), R.color.color_292929)
        )
        progressTextColor = a.getColor(
            R.styleable.CustomCircleProgressBar_progress_text_color,
            ContextCompat.getColor(getContext(), R.color.colorPrimary)
        )
        progressTextSize = a.getDimension(R.styleable.CustomCircleProgressBar_progress_text_size, dp2px(14.0f))
        progressWidth = a.getDimension(R.styleable.CustomCircleProgressBar_progress_width, dp2px(10.0f))
        progress = a.getFloat(R.styleable.CustomCircleProgressBar_progress, 50.0f)
        maxProgress = a.getInt(R.styleable.CustomCircleProgressBar_max_progress, 100)
        direction = a.getInt(R.styleable.CustomCircleProgressBar_direction, 3)
        a.recycle()
        paint = Paint()
    }

    fun startDraw(progress:Float,maxProgress:Int){
        this.progress = progress
        this.maxProgress = if(maxProgress == 0) 100 else maxProgress
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        val circlePoint = width / 2
        //第一步:画背景(即内层圆)
        paint.color = insideColor //设置圆的颜色
        paint.style = Paint.Style.STROKE //设置空心
        paint.strokeWidth = progressWidth //设置圆的宽度
        paint.isAntiAlias = true //消除锯齿
        canvas.drawCircle(circlePoint.toFloat(), circlePoint.toFloat(), outsideRadius, paint) //画出圆

        //第二步:画进度(圆弧)不连接圆心
        paint.color = outsideStartColor //设置进度的颜色
        paint.strokeCap = Paint.Cap.ROUND //设置圆角
        val oval = RectF(
            circlePoint - outsideRadius,
            circlePoint - outsideRadius,
            circlePoint + outsideRadius,
            circlePoint + outsideRadius
        ) //用于定义的圆弧的形状和大小的界限
        paint.strokeWidth = progressWidth+5 //设置圆的宽度
        paint.shader = SweepGradient(oval.centerX(),oval.centerY()-10, intArrayOf(outsideStartColor,outsideEndColor),null)
        canvas.drawArc(
            oval,
            direction.toFloat(),
            360 * (progress / maxProgress),
            false,
            paint
        ) //根据进度画圆弧

        //第三步:画圆环内百分比文字
        val rect = Rect()
        paint.style = Paint.Style.FILL //设置空心
        paint.color = progressTextColor
        paint.textSize = progressTextSize
        paint.shader = null
        paint.strokeWidth = 0f
        val text = "${(progress/maxProgress*100).toInt()}%"
        paint.getTextBounds(text, 0, text.length, rect)
        val fontMetrics = paint.fontMetricsInt
        val baseline = (measuredHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top //获得文字的基准线
        canvas.drawText(text, (measuredWidth / 2 - rect.width() / 2).toFloat(), baseline.toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width: Int
        val height: Int
        var size = MeasureSpec.getSize(widthMeasureSpec)
        var mode = MeasureSpec.getMode(widthMeasureSpec)
        width = if (mode == MeasureSpec.EXACTLY) {
            size
        } else {
            (2 * outsideRadius + progressWidth).toInt()+5
        }
        size = MeasureSpec.getSize(heightMeasureSpec)
        mode = MeasureSpec.getMode(heightMeasureSpec)
        height = if (mode == MeasureSpec.EXACTLY) {
            size
        } else {
            (2 * outsideRadius + progressWidth).toInt()+5
        }
        setMeasuredDimension(width, height)
    }
}