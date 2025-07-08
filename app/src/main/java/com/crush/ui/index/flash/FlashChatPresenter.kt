package com.crush.ui.index.flash

import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crush.Constant
import com.crush.R
import io.rong.imkit.dialog.MemberBuyDialog
import com.crush.entity.BaseStringEntity
import io.rong.imkit.entity.BuyMemberPageEntity
import io.rong.imkit.entity.MemberSubscribeEntity
import com.crush.entity.UserMemberStatusEntity
import com.crush.ui.chat.UpSourceEnum
import io.rong.imkit.event.EnumEventTag
import com.crush.view.Loading.LoadingDialog
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.IMCenter
import io.rong.imkit.RongIM
import io.rong.imkit.SpName
import io.rong.imkit.dialog.MemberUnitaryBuyNewDialog
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.utils.FirebaseEventUtils
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.UserInfo
import io.rong.message.TextMessage


class FlashChatPresenter : BasePresenterImpl<FlashChatContract.View>(), FlashChatContract.Presenter {

    var upSource: String? = null
    var position=-1
    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            upSource = bundle.getString(UpSourceEnum.SOURCE.name)
            imgClose.setOnClickListener {
                finish()
            }
            val style = SpannableStringBuilder()
            val userName = bundle.getString("userName")
            position = bundle.getInt("position")
            style.append("${mActivity.getString(R.string.flash_chat_start)} $userName? ${mActivity.getString(R.string.flash_chat_end)}")

            style.setSpan(object : ClickableSpan() {
                override fun onClick(p0: View) {
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.color=ContextCompat.getColor(mActivity,R.color.color_FF3437)
                    ds.clearShadowLayer()
                }
            }, 32, 32+ userName!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            flashChatUserTitle.text = style
            flashChatUserTitle.movementMethod = LinkMovementMethod.getInstance()


            Glide.with(flashChatUserAvatar)
                .load(bundle.getString("avatar"))
                .error(R.drawable.image_error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(flashChatUserAvatar)

            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_config_url)
                    requestBody.add("code", 5)
                }
            }, object : SDOkHttpResoutCallBack<BaseStringEntity>(false) {
                override fun onSuccess(entity: BaseStringEntity) {
                    startFlashChatContainer.setOnClickListener {
                        flashChatClick(bundle.getString("userCode",""),bundle.getString("userName",""),bundle.getString("avatar",""),entity.data)

                    }
                }
            })


        }

    }
    /**
    * 点击flashChat请求接口
    */
    private fun flashChatClick(userCode: String, userName: String, avatarUrl: String, data: String) {
        LoadingDialog.showLoading(mActivity)
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_reduce_benefits_url)
                requestBody.add("benefitCode", 1)
                requestBody.add("chatUserCode", userCode)
            }
        }, object : SDOkHttpResoutCallBack<UserMemberStatusEntity>() {
            override fun onSuccess(entity: UserMemberStatusEntity) {
                LoadingDialog.dismissLoading(mActivity)
                chatMethod(userCode, userName, avatarUrl,data)
            }

            override fun onFailure(code: Int, msg: String) {
                LoadingDialog.dismissLoading(mActivity)
                when (code) {
                    2003 -> {
                        FirebaseEventUtils.logEvent(if (upSource == UpSourceEnum.HOME.name) FirebaseEventTag.Home_Flashchat_Buy.name else FirebaseEventTag.Home_Profile_Flash_chat_Buy.name)
                        MemberBuyDialog(
                            mActivity,
                            2,
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
                                                mActivity,
                                                object : EmptySuccessCallBack {
                                                    override fun OnSuccessListener() {
                                                        FirebaseEventUtils.logEvent(if (upSource == UpSourceEnum.HOME.name) FirebaseEventTag.Home_Flashchat_Buysuccess.name else FirebaseEventTag.Home_Profile_Flash_chat_Buysuccess.name)
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

                    2002 -> {
                        FirebaseEventUtils.logEvent(if (upSource == UpSourceEnum.HOME.name) FirebaseEventTag.Home_Flashchat_Buy.name else FirebaseEventTag.Home_Profile_Flash_chat_Buy.name)
                        MemberUnitaryBuyNewDialog(
                            mActivity,
                            2,
                            object : MemberUnitaryBuyNewDialog.MemberUnitaryBuyListener {
                                override fun onListener(bean: BuyMemberPageEntity.ProductExt) {
                                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                        override fun addBody(requestBody: OkHttpBodyEntity) {
                                            requestBody.setPost(Constant.user_create_order_url)
                                            requestBody.add("productCode", bean.productCode)
                                            requestBody.add("productCategory", 2)
                                        }

                                    }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                        override fun onSuccess(entity: OrderCreateEntity) {
                                            entity.data.benefitNum = bean.benefitNum
                                            entity.data.productCategory = 2
                                            PayUtils.instance.start(
                                                entity,
                                                mActivity,
                                                object : EmptySuccessCallBack {
                                                    override fun OnSuccessListener() {
                                                        FirebaseEventUtils.logEvent(FirebaseEventTag.Home_Flashchat_Buysuccess.name)

                                                    }

                                                })
                                        }
                                    })

                                }

                            })
                    }
                }
            }
        })
    }

    /**
     * flashChat事件
     */
    private fun chatMethod(userCode: String, userName: String, avatarUrl: String, data: String) {
        SDEventManager.post(position,EnumEventTag.FLASH_CHAT_REMOVE.ordinal)
        SDEventManager.post(EnumEventTag.FLASH_CHAT_END_NUM_REDUCTION.ordinal)
        FirebaseEventUtils.logEvent(FirebaseEventTag.Home_Flash_chat.name)
        RongIM.getInstance().refreshUserInfoCache(
            UserInfo(userCode, userName, Uri.parse(avatarUrl))
        )
        val message: Message = Message.obtain(userCode, Conversation.ConversationType.PRIVATE, TextMessage.obtain(data))
        IMCenter.getInstance()
            .sendMessage(message, null, null, object : IRongCallback.ISendMessageCallback {
                override fun onAttached(message: Message?) {}
                override fun onSuccess(message: Message?) {
                }
                override fun onError(message: Message?, errorCode: RongIMClient.ErrorCode?) {}
            })
        val bundle = Bundle()
        bundle.putBoolean("flashClick", true)
        RouteUtils.routeToConversationActivity(
            mActivity,
            Conversation.ConversationType.PRIVATE,
            userCode, bundle
        )
        finish()
    }
}