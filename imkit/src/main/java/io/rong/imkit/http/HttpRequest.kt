package io.rong.imkit.http

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.API
import io.rong.imkit.SpName
import io.rong.imkit.SpName.evaluateCheck
import io.rong.imkit.dialog.MemberBuyDialog
import io.rong.imkit.dialog.MemberUnitaryBuyNewDialog
import io.rong.imkit.entity.BaseEntity
import io.rong.imkit.entity.BaseResutlEntity
import io.rong.imkit.entity.BuyMemberPageEntity
import io.rong.imkit.entity.ConfigsEntity
import io.rong.imkit.entity.EvaluateCheckBean
import io.rong.imkit.entity.EvaluateCheckEntity
import io.rong.imkit.entity.GreetingFlirtingEntity
import io.rong.imkit.entity.GreetingMessageEntity
import io.rong.imkit.entity.MemberSubscribeEntity
import io.rong.imkit.entity.OpenChatEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.entity.PrivateAlbumEntity
import io.rong.imkit.entity.TipsEntity
import io.rong.imkit.entity.TipsPopEntity
import io.rong.imkit.entity.UserProfileEntity
import io.rong.imkit.entity.WLMListBean
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.userinfo.RongUserInfoManager
import io.rong.imkit.utils.ChatMessageNumber
//import io.rong.imkit.utils.DataCommonUtil
import io.rong.imkit.utils.FirebaseEventUtils
import io.rong.imkit.utils.JsonUtils
import org.json.JSONObject


object HttpRequest {

    var wlmLiveData = MutableLiveData<ArrayList<WLMListBean>>()
    var canSendImageNumLiveData = MutableLiveData<Int>()
    var canSendUnlockImageNumLiveData = MutableLiveData<Int>()


