package io.rong.imkit.utils.ktl

import android.app.Activity
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import io.rong.imkit.API
import io.rong.imkit.dialog.MemberBuyDialog
import io.rong.imkit.entity.BaseEntity
import io.rong.imkit.entity.MemberSubscribeEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.widget.EmptyCallBack

object WlmClick {

    fun benefitsReduceWLM(friendUserCode: String, type: Int, callBack: EmptyCallBack) {
        return OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_benefits_reduceWLM_url)
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
                            requestBody.setPost(API.user_create_order_url)
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