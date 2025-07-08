package com.crush.ui

import UserUtil
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.crush.App
import com.crush.BuildConfig
import com.crush.Constant
import com.crush.R
import com.crush.bean.WLMListBean
import com.crush.dialog.NotificationPermissionDialog
import com.crush.dot.AFDotLogUtil
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.entity.BaseEntity
import com.crush.entity.ConfigsEntity
import com.crush.entity.ConversationListEntity
import com.crush.entity.IMTokenGetEntity
import com.crush.entity.UserBaseInfoEntity
import com.crush.rongyun.RongConfigUtil
import com.crush.ui.index.helper.IndexHelper
import com.crush.ui.index.helper.NewBieHelper
import com.crush.util.HttpRequest
import com.crush.util.SystemAppCallUtil
import com.crush.view.LayoutNewBieMiniView
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.OkHttpResoutCallBack
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.MVPBaseActivity
import com.crush.ui.chat.WLMFragment
import com.crush.ui.index.IndexFragment
import com.crush.ui.my.MyFragment
import com.crush.util.CollectionUtils
import com.crush.util.PermissionUtils
import com.crush.view.NavigationLayout
import com.crush.view.ScrollableCustomViewPager
import com.crush.view.ViewChatHeaderWlm
import com.crush.view.ViewMinDown
import com.custom.base.util.ToastUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.sunday.eventbus.SDBaseEvent
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.RongIM
import io.rong.imkit.SpName
import io.rong.imkit.config.BaseDataProcessor
import io.rong.imkit.config.RongConfigCenter
import io.rong.imkit.conversationlist.ConversationListFragment
import io.rong.imkit.entity.DiscountInfoEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.manager.UnReadMessageManager
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.userinfo.RongUserInfoManager
import io.rong.imkit.userinfo.UserDataProvider
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.RongCoreClient
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask


/**
 * 作者：
 * 时间：
 * 描述：主页
 */

class HomeActivity : MVPBaseActivity<HomeContract.View, HomePresenter>(), HomeContract.View {

    private var countDownTimer: CountDownTimer? = null
    private val Permission_Request = 10001

    override fun bindLayout(): Int {
        return R.layout.act_home
    }