    var sayHiUrl:String=""
    fun getMemberReduce(context: Context,chatUserCode:String,uid:String,callback:RequestCallBack,benefitCode:Int,type:Int,imageCode:String) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_reduce_benefits_url)
                requestBody.add("benefitCode", benefitCode)
                requestBody.add("chatUserCode", chatUserCode)
                requestBody.add("imageCode", imageCode)
                requestBody.add("msgId", uid)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
                callback.onSuccess()
            }

            override fun onFailure(code: Int, msg: String) {
                when (code) {
                    2003 -> {
                        FirebaseEventUtils.logEvent(if (type==3)FirebaseEventTag.Chat_PP_Sub.name else FirebaseEventTag.Chat_PV_Sub.name)
                        MemberBuyDialog(
                            context,
                            type,
                            object : MemberBuyDialog.ChangeMembershipListener {
                                override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                        override fun addBody(requestBody: OkHttpBodyEntity) {
                                            requestBody.setPost(API.user_create_order_url)
                                            requestBody.add("productCode", bean.productCode)
                                            requestBody.add("productCategory", 1)
                                            requestBody.add("modelUserCode",chatUserCode)
                                        }
                                    }, object : SDOkHttpResoutCallBack<OrderCreateEntity?>() {

                                        override fun onSuccess(entity: OrderCreateEntity?) {
                                            PayUtils.instance.start(entity as OrderCreateEntity,context,object : EmptySuccessCallBack {
                                                override fun OnSuccessListener() {
                                                    BaseConfig.getInstance.setBoolean(SpName.isMember, true)
                                                    FirebaseEventUtils.logEvent(if (type==3)FirebaseEventTag.Chat_PP_Subsuccess.name else FirebaseEventTag.Chat_PV_Subsuccess.name)

                                                }

                                            },chatUserCode)
                                        }
                                    })
                                }

                                override fun closeListener(refreshTime: Long) {

                                }
                            })
                    }

                    2002 -> {
                        FirebaseEventUtils.logEvent(if (type==3)FirebaseEventTag.Chat_PP_Buy.name else FirebaseEventTag.Chat_PV_Buy.name)
                        MemberUnitaryBuyNewDialog(
                            context,
                            type,
                            object : MemberUnitaryBuyNewDialog.MemberUnitaryBuyListener {
                                override fun onListener(bean: BuyMemberPageEntity.ProductExt) {
                                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                        override fun addBody(requestBody: OkHttpBodyEntity) {
                                            requestBody.setPost(API.user_create_order_url)
                                            requestBody.add("productCode", bean.productCode)
                                            requestBody.add("productCategory", type)
                                            requestBody.add("modelUserCode",chatUserCode)
                                        }
                                    }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {

                                        override fun onSuccess(entityOrder: OrderCreateEntity) {
                                            entityOrder.data.benefitNum=bean.benefitNum
                                            entityOrder.data.productCategory=type
                                            PayUtils.instance.start(entityOrder,context,object : EmptySuccessCallBack {
                                                override fun OnSuccessListener() {
                                                    FirebaseEventUtils.logEvent(if (type==3)FirebaseEventTag.Chat_PP_Buysuccess.name else FirebaseEventTag.Chat_PV_Buysuccess.name)

                                                    getMemberReduce(context, chatUserCode,uid, callback, benefitCode, type, imageCode)
                                                }

                                            },chatUserCode)
                                        }
                                    })
                                }
                            })
                    }
                }
            }

        })
    }

    fun showMemberDialog(context: Context,callback:RequestSuccessCallBack){
        FirebaseEventUtils.logEvent(FirebaseEventTag.Chat_PrivateAlbum_Sub.name)
        MemberBuyDialog(
            context,
            5,
            object : MemberBuyDialog.ChangeMembershipListener {
                override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                        override fun addBody(requestBody: OkHttpBodyEntity) {
                            requestBody.setPost(API.user_create_order_url)
                            requestBody.add("productCode", bean.productCode)
                            requestBody.add("productCategory", 1)
                        }
                    }, object : SDOkHttpResoutCallBack<OrderCreateEntity?>() {

                        override fun onSuccess(entity: OrderCreateEntity?) {
                            PayUtils.instance.start(entity as OrderCreateEntity,context,object : EmptySuccessCallBack {
                                override fun OnSuccessListener() {
                                    FirebaseEventUtils.logEvent(FirebaseEventTag.Chat_PrivateAlbum_Subsuccess.name)
                                    BaseConfig.getInstance.setBoolean(SpName.isMember, true)

                                    callback.onSuccess()
                                }

                            })
                        }
                    })
                }

                override fun closeListener(refreshTime: Long) {

                }
            })
    }

    fun getUserInfo(callback:RequestUserInfoCallBack){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_info_url)
            }
        },object : SDOkHttpResoutCallBack<UserProfileEntity>() {
            override fun onSuccess(entity: UserProfileEntity) {
                callback.onSuccess(entity)
            }
        })
    }

    fun getEvaluateCheck(){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(API.evaluate_check_url)
                requestBody.add("userCode",BaseConfig.getInstance.getString(SpName.userCode,""))
            }
        },object : SDOkHttpResoutCallBack<EvaluateCheckEntity>() {
            override fun onSuccess(entity: EvaluateCheckEntity) {
                BaseConfig.getInstance.setString(SpName.evaluateCheck,Gson().toJson(entity.data))
            }
        })
    }
    fun getEvaluateGoShop(){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(API.evaluate_go_shop_url)
                requestBody.add("userCode",BaseConfig.getInstance.getString(SpName.userCode,""))
            }
        },object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
                BaseConfig.getInstance.setBoolean(SpName.goEvaluate,true)
            }
        })
    }
    fun getEvaluateBackApp(){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(API.evaluate_back_app_url)
                requestBody.setTypeFormData()
                requestBody.add("userCode",BaseConfig.getInstance.getString(SpName.userCode,""))
            }
        },object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
                BaseConfig.getInstance.setBoolean(SpName.goEvaluate,false)
                val evaluate = BaseConfig.getInstance.getString(evaluateCheck, "")
                val evaluateCheckBean = Gson().fromJson(evaluate, EvaluateCheckBean::class.java)
                if (evaluateCheckBean != null) {
                    evaluateCheckBean.status=0
                    BaseConfig.getInstance.setString(SpName.evaluateCheck,Gson().toJson(evaluateCheckBean))

                }
            }
        })
    }

    fun getUserInfoEmpty(
        context: Context,
        callback: RequestSuccessCallBack
    ){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_info_url)
            }
        },object : SDOkHttpResoutCallBack<UserProfileEntity>() {
            override fun onSuccess(entity: UserProfileEntity) {
                if (entity.data.isMember) {
                    callback.onSuccess()
                }else{
                    MemberBuyDialog(
                        context,
                        5,
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
                                        PayUtils.instance.start(entity,context,object : EmptySuccessCallBack {
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
        })
    }

    fun getPrivateAlbum(callBack: RequestPrivateAlbumCallBack){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_user_albums_url)
                requestBody.add("type", 3)
            }

        }, object : SDOkHttpResoutCallBack<PrivateAlbumEntity>() {
            override fun onSuccess(entity: PrivateAlbumEntity) {
                callBack.onSuccess(entity)
            }
        })
    }

    fun getTrySendMessage(toUserId:String,context:Context,callback:RequestCallBack){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_send_message_url)
                requestBody.add("toUserId", toUserId)
            }

        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
                callback.onSuccess()
            }

            override fun onFailure(code: Int, msg: String) {
                super.onFailure(code, msg)
                when(code){
                    3004,3007,3008,3009->{
                        callback.onFailure(code,msg)
                        MemberBuyDialog(
                            context,
                            0,
                            object : MemberBuyDialog.ChangeMembershipListener {
                                override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                        override fun addBody(requestBody: OkHttpBodyEntity) {
                                            requestBody.setPost(API.user_create_order_url)
                                            requestBody.add("productCode", bean.productCode)
                                            requestBody.add("productCategory", 1)
                                            requestBody.add("modelUserCode",toUserId)

                                        }
                                    }, object : SDOkHttpResoutCallBack<OrderCreateEntity?>() {

                                        override fun onSuccess(entity: OrderCreateEntity?) {
                                            PayUtils.instance.start(entity as OrderCreateEntity,context,object : EmptySuccessCallBack {
                                                override fun OnSuccessListener() {
                                                    BaseConfig.getInstance.setBoolean(SpName.isMember, true)
                                                }

                                            },toUserId)
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

    fun addAlbums(path:String,videoLength:Long){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_user_albums_add_url)
                requestBody.add("imageUrl", path)
                requestBody.add("type", 3)
                requestBody.add("videoLength", videoLength)

            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }

    fun requestOpenChatData(targetId:String,type:Int,callback:SDOkHttpResoutCallBack<OpenChatEntity>){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_chat_open_url)
                requestBody.add("userCodeFriend", targetId)
                requestBody.add("type", type)
                if (type==2) {
                    requestBody.add("sendMessagesNumber", BaseConfig.getInstance.getInt("${targetId}_sendMessagesNumber", 0))
                }

            }
        }, object : SDOkHttpResoutCallBack<OpenChatEntity>() {
            override fun onSuccess(entity: OpenChatEntity) {
                ChatMessageNumber.userMessageNum = entity.data.userMessageNum
                callback.onSuccess(entity)
            }

            override fun onFailure(code: Int, msg: String) {
                callback.onFailure(code,msg)
            }
        })
    }

    fun chatOpen(targetId:String,type:Int,callback:RequestTurnOnsSuccessCallBack){
        requestOpenChatData(targetId,type,object :SDOkHttpResoutCallBack<OpenChatEntity>(){
            override fun onSuccess(entity: OpenChatEntity) {
                if (type==1) {
                    sayHiUrl=entity.data.sayHiUrl?:""
                    BaseConfig.getInstance.setInt(SpName.conversationList+targetId, entity.data.onlineStatus)
                    BaseConfig.getInstance.setInt(SpName.conversationFlashList+targetId, entity.data.flashchatFlag)

                    BaseConfig.getInstance.setInt("${targetId}_sendMessagesNumber", 0)
                    BaseConfig.getInstance.setInt("${targetId}_messagesNumber", entity.data.messagesNumber)
                    BaseConfig.getInstance.setBoolean("${targetId}_member", entity.data.member)
                    canSendImageNumLiveData.postValue(entity.data.canSendImageNum?:0)
                    canSendUnlockImageNumLiveData.postValue(entity.data.canSendUnlockImageNum?:0)
                    try {
                        val userInfo = RongUserInfoManager.getInstance().getUserInfo(targetId)
                        if (userInfo != null) {
                            if (userInfo.extra != null && userInfo.extra!="") {
                                if (JsonUtils.isJSON(userInfo.extra)) {
                                    val extra = JSONObject(userInfo.extra)
                                    extra.put("onlineStatus", entity.data.onlineStatus)
                                }else{
                                    val extra = JsonObject()
                                    extra.addProperty("onlineStatus", entity.data.onlineStatus)
                                    userInfo.extra = extra.toString()
                                }
                            } else {
                                val extra = JsonObject()
                                extra.addProperty("onlineStatus", entity.data.onlineStatus)
                                userInfo.extra = extra.toString()
                            }
                        }
                        RongUserInfoManager.getInstance().refreshUserInfoCache(userInfo)
                    }catch (e:java.lang.Exception){
                    }
                    callback.onSuccess(entity.data)
                }else{
                    if (entity.data.effectiveChat) {
                        val userInfo = RongUserInfoManager.getInstance().getUserInfo(targetId)
                        SDEventManager.post(userInfo.name, EnumEventTag.SHOW_NOTIFICATION_DIALOG.ordinal)
                    }
                }
            }

        })
    }
    fun getGreetingMessage(callback: RequestGreetingMessageCallBack){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(API.greeting_message_url)
            }
        }, object : SDOkHttpResoutCallBack<GreetingMessageEntity>() {
            override fun onSuccess(entity: GreetingMessageEntity) {
                callback.onSuccess(entity)
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }

    fun getGreetingFlirting(callback: RequestStringCallBack){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(API.greeting_flirting_url)
            }
        }, object : SDOkHttpResoutCallBack<GreetingFlirtingEntity>() {
            override fun onSuccess(entity: GreetingFlirtingEntity) {
                callback.onSuccess(entity.data)
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }
    interface RequestCallBack{
        fun onSuccess()
        fun onFailure(code: Int, msg: String)
    }
    interface RequestSuccessCallBack{
        fun onSuccess()
    }
    interface RequestStringCallBack{
        fun onSuccess(text:String)
    }

    interface RequestTurnOnsSuccessCallBack{
        fun onSuccess(entity: OpenChatEntity.Data)
    }
    interface RequestUserInfoCallBack{
        fun onSuccess(entity:UserProfileEntity)
    }
    interface RequestPrivateAlbumCallBack{
        fun onSuccess(entity:PrivateAlbumEntity)
    }
    interface RequestGreetingMessageCallBack{
        fun onSuccess(entity:GreetingMessageEntity)
    }


    fun showBuyMember(context: Context, type: Int, targetId: String){
        MemberBuyDialog(
            context,
            type,
            object : MemberBuyDialog.ChangeMembershipListener {
                override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                        override fun addBody(requestBody: OkHttpBodyEntity) {
                            requestBody.setPost(API.user_create_order_url)
                            requestBody.add("productCode", bean.productCode)
                            requestBody.add("productCategory", 1)
                            requestBody.add("modelUserCode",targetId)
                        }
                    }, object : SDOkHttpResoutCallBack<OrderCreateEntity?>() {

                        override fun onSuccess(entity: OrderCreateEntity?) {
                            PayUtils.instance.start(entity as OrderCreateEntity,context,object : EmptySuccessCallBack {
                                override fun OnSuccessListener() {
                                    BaseConfig.getInstance.setBoolean(SpName.isMember, true)
                                    BaseConfig.getInstance.setBoolean("${targetId}_member", true)
                                }

                            },targetId)
                        }
                    })
                }

                override fun closeListener(refreshTime: Long) {

                }
            })
    }


    fun commonNotify(type:Int,content:String){
        try {
            if (BaseConfig.getInstance.getBoolean(SpName.openNotify,true)) {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(API.user_common_notify_url)
                        requestBody.add("code", type)
                        requestBody.add("content", content)
                        requestBody.add(
                            "userCode",
                            BaseConfig.getInstance.getString("userCode", "")
                        )
                    }
                }, object : SDOkHttpResoutCallBack<BaseEntity>(false) {
                    override fun onSuccess(entity: BaseEntity) {
                    }
                })
            }
        }catch (e:Exception){

        }

    }

    @JvmStatic
    fun requestChatTipsData(
        count: Int,
        sdOkHttpResoutCallBack: SDOkHttpResoutCallBack<BaseResutlEntity<TipsPopEntity>>
    ) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(API.im_notice_getIMPop)
                requestBody.add("userMessageNum",count)
            }
        }, object : SDOkHttpResoutCallBack<BaseResutlEntity<TipsPopEntity>>(false) {
            override fun onSuccess(entity: BaseResutlEntity<TipsPopEntity>) {
                sdOkHttpResoutCallBack.onSuccess(entity)
            }
        })
    }
    @JvmStatic
    fun requestTipsData(
        count: Int,
        sdOkHttpResoutCallBack: SDOkHttpResoutCallBack<BaseResutlEntity<TipsEntity>>
    ) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(API.im_notice_getTips)
                requestBody.add("conversationType",count)
            }
        }, object : SDOkHttpResoutCallBack<BaseResutlEntity<TipsEntity>>(false) {
            override fun onSuccess(entity: BaseResutlEntity<TipsEntity>) {
                sdOkHttpResoutCallBack.onSuccess(entity)
            }
        })
    }

    fun getServiceInfo(callback:(ConfigsEntity)->Unit){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_config_url)
                requestBody.add("code", 1)
            }
        }, object : SDOkHttpResoutCallBack<ConfigsEntity>(false) {
            override fun onSuccess(entity: ConfigsEntity) {
                callback.invoke(entity)
            }
        })
    }
}