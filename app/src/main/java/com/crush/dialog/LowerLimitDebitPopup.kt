package com.crush.dialog

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import com.crush.Constant
import com.crush.R
import com.crush.entity.LowerLimitDebitEntity
import com.crush.ui.HomeActivity
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import com.crush.util.IntentUtil
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.entity.BaseEntity
import io.rong.imkit.event.EnumEventTag
import razerdp.basepopup.BasePopupWindow

/**
 * 划卡下限提醒
 */
class LowerLimitDebitPopup(var ctx: Context,val entity: LowerLimitDebitEntity) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.popup_lower_limit_debit)
        initView()
        isOutSideTouchable = true
    }


    private fun initView() {
        val popupTopTitle = findViewById<TextView>(R.id.popup_top_title)
        val popupTopTip = findViewById<TextView>(R.id.popup_top_tip)
        val popupLetsGo = findViewById<TextView>(R.id.popup_lets_go)
        popupTopTitle.text=entity.data.title
        popupTopTip.text=entity.data.content
        popupLetsGo.text=entity.data.btnStr
        popupLetsGo.setOnClickListener {
            dismiss()
            SDActivityManager.instance.finishAllActivityExcept(HomeActivity::class.java)
            SDEventManager.post("like",EnumEventTag.INDEX_TO_INDEX.ordinal)
            SDEventManager.post(EnumEventTag.LOWER_LIMIT_DEBIT_TO_INDEX.ordinal)
        }

        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.home_notice_read_url)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>(false) {
            override fun onSuccess(entity: BaseEntity) {
            }
        })


    }

}