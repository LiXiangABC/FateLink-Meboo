package com.crush.dialog

import android.app.Activity
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Display
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.R
import com.crush.util.SoftInputUtils
import razerdp.basepopup.BasePopupWindow

class UpdateAboutMeDialog(var ctx: Activity,var content: String, var listener: onCallBack) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_modify_about_me)
        initView()
    }

    private fun initView() {
        val outsideContainer = findViewById<ConstraintLayout>(R.id.outside_container)
        val outsideView = findViewById<View>(R.id.outside_view)
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)
        val editAboutMe = findViewById<EditText>(R.id.edit_about_me)
        val txtAboutMeSave = findViewById<TextView>(R.id.txt_about_me_save)
        outsideContainer.visibility=View.INVISIBLE
        editAboutMe.setText(content)
        SoftInputUtils.showSoftInput(editAboutMe)
        val decorView: View = ctx.window.decorView
        decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            ctx.window.decorView.getWindowVisibleDisplayFrame(r)
            val defaultDisplay: Display = ctx.windowManager.defaultDisplay
            val point = Point()
            defaultDisplay.getSize(point)
            val height = point.y
            val heightDifference = height - (r.bottom - r.top) // 实际高度减去可视图高度即是键盘高度
            outsideContainer.setPadding(0,0,0,heightDifference)
            Handler().postDelayed(Runnable {
                outsideContainer.visibility=View.VISIBLE
            },200)
        }

        editAboutMe.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                txtAboutMeSave.isEnabled = editAboutMe.text.toString().isNotEmpty()

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        txtAboutMeSave.setOnClickListener {
            SoftInputUtils.hideSoftInput(editAboutMe)
            listener.onCallback(editAboutMe.text.toString())
            dismiss()
        }

        dialogClose.setOnClickListener {
            SoftInputUtils.hideSoftInput(editAboutMe)
            dismiss()
        }
        outsideView.setOnClickListener {
            SoftInputUtils.hideSoftInput(editAboutMe)
            dismiss()
        }

        setOutSideDismiss(true)
    }
    interface onCallBack{
       fun onCallback(value:String)
    }
}