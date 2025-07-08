package com.crush.ui

import android.Manifest
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crush.App
import com.crush.BuildConfig
import com.crush.Constant
import com.crush.adapter.IMMatchedAdapter
import com.crush.bean.WLMListBean
import com.crush.dialog.AnnouncementDialog
import com.crush.dialog.GoogleEvaluateDialog
import com.crush.dialog.LowerLimitDebitPopup
import com.crush.dialog.UpdateApkDialog
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.entity.AnnouncementEntity
import com.crush.entity.ConfigsPrivateEntity
import com.crush.entity.GoogleReviewEntity
import com.crush.entity.IMMatchEntity
import com.crush.entity.LowerLimitDebitEntity
import com.crush.entity.NewComerGifEntity
import com.crush.entity.TrafficEntity
import com.crush.entity.UpdateApkEntity
import com.crush.entity.WhoLikeMeEntity
import com.crush.mvp.BasePresenterImpl
import com.crush.socket.event.EventFactory
import com.crush.socket.event.UnifyEvent
import com.crush.socket.netty.NettyClient
import com.crush.ui.index.helper.NewBieHelper
import com.crush.util.DateUtils
import com.crush.util.HandlerUtils
import com.crush.util.PermissionUtils
import com.crush.util.SystemUtils
import com.crush.view.ViewChatHeaderWlm
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.entity.PageModel
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.util.SystemUtil
import com.google.firebase.installations.FirebaseInstallations
import com.google.gson.JsonObject
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.RongIM
import io.rong.imkit.SpName
import io.rong.imkit.entity.BaseEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.userinfo.RongUserInfoManager
import io.rong.imkit.utils.JsonUtils
import io.rong.imkit.utils.RongUtils
import io.rong.imlib.model.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import razerdp.basepopup.BasePopupWindow
import java.util.Calendar
import java.util.Date
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors


/**
 * 作者：
 * 时间：
 * 描述：主页
 */
