package com.crush.rongyun

import UserUtil
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.crush.Constant
import com.crush.entity.IMTokenGetEntity
import com.crush.entity.MatchIndexEntity
import com.crush.ui.HomeActivity
import com.crush.ui.chat.profile.UserProfileInfoActivity
import com.crush.util.CollectionUtils
import com.crush.util.IntentUtil
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import io.rong.imkit.GlideKitImageEngine
import io.rong.imkit.IMCenter
import io.rong.imkit.RongIM
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import io.rong.imkit.config.ConversationClickListener
import io.rong.imkit.config.ConversationListBehaviorListener
import io.rong.imkit.config.RongConfigCenter
import io.rong.imkit.conversation.extension.RongExtensionManager
import io.rong.imkit.conversationlist.model.BaseUiConversation
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.utils.FirebaseEventUtils
import io.rong.imlib.IRongCoreCallback
import io.rong.imlib.IRongCoreEnum
import io.rong.imlib.IRongCoreEnum.CoreErrorCode
import io.rong.imlib.RongCoreClient
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.UserInfo
import io.rong.sight.SightExtensionModule


object RongConfigUtil {
    fun updatePortrait() {
        RongConfigCenter.featureConfig().kitImageEngine = object : GlideKitImageEngine() {
            override fun loadConversationListPortrait(context: Context, url: String, imageView: ImageView, conversation: Conversation) {
                Glide.with(context).load(url)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(imageView)
            }

            override fun loadConversationPortrait(context: Context, url: String, imageView: ImageView, message: Message) {
                Glide.with(context).load(url)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(imageView)
            }
        }
        IMCenter.setConversationClickListener(object :ConversationClickListener{
            override fun onUserTitleClick(
                context: Context?,
                conversationType: Conversation.ConversationType?,
                targetId: String?
            ): Boolean {
                if(targetId.equals(BaseConfig.getInstance.getString(SpName.serverUserCode,""))){
                    return true
                }
                Activities.get().top?.let {
                    val bundle = Bundle()
                    bundle.putBoolean("isIM", true)
                    bundle.putString("userCodeFriend", targetId)
                    IntentUtil.startActivity(UserProfileInfoActivity::class.java, bundle)
                }
                return true
            }

            override fun onUserPortraitClick(
                context: Context?,
                conversationType: Conversation.ConversationType?,
                user: UserInfo?,
                targetId: String?
            ): Boolean {
                if (user?.userId != targetId){
                    return true
                }
                if (targetId == BaseConfig.getInstance.getString(SpName.serverUserCode,"")){
                    return true
                }
                Activities.get().top?.let {
                    FirebaseEventUtils.logEvent(FirebaseEventTag.Chat_Avatar.name)
                    val bundle = Bundle()
                    bundle.putBoolean("isIM",true)
                    bundle.putString("userCodeFriend",targetId)
                    IntentUtil.startActivity(UserProfileInfoActivity::class.java,bundle)
                }

                return true
            }

            override fun onUserPortraitLongClick(
                context: Context?,
                conversationType: Conversation.ConversationType?,
                user: UserInfo?,
                targetId: String?
            ): Boolean {

                return false
            }

            override fun onMessageClick(
                context: Context?,
                view: View?,
                message: Message?
            ): Boolean {
                return false
            }

            override fun onMessageLongClick(
                context: Context?,
                view: View?,
                message: Message?
            ): Boolean {
                return true
            }

            override fun onMessageLinkClick(
                context: Context?,
                link: String?,
                message: Message?
            ): Boolean {
                return false
            }

            override fun onReadReceiptStateClick(context: Context?, message: Message?): Boolean {
                return false
            }

        })

        RongIM.getInstance().setMessageAttachedUserInfo(true)

        RongIM.setConversationListBehaviorListener(object :ConversationListBehaviorListener{
            override fun onConversationPortraitClick(
                context: Context?,
                conversationType: Conversation.ConversationType?,
                targetId: String?
            ): Boolean {
                if(targetId.equals(BaseConfig.getInstance.getString(SpName.serverUserCode,""))){
                    return true
                }

                FirebaseEventUtils.logEvent(FirebaseEventTag.Chat_Avatar.name)
                val bundle = Bundle()
                bundle.putBoolean("isIM",true)
                bundle.putString("userCodeFriend",targetId)
                IntentUtil.startActivity(UserProfileInfoActivity::class.java,bundle)
                return false
            }

            override fun onConversationPortraitLongClick(
                context: Context?,
                conversationType: Conversation.ConversationType?,
                targetId: String?
            ): Boolean {
                return false
            }

            override fun onConversationLongClick(
                context: Context?,
                view: View?,
                conversation: BaseUiConversation?
            ): Boolean {
                return true
            }

            override fun onConversationClick(
                context: Context?,
                view: View?,
                conversation: BaseUiConversation?
            ): Boolean {
                if (conversation?.mCore?.targetId.equals(BaseConfig.getInstance.getString(SpName.serverUserCode,""))) {
                    FirebaseEventUtils.logEvent(FirebaseEventTag.IM_Notification.name)
                }else {
                    FirebaseEventUtils.logEvent(FirebaseEventTag.IM_Chat.name)
                }
                return false
            }

        })

    }

