package com.crush.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.crush.Constant
import com.crush.R
import com.crush.entity.BlindBoxGetPrizeEntity
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import io.rong.imkit.SpName
import io.rong.imkit.entity.BaseEntity
import razerdp.basepopup.BasePopupWindow

class DailyBlindBoxDialog(ctx: Context) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_daily_blind_box)
        initView()
    }

    private fun initView() {
        val loDailyBlindBoxNotPoke = findViewById<LottieAnimationView>(R.id.lo_daily_blind_box_not_poke)
        val btnPokeDaily = findViewById<TextView>(R.id.btn_poke_daily)
        OkHttpManager.instance.requestInterface(object :OkHttpFromBoy{
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.blind_box_get_prize_url)
                requestBody.add("userCode",BaseConfig.getInstance.getString(SpName.userCode,""))
            }
        },object :SDOkHttpResoutCallBack<BlindBoxGetPrizeEntity>(){
            override fun onSuccess(entity: BlindBoxGetPrizeEntity) {

            }

        })

        btnPokeDaily.setOnClickListener {
            loDailyBlindBoxNotPoke.cancelAnimation()
            OkHttpManager.instance.requestInterface(object :OkHttpFromBoy{
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setGet(Constant.blind_box_add_prize_url)
                    requestBody.add("userCode",BaseConfig.getInstance.getString(SpName.userCode,""))
                }
            },object :SDOkHttpResoutCallBack<BaseEntity>(){
                override fun onSuccess(entity: BaseEntity) {
                    dismiss()
                }

            })
        }

        val dialogCancel = findViewById<ImageView>(R.id.dialog_cancel)
        dialogCancel.setOnClickListener {
            dismiss()
        }
        setOutSideDismiss(false)
    }

    interface ChangeMembershipListener{
        fun onListener()
    }

}