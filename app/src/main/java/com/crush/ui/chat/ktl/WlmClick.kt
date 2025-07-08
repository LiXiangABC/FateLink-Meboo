package com.crush.ui.chat.ktl

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import com.crush.Constant
import com.crush.bean.WLMListBean
import com.crush.callback.EmptyCallBack
import com.crush.callback.WLMSwipedCallBack
import com.crush.entity.BaseEntity
import com.crush.ui.chat.profile.UserProfileInfoActivity
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import com.crush.util.IntentUtil
import io.rong.imkit.dialog.MemberBuyDialog
import io.rong.imkit.entity.MemberSubscribeEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.utils.FirebaseEventUtils

object WlmClick {

    fun itemClick(
        isMember: Boolean,
        position: Int,
        model: WLMListBean,
        wLMSwipedCallBack: WLMSwipedCallBack?=null
    ) {
        if (isMember) {
            val bundle = Bundle()
            bundle.putBoolean("isWlm", true)
            bundle.putInt("selectPosition", position)
            bundle.putString("userCodeFriend", model.userCodeFriend)
            IntentUtil.startActivity(UserProfileInfoActivity::class.java, bundle)
        } else {
            wLMSwipedCallBack?.swipedCallback(ItemTouchHelper.END, model)
        }
    }

    fun benefitsReduceWLM(friendUserCode: String, type: Int, callBack: EmptyCallBack) {
        return OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_benefits_reduceWLM_url)
                requestBody.add("likeType", type)
                requestBody.add("userCodeFriend", friendUserCode)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
                callBack.OnSuccessListener()
            }

            override fun onFailure(code: Int, msg: String) {
                when (code) {
                    2003 -> {
                        callBack.OnFailListener()
                    }
                }
            }
        }, isShowDialog = false)

    }

    fun openMemberBuyDialog(isClickBuy:(entity: OrderCreateEntity)->Unit){
        MemberBuyDialog(
            SDActivityManager.instance.lastActivity,
            1,
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
                            isClickBuy.invoke(entity)
                        }
                    })
                }

                override fun closeListener(refreshTime: Long) {

                }

            })
    }

    fun openPay(mActivity: Activity,entity: OrderCreateEntity,type: Int,payResult:(isSuccess:Boolean)->Unit){
        PayUtils.instance.start(
            entity,
            mActivity,
            object : EmptySuccessCallBack {
                override fun OnSuccessListener() {
                    payResult.invoke(true)
                }

            })
    }
}