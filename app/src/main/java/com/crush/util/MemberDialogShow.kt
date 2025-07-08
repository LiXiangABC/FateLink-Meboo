package com.crush.util

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.crush.Constant
import io.rong.imkit.dialog.MemberBuyDialog
import io.rong.imkit.entity.MemberSubscribeEntity
import io.rong.imkit.event.EnumEventTag
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.SpName
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.utils.FirebaseEventUtils
import razerdp.basepopup.BasePopupWindow

object MemberDialogShow {
    private var memberBuyDialog: MemberBuyDialog?=null

    /**
     *  type: if (direction.toString() == "Right") 1 else 2
     */
    fun memberBuyShow(type:Int?,context:Context){
        if (memberBuyDialog !=null){
            return
        }
        if (type!=null) {
            FirebaseEventUtils.logEvent(if (type == 1) FirebaseEventTag.Home_Like_Sub.name else FirebaseEventTag.Home_View_Sub.name)
        }
        memberBuyDialog = MemberBuyDialog(
            context,
            0,
            object : MemberBuyDialog.ChangeMembershipListener {
                override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                        override fun addBody(requestBody: OkHttpBodyEntity) {
                            requestBody.setPost(Constant.user_create_order_url)
                            requestBody.add("productCode", bean.productCode)
                            requestBody.add("productCategory", 1)
                        }

                    }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                        override fun onSuccess(entity: OrderCreateEntity) {
                            PayUtils.instance.start(entity,context,object : EmptySuccessCallBack {
                                override fun OnSuccessListener() {
                                    if (type!=null) {
                                        FirebaseEventUtils.logEvent(if (type == 1) FirebaseEventTag.Home_Like_Subsuccess.name else FirebaseEventTag.Home_View_Subsuccess.name)
                                    }
                                    BaseConfig.getInstance.setBoolean(SpName.isMember, true)
                                }

                            })
                        }
                    })
                }

                override fun closeListener(refreshTime:Long) {
                    if (type==2){
                        SDEventManager.post(refreshTime,EnumEventTag.INDEX_COUNTDOWN_SHOW.ordinal)
                    }
                }
            })
        memberBuyDialog?.onDismissListener = object : BasePopupWindow.OnDismissListener(){
            override fun onDismiss() {
                memberBuyDialog=null
            }

        }
    }
}