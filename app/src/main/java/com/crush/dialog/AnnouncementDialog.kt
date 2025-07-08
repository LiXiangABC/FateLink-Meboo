package com.crush.dialog

import android.app.Activity
import android.widget.TextView
import com.crush.R
import com.crush.entity.AnnouncementEntity
import razerdp.basepopup.BasePopupWindow

/**
 * 公告弹窗
 */
class AnnouncementDialog(var ctx: Activity, entity: AnnouncementEntity) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_announcement)
        initView(entity)
    }

    private fun initView(entity: AnnouncementEntity) {
        val popupTitle = findViewById<TextView>(R.id.popup_title)
        val txtOneContent = findViewById<TextView>(R.id.txt_one_content)
        val txtTwoContent = findViewById<TextView>(R.id.txt_two_content)
        val txtPpNumber = findViewById<TextView>(R.id.txt_pp_number)
        val txtPvNumber = findViewById<TextView>(R.id.txt_pv_number)
        val txtIKnowIt = findViewById<TextView>(R.id.txt_i_know_it)
        popupTitle.text=entity.data.title
        txtOneContent.text=entity.data.msg
        txtTwoContent.text=entity.data.msg2
        txtPpNumber.text="X${entity.data.ppNum}"
        txtPvNumber.text="X${entity.data.pvNum}"
        txtIKnowIt.setOnClickListener {
            dismiss()
        }
        setOutSideDismiss(true)
    }
}