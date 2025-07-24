package com.crush.ui.index

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.SimpleItemAnimator
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crush.Constant
import com.crush.R
import com.crush.adapter.CardStackAdapter
import com.crush.bean.MatchIndexBean
import com.crush.entity.MatchIndexEntity
import com.crush.entity.MatchResultEntity
import com.crush.entity.ResultDataEntity
import com.crush.ui.HomeActivity
import com.crush.ui.chat.UpSourceEnum
import com.crush.ui.index.flash.FlashChatActivity
import com.crush.ui.index.helper.IndexHelper
import com.crush.ui.index.match.MatchUserActivity
import com.crush.ui.like.ILikeActivity
import com.crush.ui.my.turnons.ChooseTurnOnsActivity
import com.crush.util.CollectionUtils
import com.crush.util.DateUtils
import com.crush.util.MemberDialogShow.memberBuyShow
import com.crush.util.SystemUtils
import com.crush.video.PrepareView
import com.crush.video.StandardVideoController
import com.crush.view.CircleImageView
import com.crush.view.HorizontalProgressBar
import com.crush.view.SnapUpCountDownTimerView
import com.crush.view.SpotDiffCallback
import com.crush.view.ViewMinDown
import com.crush.view.cardstackview.CardStackLayoutManager
import com.crush.view.cardstackview.CardStackListener
import com.crush.view.cardstackview.CardStackView
import com.crush.view.cardstackview.Direction
import com.crush.view.cardstackview.Duration
import com.crush.view.cardstackview.SwipeAnimationSetting
import com.crush.view.cardstackview.SwipeableMethod
import com.crush.view.delay.DelayClickImageView
import com.crush.view.delay.DelayClickLottieAnimationView
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.MVPBaseFragment
import com.crush.util.IntentUtil
import com.crush.util.PermissionUtil
import com.custom.base.util.ToastUtil
import com.gyf.immersionbar.ImmersionBar
import com.sunday.eventbus.SDBaseEvent
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.API
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import io.rong.imkit.dialog.CustomDialog
import io.rong.imkit.dialog.MemberBuyDialog
import io.rong.imkit.entity.MemberSubscribeEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.utils.FirebaseEventUtils
import io.rong.imkit.utils.JsonUtils
import io.rong.imkit.utils.RongUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import xyz.doikki.videoplayer.player.BaseVideoView
import xyz.doikki.videoplayer.player.VideoView
import java.util.Date