    var indexFragment: IndexFragment? = null
    var myFragment: MyFragment? = null
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mPresenter?.getPosition()?.let { outState.putInt("selectPosition", it) }
    }

    override fun initView() {
        if (BaseConfig.getInstance.getString(SpName.token, "") != "") {
            PayUtils.instance
        }

        viewPager.offscreenPageLimit = 4
        viewPager.isScrollable = false
        val conversationListFragment = ConversationListFragment()
        indexFragment = IndexFragment()
        myFragment = MyFragment()
        viewPager.adapter = object :
            FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount(): Int = 4
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> indexFragment ?: IndexFragment().also { indexFragment = it }
                    1 -> WLMFragment()
                    2 -> conversationListFragment
                    else -> myFragment ?: MyFragment().also { myFragment = it }
                }
            }
        }
        IMConfig(conversationListFragment)

        navigation.attach(viewPager)
        navigation.setAvatar(BaseConfig.getInstance.getString(SpName.avatarUrl, ""))
        NewBieHelper.showNewBie(layoutNewBieMiniViews, 0, false)
        navigation.setListener(object : NavigationLayout.Callback {
            override fun onItemClick(position: Int): Boolean {
                viewPager.setCurrentItem(position, false)
                BaseConfig.getInstance.setInt(SpName.homeIndex, position)
                if (position == 2) {
                    showNotificationDialog("")
                }
                if (position == 1) {
                    SDEventManager.post(EnumEventTag.WLM_INIT_REFRESH.ordinal)
                }

                return false
            }

            override fun onItemClickAgain(position: Int) {
            }
        })


        //获取FCM的push token
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            val token = task.result
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_push_token_url)
                    requestBody.add("sourceType", 1)
                    requestBody.add("token", token)
                }

            }, object : SDOkHttpResoutCallBack<BaseEntity>(false) {
                override fun onSuccess(entity: BaseEntity) {

                }

            })
        })
        OkHttpManager.instance.setHttpCodeListener(object : OkHttpResoutCallBack.OnHttpCode {
            override fun code(code: Int, msg: String, url: String) {
                when (code) {
                    700 -> {
                        Toast.makeText(
                            mActivity,
                            mActivity.getString(R.string.account_logout_tip),
                            Toast.LENGTH_SHORT
                        ).show()
                        UserUtil.out(mActivity)
                    }
                }
            }
        })

        RouteUtils.registerActivity(
            RouteUtils.RongActivityType.ConversationListActivity,
            HomeActivity::class.java
        )

        this.mPresenter?.viewChatHeaderWlm?.getBanner()?.addBannerLifecycleObserver(this)
        try {
            startPolling()
        }catch (e:Exception){

        }
    }
    private fun startPolling() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                performPollingTask()
            }
        }, 150000, 150000)
    }
    private fun performPollingTask() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.heart_beat_url)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(entity: BaseEntity) {
            }
        })
    }

    private var imgNotificationClose: ImageView? = null
    private var emptyView: View? = null
    private var notificationContainer: ConstraintLayout? = null
    var viewChatHeaderWlm: ViewChatHeaderWlm? = null
    private var viewChatDowm: ViewMinDown? = null
    private var serverCount = 0
    var member = false
    var trafficSource = BaseConfig.getInstance.getInt(SpName.trafficSource, 0)
    private var loRingTheBell: LottieAnimationView? = null
    private var serverUserCode: String = ""
    private var timer: Timer? = null

    /**
     * 融云配置
     */
    fun IMConfig(conversationListFragment: ConversationListFragment) {
        val inflate = View.inflate(mActivity, R.layout.layout_chat_header, null)
        conversationListFragment.addHeaderView(inflate)
        emptyView = View.inflate(mActivity, R.layout.layout_chat_empty, null)
        val insideContainer = emptyView?.findViewById<LinearLayout>(R.id.inside_container)
        conversationListFragment.setEmptyView(emptyView)
        imgNotificationClose = inflate?.findViewById(R.id.img_notification_close)
        viewChatHeaderWlm = inflate?.findViewById(R.id.viewChatHeaderWlm)
        viewChatDowm = inflate?.findViewById(R.id.view_chat_dowm)
        notificationContainer = inflate?.findViewById(R.id.notification_container)
        loRingTheBell = inflate?.findViewById(R.id.lo_ring_the_bell)

        viewChatHeaderWlm?.getBanner()?.addBannerLifecycleObserver(this)
        mPresenter?.wlmLiveData?.observe(this) { list ->
            viewChatHeaderWlm?.setData(member, list)
        }
        //系统客服信息请求
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_config_url)
                requestBody.add("code", 1)
            }
        }, object : SDOkHttpResoutCallBack<ConfigsEntity>(false) {
            override fun onSuccess(entity: ConfigsEntity) {
                if (entity.data.isNotEmpty()) {
                    serverUserCode = entity.data[0].userCode.toString()
                    BaseConfig.getInstance.setString(SpName.serverUserCode, serverUserCode)
                    RongIM.getInstance().refreshUserInfoCache(
                        UserInfo(
                            entity.data[0].userCode,
                            entity.data[0].name,
                            Uri.parse(entity.data[0].iocn)
                        )
                    )
                    //加白用户不显示系统消息
                    if (trafficSource == 1) {
                        RongConfigCenter.conversationListConfig()
                            .setDataProcessor(object : BaseDataProcessor<Conversation?>() {
                                override fun filtered(data: List<Conversation?>): List<Conversation?> {
                                    val newList: ArrayList<Conversation?> = arrayListOf()
                                    for (item in data) {
                                        val targetId = item?.targetId
                                        if (serverUserCode != "" && serverUserCode == targetId) {
                                            serverCount = item.unreadMessageCount
                                        } else {
                                            newList.add(item)
                                        }
                                    }
                                    //过滤后的数据
                                    return super.filtered(newList)
                                }
                            })
                    }

                }
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PermissionUtils.lacksPermission(
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            DotLogUtil.setEventName(DotLogEventName.NOTIFICATION_PAGE_IN_CHAT_LIST)
                .commit(mActivity)
        }

        //通知权限栏是否展示
        notificationContainer?.visibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PermissionUtils.lacksPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) && BaseConfig.getInstance.getBoolean(SpName.firstShowNotification, false)
            ) View.VISIBLE else View.GONE
        insideContainer?.isVisible = notificationContainer?.isVisible != true

        //通知权限请求
        notificationContainer?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionUtils.requestPermission(mActivity,
                    {
                        loRingTheBell?.cancelAnimation()
                        notificationContainer?.visibility = View.GONE
//                        HttpRequest.commonNotify(503, "502")
                        DotLogUtil.setEventName(DotLogEventName.NOTIFICATION_PAGE_GRANTED)
                            .setRemark("502").commit(mActivity)
                    }, {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", mActivity.packageName, null)
                        intent.data = uri
                        mActivity.startActivityForResult(intent, Permission_Request)
//                        HttpRequest.commonNotify(503, "502")
                        DotLogUtil.setEventName(DotLogEventName.NOTIFICATION_PAGE_GRANTED)
                            .setRemark("502").commit(mActivity)
                    }, Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }

        //关闭通知权限请求栏
        imgNotificationClose?.setOnClickListener {
            loRingTheBell?.cancelAnimation()
            notificationContainer?.visibility = View.GONE
            insideContainer?.visibility = View.VISIBLE
        }

        setRongInfo()
    }

    private fun setRongInfo() {
        //设置用户头像/昵称
        RongUserInfoManager.getInstance()
            .setUserInfoProvider(object : UserDataProvider.UserInfoProvider {
                override fun getUserInfo(userId: String?): UserInfo? {
                    var newUserInfo: UserInfo? = null
                    userId?.let {
                        if (newUserInfo == null) {
                            lifecycleScope.launch {
                                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                    override fun addBody(requestBody: OkHttpBodyEntity) {
                                        requestBody.setPost(Constant.user_user_base_url)
                                        requestBody.add("userCode", userId)
                                    }
                                }, object : SDOkHttpResoutCallBack<UserBaseInfoEntity>(false) {
                                    override fun onSuccess(entity: UserBaseInfoEntity) {
                                        newUserInfo = UserInfo(
                                            entity.data.userCode,
                                            entity.data.nickName,
                                            Uri.parse(entity.data.avatarUrl)
                                        )
                                        val extra = JsonObject()
                                        extra.addProperty(
                                            "flashchatFlag",
                                            entity.data.flashchatFlag
                                        )
                                        extra.addProperty("onlineStatus", entity.data.online)
                                        newUserInfo?.extra = extra.toString()
                                        RongUserInfoManager.getInstance()
                                            .refreshUserInfoCache(newUserInfo)
                                    }
                                })
                            }

                        }
                    }
                    return newUserInfo
                }
            }, true)
        //设置未读消息
        UnReadMessageManager.getInstance()
            .addObserver(arrayOf(Conversation.ConversationType.PRIVATE),
                object : UnReadMessageManager.IUnReadMessageObserver {
                    override fun onCountChanged(count: Int) {
                        if (trafficSource == 1) {
                            RongIMClient.getInstance()
                                .getUnreadCount(Conversation.ConversationType.PRIVATE,
                                    serverUserCode,
                                    object : RongIMClient.ResultCallback<Int?>() {
                                        override fun onSuccess(t: Int?) {
                                            val showCount = count - (t ?: 0)
                                            navigation.setUnreadNum(showCount)
                                        }

                                        override fun onError(e: RongIMClient.ErrorCode?) {
                                            navigation.setUnreadNum(count)
                                        }
                                    })
                        } else {
                            navigation.setUnreadNum(count)
                        }
                    }

                })


        //获取用户列表的在线状态与fc表示
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_conversation_list_url)
            }
        }, object : SDOkHttpResoutCallBack<ConversationListEntity>(false) {
            override fun onSuccess(entity: ConversationListEntity) {
                if (entity.data != null) {
                    if (CollectionUtils.isNotEmpty(entity.data)) {
                        repeat(entity.data.size) {
                            val userInfo = RongUserInfoManager.getInstance()
                                .getUserInfo(entity.data[it].userCodeFriend)
                            if (userInfo != null) {
                                val extra = JsonObject()
                                extra.addProperty(
                                    "flashchatFlag",
                                    entity.data[it].flashchatFlag
                                )
                                extra.addProperty("onlineStatus", entity.data[it].onlineStatus)
                                userInfo.extra = extra.toString()
                                RongUserInfoManager.getInstance().refreshUserInfoCache(userInfo)
                            }
                        }
                    }
                }
            }
        })
    }


    fun showDiscountDownTime(discountInfoEntity: DiscountInfoEntity) {
        if (isFinishing) {
            return
        }

        indexFragment?.let {  fragment ->
            //首页
            if (fragment.isAdded && !fragment.isDetached) {
                val indexFrag = indexFragment as IndexFragment
                indexFrag.viewDown.setDownTime(mActivity, discountInfoEntity.popOverTime)
                IndexHelper.setOnClick(mActivity, indexFrag.viewDown)
            }
        }

        myFragment?.let { fragment ->
            if (fragment.isAdded && !fragment.isDetached) {
                val myFrag = myFragment as MyFragment
                myFrag.discountDownTime()
            }
        }

        //wlm倒计时在本身fragment
        SDEventManager.post(
            discountInfoEntity.popOverTime,
            EnumEventTag.WLM_DISCOUNT_DOWN_SHOW.ordinal
        )
        //聊天列表
        viewChatDowm?.setDownTime(mActivity, discountInfoEntity.popOverTime)
        viewChatDowm?.let { IndexHelper.setOnClick(mActivity, it) }
    }

    fun closeDiscountDownTime() {
        //首页
        if (indexFragment != null && !(indexFragment?.isDetached)!!) {
            val indexFrag = indexFragment as IndexFragment
            indexFrag.viewDown.setDownTime(mActivity, 0)
        }
        SDEventManager.post(EnumEventTag.DISCOUNT_COUNTDOWN_END.ordinal)
        SDEventManager.post(0, EnumEventTag.WLM_DISCOUNT_DOWN_SHOW.ordinal)
        //聊天列表
        viewChatDowm?.setDownTime(mActivity, 0)
    }

    fun showNotificationDialog(nickName: String) {
        //加白不显示通知权限弹窗
        if (trafficSource == 1) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PermissionUtils.lacksPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) && !BaseConfig.getInstance.getBoolean(SpName.firstShowNotification + nickName, false)
        ) {
            NotificationPermissionDialog(
                mActivity,
                nickName,
                object : NotificationPermissionDialog.OnListener {
                    override fun onGrantedListener() {
                        loRingTheBell?.cancelAnimation()
                        notificationContainer?.visibility = View.GONE
                    }

                    override fun onDeniedListener() {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", mActivity.packageName, null)
                        intent.data = uri
                        mActivity.startActivityForResult(intent, Permission_Request)
                    }

                }).showPopupWindow()
        }
    }

    override fun setFullScreen(): Boolean {
        return true
    }

    override fun onEventMainThread(event: SDBaseEvent) {
        when (EnumEventTag.valueOf(event.tagInt)) {
            EnumEventTag.INDEX_TO_INDEX -> {
                navigation.select(0)
            }

            EnumEventTag.INDEX_TO_WLM -> {
                navigation.select(1)
            }

            EnumEventTag.BUY_MEMBER_CHAT -> {
                navigation.select(1)
            }

            EnumEventTag.REFRESH_WLM_LIKE_SIZE -> {
                mPresenter?.setWLMLikeSize(
                    if (event.data != null) event.data.toString().toInt() else 0
                )
            }

            EnumEventTag.SHOW_NOTIFICATION_DIALOG -> {
                showNotificationDialog(if (event.data != null) event.data.toString() else "")
            }

            EnumEventTag.WLM_REFRESH -> {
                mPresenter?.getWLMData()
            }

            EnumEventTag.SEND_VIP_NOTICE -> {
                HttpRequest.sendVipNotice(event.data.toString())
            }

            EnumEventTag.PAY_RESULT -> {
                try {
                    closeDiscountDownTime()
                    if (event.data.toString() != "") {
                        val orderCreateEntity = event.data as OrderCreateEntity
                        AFDotLogUtil().addPurchaseEvent(
                            this,
                            orderCreateEntity.data.payableAmount,
                            orderCreateEntity.data.productCode,
                            orderCreateEntity.data.orderNo,
                            orderCreateEntity.data.currencyType,
                            orderCreateEntity.data.pushAfSwitch,
                            orderCreateEntity.data.pushFBSwitch,
                        )
                    }
                } catch (e: Exception) {

                }
            }

            EnumEventTag.CLOSE_MEMBER_POP -> {
                //因为私信里面显示促销弹窗，现在显示在外面而不是里面，因为传的是 this@HomeActivity
                App.applicationContext()?.apply {
                    IndexHelper.showDiscountPop(
                        trigger = true,
                        activity = this,
                        triggerType = event.data.toString().toInt(),
                        shwDown = {
                            showDiscountDownTime(it)
                        })
                }

            }

            EnumEventTag.CLOSE_DISCOUNT_POP -> {
                IndexHelper.getDiscountPopData {
                    showDiscountDownTime(it)
                }
            }

            EnumEventTag.REFRESH_ME_BOTTOM_AVATAR -> {
                mPresenter?.refreshMeBottomAvatar(event.data.toString())
            }

            EnumEventTag.REFRESH_CHAT_HEAD_WLM -> {
                mPresenter?.refreshChatHeadWlm(event.data as ArrayList<WLMListBean>)
            }

            EnumEventTag.VIEW_CHAT_WLM_START -> {
                mPresenter?.viewChatStart()
            }

            EnumEventTag.VIEW_CHAT_WLM_STOP -> {
                mPresenter?.viewChatStop()
            }

            EnumEventTag.EMAIL_EDIT -> {
                val emailTitle = String.format(
                    this.getString(R.string.send_email_default_title),
                    BaseConfig.getInstance.getString(SpName.userCode, ""), BuildConfig.VERSION_NAME
                )
                SystemAppCallUtil().sendEmail(
                    this,
                    emailTitle,
                    getString(R.string.send_email_default_txt)
                )
            }

            EnumEventTag.PAY_FAILED_POP_SHOW -> {
                val orderCreateEntity = event.data as OrderCreateEntity
                //失败关闭新手礼包弹窗 purchaseTypeExt跟克朝那边约定的
                if (orderCreateEntity.data.purchaseTypeExt == 2) {
                    NewBieHelper.dismissDialog(layoutNewBieMiniViews)
                }
//                if(SDActivityManager.instance.lastActivity!= null) {
//                    PayFailedTipDialog(
//                        SDActivityManager.instance.lastActivity,
//                        orderCreateEntity
//                    ).showPopupWindow()
//                }
            }

            EnumEventTag.LOWER_LIMIT_DEBIT_SHOW -> {
                val countDownTime = event.data.toString().toLong()
                if (countDownTime > 0) {
                    countDownTimer = object : CountDownTimer(countDownTime * 1000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                        }

                        override fun onFinish() {
                            mPresenter?.lowerLimitDebitDialog()
                        }
                    }
                    countDownTimer?.start()
                } else {
                    mPresenter?.lowerLimitDebitDialog()
                }
            }


            else -> {

            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (RongCoreClient.getInstance().currentConnectionStatus.ordinal == RongIMClient.ConnectionStatusListener.ConnectionStatus.UNCONNECTED.ordinal) {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.im_token_Url)
                }
            }, object : SDOkHttpResoutCallBack<IMTokenGetEntity>() {
                override fun onSuccess(parms: IMTokenGetEntity) {
                    RongConfigUtil.reConnectIM(parms.data.token, mActivity)
                }

                override fun onFailure(code: Int, msg: String) {
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        onBackPressedDispatcher.addCallback(callback)

        mPresenter?.googleEvaluate()

        if (mPresenter?.googleEvaluate == true) {
            mPresenter?.googleEvaluate = false
            mPresenter?.googleEvaluateBackApp()
        }
    }

    private var mExitTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            ToastUtil.toast("Press exit again!")
        } else {
            finish()
        }
        mExitTime = System.currentTimeMillis()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestroy()
        countDownTimer?.cancel()
        if (timer != null) {
            timer?.cancel()
        }

    }

    override val fragmentId: FrameLayout
        get() = findViewById(R.id.home_frame)
    override val bottomContainer: LinearLayout
        get() = findViewById(R.id.bottom_container)
    override val layoutNewBieMiniViews: LayoutNewBieMiniView
        get() = findViewById(R.id.layoutNewBieMiniView)
    override val navigation: NavigationLayout
        get() = findViewById(R.id.navigation)
    override val viewPager: ScrollableCustomViewPager
        get() = findViewById(R.id.viewPager)

}