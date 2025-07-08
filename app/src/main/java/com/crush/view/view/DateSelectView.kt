package com.crush.view.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.contrarywind.view.WheelView
import com.crush.R
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.event.EnumEventTag
import java.math.BigDecimal
import java.util.Calendar


class DateSelectView : FrameLayout {
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(context: Context) : this(context = context, null)
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    var year:String="18"
    var monthWheelView: WheelView? = null
    var dayWheelView: WheelView? = null
    var yearWheelView: WheelView? = null

    var wheelViewDayAdapter: ArrayWheelAdapter<String>? = null
    var wheelViewYearAdapter: ArrayWheelAdapter<String>? = null
    var wheelViewMonthAdapter: ArrayWheelAdapter<String>? = null

    var yearPosition = 0
    var monthPosition = 0
    var dayPosition = 0
    var listener:ChangeYears?=null
    private var years: MutableList<String>? = ArrayList()

    @RequiresApi(Build.VERSION_CODES.O)
    fun initView(context: Context, attrs: AttributeSet?) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.view_data_select, this)
        val obtainStyledAttributes =
            context.obtainStyledAttributes(attrs, R.styleable.DateSelectView)

        monthWheelView = view.findViewById(R.id.wheel1)
        dayWheelView = view.findViewById(R.id.wheel2)
        yearWheelView = view.findViewById(R.id.wheel3)

        yearWheelView?.setLineSpacingMultiplier(3f)
        dayWheelView?.setLineSpacingMultiplier(3f)
        monthWheelView?.setLineSpacingMultiplier(3f)

        yearWheelView?.setItemsVisibleCount(6)
        dayWheelView?.setItemsVisibleCount(6)
        monthWheelView?.setItemsVisibleCount(6)
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O) {
            yearWheelView?.setTypeface(context.resources.getFont(R.font.intersemibold))
            dayWheelView?.setTypeface(context.resources.getFont(R.font.intersemibold))
            monthWheelView?.setTypeface(context.resources.getFont(R.font.intersemibold))
            setParams(obtainStyledAttributes)
        }
    }

    fun initDate(date: String) {
        val year = date.substring(date.length - 4, date.length)
        val month = date.substring(0, 2)
        val day = date.substring(3, date.length - 5)
        dayPosition = day.toInt() - 1
        monthPosition = month.toInt() - 1
        yearPosition = years?.indexOf(year)!!
        setDayAdapter()
        monthWheelView?.currentItem = monthPosition
        dayWheelView?.currentItem = dayPosition
        yearWheelView?.currentItem = yearPosition
    }

    fun setParams(obtainStyledAttributes: TypedArray) {
        val background =
            obtainStyledAttributes.getDrawable(R.styleable.DateSelectView_view_background)
        val textColor =
            obtainStyledAttributes.getColor(R.styleable.DateSelectView_textColorCenter, 0x999999)
        val textSize =
            obtainStyledAttributes.getDimension(R.styleable.DateSelectView_dataTextSize, 20f)
        val isCyclic = obtainStyledAttributes.getBoolean(R.styleable.DateSelectView_isCyclic, false)
        val dividerColor =
            obtainStyledAttributes.getColor(R.styleable.DateSelectView_dividerColor, 0xFF4F8E)
        if (background != null)
            this.background = background


        monthWheelView?.setCyclic(isCyclic)
        dayWheelView?.setCyclic(isCyclic)
        yearWheelView?.setCyclic(isCyclic)

        monthWheelView?.setTextSize(textSize)
        dayWheelView?.setTextSize(textSize)
        yearWheelView?.setTextSize(textSize)

        monthWheelView?.setTextColorCenter(textColor)
        monthWheelView?.setTextColorOut(Color.parseColor("#BCBCBC"))
        dayWheelView?.setTextColorCenter(textColor)
        dayWheelView?.setTextColorOut(Color.parseColor("#BCBCBC"))

        yearWheelView?.setTextColorCenter(textColor)
        yearWheelView?.setTextColorOut(Color.parseColor("#BCBCBC"))


        monthWheelView?.setDividerColor(dividerColor)
        monthWheelView?.setDividerWidth(4)
        monthWheelView?.setDividerType(WheelView.DividerType.FILL)
        dayWheelView?.setDividerColor(dividerColor)
        dayWheelView?.setDividerWidth(4)
        dayWheelView?.setDividerType(WheelView.DividerType.FILL)
        yearWheelView?.setDividerColor(dividerColor)
        yearWheelView?.setDividerWidth(4)
        yearWheelView?.setDividerType(WheelView.DividerType.FILL)

        val d = Calendar.getInstance()
        val years = getYears(d.weekYear - 70, d.weekYear - 18)
        wheelViewYearAdapter = ArrayWheelAdapter(years)
        wheelViewMonthAdapter = ArrayWheelAdapter(getMonth())

        monthWheelView?.adapter = wheelViewMonthAdapter
        monthWheelView?.currentItem = Calendar.getInstance().get(Calendar.MONTH) - 1
        monthPosition = Calendar.getInstance().get(Calendar.MONTH) - 1
        monthWheelView?.setOnItemSelectedListener { index ->
            monthPosition = index
            dayPosition = 0
            setDayAdapter()
        }

        setDayAdapter()
        dayWheelView?.currentItem = Calendar.getInstance().get(Calendar.DATE) - 1
        dayPosition = Calendar.getInstance().get(Calendar.DATE) - 1
        dayWheelView?.setOnItemSelectedListener { index ->
            dayPosition = index
        }

        yearWheelView?.adapter = wheelViewYearAdapter
        yearWheelView?.currentItem = years.size - 1
        yearPosition = years.size - 1
        yearWheelView?.setOnItemSelectedListener { index ->
            yearPosition = index
            dayPosition = 0
            setDayAdapter()
            SDEventManager.post(EnumEventTag.UPDATE_YEAR.ordinal)
            listener?.onChange()
        }
    }

    fun getSelectDate(style: DateSelectStyle = DateSelectStyle.MM_DD_YYYY): String {
        when (style) {
            DateSelectStyle.MM_DD_YYYY -> {
                var month = (monthPosition + 1).toString()
                month = if (month.length == 1) ("0$month") else month
                var day = wheelViewDayAdapter?.getItem(dayPosition).toString()
                day = if (day.length == 1) ("0$day") else day
                val year = wheelViewYearAdapter?.getItem(yearPosition).toString()
                return "$month/$day/$year"
            }

            DateSelectStyle.MMDDYYYY -> {
                val month = (monthPosition + 1).toString()
                val day = wheelViewDayAdapter?.getItem(dayPosition).toString()
                val year = wheelViewYearAdapter?.getItem(yearPosition).toString()
                return "$month-$day-$year"
            }

            else -> {}
        }
        return ""
    }

    fun getAge():String{
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        if (wheelViewYearAdapter != null){
            if(yearPosition< wheelViewYearAdapter!!.itemsCount)
                return if(null != wheelViewYearAdapter?.getItem(yearPosition) && wheelViewYearAdapter?.getItem(yearPosition).toString().isNotEmpty()){
                    BigDecimal(year).subtract(BigDecimal(wheelViewYearAdapter?.getItem(yearPosition).toString())).toString()
                }else "18"
        }
        return "18"
    }

    fun setDayAdapter() {
        wheelViewDayAdapter = ArrayWheelAdapter(
            getDay(
                wheelViewYearAdapter?.getItem(yearPosition).toString().toInt(),
                getMonth().indexOf(wheelViewMonthAdapter?.getItem(monthPosition) as String)
            )
        )
        dayWheelView?.adapter = wheelViewDayAdapter
        dayWheelView?.currentItem = 0
    }

    fun getYears(minYear: Int, maxYear: Int): MutableList<String> {
        years = mutableListOf()
        for (year in minYear..maxYear) {
            years!!.add(year.toString())
        }

        return years!!
    }

    fun getDay(year: Int, month: Int): MutableList<String> {
        val calendar = Calendar.getInstance()//获取Calendar对象
        calendar.set(year, month + 1, 1)//Month初始是从0开始的 也就是说Month=0相当于1月份  2就相当于3月份
        calendar.add(Calendar.DATE, -1)
        val days: MutableList<String> = ArrayList()
        for (day in 1..calendar.get(Calendar.DAY_OF_MONTH)) {
            days.add(day.toString())
        }
        return days
    }

    fun getMonth(): MutableList<String> {
        val mOptionsItems1: MutableList<String> = ArrayList()
        mOptionsItems1.add("Jan")
        mOptionsItems1.add("Feb")
        mOptionsItems1.add("Mar")
        mOptionsItems1.add("Apr")
        mOptionsItems1.add("May")
        mOptionsItems1.add("Jun")
        mOptionsItems1.add("Jul")
        mOptionsItems1.add("Aug")
        mOptionsItems1.add("Sept")
        mOptionsItems1.add("Oct")
        mOptionsItems1.add("Nov")
        mOptionsItems1.add("Dec")
        return mOptionsItems1
    }

    fun setChangeYearListener(listener: ChangeYears){
        this.listener=listener
    }

    interface ChangeYears{
       fun onChange()
    }
}