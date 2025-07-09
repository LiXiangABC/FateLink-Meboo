package com.crush.dialog

import android.app.Activity
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bigkoo.pickerview.view.TimePickerView
import com.crush.R
import com.crush.view.view.DateSelectStyle
import com.crush.view.view.DateSelectView
import razerdp.basepopup.BasePopupWindow

class UpdateDateDialog(var ctx: Activity,var nowDate: String, var listener: onCallBack) :  BasePopupWindow(ctx) {
    private var pvTime: TimePickerView? = null

    init {
        setContentView(R.layout.dialog_modify_date)
        initView()
    }

    private fun initView() {
        val outsideView = findViewById<View>(R.id.outside_view)
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)
        val selectDateTips = findViewById<TextView>(R.id.select_date_tips)
        val dateSelectView = findViewById<DateSelectView>(R.id.date_select_view)
        val txtDateSave = findViewById<TextView>(R.id.txt_date_save)

        dateSelectView.initDate(nowDate)
        dateSelectView.setChangeYearListener(object :DateSelectView.ChangeYears{
            override fun onChange() {
                setYear(dateSelectView, selectDateTips)
            }

        })
        setYear(dateSelectView, selectDateTips)


        txtDateSave.setOnClickListener {
            val selectDate = dateSelectView.getSelectDate(DateSelectStyle.MM_DD_YYYY)
            listener.onCallback(selectDate)
            dismiss()
        }

        dialogClose.setOnClickListener {
            dismiss()
        }
        outsideView.setOnClickListener {
            dismiss()
        }

        setOutSideDismiss(true)
    }

    private fun setYear(
        dateSelectView: DateSelectView,
        selectDateTips: TextView
    ) {
        val spannableString =
            SpannableString(
                String.format(
                    ctx.getString(R.string.you_re_18_years_old),
                    dateSelectView.getAge()
                )
            )
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(ctx, R.color.color_44F3C4)),
            7,
            9,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        val sizeSpan = RelativeSizeSpan(1.25f)
        spannableString.setSpan(sizeSpan, 7, 9, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
        selectDateTips.text = spannableString
    }

    interface onCallBack{
       fun onCallback(value:String)
    }
}