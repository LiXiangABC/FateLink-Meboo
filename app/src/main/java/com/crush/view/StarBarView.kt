package com.crush.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.crush.R
import kotlin.math.min


/**
 * 显示星星评论数控件
 * Created by CaptionDeng on 2016/8/30.
 */
class StarBarView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0
) : View(
    context, attrs, defStyle
) {
    //实心图片
    private val mSolidBitmap: Bitmap?

    //空心图片
    private val mHollowBitmap: Bitmap?

    //最大的数量
    private var starMaxNumber: Int
    private var starRating: Float
    private val paint = Paint()
    private val mSpaceWidth: Int //星星间隔
    private val mStarWidth: Int //星星宽度
    private val mStarHeight: Int //星星高度
    var isIndicator: Boolean //是否是一个指示器（用户无法进行更改）
    private val mOrientation: Int

    override fun onDraw(canvas: Canvas) {
        if (mHollowBitmap == null || mSolidBitmap == null) {
            return
        }
        //绘制实心进度
        val solidStarNum = starRating.toInt()
        //绘制实心的起点位置
        var solidStartPoint = 0
        if (mOrientation == HORIZONTAL) for (i in 1..solidStarNum) {
            canvas.drawBitmap(mSolidBitmap, solidStartPoint.toFloat(), 0f, paint)
            solidStartPoint = solidStartPoint + mSpaceWidth + mSolidBitmap.width
        }
        else for (i in 1..solidStarNum) {
            canvas.drawBitmap(mSolidBitmap, 0f, solidStartPoint.toFloat(), paint)
            solidStartPoint = solidStartPoint + mSpaceWidth + mSolidBitmap.height
        }
        //虚心开始位置
        var hollowStartPoint = solidStartPoint
        //多出的实心部分起点
        val extraSolidStarPoint = hollowStartPoint
        //虚心数量
        val hollowStarNum = starMaxNumber - solidStarNum
        if (mOrientation == HORIZONTAL) for (j in 1..hollowStarNum) {
            canvas.drawBitmap(mHollowBitmap, hollowStartPoint.toFloat(), 0f, paint)
            hollowStartPoint = hollowStartPoint + mSpaceWidth + mHollowBitmap.width
        }
        else for (j in 1..hollowStarNum) {
            canvas.drawBitmap(mHollowBitmap, 0f, hollowStartPoint.toFloat(), paint)
            hollowStartPoint = hollowStartPoint + mSpaceWidth + mHollowBitmap.width
        }
        //多出的实心长度
        val extraSolidLength = ((starRating - solidStarNum) * mHollowBitmap.width).toInt()
        val rectSrc = Rect(0, 0, extraSolidLength, mHollowBitmap.height)
        val dstF = Rect(
            extraSolidStarPoint,
            0,
            extraSolidStarPoint + extraSolidLength,
            mHollowBitmap.height
        )
        canvas.drawBitmap(mSolidBitmap, rectSrc, dstF, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isIndicator) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> if (mOrientation == HORIZONTAL) {
                    val TotalWidth = (starMaxNumber * (mStarWidth + mSpaceWidth)).toFloat()
                    if (event.x <= TotalWidth) {
                        val newStarRating =
                            (event.x.toInt() / (mStarWidth + mSpaceWidth) + 1).toFloat()
                        setStarRating(newStarRating)
                    }
                } else {
                    val TotalHeight = (starMaxNumber * (mStarHeight + mSpaceWidth)).toFloat()
                    if (event.y <= TotalHeight) {
                        val newStarRating =
                            (event.y.toInt() / (mStarHeight + mSpaceWidth) + 1).toFloat()
                        setStarRating(newStarRating)
                    }
                }

                MotionEvent.ACTION_MOVE -> {}
                MotionEvent.ACTION_UP -> if (listener != null) {
                    listener!!.onListener()
                }

                MotionEvent.ACTION_CANCEL -> {}
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 设置星星的进度
     *
     * @param starRating
     */
    fun setStarRating(starRating: Float) {
        this.starRating = starRating
        invalidate()
    }

    fun getStarRating(): Float {
        return starRating
    }


    /**
     * 获取缩放图片
     *
     * @param bitmap
     * @return
     */
    fun getZoomBitmap(bitmap: Bitmap): Bitmap {
        if (mStarWidth == 0 || mStarHeight == 0) {
            return bitmap
        }
        // 获得图片的宽高
        val width = bitmap.width
        val height = bitmap.height

        // 设置想要的大小
        val newWidth = mStarWidth
        val newHeight = mStarHeight
        // 计算缩放比例
        val scaleWidth = (newWidth.toFloat()) / width
        val scaleHeight = (newHeight.toFloat()) / height
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        // 得到新的图片
        val newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        return newbm
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mOrientation == HORIZONTAL) {
            //判断是横向还是纵向，测量长度
            setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec))
        } else {
            setMeasuredDimension(measureShort(widthMeasureSpec), measureLong(heightMeasureSpec))
        }
    }

    private fun measureLong(measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if ((specMode == MeasureSpec.EXACTLY)) {
            result = specSize
        } else {
            result = (paddingLeft + paddingRight + (mSpaceWidth + mStarWidth) * (starMaxNumber))
            if (specMode == MeasureSpec.AT_MOST) {
                result = min(result.toDouble(), specSize.toDouble()).toInt()
            }
        }
        return result
    }

    private fun measureShort(measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = (mStarHeight + paddingTop + paddingBottom)
            if (specMode == MeasureSpec.AT_MOST) {
                result = min(result.toDouble(), specSize.toDouble()).toInt()
            }
        }
        return result
    }

    fun getStarMaxNumber(): Int {
        return starMaxNumber
    }

    fun setStarMaxNumber(starMaxNumber: Int) {
        this.starMaxNumber = starMaxNumber
        //利用invalidate()；刷新界面
        invalidate()
    }

    private var listener: OnStarStarListener? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.StarBarView, defStyle, 0)
        mSpaceWidth = a.getDimensionPixelSize(R.styleable.StarBarView_space_width, 0)
        mStarWidth = a.getDimensionPixelSize(R.styleable.StarBarView_star_width, 0)
        mStarHeight = a.getDimensionPixelSize(R.styleable.StarBarView_star_height, 0)
        starMaxNumber = a.getInt(R.styleable.StarBarView_star_max, 0)
        starRating = a.getFloat(R.styleable.StarBarView_star_rating, 0f)
        mSolidBitmap = getZoomBitmap(
            BitmapFactory.decodeResource(
                context.resources, a.getResourceId(
                    R.styleable.StarBarView_star_solid, 0
                )
            )
        )
        mHollowBitmap = getZoomBitmap(
            BitmapFactory.decodeResource(
                context.resources, a.getResourceId(
                    R.styleable.StarBarView_star_hollow, 0
                )
            )
        )
        mOrientation = a.getInt(R.styleable.StarBarView_star_orientation, HORIZONTAL)
        isIndicator = a.getBoolean(R.styleable.StarBarView_star_isIndicator, false)
        a.recycle()
    }

    fun setOnStarStarListener(listener: OnStarStarListener?) {
        this.listener = listener
    }

    interface OnStarStarListener {
        fun onListener()
    }

    companion object {
        //星星水平排列
        const val HORIZONTAL: Int = 0

        //星星垂直排列
        const val VERTICAL: Int = 1
    }
}