class HomePresenter : BasePresenterImpl<HomeContract.View>(), HomeContract.Presenter,
    HandlerUtils.OnReceiveMessageListener {

    private lateinit var imMatchedAdapter: IMMatchedAdapter
    var viewChatHeaderWlm: ViewChatHeaderWlm? = null
    private var index = 0
    var wlmLiveData = MutableLiveData<ArrayList<WLMListBean>>()
    var member=false
    var googleEvaluate=false
    var handel = HandlerUtils.HandlerHolder(this)
    var trafficSource = BaseConfig.getInstance.getInt(SpName.trafficSource, 0)

    override fun init() {
        mView?.apply {
            uploadVersionInfo()
            getWLMData()
            noticeDialog()
            //私密开关
            ppSwitch()
            newcomerTrigger { config ->
                Log.i(
                    "新手礼包",
                    "开关=${config.data.newUserGiftFlag}  倒计时=${config.data.newUserGiftCountDownTimes}"
                )
                //拿到新手礼包开关
                if (config.data.newUserGiftFlag) {
                    //开关 true
                    //自然和非自然流量用户== 产品确认不要加这个逻辑
                    //NewBieHelper.handleAfStatus {
                    //if (it == 0) {
                    //再去新手礼包请求数据接口
                    //刚打开进来的时候必须要请求的 这一关少不了 因为可能用户付费过了
                    NewBieHelper.requestNewBieData {
                        NewBieHelper.startNewBieTime(
                            layoutNewBieMiniViews,
                            config.data.newUserGiftCountDownTimes,
                            it.data
                        )
                    }
                    //}
                    //}
                }
            }

            checkUpdate()

            //初始化促销弹窗buyType月会员
            BaseConfig.getInstance.setInt(SpName.buyType, 2)

            //最小化新手礼包悬浮窗点击事件
            layoutNewBieMiniViews.setOnClickListener {
                if (layoutNewBieMiniViews.isEnabled) {
                    layoutNewBieMiniViews.isEnabled = false

                    //Log.i("新手礼包", "用户点击了悬浮窗按钮")
                    //缓存下来的data
                    NewBieHelper.requestNewBieOpen {
                        //每次点击悬浮球的时候先去获取一下开关接口
                        //Log.i("新手礼包", "用户点击了悬浮窗按钮 请求了开关 ${it.data.newcomerGiftFlag}")
                        if (it.data.newUserGiftFlag) {
                            //再去请求一遍礼包数据
                            NewBieHelper.requestNewBieData { entity ->
                                NewBieHelper.showNewBieDialog(
                                    entity.data,
                                    layoutNewBieMiniViews
                                )
                            }
                        } else {
                            //「The limited-time special has ended/很遗憾，限时福利已结束」
                            showToast("The limited-time special has ended")
                            //关闭悬浮球
                            NewBieHelper.showNewBie(layoutNewBieMiniViews, -1, false)
                        }
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        layoutNewBieMiniViews.isEnabled = true
                    }, 2000)
                }
                //打点上报
                when (index) {
                    0 -> {
                        DotLogUtil.setEventName(DotLogEventName.Home_Egg_Sub)
                            .commit(mActivity)
                    }

                    1 -> {
                        DotLogUtil.setEventName(DotLogEventName.WLM_Egg_Sub)
                            .commit(mActivity)
                    }

                    2 -> {
                        DotLogUtil.setEventName(DotLogEventName.Chat_Egg_Sub)
                            .commit(mActivity)
                    }

                    3 -> {
                        DotLogUtil.setEventName(DotLogEventName.Me_Egg_Sub)
                            .commit(mActivity)
                    }
                }
            }
        }

        startSocket(if (BuildConfig.DEBUG) "ws://120.26.196.82:9002/ws" else "ws://165.154.163.79:9002/ws")
        handel.sendEmptyMessage(1)


        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        if (month == 11 && dayOfMonth < 27) {
            Handler().postDelayed({
                getMerryChristmas()
            }, 5000)
        }

        initFirebase()
    }
    fun getPosition(): Int {
        return index
    }



    /**
     * 圣诞通知请求接口，一天一次
     */
    private fun getMerryChristmas() {
        val date = DateUtils.getTime(Date())
        if (!BaseConfig.getInstance.getBoolean(date, false)) {
            if (BaseConfig.getInstance.getString(SpName.userCode, "") != "") {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setGet(Constant.merry_christmas)
                        requestBody.add(
                            "userCode",
                            BaseConfig.getInstance.getString(SpName.userCode, "")
                        )
                    }
                }, object : SDOkHttpResoutCallBack<BaseEntity>() {
                    override fun onSuccess(entity: BaseEntity) {
                        BaseConfig.getInstance.setBoolean(date, true)
                    }
                })
            }
        }
    }

    /**
     * 公告弹窗
     */
    fun noticeDialog() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.gifting_equity_msg_url)
            }
        }, object : SDOkHttpResoutCallBack<AnnouncementEntity>(false) {
            override fun onSuccess(entity: AnnouncementEntity) {
                if (entity.data != null) {
                    AnnouncementDialog(mActivity, entity).showPopupWindow()
                }
            }
        })
    }

    /**
     * 划卡下限提醒
     */
    fun lowerLimitDebitDialog() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.home_notice_url)
            }
        }, object : SDOkHttpResoutCallBack<LowerLimitDebitEntity>(false) {
            override fun onSuccess(entity: LowerLimitDebitEntity) {
                if (entity.data.showFlag) {
                    App.applicationContext()?.apply {
                        LowerLimitDebitPopup(this, entity).showPopupWindow()
                    }
                }
            }
        })
    }

    //pp开关
    private fun ppSwitch() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_config_url)
                requestBody.add("code", 3)
            }
        }, object : SDOkHttpResoutCallBack<ConfigsPrivateEntity>(false) {
            override fun onSuccess(entity: ConfigsPrivateEntity) {
                BaseConfig.getInstance.setBoolean(
                    SpName.privatePhotoShowUnableFlag,
                    entity.data.photoShowUnableFlag
                )
            }
        })
    }

    private fun newcomerTrigger(callBack: (NewComerGifEntity) -> Unit) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.newcomer_trigger_url)
            }
        }, object : SDOkHttpResoutCallBack<NewComerGifEntity>(false) {
            override fun onSuccess(entity: NewComerGifEntity) {
                callBack.invoke(entity)
            }
        })
    }

    /**
     * 上传设备信息
     */
    private fun uploadVersionInfo() {
        if (!PermissionUtils.lacksPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || !PermissionUtils.lacksPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            uploadPermission(1, 1)
            scope.launch {
                uploadGps()
            }
        } else {
            uploadPermission(0, 1)
        }
        uploadPermission(
            if (PermissionUtils.lacksPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) 0 else 1, 2
        )

    }

    private fun uploadPermission(grantFlag: Int, type: Int) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_permission_operate_url)

                requestBody.add("permissionType", type)
                requestBody.add("grantFlag", grantFlag)
            }
        }, object : SDOkHttpResoutCallBack<IMMatchEntity>(false) {
            override fun onSuccess(entity: IMMatchEntity) {

            }
        }, isShowDialog = false)
    }

    private fun uploadGps() {
        try {
            Thread {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(Constant.user_add_device_info_url)
                        SystemUtils.getGPS(mActivity)?.let { requestBody.add("gps", it) }

                        if (SystemUtils.isWifiEnabled(mActivity)) {
                            if (SystemUtils.getWifiList(mActivity).isNotEmpty())
                                requestBody.add("wifilist", SystemUtils.getWifiList(mActivity))
                        }
                        val systemInfo = JsonObject()
                        systemInfo.addProperty("brand", SystemUtil.deviceBrand)//	品牌
                        requestBody.add("systeminfo", systemInfo)
                    }
                }, object : SDOkHttpResoutCallBack<IMMatchEntity>(false) {
                    override fun onSuccess(entity: IMMatchEntity) {

                    }
                }, isShowDialog = false)
            }.start()

        } catch (e: Exception) {

        }

    }

    fun refreshMeBottomAvatar(avatarUrl: String) {
        mView?.apply {
            if (!RongUtils.isDestroy(mActivity)) {
                navigation.setAvatar(avatarUrl)
            }
            BaseConfig.getInstance.setString(SpName.avatarUrl, avatarUrl)
        }
    }

    fun refreshGps() {
        scope.launch {
            SystemUtils.getRefreshGPS(mActivity)?.let {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(Constant.user_add_device_info_url)
                        requestBody.add("gps", it)
                    }
                }, object : SDOkHttpResoutCallBack<IMMatchEntity>(false) {
                    override fun onSuccess(entity: IMMatchEntity) {

                    }
                })
            }
        }


    }

    /**
     * 检查更新
     */
    fun checkUpdate() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.user_apk_update_url)
                requestBody.add("versionCode", SystemUtils.getVersionCode(mActivity))
                requestBody.add("appCode", "chat01")
            }
        }, object : SDOkHttpResoutCallBack<UpdateApkEntity>(false) {
            override fun onSuccess(entity: UpdateApkEntity) {
                if (entity.data.updateFlag) {
                    val updateApkDialog = UpdateApkDialog(
                        mActivity,
                        entity.data.upGradationDesc,
                        entity.data.updateType
                    )
                    updateApkDialog.showPopupWindow()
                    updateApkDialog.onDismissListener = object :
                        BasePopupWindow.OnDismissListener() {
                        override fun onDismiss() {
                            if (entity.data.updateType == 1) {
                                RongIM.getInstance().logout()
                                mActivity.finish()
                            }
                        }

                    }
                }
            }
        }, isShowDialog = false)
    }

    fun refreshChatHeadWlm(wlmList: ArrayList<WLMListBean>) {
        member = BaseConfig.getInstance.getBoolean(SpName.isMember, false)
        wlmLiveData.postValue(wlmList)
    }


    fun setWLMLikeSize(size: Int) {
        mView?.apply {
            navigation.setWLMNum(size)
        }
    }

    var page = PageModel()

    fun getWLMData() {
        mView?.apply {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_wlm_list_url)
                    requestBody.addPage(page)
                }
            }, object : SDOkHttpResoutCallBack<WhoLikeMeEntity>(false) {
                override fun onSuccess(entity: WhoLikeMeEntity) {
                    navigation.setWLMNum(entity.data.wlmNum)
                    val number = if (entity.data.wlmList.size > 6) 5 else entity.data.wlmList.size
                    for (i in 0 until number) {
                        if (!RongUtils.isDestroy(mActivity)) {
                            Glide.with(mActivity)
                                .load(entity.data.wlmList[i].avatarUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .preload()
                        }
                    }
                    member = entity.data.isMember
                    wlmLiveData.postValue(entity.data.wlmList)
                }

                override fun onFinish() {

                }
            })
        }
    }

    fun startSocket(hostUrl: String) {
        Executors.newSingleThreadExecutor().execute {
            Log.e("NettyClient", "run: $hostUrl")
            // 启动并初始化
            try {
                val nettyClient: NettyClient = NettyClient.getInst()
                nettyClient.setContext(mActivity)
                nettyClient.hostUrl = hostUrl
                var tk = ""
                tk = BaseConfig.getInstance.getString(SpName.userCode, "")
                nettyClient.token = tk
                if (!nettyClient.isConnectFlag) {
                    nettyClient.startConnectServerAlways()
                }
            } catch (e: Exception) {
                Log.i("HomeFragment", "Get token exception")
            }

        }
    }


    /**
     *
     */
    private fun getSocketInfo() {
        try {
            val queue: ConcurrentLinkedQueue<UnifyEvent> = EventFactory.getQuotesInstance()
            while (!queue.isEmpty()) {
                //从队列取出一个元素 排队的人少一个
                val poll: UnifyEvent = queue.poll() ?: break
                val body: String = poll.body
                val bizCode: String = poll.bizCode
                when {
                    "S20001".equals(bizCode, ignoreCase = true) -> {
                        if (JsonUtils.isJSON(body)) {
                            val jsonObject = JSONObject(body)
                            RongIM.getInstance().refreshUserInfoCache(
                                UserInfo(
                                    jsonObject.getString("userCode"),
                                    jsonObject.getString("nickName"),
                                    Uri.parse(jsonObject.getString("avatarUrl"))
                                )
                            )
                        }
                    }

                    "S20002".equals(bizCode, ignoreCase = true) -> {//首页在线状态
                        SDEventManager.post(body, EnumEventTag.INDEX_REFRESH_DATA_ITEM.ordinal)

                    }

                    "S20003".equals(bizCode, ignoreCase = true) -> {//WLM在线状态
                        SDEventManager.post(body, EnumEventTag.WLM_REFRESH_ITEM.ordinal)
                    }

                    "S20004".equals(bizCode, ignoreCase = true) -> {//NEW MATCH在线状态
                        if (JsonUtils.isJSON(body)) {
                            val jsonObject = JSONObject(body)
                            refreshItem(
                                jsonObject.getString("userCode"),
                                jsonObject.getInt("online")
                            )
                        }
                    }

                    "S20005".equals(bizCode, ignoreCase = true) -> {//CHAT LIST在线状态和flash chat标识
                        if (JsonUtils.isJSON(body)) {
                            val jsonObject = JSONObject(body)
                            val userInfo = RongUserInfoManager.getInstance()
                                .getUserInfo(jsonObject.getString("userCode"))
                            if (jsonObject.has("flashchatFlag")) {//更新flash chat标识
                                if (jsonObject.has("flashchatFlag")) {
                                    if (userInfo != null) {
                                        if (userInfo.extra != null) {
                                            val extra = JSONObject(userInfo.extra)
                                            extra.put(
                                                "flashchatFlag",
                                                jsonObject.getInt("flashchatFlag")
                                            )
                                        } else {
                                            val extra = JsonObject()
                                            extra.addProperty(
                                                "flashchatFlag",
                                                jsonObject.getInt("flashchatFlag")
                                            )
                                            userInfo.extra = extra.toString()
                                        }
                                    }
                                }
                            }
                            if (jsonObject.has("online")) {//更新在线状态
                                if (userInfo != null) {
                                    if (userInfo.extra != null) {
                                        val extra = JSONObject(userInfo.extra)
                                        extra.put("onlineStatus", jsonObject.getInt("online"))
                                    } else {
                                        val extra = JsonObject()
                                        extra.addProperty(
                                            "onlineStatus",
                                            jsonObject.getInt("online")
                                        )
                                        userInfo.extra = extra.toString()
                                    }
                                }
                            }
                            RongUserInfoManager.getInstance().refreshUserInfoCache(userInfo)
                        }
                    }

                    "S20006".equals(bizCode, ignoreCase = true) -> {
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    val scope = MainScope()

    override fun handlerMessage(msg: Message?) {
        when (msg?.what) {
            1 -> {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        // 在异步线程中执行长链接操作
                        getSocketInfo()
                        handel.sendEmptyMessageDelayed(1, 5000)
                    }
                }


            }
        }
    }


    fun refreshItem(userCode: String, onlineStatus: Int) {
        imMatchedAdapter?.let {
            var position = 0
            for (i in 0 until imMatchedAdapter.dataList.size) {
                if (imMatchedAdapter.dataList[i].userCodeFriend == userCode) {
                    position = i
                    imMatchedAdapter.dataList[i].online = onlineStatus
                    break
                }
            }
            imMatchedAdapter.notifyItemChanged(position)
        }

    }

    fun viewChatStop() {
//        viewChatHeaderWlm?.let {
//            it.getBanner()?.stop()
//        }
    }

    fun viewChatStart() {
//        viewChatHeaderWlm?.let {
//            it.getBanner()?.start()
//        }
    }

    fun onDestroy() {
        scope.cancel()
//        mView?.apply {
//            viewChatHeaderWlm?.getBanner()?.destroy()
//        }
        Executors.newSingleThreadExecutor().shutdownNow()
        NewBieHelper.releaseNewBieData()
    }


    fun initFirebase(){
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val installationId = task.result
                    OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                        override fun addBody(requestBody: OkHttpBodyEntity) {
                            requestBody.setPost(Constant.firebase_init_url)
                            requestBody.add("clientId", installationId)
                            requestBody.add("androidId",Settings.Secure.getString(mActivity.contentResolver, Settings.Secure.ANDROID_ID))
                        }
                    }, object : SDOkHttpResoutCallBack<TrafficEntity>() {
                        override fun onSuccess(entity: TrafficEntity) {
                        }

                        override fun onFailure(code: Int, msg: String) {
                        }
                    })
                }
            }
        }
    }

    fun googleEvaluate(){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.review_review_pop_status_url)
            }
        }, object : SDOkHttpResoutCallBack<GoogleReviewEntity>() {
            override fun onSuccess(entity: GoogleReviewEntity) {
                if (entity.data.status == 1) {
                    if (!BaseConfig.getInstance.getBoolean("GoogleEvaluateNoRemind"+BaseConfig.getInstance.getString(SpName.token,""), false)) {
                        GoogleEvaluateDialog(mActivity,entity.data) {
                            googleEvaluate = true
                        }.showPopupWindow()
                    }
                }

            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }
    fun googleEvaluateBackApp(){
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.review_backApp_url)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }

}