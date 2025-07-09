package com.crush.dialog

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.crush.R
import com.crush.util.DensityUtil
import razerdp.basepopup.BasePopupWindow

class UpdateSeekingDialog(var ctx: Activity, var lookingFor: Int, var listener: onCallBack) :
    BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_modify_seeking)
        initView()
    }


    private fun initView() {
        val outsideView = findViewById<View>(R.id.outside_view)
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)
        val rbFemale = findViewById<RadioButton>(R.id.rb_female)
        val rbMale = findViewById<RadioButton>(R.id.rb_male)
        val rbQueer = findViewById<RadioButton>(R.id.rb_queer)
        val txtSeekingSave = findViewById<TextView>(R.id.txt_seeking_save)
        val femaleDrawable = ContextCompat.getDrawable(ctx, R.drawable.selector_gender_female_check)
        femaleDrawable?.setBounds(0, 0, DensityUtil.dp2px(ctx, 38f), DensityUtil.dp2px(ctx, 38f))
        rbFemale.setCompoundDrawables(null, femaleDrawable, null, null);
        val maleDrawable = ContextCompat.getDrawable(ctx, R.drawable.selector_gender_male_check)
        maleDrawable?.setBounds(0, 0, DensityUtil.dp2px(ctx, 38f), DensityUtil.dp2px(ctx, 38f))
        rbMale.setCompoundDrawables(null, maleDrawable, null, null);
        val queerDrawable = ContextCompat.getDrawable(ctx, R.drawable.selector_gender_queer_check)
        queerDrawable?.setBounds(0, 0, DensityUtil.dp2px(ctx, 38f), DensityUtil.dp2px(ctx, 38f))
        rbQueer.setCompoundDrawables(null, queerDrawable, null, null);

        rbFemale.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                rbFemale.setTextColor(ContextCompat.getColor(context, R.color.color_001912))
            }else {
                rbFemale.setTextColor(ContextCompat.getColor(context, R.color.color_44F3C4))
            }
        }

        rbMale.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                rbMale.setTextColor(ContextCompat.getColor(context, R.color.color_001912))
            }else {
                rbMale.setTextColor(ContextCompat.getColor(context, R.color.color_44F3C4))
            }
        }


        rbQueer.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                rbQueer.setTextColor(ContextCompat.getColor(context, R.color.color_001912))
            }else {
                rbQueer.setTextColor(ContextCompat.getColor(context, R.color.color_44F3C4))
            }
        }


        when (lookingFor) {
            0 -> {
                rbQueer.isChecked=true
            }
            1 -> {
                rbMale.isChecked=true
            }
            2 -> {
                rbFemale.isChecked=true
            }
        }
        txtSeekingSave.setOnClickListener {
            val seeking = if (rbQueer.isChecked) 0 else if (rbMale.isChecked)1 else 2
            listener.onCallback("$seeking")
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

    interface onCallBack {
        fun onCallback(value: String)
    }
}