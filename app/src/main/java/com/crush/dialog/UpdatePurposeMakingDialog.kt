package com.crush.dialog

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.crush.R
import com.custom.base.util.ToastUtil
import razerdp.basepopup.BasePopupWindow

class UpdatePurposeMakingDialog(var ctx: Activity, var purpose: String, var listener: onCallBack) :
    BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_modify_purpose_making)
        initView()
    }

    private fun initView() {
        val outsideView = findViewById<View>(R.id.outside_view)
        val dialogClose = findViewById<ImageView>(R.id.dialog_close)
        val radioLookingGroup = findViewById<RadioGroup>(R.id.radio_looking_group)
        val rbMakeNewFriends = findViewById<RadioButton>(R.id.rb_make_new_friends)
        val rbSeekingLongTermPartner = findViewById<RadioButton>(R.id.rb_seeking_long_term_partner)
        val rbLookingForDatingPartner = findViewById<RadioButton>(R.id.rb_looking_for_dating_partner)
        val txtPurposeSave = findViewById<TextView>(R.id.txt_purpose_save)
        repeat(radioLookingGroup.childCount){
            val radioButton = radioLookingGroup.getChildAt(it) as RadioButton
            radioButton.isChecked=radioButton.text.toString() == purpose
        }

        txtPurposeSave.setOnClickListener {
            var socialConnections = ""
            var socialConnectionsUrl = 0
            when (radioLookingGroup.checkedRadioButtonId) {
                R.id.rb_make_new_friends -> {
                    socialConnections=  rbMakeNewFriends.text.toString()
                    socialConnectionsUrl=R.mipmap.icon_make_new_friends
                }

                R.id.rb_seeking_long_term_partner -> {
                    socialConnections= rbSeekingLongTermPartner.text.toString()
                    socialConnectionsUrl=R.mipmap.icon_seeking_long_term_partner

                }

                R.id.rb_looking_for_dating_partner -> {
                    socialConnections= rbLookingForDatingPartner.text.toString()
                    socialConnectionsUrl=R.mipmap.icon_looking_for_dating_partner

                }
            }
            if (socialConnections=="") {
                ToastUtil.toast(ctx.getString(R.string.please_select))
                return@setOnClickListener
            }
            listener.onCallback(socialConnections,socialConnectionsUrl)
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
        fun onCallback(name: String,url:Int)
    }
}