class IndexFragment : MVPBaseFragment<IndexContract.View, IndexPresenter>(), IndexContract.View,
    CardStackListener {

    private var isFlashChat: Boolean = false
    private var cardShowNum: Int = 0
    private var locationRequestArray = arrayListOf<Int>()//记录定位权限请求的次数
    private var turnOns: Boolean = true//是否编辑turnOns,默认为true，不展示
    private var newUserFlag: Boolean = true//是否新老客  true 新客  false  老客
    private var turnOnsLimit: Int = 0//turn-ons编辑引导滑动次数
    private var flashChatNum: Int = 0//flashchat剩余次数
    private var flashChatLimit: Int = -1 //flashchat展示位置
    private var locationLimit: Int = 0//定位授权连续pass次数
    val remainingBrowseBNumLive = MutableLiveData<Int>()
    val remainingWlmNumLive = MutableLiveData<Int>()

    //    private var feedbackLimit: Int = 0//用户反馈连续pass次数
    private var continuousPassNum: Int = 0//连续不喜欢的次数
    private var noMatchData: MatchIndexEntity.Data? = null
    private val manager by lazy {
        CardStackLayoutManager(
            this,
            remainingBrowseBNumLive,
            remainingWlmNumLive
        )
    }
    private val adapter by lazy {
        CardStackAdapter(
            arrayListOf(),
            turnOns,
            turnOnsLimit,
            newUserFlag
        )
    }
    private var countDownTimer: CountDownTimer? = null

    lateinit var cardStackView: CardStackView
    lateinit var userDislike: DelayClickImageView
    lateinit var userLike: DelayClickImageView
    lateinit var userLikeAnim: DelayClickLottieAnimationView
    lateinit var userChat: DelayClickImageView
    lateinit var imgLikeEmpty: View
    lateinit var listLoaderJson: LottieAnimationView
    lateinit var networkErrorLoading: LottieAnimationView
    lateinit var containerUserList: ConstraintLayout
    lateinit var networkErrorContainer: View
    lateinit var networkErrorTryAgain: LinearLayout
    lateinit var threeDaysAgoContainer: ConstraintLayout
    lateinit var threeDaysAgoUnlockNow: TextView
    lateinit var threeDaysAgoCountDown: SnapUpCountDownTimerView
    lateinit var userActionContainer: ConstraintLayout
    lateinit var listLoaderJsonAvatar: CircleImageView
    lateinit var emptyGoWlm: TextView
    lateinit var imgLikeEmptyLogo: LottieAnimationView
    lateinit var flashChatGuideOne: LottieAnimationView
    lateinit var flashChatGuideTwo: LottieAnimationView
    lateinit var flashChatGuideThree: LottieAnimationView
    lateinit var cardFlashChat: LottieAnimationView
    lateinit var videoView: VideoView
    lateinit var videoContainer: ConstraintLayout
    lateinit var imgNoMoreMatchLogo: ImageView
    lateinit var txtMoreMatchTip: TextView
    lateinit var userChatSize: TextView
    lateinit var userLikeProgress: HorizontalProgressBar
    lateinit var viewDown: ViewMinDown
    lateinit var memberUpperLimitContainer: ConstraintLayout
    lateinit var limitCountDown: SnapUpCountDownTimerView
    lateinit var txtRemainingCardReminder: TextView
    lateinit var limitViewMyFavoritesContainer: ConstraintLayout
    lateinit var limitSeeWhoLikesMeContainer: ConstraintLayout
    lateinit var imgWhiteLikeEmptyLogo: ImageView
    lateinit var emptyTitle: TextView
    lateinit var emptyTip: TextView

    //    lateinit var tenPassContainer: ConstraintLayout
//    lateinit var nonOrganicContainer: ConstraintLayout
//    lateinit var organicContainer: ConstraintLayout
    override fun bindLayout(): Int {
        return R.layout.frag_index
    }

    fun findView() {
        Activities.get().top?.let {
            cardStackView = it.findViewById(R.id.card_stack_view)
            userDislike = it.findViewById(R.id.user_dislike)
            userLike = it.findViewById(R.id.user_like)
            userLikeAnim = it.findViewById(R.id.user_like_anim)
            userChat = it.findViewById(R.id.user_chat)
            imgLikeEmpty = it.findViewById(R.id.img_like_empty)
            listLoaderJson = it.findViewById(R.id.list_loader_json)
            networkErrorLoading = it.findViewById(R.id.network_error_loading)
            containerUserList = it.findViewById(R.id.container_user_list)
            networkErrorContainer = it.findViewById(R.id.network_error_container)
            networkErrorTryAgain = it.findViewById(R.id.network_error_try_again)
            threeDaysAgoContainer = it.findViewById(R.id.three_days_ago_container)
            threeDaysAgoUnlockNow = it.findViewById(R.id.three_days_ago_unlock_now)
            threeDaysAgoCountDown = it.findViewById(R.id.three_days_ago_count_down)
            userActionContainer = it.findViewById(R.id.user_action_container)
            listLoaderJsonAvatar = it.findViewById(R.id.list_loader_json_avatar)
            emptyGoWlm = it.findViewById(R.id.empty_go_wlm)
            imgLikeEmptyLogo = it.findViewById(R.id.img_like_empty_logo)
            imgWhiteLikeEmptyLogo = it.findViewById(R.id.img_white_like_empty_logo)
            emptyTitle = it.findViewById(R.id.empty_title)
            emptyTip = it.findViewById(R.id.empty_tip)
            videoView = it.findViewById(R.id.video_view)
            imgNoMoreMatchLogo = it.findViewById(R.id.img_no_more_match_logo)
            videoContainer = it.findViewById(R.id.video_container)
            txtMoreMatchTip = it.findViewById(R.id.txt_more_match_tip)
            userLikeProgress = it.findViewById(R.id.user_like_progress)
            viewDown = it.findViewById(R.id.view_down)
            flashChatGuideOne = it.findViewById(R.id.flash_chat_guide_one)
            flashChatGuideTwo = it.findViewById(R.id.flash_chat_guide_two)
            flashChatGuideThree = it.findViewById(R.id.flash_chat_guide_three)
            cardFlashChat = it.findViewById(R.id.card_flash_chat)
            userChatSize = it.findViewById(R.id.user_chat_size)
            memberUpperLimitContainer = it.findViewById(R.id.member_upper_limit_container)
            limitCountDown = it.findViewById(R.id.limit_count_down)
            txtRemainingCardReminder = it.findViewById(R.id.txt_remaining_card_reminder)
            limitViewMyFavoritesContainer =
                it.findViewById(R.id.limit_view_my_favorites_container)
            limitSeeWhoLikesMeContainer =
                it.findViewById(R.id.limit_see_who_likes_me_container)
//        tenPassContainer = it.findViewById(R.id.ten_pass_container)
//        nonOrganicContainer = it.findViewById(R.id.non_organic_container)
//        organicContainer = it.findViewById(R.id.organic_container)
        }

    }

    override fun initView(view: View) {
        findView()
        setupCardStackView()

        //加载动画显示用户头像
        if (SystemUtils.isConnected()) {
            flashChatGuideShow()//初始化数据
        } else {
            containerUserList.visibility = View.GONE
            imgLikeEmpty.visibility = View.GONE
            networkErrorContainer.visibility = View.VISIBLE
        }
        userDislike.setOnClickListener {
            dislikeMethod()
        }
        userLike.setOnClickListener {
            isFlashChat = false
            likeMethod()
        }
        threeDaysAgoCountDown.setTimeFinish {
            if (threeDaysAgoContainer != null) {
                threeDaysAgoContainer.visibility = View.GONE
            }
            initPaginate()
        }

        userChat.setOnClickListener {
            if (CollectionUtils.isNotEmpty(adapter.getSpots())) {
                val bundle = Bundle()
                bundle.putString("userName", adapter.getSpots()[manager.topPosition].nickName)
                bundle.putString("userCode", adapter.getSpots()[manager.topPosition].friendUserCode)
                bundle.putString("avatar", adapter.getSpots()[manager.topPosition].avatarUrl)
                bundle.putString(UpSourceEnum.SOURCE.name, UpSourceEnum.HOME.name)
                IntentUtil.startActivity(FlashChatActivity::class.java, bundle)
            }
        }
        networkErrorTryAgain.setOnClickListener {
            networkErrorLoading.visibility = View.VISIBLE
            networkErrorLoading.playAnimation()
            if (SystemUtils.isConnected()) {
                initPaginate()
            } else {
                networkErrorLoading.visibility = View.GONE
                networkErrorLoading.pauseAnimation()
                ToastUtil.toast(getString(R.string.ooops_network_error))
            }

        }
        threeDaysAgoUnlockNow.setOnClickListener {
            FirebaseEventUtils.logEvent(FirebaseEventTag.Home_Unlock_Sub.name)
            Activities.get().top?.let { it1 ->
                MemberBuyDialog(it1, 0, object : MemberBuyDialog.ChangeMembershipListener {
                    override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                            override fun addBody(requestBody: OkHttpBodyEntity) {
                                requestBody.setPost(Constant.user_create_order_url)
                                requestBody.add("productCode", bean.productCode)
                                requestBody.add("productCategory", 1)
                            }

                        }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                            override fun onSuccess(entity: OrderCreateEntity) {
                                Activities.get().top?.let { it2 ->
                                    PayUtils.instance.start(
                                        entity,
                                        it2,
                                        object : EmptySuccessCallBack {
                                            override fun OnSuccessListener() {
                                                FirebaseEventUtils.logEvent(FirebaseEventTag.Home_Unlock_Subsuccess.name)
                                                initPaginate()
                                                threeDaysAgoCountDown.stop()
                                            }

                                        })
                                }
                            }
                        })
                    }

                    override fun closeListener(refreshTime: Long) {

                    }

                })
            }
        }

        cardFlashChat.setOnClickListener {

        }
        cardFlashChat.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                cardFlashChat.isVisible = false
                userActionContainer.isVisible = true
                userChatSize.isVisible = flashChatNum > 0
                BaseConfig.getInstance.setBoolean(SpName.flashChatGuide, true)

            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
        emptyGoWlm.setOnClickListener {
            SDEventManager.post(EnumEventTag.INDEX_TO_WLM.ordinal)
        }
        checkWelcomeBack()


        userLikeAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                userLikeAnim.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
        startActivity(ILikeActivity::class.java, view = limitViewMyFavoritesContainer)

        limitSeeWhoLikesMeContainer.setOnClickListener {
            SDEventManager.post(EnumEventTag.INDEX_TO_WLM.ordinal)
        }
    }

    private fun initData() {
        //加白用户需求
        listLoaderJson.setAnimation(
            if (BaseConfig.getInstance.getInt(
                    SpName.trafficSource,
                    0
                ) != 1
            ) "indexloader.json" else "index_loader_white.json"
        )
        if (BaseConfig.getInstance.getInt(SpName.trafficSource, 0) == 1) {
            listLoaderJson.scaleType = ImageView.ScaleType.CENTER_CROP
            val layoutParams = listLoaderJsonAvatar.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.marginStart = 1
            layoutParams.topMargin = 1
            listLoaderJsonAvatar.layoutParams = layoutParams
        }
        //加载动画
        listLoaderJson.isVisible = true
        listLoaderJson.playAnimation()
        scope.launch {
            Activities.get().top?.let {
                Glide.with(it).load(BaseConfig.getInstance.getString(SpName.avatarUrl, ""))
                    .into(listLoaderJsonAvatar)
            }
//            try {
//                cardFlashChat.setAnimationFromUrl(App.FLASH_CHAT_URL)
//            } catch (e: Exception) {
//                cardFlashChat.setAnimation("flashchat-guide.json")
//            }
        }
        listLoaderJsonAvatar.visibility = View.VISIBLE
        //初始化接口请求
        initPaginate()
    }

    /**
     * 用户浏览次数归零，无model可浏览时展示
     */
    private fun startVideo(data: MatchIndexEntity.Data) {
        if (RongUtils.isDestroy(Activities.get().top)) {
            return
        }

        txtMoreMatchTip.text = data.noticeMsg
        Activities.get().top?.let {
            val controller = StandardVideoController(it)
            //根据屏幕方向自动进入/退出全屏
            controller.setEnableOrientation(false)
            val prepareView = PrepareView(it) //准备播放界面
            prepareView.setClickStart()
            val thumb = prepareView.findViewById<ImageView>(R.id.thumb) //封面图
            Glide.with(this)
                .load(data.defaultCoverImage)
                .into(thumb)
            controller.addControlComponent(prepareView)
            controller.setDoubleTapTogglePlayEnabled(false)
            //如果你不想要UI，不要设置控制器即可
            videoView.setVideoController(controller)
            val url = data.coverImage
            videoView.setUrl(url)
            //播放状态监听
            videoView.addOnStateChangeListener(mOnStateChangeListener)
            videoView.isMute = true
            videoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP)
            videoView.start()
        }
    }

    /**
     * 视频监听
     */
    private val mOnStateChangeListener: BaseVideoView.OnStateChangeListener =
        object : BaseVideoView.SimpleOnStateChangeListener() {
            override fun onPlayerStateChanged(playerState: Int) {
                when (playerState) {
                    VideoView.PLAYER_NORMAL -> {
                    }

                    VideoView.PLAYER_FULL_SCREEN -> {
                    }
                }
            }

            override fun onPlayStateChanged(playState: Int) {
                when (playState) {
                    VideoView.STATE_IDLE -> {
                    }

                    VideoView.STATE_PREPARING -> {
                    }

                    VideoView.STATE_PREPARED -> {
                    }

                    VideoView.STATE_PLAYING -> {
                        //需在此时获取视频宽高

                    }

                    VideoView.STATE_PAUSED -> {
                    }

                    VideoView.STATE_BUFFERING -> {
                    }

                    VideoView.STATE_BUFFERED -> {
                    }

                    VideoView.STATE_PLAYBACK_COMPLETED -> {
                        if (videoView != null) {
                            videoView.replay(true)
                        }
                    }

                    VideoView.STATE_ERROR -> {
                    }
                }
            }
        }

    /**
     * 初次安装时 flash chat引导
     */
    private fun flashChatGuideShow() {
        if (!BaseConfig.getInstance.getBoolean(
                SpName.appGuide,
                false
            ) && BaseConfig.getInstance.getInt(SpName.trafficSource, 0) != 1
        ) {
            BaseConfig.getInstance.setBoolean(SpName.appGuide, true)
            flashChatGuideOne.isVisible = true
            flashChatGuideOne.playAnimation()
            flashChatGuideOne.setOnClickListener {
                flashChatGuideOne.cancelAnimation()
                flashChatGuideOne.isVisible = false
                flashChatGuideTwo.isVisible = true
                flashChatGuideTwo.playAnimation()
            }
            flashChatGuideTwo.setOnClickListener {
                flashChatGuideTwo.cancelAnimation()
                flashChatGuideTwo.isVisible = false
                flashChatGuideThree.isVisible = true
                flashChatGuideThree.playAnimation()
            }
            flashChatGuideThree.setOnClickListener {
                flashChatGuideThree.cancelAnimation()
                flashChatGuideThree.isVisible = false
                initData()
            }
        } else {
            initData()
        }
    }

    private fun setupCardStackView() {
        initialize()
    }

    override fun onResume() {
        super.onResume()
        if (isAdded && activity != null && !requireActivity().isFinishing) {
            ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .init()
            if (videoView.currentPlayState == VideoView.STATE_PAUSED) {
                videoView.resume()
            } else if (videoView.currentPlayState == VideoView.STATE_IDLE) {
                videoView.start()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        //如果视频还在准备就 activity 就进入了后台，建议直接将 VideoView release
        //防止进入后台后视频还在播放
        if (videoView.currentPlayState == VideoView.STATE_PREPARING) {
            videoView.release()
        } else {
            if (videoView.currentPlayState == VideoView.STATE_PLAYING) {
                videoView.pause()
            }
        }
    }

    var mHidden = false

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!isAdded || activity == null || requireActivity().isFinishing) {
            return
        }
        mHidden = hidden
        if (!hidden) {
            ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .init()
            if (videoView.currentPlayState == VideoView.STATE_PAUSED) {
                videoView.resume()
            }
        } else {
            if (videoView.currentPlayState == VideoView.STATE_PLAYING) {
                videoView.pause()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onEventMainThread(event: SDBaseEvent) {
        when (EnumEventTag.valueOf(event.tagInt)) {
            EnumEventTag.INDEX_DISLIKE_SWIPED -> {//用户点击不喜欢
                Handler().postDelayed({
                    dislikeMethod()
                }, 150)

            }

            EnumEventTag.INDEX_LIKE_SWIPED -> {//用户点击喜欢
                Handler().postDelayed({
                    if (event.data != null) {
                        isFlashChat = event.data.toString().toBoolean()
                    }
                    likeMethod()
                }, 150)
            }

            EnumEventTag.INDEX_LOCATION_SWIPED -> {//用户点击了请求位置权限
                Handler().postDelayed({
                    if (event.data != null) {
                        isFlashChat = event.data.toString().toBoolean()
                    }
                    clickRequestLocation()
                }, 150)
            }

//            EnumEventTag.INDEX_RELOAD_DATA -> {
//                reload()
//            }

            EnumEventTag.INDEX_REFRESH_DATA -> {//刷新数据
                initPaginate()
            }

            EnumEventTag.INDEX_REFRESH_DATA_ITEM -> {
                if (JsonUtils.isJSON(event.data.toString())) {
                    val jsonObject = JSONObject(event.data.toString())
                    refreshItem(jsonObject.getString("userCode"), jsonObject.getInt("online"))
                }
            }

            EnumEventTag.INDEX_COUNTDOWN_SHOW -> {//用户流量次数用完，展示当天倒计时
                val refreshTime = event.data.toString().toLong()
                noMatchData?.let { showNoBrowseCardNum(it, refreshTime) }
            }

            EnumEventTag.FLASH_CHAT_END_NUM_ADD -> {//当购买了fc之后，增加次数
                val num =
                    userChatSize.text.toString().subSequence(1, userChatSize.text.toString().length)
                val toInt = event.data.toString().toInt() + num.toString().toInt()
                userChatSize.isVisible = toInt > 0
                userChatSize.text = "x$toInt"
            }

            EnumEventTag.FLASH_CHAT_END_NUM_REDUCTION -> {//当使用了fc之后，减少次数
                val num =
                    userChatSize.text.toString().subSequence(1, userChatSize.text.toString().length)
                val toInt = num.toString().toInt() - 1
                userChatSize.isVisible = toInt > 0
                userChatSize.text = "x$toInt"
            }

            EnumEventTag.REFRESH_GET_FLASH_DATA -> {
                //Log.i("新手礼包","fc==${userChatSize.text}")
                //购买会员成功以后刷新一下fc次数
                IndexHelper.requestBenefitsData {
                    userChatSize.isVisible = it > 0
                    userChatSize.text = "x$it"
                    //Log.i("新手礼包","fc==$it")
                }
            }

            EnumEventTag.LOWER_LIMIT_DEBIT_TO_INDEX -> {
                if (manager.topView != null) {
                    animateView(manager.topView)
                }
            }


            else -> {}
        }
    }

    /**
     * 划卡下限动画展示
     */
    private fun animateView(view: View) {
        // 创建平移动画
        val translateAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, 50f, 0f)
        translateAnimator.duration = 700
        translateAnimator.repeatCount = 2
        val translateXAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f, 200f, 0f)
        translateXAnimator.duration = 700
        translateXAnimator.repeatCount = 2
        // 创建旋转动画
        val rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 5f, 0f)
        rotateAnimator.duration = 700
        rotateAnimator.repeatCount = 2
        // 创建 AnimatorSet 并同时播放平移和旋转动画
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translateXAnimator, translateAnimator, rotateAnimator)
        animatorSet.start()
    }

    /**
     * 连续pass时，展示feedback，进行虚拟重载
     */
    fun reload() {
        containerUserList.visibility = View.INVISIBLE //设置为Gone会在显示时执行移除操作，INVISIBLE会立即执行
        val spots = adapter.getSpots() as ArrayList<MatchIndexBean>
        for (i in 0 until manager.topPosition + 2) {
            spots.removeFirst()
        }
        adapter.setSpots(spots)
        adapter.notifyDataSetChanged()

        userLikeProgress.isVisible = false
        listLoaderJson.isVisible = true
        listLoaderJsonAvatar.visibility = View.VISIBLE
        listLoaderJson.playAnimation()

        Handler().postDelayed({
            listLoaderJson.cancelAnimation()
            listLoaderJson.visibility = View.GONE
            listLoaderJsonAvatar.visibility = View.GONE
            containerUserList.isVisible = true
            userLikeProgress.isVisible = !BaseConfig.getInstance.getBoolean(SpName.isMember, false)
        }, 2200)


    }

    /**
     * 点击权限打开功能卡
     * */
    private fun clickRequestLocation() {
        //往右边的动作
        val direction = Direction.Right
        val duration = Duration.Normal.duration
        val interpolator: Interpolator = AccelerateInterpolator()
        val swipeAnimationSetting = SwipeAnimationSetting.Builder()
            .setDirection(direction)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .build()
        manager.setSwipeAnimationSetting(swipeAnimationSetting)
        cardStackView.swipe()
        //判断权限是否请求过了
        if (PermissionUtil.checkPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) && PermissionUtil.checkPermission(mActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            Activities.get().top?.let {fragmentActivity->
                cardShowNum++
                PermissionUtil.requestPermissionCallBack(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, activity = fragmentActivity) {
                    if (it) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", fragmentActivity.packageName, null)
                        intent.data = uri
                        fragmentActivity.startActivityForResult(intent, 10008)
                    }
                }
            }

        }

    }

    /**
     * 喜欢的点击事件
     */
    private fun likeMethod() {
        if (!isFlashChat) {
            if (!BaseConfig.getInstance.getBoolean(SpName.isMember, false)) {
                if (remainingWlmNumLive.value!! <= 0) {
                    Activities.get().top?.let { memberBuyShow(1, it) }
                    return
                }
            }
        }
        val direction = Direction.Right
        val duration = Duration.Normal.duration
        val interpolator: Interpolator = AccelerateInterpolator()
        val swipeAnimationSetting = SwipeAnimationSetting.Builder()
            .setDirection(direction)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .build()
        manager.setSwipeAnimationSetting(swipeAnimationSetting)
        cardStackView.swipe()
    }

    /**
     * 不喜欢的点击事件
     */
    private fun dislikeMethod() {
        if (!BaseConfig.getInstance.getBoolean(SpName.isMember, false)) {
            if (remainingBrowseBNumLive.value!! <= 0) {
                FirebaseEventUtils.logEvent(FirebaseEventTag.Home_Profile_View_Sub.name)
                Activities.get().top?.let { memberBuyShow(2, it) }
                return
            }
        }

        val direction = Direction.Left
        val duration = Duration.Normal.duration
        val interpolator: Interpolator = AccelerateInterpolator()
        val swipeAnimationSetting = SwipeAnimationSetting.Builder()
            .setDirection(direction)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .build()
        manager.setSwipeAnimationSetting(swipeAnimationSetting)
        cardStackView.swipe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 10009) {
            SDEventManager.post(true, EnumEventTag.INDEX_LIKE_SWIPED.ordinal)
        }
    }


    /**
     * 卡片初始化配置
     */
    private fun initialize() {
        manager.setVisibleCount(2)
        manager.setTranslationInterval(4.0f)
        manager.setScaleInterval(0.70f)
        manager.setSwipeThreshold(0.6f)
        manager.setMaxDegree(50.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(false)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        val direction = Direction.Left
        val duration = Duration.Normal.duration
        val interpolator: Interpolator = AccelerateInterpolator()
        val swipeAnimationSetting = SwipeAnimationSetting.Builder()
            .setDirection(direction)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .build()
        manager.setSwipeAnimationSetting(swipeAnimationSetting)
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        (cardStackView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    /**
     * 获取列表数据
     */
    private fun initPaginate() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.match_user_url)
            }
        }, object : SDOkHttpResoutCallBack<MatchIndexEntity>() {
            override fun onSuccess(entity: MatchIndexEntity) {
                try {
                    noMatchData = entity.data
                    newUserFlag = entity.data.newUserFlag
                    turnOns = entity.data.turnOns
                    turnOnsLimit = entity.data.turnOnsLimit
                    flashChatNum = entity.data.flashChatNum
                    flashChatLimit = entity.data.flashChatLimit - 1//举例：第三张展示，索引减1
                    locationLimit = entity.data.locationLimit
                    txtRemainingCardReminder.text = entity.data.remainingBrowseNotice
                    scope.launch {
                        remainingBrowseBNumLive.value = entity.data.remainingBrowseBNum
                        remainingWlmNumLive.value = entity.data.remainingWlmNum
//                        BaseConfig.getInstance.setInt(
//                            SpName.remainingBrowseBNum,
//                            entity.data.remainingBrowseBNum
//                        )
                        IndexHelper.saveProgressMaxValues(entity.data.remainingBrowseBNum)
//                        BaseConfig.getInstance.setInt(
//                            SpName.remainingWlmNum,
//                            entity.data.remainingWlmNum
//                        )
                        BaseConfig.getInstance.setBoolean(SpName.isMember, entity.data.ismember)
                        Glide.with(this@IndexFragment)
                            .load(entity.data.defaultCoverImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .preload()

                        if (!CollectionUtils.isEmpty(entity.data.homepageList)) {
                            for (i in 0 until entity.data.homepageList.size) {

                                HomeCardPreloadHelper.preloadPic(
                                    0,
                                    entity.data.homepageList
                                )

                            }
                        }

                    }
                    Handler().postDelayed({
                        if (listLoaderJson != null && listLoaderJson.isVisible) {
                            listLoaderJson.cancelAnimation()
                            listLoaderJson.visibility = View.GONE
                            listLoaderJsonAvatar.visibility = View.GONE
                        }
                        if (networkErrorLoading.isVisible) {
                            networkErrorLoading.visibility = View.GONE
                            networkErrorLoading.cancelAnimation()
                        }

                        networkErrorContainer.visibility = View.GONE

                        SDEventManager.post(
                            entity.data.noChatTriggerTimes,
                            EnumEventTag.LOWER_LIMIT_DEBIT_SHOW.ordinal
                        )
                        when (entity.data.preferentialFlag) {
                            1 -> {
                                //无浏览次数倒计时展示
                                showNoBrowseCardNum(entity.data, entity.data.refreshTime)
                            }

                            else -> {
                                threeDaysAgoContainer.visibility = View.GONE
                                containerUserList.visibility =
                                    if (CollectionUtils.isEmpty(entity.data.homepageList)) View.GONE else View.VISIBLE
                                imgLikeEmpty.visibility =
                                    if (CollectionUtils.isEmpty(entity.data.homepageList)) View.VISIBLE else View.GONE
                                userLikeProgress.visibility =
                                    if (CollectionUtils.isEmpty(entity.data.homepageList) || (entity.data.ismember && !entity.data.memberBrowseEnableSwitch)) View.GONE else View.VISIBLE
                                if (videoView.isPlaying) {
                                    videoView.release()
                                }
                                //加白需求-划卡空页面
                                setWhiteUserVisible()

                                if (imgLikeEmpty.isVisible && imgLikeEmptyLogo.isVisible) {
                                    imgLikeEmptyLogo.playAnimation()
                                }
                                userChatSize.isVisible = BaseConfig.getInstance.getBoolean(
                                    SpName.flashChatGuide,
                                    false
                                ) && flashChatNum > 0//flash chat
                                userChatSize.text = "x${entity.data.flashChatNum}"
                                if (!CollectionUtils.isEmpty(entity.data.homepageList)) {
                                    adapter.setSpots(emptyList())
                                    adapter.setNewUserFlag(newUserFlag)
                                    adapter.setTurnOns(turnOns)
                                    adapter.setTurnOnsLimit(turnOnsLimit)
                                    val old = arrayListOf<MatchIndexBean>()
                                    val new = old.plus(entity.data.homepageList)
                                    val callback = SpotDiffCallback(old, new)
                                    val result = DiffUtil.calculateDiff(callback)
                                    adapter.setSpots(new)
                                    result.dispatchUpdatesTo(adapter)
                                    if (userLikeProgress.visibility == View.VISIBLE) {
                                        val progressMaxValues = IndexHelper.getProgressMaxValues()
                                        userLikeProgress.max = progressMaxValues
                                        userLikeProgress.progress =
                                            progressMaxValues - entity.data.remainingBrowseBNum
                                    }

                                }
                            }
                        }
                    }, 2200)
                    Activities.get().top?.let {
                        IndexHelper.showDiscountPop(false, entity.data.ismember, it) { discountInfoEntity ->
                            try {
                                if (it is HomeActivity){
                                    it.showDiscountDownTime(discountInfoEntity)
                                }
                            } catch (e: Exception) {
                            }

                        }
                    }
                } catch (_: Exception) {
                }
            }

            override fun onFailure(code: Int, msg: String) {
                if (listLoaderJson != null) {
                    listLoaderJson.cancelAnimation()
                    listLoaderJson.visibility = View.GONE
                    listLoaderJsonAvatar.visibility = View.GONE
                }
                containerUserList.visibility = View.GONE
                imgLikeEmpty.visibility = View.GONE
                networkErrorContainer.visibility = View.VISIBLE
                if (networkErrorLoading.isVisible) {
                    networkErrorLoading.visibility = View.GONE
                    networkErrorLoading.cancelAnimation()
                }
            }
        }, isShowDialog = false)
    }

    /**
     * 加白需求-划卡空页面
     */
    private fun setWhiteUserVisible() {
        imgLikeEmptyLogo.isVisible = BaseConfig.getInstance.getInt(SpName.trafficSource, 0) != 1
        imgWhiteLikeEmptyLogo.isVisible =
            BaseConfig.getInstance.getInt(SpName.trafficSource, 0) == 1
        emptyTip.isVisible = BaseConfig.getInstance.getInt(SpName.trafficSource, 0) != 1
        emptyTitle.text = if (BaseConfig.getInstance.getInt(
                SpName.trafficSource,
                0
            ) == 1
        ) Activities.get().top?.getString(R.string.no_more) else Activities.get().top?.getString(R.string.did_you_miss_out_on_many_people)
    }

    private fun showNoBrowseCardNum(entity: MatchIndexEntity.Data, refreshTime: Long) {
        //当日划卡达到上限，判断根据是否会员，当是会员且有划卡限制时，走会员划卡上限逻辑
        if (entity.ismember && entity.memberBrowseEnableSwitch) {
            memberUpperLimitContainer.visibility = View.VISIBLE
            userLikeProgress.visibility = View.GONE
            containerUserList.visibility = View.GONE
            imgLikeEmpty.visibility = View.GONE
            networkErrorContainer.visibility = View.GONE
            val hours: Long = refreshTime / 60 / 60
            val min = (refreshTime - hours * 60 * 60) / 60
            val s = refreshTime - hours * 60 * 60 - min * 60
            limitCountDown.setTime(
                hours.toInt(),
                min.toInt(),
                s.toInt()
            )
            limitCountDown.start()
        } else {
            if (txtRemainingCardReminder.isVisible) {
                txtRemainingCardReminder.visibility = View.GONE
            }
            threeDaysAgoContainer.visibility = View.VISIBLE
            userLikeProgress.visibility = View.GONE
            videoContainer.isVisible = BaseConfig.getInstance.getInt(SpName.trafficSource, 0) != 1
            imgNoMoreMatchLogo.isVisible =
                BaseConfig.getInstance.getInt(SpName.trafficSource, 0) == 1
            if (videoContainer.isVisible) {
                startVideo(entity)
            }
            containerUserList.visibility = View.GONE
            imgLikeEmpty.visibility = View.GONE
            networkErrorContainer.visibility = View.GONE
            val hours: Long = refreshTime / 60 / 60
            val min = (refreshTime - hours * 60 * 60) / 60
            val s = refreshTime - hours * 60 * 60 - min * 60
            threeDaysAgoCountDown.setTime(
                hours.toInt(),
                min.toInt(),
                s.toInt()
            )
            threeDaysAgoCountDown.start()
        }
    }

    /**
     * 加载更多数据
     */
    private fun paginate() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.match_user_url)
                requestBody.add("recordNum", 10)
            }
        }, object : SDOkHttpResoutCallBack<MatchIndexEntity>() {
            override fun onSuccess(entity: MatchIndexEntity) {
                try {
                    scope.launch {
                        BaseConfig.getInstance.setBoolean(SpName.isMember, entity.data.ismember)
                    }
                    if (CollectionUtils.isNotEmpty(entity.data.homepageList)) {
                        val old = adapter.getSpots()
                        val new = old.plus(entity.data.homepageList)
                        adapter.setSpots(new)
                        adapter.notifyItemRangeChanged(
                            manager.topPosition + 1,
                            new.size - manager.topPosition - 1
                        )
                    }
                } catch (_: Exception) {

                }
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })

    }

    /**
     * 检查是否是注销用户回归
     */
    fun checkWelcomeBack() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.check_logout)
                requestBody.add("userCode", BaseConfig.getInstance.getString(SpName.userCode, ""))
            }
        }, object : SDOkHttpResoutCallBack<ResultDataEntity<String>>() {
            override fun onSuccess(entity: ResultDataEntity<String>) {
                entity.data?.let { showWelcomeBackDialog(it) }
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }

    /**
     * 展示回归用户弹窗
     */
    fun showWelcomeBackDialog(content: String) {
        if (content.isNullOrEmpty()) {
            return
        }
        val dialog = Activities.get().top?.let {
            CustomDialog(it)
                .setLayoutId(R.layout.dialog_welcome_back)
                .setControllerListener { dialog ->
                    dialog.findViewById<TextView>(R.id.tvContent).text = content
                }.show()
        }
        Handler().postDelayed(Runnable {
            dialog?.dismiss()
        }, 3000)
    }

    /**
     * 卡片滑动监听
     */
    override fun onCardDragging(direction: Direction, ratio: Float) {
        isFlashChat = false
        if (direction == Direction.Left) {
            userDislike.setImageResource(R.mipmap.icon_dislike_new)
            userDislike.setBackgroundResource(R.drawable.shape_dislike_move_bg)

            userLike.setImageResource(R.drawable.selector_user_like_click_status_img)
            userLike.setBackgroundResource(R.drawable.selector_user_like_click_status_transfer)
        } else {
            userLike.setImageResource(R.drawable.selector_user_like_click_status_img)
            userLike.setBackgroundResource(R.drawable.shape_like_move_bg)

            userDislike.setImageResource(R.mipmap.icon_dislike_new)
            userDislike.setBackgroundResource(R.drawable.selector_user_dislike_click_status_transfer)
        }
    }

    /**
     * 卡片滑动完成监听
     */
    override fun onCardSwiped(direction: Direction) {
        //展示flash chat引导
        if (manager.topPosition == flashChatLimit && flashChatLimit > 0 && !BaseConfig.getInstance.getBoolean(
                SpName.flashChatGuide,
                false
            ) && flashChatNum > 0
        ) {
            //加白用户不展示动画
            if (BaseConfig.getInstance.getInt(SpName.trafficSource, 0) != 1) {
                cardFlashChat.isVisible = true
                cardFlashChat.playAnimation()
            } else {
                userChatSize.isVisible = flashChatNum > 0
            }
        }

        if (direction.toString() == "Right") {
            if (!isFlashChat) {
                if (!turnOns && (manager.topPosition - 1 == turnOnsLimit)) {
                    val bundle = Bundle()
                    bundle.putBoolean("move", false)
                    IntentUtil.startActivity(ChooseTurnOnsActivity::class.java, bundle)
                    isFlashChat = false

                } else if (CollectionUtils.isNotEmpty(locationRequestArray) && manager.topPosition - 1 == locationRequestArray[locationRequestArray.size - 1] && PermissionUtil.checkPermission(
                        mActivity,Manifest.permission.ACCESS_FINE_LOCATION
                    ) && PermissionUtil.checkPermission(mActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    Activities.get().top?.let {fac->
                        cardShowNum++
                        PermissionUtil.requestPermissionCallBack(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION, activity = fac) {
                            if (it) {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", fac.packageName, null)
                                intent.data = uri
                                fac.startActivityForResult(intent, 10008)
                            }
                        }
                    }

//                } else if (MathUtil().isMultipleOfEight(
//                        continuousPassNum,
//                        feedbackLimit
//                    ) && !BaseConfig.getInstance.getBoolean(
//                        SpName.passFeedBackShow + DateUtils.getTime(
//                            Date()
//                        ), false
//                    )
//                ) {
//                    BaseConfig.getInstance.setBoolean(
//                        SpName.passFeedBackShow + DateUtils.getTime(
//                            Date()
//                        ), true
//                    )
//
                } else {
                    cardShowNum = 0
                    continuousPassNum = 0
                    locationRequestArray.clear()
                    scope.launch {
                        remainingWlmNumLive.value = remainingWlmNumLive.value?.minus(1)
//                        val remainingWlmNum =
//                            BaseConfig.getInstance.getInt(SpName.remainingWlmNum, 0) - 1
//                        BaseConfig.getInstance.setInt(SpName.remainingWlmNum, remainingWlmNum)
                        swipeCardAction(direction)
                    }

                }
            } else {
                if (!getLocationAgain() && PermissionUtil.checkPermission(mActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) && PermissionUtil.checkPermission(mActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    Activities.get().top?.let {fac->
                        cardShowNum++
                        PermissionUtil.requestPermissionCallBack(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION, activity = fac) {
                            if (it) {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", fac.packageName, null)
                                intent.data = uri
                                fac.startActivityForResult(intent, 10008)
                            }
                        }
                    }

                }
            }
        } else {
            if (!turnOns && (manager.topPosition - 1 == turnOnsLimit)) {

//            } else if (MathUtil().isMultipleOfEight(
//                    continuousPassNum,
//                    feedbackLimit
//                ) && !BaseConfig.getInstance.getBoolean(
//                    SpName.passFeedBackShow + DateUtils.getTime(
//                        Date()
//                    ), false
//                )
//            ) {
//                BaseConfig.getInstance.setBoolean(
//                    SpName.passFeedBackShow + DateUtils.getTime(Date()),
//                    true
//                )

            } else {
                //当下标索引不符合权限请求索引时或者权限已有时，进入以下逻辑
                if (getLocationAgain() || !(PermissionUtil.checkPermission(mActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) && PermissionUtil.checkPermission(mActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                ) {
                    continuousPassNum++
                    swipeCardAction(direction)
//                    if (MathUtil().isShowFeedBackCard(continuousPassNum, feedbackLimit)) {
//                        adapter.setShowFeedback(true, manager.topPosition)
//                    }
                } else {
                    //符合展示定位权限
                    cardShowNum++
                    locationRequestArray.add(manager.topPosition)
                    adapter.setShowLocation(true, manager.topPosition)
                }
            }
        }


        if (manager.topPosition == adapter.itemCount - 10) {
            paginate()
        }
    }

    /**
     * model的划卡操作
     */
    private fun swipeCardAction(direction: Direction) {
        scope.launch {
            remainingBrowseBNumLive.value = remainingBrowseBNumLive.value?.toInt()?.minus(1)
            if (userLikeProgress.isVisible) {
                userLikeProgress.progress =
                    userLikeProgress.max - remainingBrowseBNumLive.value?.toInt()!!
                //划卡剩余5次数提醒，每次显示一次
                if (userLikeProgress.max - userLikeProgress.progress == 5 && !BaseConfig.getInstance.getBoolean(
                        SpName.remainingCardTipShow + DateUtils.getTime(Date()) + BaseConfig.getInstance.getString(
                            SpName.userCode,
                            ""
                        ),
                        false
                    )
                ) {
                    txtRemainingCardReminder.visibility = View.VISIBLE
                    countDownTimer = object :
                        CountDownTimer((noMatchData?.displayTimes?.toLong() ?: 6) * 1000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                        }

                        override fun onFinish() {
                            txtRemainingCardReminder.visibility = View.GONE
                        }
                    }
                    countDownTimer?.start()
                    BaseConfig.getInstance.setBoolean(
                        SpName.remainingCardTipShow + DateUtils.getTime(
                            Date()
                        ) + BaseConfig.getInstance.getString(SpName.userCode, ""), true
                    )
                }
            }
        }
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_add_wlm_url)
                requestBody.add(
                    "likeType",
                    if (direction.toString() == "Right") 1 else 4
                )
                requestBody.add(
                    "userCodeFriend",
                    adapter.getSpots()[manager.topPosition - 1].friendUserCode
                )
            }
        }, object : SDOkHttpResoutCallBack<MatchResultEntity>() {
            override fun onSuccess(entity: MatchResultEntity) {
                FirebaseEventUtils.logEvent(if (direction.toString() == "Right") FirebaseEventTag.Home_Like.name else FirebaseEventTag.Home_Pass.name)

                if (direction.toString() == "Right" && entity.data.matched) {
                    val bundle = Bundle()
                    bundle.putString(
                        "userCode",
                        adapter.getSpots()[manager.topPosition - 1].userCode
                    )
                    bundle.putString(
                        "userCodeFriend",
                        adapter.getSpots()[manager.topPosition - 1].friendUserCode
                    )
                    bundle.putString(
                        "avatarUrl",
                        adapter.getSpots()[manager.topPosition - 1].avatarUrl
                    )
                    IntentUtil.startActivity(MatchUserActivity::class.java, bundle)
                }

                if (manager.topPosition == adapter.getSpots().size) {
                    containerUserList.visibility = View.GONE
                    imgLikeEmpty.visibility = View.VISIBLE
                    setWhiteUserVisible()
                    if (imgLikeEmptyLogo.isVisible) {
                        imgLikeEmptyLogo.playAnimation()
                    }
                }
            }

            override fun onFailure(code: Int, msg: String) {
                //当该用户是会员且开启会员划卡限制
                if (noMatchData?.ismember == true && noMatchData?.memberBrowseEnableSwitch == true) {
                    //请求接口获取剩余倒计时
                    getMemberCountDownTime()
                    return
                }
                if (code == 3002) {
                    Activities.get().top?.let { memberBuyShow(null, it) }
                }
                if (cardStackView != null) {//可能存在客户端划卡与服务端不准确
                    cardStackView.rewind()
                }

            }
        })
    }

    fun getMemberCountDownTime() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(API.user_member_product_category_url)
                requestBody.add("productCategory", 1)
            }

        }, object : SDOkHttpResoutCallBack<MemberSubscribeEntity>() {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(entity: MemberSubscribeEntity) {
                SDEventManager.post(
                    entity.data.refreshTime,
                    EnumEventTag.INDEX_COUNTDOWN_SHOW.ordinal
                )
            }

        })
    }


    /**
     * 卡片回滚监听
     */
    override fun onCardRewound() {
    }

    /**
     * 卡片取消滑动监听
     */
    override fun onCardCanceled() {
        userDislike.setImageResource(R.mipmap.icon_dislike_new)
        userDislike.setBackgroundResource(R.drawable.selector_user_dislike_click_status_transfer)
        userLike.setImageResource(R.drawable.selector_user_like_click_status_img)
        userLike.setBackgroundResource(R.drawable.selector_user_like_click_status_transfer)
    }

    val scope = MainScope()


    /**
     * 卡片出现监听
     */
    override fun onCardAppeared(view: View, position: Int) {
        //对图片进行缓存
        HomeCardPreloadHelper.preloadPic(position, adapter.getSpots())

//        if ((!turnOns && manager.topPosition == turnOnsLimit) || MathUtil().isShowFeedBackCard(
//                continuousPassNum,
//                feedbackLimit
//            )
//        ) {
        //记录功能卡片出现次数
        if ((!turnOns && manager.topPosition == turnOnsLimit)) {
            cardShowNum++
        }

//        fc引导展示判断
//        tenPassContainer.isVisible = continuousPassNum == 10 && !BaseConfig.getInstance.getBoolean(
//            SpName.tenPassFeedBackShow + DateUtils.getTime(Date()), false
//        )
//        if (tenPassContainer.isVisible) {//pass10张并且为当日首次的时候展示
//            val channel = BaseConfig.getInstance.getString(SpName.channel, "")
//            if (channel != "" && JsonUtils.isJSON(channel)) {
//                val jsonObject = JSONObject(channel)
//                nonOrganicContainer.isVisible =
//                    jsonObject.has("af_status") && jsonObject.getString("af_status")
//                        .equals("Organic", true)
//                organicContainer.isVisible = !nonOrganicContainer.isVisible
//            } else {
//                nonOrganicContainer.isVisible = false
//                organicContainer.isVisible = true
//            }
//            BaseConfig.getInstance.setBoolean(
//                SpName.tenPassFeedBackShow + DateUtils.getTime(Date()),
//                true
//            )
//        }

        //判断用户操作按钮是否展示，当展示turn-ons，定位权限请求卡片，flashChat引导卡片时隐藏按钮
        userActionContainer.visibility =
            if (!turnOns && manager.topPosition == turnOnsLimit)
                View.GONE
            else if (CollectionUtils.isNotEmpty(locationRequestArray) && manager.topPosition == locationRequestArray[locationRequestArray.size - 1] && PermissionUtil.checkPermission(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) && PermissionUtil.checkPermission(mActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
                View.GONE
//            else if (MathUtil().isShowFeedBackCard(
//                    continuousPassNum,
//                    feedbackLimit
//                )
//            )
//                View.GONE
//            else if (manager.topPosition == flashChatLimit && flashChatLimit > 0 && !BaseConfig.getInstance.getBoolean(
//                    SpName.flashChatGuide,
//                    false
//                ) && flashChatNum > 0
//            )
//                View.GONE
            else
                View.VISIBLE
    }

    /**
     * 卡片消失监听
     */
    override fun onCardDisappeared(view: View, position: Int) {
        userDislike.setImageResource(R.mipmap.icon_dislike_new)
        userDislike.setBackgroundResource(R.drawable.selector_user_dislike_click_status_transfer)
        userLike.setImageResource(R.drawable.selector_user_like_click_status_img)
        userLike.setBackgroundResource(R.drawable.selector_user_like_click_status_transfer)
        if (position + 1 == adapter.itemCount) {
            containerUserList.visibility = View.GONE
            imgLikeEmpty.visibility = View.VISIBLE
            setWhiteUserVisible()
            if (imgLikeEmptyLogo.isVisible) {
                imgLikeEmptyLogo.playAnimation()
            }
        }
        userLikeAnim.visibility = View.VISIBLE
        userLikeAnim.playAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (manager != null)
            manager.removeAllViews()
        if (scope != null) {
            scope.cancel()
        }
        if (videoView != null) {
            if (videoView.currentPlayState == VideoView.STATE_PLAYING) {
                videoView.release()
            }
        }

//        if (timer!= null){
//            timer?.cancel()
//        }
        countDownTimer?.cancel()
        countDownTimer = null
    }

    /**
     * 记录定位权限触发的次数和间隔
     */
    fun getLocationAgain(): Boolean {
        var lastPosition = 0
        if (CollectionUtils.isNotEmpty(locationRequestArray)) {
            lastPosition = locationRequestArray[locationRequestArray.size - 1]
        }
        return continuousPassNum - lastPosition + cardShowNum != locationLimit
    }

    /**
     * 刷新在线状态
     */
    fun refreshItem(userCode: String, onlineStatus: Int) {
        adapter?.let {
            for (i in 0 until adapter.getSpots().size) {
                if (adapter.getSpots()[i].friendUserCode == userCode) {
                    if (adapter.getSpots()[i].online != onlineStatus) {
                        adapter.getSpots()[i].online = onlineStatus
                        adapter.notifyItemChanged(i)
                    }
                    break
                }
            }
        }

    }

}