    fun getUnreadConversationList():List<Conversation?>{
        var conversation = listOf<Conversation?>()
        RongCoreClient.getInstance().getUnreadConversationList(object :
            IRongCoreCallback.ResultCallback<List<Conversation?>?>() {
            override fun onSuccess(conversations: List<Conversation?>?) {
                conversation= conversations!!
                // 成功并返回会话信息
            }

            override fun onError(e: IRongCoreEnum.CoreErrorCode) {}
        }, Conversation.ConversationType.PRIVATE)
        return conversation

    }

    fun configurationRong() {
        RongExtensionManager.getInstance().registerExtensionModule(SightExtensionModule())
        RongExtensionManager.getInstance().extensionConfig = MyExtensionConfig()
        RongExtensionManager.getInstance().extensionConfig
        RongConfigCenter.featureConfig().isHideEmojiButton = false

    }

    fun reConnectIM(token:String,activity: Activity){
        RongIM.connect(token,  object : RongIMClient.ConnectCallback() {
            override fun onSuccess(code: String) {
            }

            override fun onError(errorCode: RongIMClient.ConnectionErrorCode) {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(Constant.im_token_Url)
                    }
                }, object : SDOkHttpResoutCallBack<IMTokenGetEntity>() {
                    override fun onSuccess(entity: IMTokenGetEntity) {
                        connectIM(entity.data.token,activity)
                    }

                    override fun onFailure(code: Int, msg: String) {
                    }
                })
            }
            override fun onDatabaseOpened(code : RongIMClient.DatabaseOpenStatus) {
            }
        })
    }
    fun connectIM(token:String,activity: Activity){
        RongIM.connect(token,  object : RongIMClient.ConnectCallback() {
            override fun onSuccess(code: String) {
            }

            override fun onError(errorCode: RongIMClient.ConnectionErrorCode) {
                RongIM.getInstance().logout()
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(Constant.im_token_Url)
                    }
                }, object : SDOkHttpResoutCallBack<IMTokenGetEntity>() {
                    override fun onSuccess(entity: IMTokenGetEntity) {
                        connectIM(entity.data.token,activity)
                    }

                    override fun onFailure(code: Int, msg: String) {
                        if (code == 700) {
                            UserUtil.startLogin(null)
                        }
                    }
                })
            }
            override fun onDatabaseOpened(code : RongIMClient.DatabaseOpenStatus) {
                if(RongIMClient.DatabaseOpenStatus.DATABASE_OPEN_SUCCESS == code) {
                    // 登录成功，跳转到默认会话列表页。
                    initPaginate(activity)
                } else {
                    //数据库打开失败，可以弹出 toast 提示。
//                    ToastUtil.toast(code.name)
                }

            }
        })
    }
    fun connectIMLogin(token:String,userCode:String,nickName:String,avatarUrl:String,activity: Activity){
        RongIM.connect(token,  object : RongIMClient.ConnectCallback() {
            override fun onSuccess(code: String) {
            }

            override fun onError(errorCode: RongIMClient.ConnectionErrorCode) {
                RongIM.getInstance().logout()
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(Constant.im_token_Url)
                    }
                }, object : SDOkHttpResoutCallBack<IMTokenGetEntity>() {
                    override fun onSuccess(entity: IMTokenGetEntity) {
                        connectIMLogin(entity.data.token,userCode,nickName,avatarUrl,activity)

                    }

                    override fun onFailure(code: Int, msg: String) {
                        if (code == 700) {
                            UserUtil.startLogin(null)
                        }
                    }
                })
            }
            override fun onDatabaseOpened(code : RongIMClient.DatabaseOpenStatus) {
                if(RongIMClient.DatabaseOpenStatus.DATABASE_OPEN_SUCCESS == code) {
                    // 登录成功，跳转到默认会话列表页。
                    initPaginate(activity)
                } else {
                    //数据库打开失败，可以弹出 toast 提示。
//                    ToastUtil.toast(code.name)
                }

            }
        })
    }

    /**
     * 获取列表数据
     */
    private fun initPaginate(activity: Activity) {
        IntentUtil.startActivity(HomeActivity::class.java)
        Activities.get().finishAllExclude(HomeActivity::class.java)
    }



    fun deleteRongUserHistory(targetId:String){
        RongIMClient.getInstance()
            .removeConversation(Conversation.ConversationType.PRIVATE,
                targetId, object : RongIMClient.ResultCallback<Boolean?>() {
                    override fun onSuccess(success: Boolean?) {}
                    override fun onError(errorCode: RongIMClient.ErrorCode?) {}
                })
        RongCoreClient.getInstance().deleteMessages(
            Conversation.ConversationType.PRIVATE,
            targetId,
            object : IRongCoreCallback.ResultCallback<Boolean?>() {
                /**
                 * 删除消息成功回调
                 */
                override fun onSuccess(bool: Boolean?) {
                }

                /**
                 * 删除消息失败回调
                 * @param errorCode 错误码
                 */
                override fun onError(coreErrorCode: IRongCoreEnum.CoreErrorCode) {}
            })

        val recordTime: Long = System.currentTimeMillis()
        val cleanRemote = true // 同时从服务端删除对应的消息历史记录


        RongCoreClient.getInstance()
            .cleanHistoryMessages( Conversation.ConversationType.PRIVATE,
                targetId, recordTime, cleanRemote,
                object : IRongCoreCallback.OperationCallback() {
                    /**
                     * 删除消息成功回调
                     */
                    override fun onSuccess() {
                    }

                    /**
                     * 删除消息失败回调
                     * @param errorCode 错误码
                     */
                    override fun onError(coreErrorCode: CoreErrorCode) {}
                })
    }

}