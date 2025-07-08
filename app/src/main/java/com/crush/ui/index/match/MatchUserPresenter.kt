package com.crush.ui.index.match

import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.Display
import android.view.View
import com.crush.Constant
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.dialog.MemberBuyDialog
import io.rong.imkit.entity.MemberSubscribeEntity
import io.rong.imkit.entity.OrderCreateEntity
import com.crush.util.DensityUtil
import com.crush.util.GlideUtil
import io.rong.imkit.http.HttpRequest
import com.custom.base.config.BaseConfig
import io.rong.imkit.pay.PayUtils
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import io.rong.imkit.API
import io.rong.imkit.IMCenter
import io.rong.imkit.SpName
import io.rong.imkit.entity.BaseEntity
import io.rong.imkit.entity.OpenChatEntity
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.message.TextMessage
import kotlin.random.Random


class MatchUserPresenter : BasePresenterImpl<MatchUserContract.View>(), MatchUserContract.Presenter {
    var bun :Bundle?=null
    private val textList= arrayListOf("Go ahead and flirt boldly!", "Be bolder in approaching!", "Be even more proactive!")
    override fun initBundle(bundle: Bundle) {
        bun=bundle
        mView?.apply {
            GlideUtil.setImageView(bundle.getString("avatarUrl"),imgUserMatch)
            GlideUtil.setImageView(BaseConfig.getInstance.getString(SpName.avatarUrl,""),imgUserMatchRight)


            val randomIndex = Random.nextInt(textList.size)
            val randomQuote = textList[randomIndex]
            editUserMatch.hint=randomQuote

            val decorView: View = mActivity.window.decorView
            decorView.viewTreeObserver.addOnGlobalLayoutListener {
                val r = Rect()
                mActivity.window.decorView.getWindowVisibleDisplayFrame(r)
                val defaultDisplay: Display = mActivity.windowManager.defaultDisplay
                val point = Point()
                defaultDisplay.getSize(point)
                val height = point.y
                val heightDifference = height - (r.bottom - r.top) // 实际高度减去可视图高度即是键盘高度
                editContainer.setPadding(DensityUtil.dip2px(mActivity,12f),DensityUtil.dip2px(mActivity,4f),DensityUtil.dip2px(mActivity,12f),heightDifference+DensityUtil.dip2px(mActivity,4f))
                if (heightDifference == 0){
                    editContainer.setPadding(DensityUtil.dip2px(mActivity,30f),DensityUtil.dip2px(mActivity,0f),DensityUtil.dip2px(mActivity,30f),DensityUtil.dip2px(mActivity,76f))
                }
            }

        }
    }
    fun userSend(content:String){
        mView?.apply {
            val targetId = bun!!.getString("userCodeFriend","")
            HttpRequest.requestOpenChatData(targetId,1,object :SDOkHttpResoutCallBack<OpenChatEntity>(){
                override fun onSuccess(entity: OpenChatEntity) {
                }

            })

            val conversationType: Conversation.ConversationType = Conversation.ConversationType.PRIVATE
            val messageContent = TextMessage.obtain(content)

            val message: Message = Message.obtain(targetId, conversationType, messageContent)


            IMCenter.getInstance()
                .sendMessage(message, null, null, object : IRongCallback.ISendMessageCallback {
                    override fun onAttached(message: Message?) {}
                    override fun onSuccess(message: Message?) {
                        RouteUtils.routeToConversationActivity(
                            mActivity,
                            Conversation.ConversationType.PRIVATE,
                            targetId,
                            false
                        )
                        mActivity.finish()
                    }
                    override fun onError(message: Message?, errorCode: RongIMClient.ErrorCode?) {}
                })
        }
    }
    fun tryToSend(content:String){
        val targetId = bun!!.getString("userCodeFriend","")
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_send_message_url)
                requestBody.add("toUserId", targetId)
            }

        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
                userSend(content)
            }

            override fun onFailure(code: Int, msg: String) {
                super.onFailure(code, msg)
                when(code){
                    3004,3007,3008,3009->{
                        MemberBuyDialog(
                            mActivity,
                            0,
                            object : MemberBuyDialog.ChangeMembershipListener {
                                override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                        override fun addBody(requestBody: OkHttpBodyEntity) {
                                            requestBody.setPost(Constant.user_create_order_url)
                                            requestBody.add("productCode", bean.productCode)
                                            requestBody.add("productCategory", 1)
                                        }
                                    }, object : SDOkHttpResoutCallBack<OrderCreateEntity?>() {

                                        override fun onSuccess(entity: OrderCreateEntity?) {
                                            PayUtils.instance.start(entity as OrderCreateEntity,mActivity,object : EmptySuccessCallBack {
                                                override fun OnSuccessListener() {
                                                    BaseConfig.getInstance.setBoolean(SpName.isMember, true)

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
        })
    }

}