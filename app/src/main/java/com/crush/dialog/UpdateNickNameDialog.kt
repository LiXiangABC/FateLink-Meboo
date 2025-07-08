package com.crush.dialog

import android.app.Activity
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Display
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.R
import com.crush.util.SoftInputUtils
import com.custom.base.util.ToastUtil
import razerdp.basepopup.BasePopupWindow


class UpdateNickNameDialog(var ctx:Activity,var userName:String ,var listener: onCallBack ) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_modify_nickname)
        initView()
    }

    private fun initView() {
        val outsideContainer = findViewById<ConstraintLayout>(R.id.outside_container)
        val outsideView = findViewById<View>(R.id.outside_view)
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)
        val editNickname = findViewById<EditText>(R.id.edit_nickname)
        val txtNicknameSave = findViewById<TextView>(R.id.txt_nickname_save)

        editNickname.setText(userName)
        txtNicknameSave.isEnabled = editNickname.text.toString().isNotEmpty()
        outsideContainer.visibility=View.INVISIBLE
        SoftInputUtils.showSoftInput(editNickname)
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

        editNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                txtNicknameSave.isEnabled = editNickname.text.toString().isNotEmpty()

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        editNickname.setOnEditorActionListener { v, actionId, event ->
            actionId == EditorInfo.IME_ACTION_DONE
        }

        editNickname.imeOptions = EditorInfo.IME_ACTION_DONE
        editNickname.setRawInputType(InputType.TYPE_CLASS_TEXT)

        txtNicknameSave.setOnClickListener {
            if (Character.isWhitespace(editNickname.text.toString().first())){
               ToastUtil.toast(ctx.getString(R.string.the_nickname_is_not_valid))
                return@setOnClickListener
            }
            SoftInputUtils.hideSoftInput(editNickname)
            listener.onCallback(editNickname.text.toString())
            dismiss()
        }

        dialogClose.setOnClickListener {
            SoftInputUtils.hideSoftInput(editNickname)
            dismiss()
        }
        outsideView.setOnClickListener {
            SoftInputUtils.hideSoftInput(editNickname)
            dismiss()
        }

        setOutSideDismiss(true)
    }
    interface onCallBack{
       fun onCallback(name:String)
    }
}