package com.crush.util

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import com.adjust.sdk.Adjust
import com.crush.App
import com.crush.BuildConfig
import com.crush.Constant
import com.crush.callback.EmptyRefreshCallBack
import io.rong.imkit.dialog.MemberBuyDialog
import com.crush.entity.BaseEntity
import io.rong.imkit.entity.MemberSubscribeEntity
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import io.rong.imkit.API
import io.rong.imkit.SpName
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.utils.FirebaseEventUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response


object HttpRequest {
    fun get(
        direction: Int,
        userCodeFriend: String,
        context: Context,
        callBack: EmptyRefreshCallBack
    ) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_benefits_reduceWLM_url)
                requestBody.add("likeType", if (direction == ItemTouchHelper.START) 2 else 1)
                requestBody.add("userCodeFriend", userCodeFriend)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
                callBack.OnSuccessListener()
            }

            override fun onFailure(code: Int, msg: String) {
                callBack.OnFailListener()
                when (code) {
                    2003 -> {
                        FirebaseEventUtils.logEvent(if (direction == ItemTouchHelper.END) FirebaseEventTag.WLM_Like_Sub.name else FirebaseEventTag.WLM_Pass_Sub.name)
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
                                            PayUtils.instance.start(
                                                entity,
                                                context,
                                                object : EmptySuccessCallBack {
                                                    override fun OnSuccessListener() {
                                                        FirebaseEventUtils.logEvent(if (direction == ItemTouchHelper.END) FirebaseEventTag.WLM_Like_Subsuccess.name else FirebaseEventTag.WLM_Pass_Subsuccess.name)
                                                        callBack.OnRefreshListener()
                                                        BaseConfig.getInstance.setBoolean(
                                                            SpName.isMember,
                                                            true
                                                        )
                                                    }

                                                })
                                        }
                                    })
                                }

                                override fun closeListener(refreshTime: Long) {

                                }

                            })
                    }
                }
            }
        }, isShowDialog = false)
    }


    fun commonNotify(
        type: Any,
        content: String? = null,
        mActivity: Context? = null, callBack: (() -> Unit?)? = null) {
        try {
            if (BaseConfig.getInstance.getBoolean(SpName.openNotify, true)) {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(API.user_common_notify_url)
                        if (type is String){
                            requestBody.add("eventName", type)
                        }else{
                            requestBody.add("code", type)
                        }

                        content?.let { requestBody.add("content", it) }
                        requestBody.add(
                            "userCode",
                            BaseConfig.getInstance.getString("userCode", "")
                        )
                        Adjust.getAdid {
                            requestBody.add("adid", it)
                        }
                    }
                }, object : SDOkHttpResoutCallBack<io.rong.imkit.entity.BaseEntity>(false) {
                    override fun onSuccess(entity: io.rong.imkit.entity.BaseEntity) {
                        callBack?.invoke()
                    }
                })
            }
        } catch (e: Exception) {

        }

    }


    fun sendVipNotice(targetId: String) {
        Thread {
            try {
                val client = OkHttpClient().newBuilder().build()
                val mediaType = "application/json".toMediaTypeOrNull()
                val body = RequestBody.create(
                    mediaType,
                    "{\"modelUserCode\":$targetId,\"type\":2,\"userCode\":${
                        BaseConfig.getInstance.getString(
                            SpName.userCode,
                            ""
                        )
                    }}"
                )
                val request: Request = Request.Builder()
                    .url(if (BuildConfig.DEBUG) "http://112.124.66.71:9105/api/chat/sendVipMessageNotice" else "https://api.chatfree-app.com/api/chat/sendVipMessageNotice")
                    .method("POST", body)
                    .addHeader(
                        "basicParams",
                        "{\"channel\":\"l1\",\"appCode\":\"auramix\",\"appVersion\":1}"
                    )
                    .addHeader("Content-Type", "application/json")
                    .build()
                val response: Response = client.newCall(request).execute()
            } catch (e: java.lang.Exception) {
            }
        }.start()


    }


}