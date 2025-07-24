package com.crush.ui.start

import UserUtil
import android.content.res.AssetFileDescriptor
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.Constant
import com.crush.R
import com.crush.entity.IMTokenGetEntity
import com.crush.entity.TrafficEntity
import com.crush.interceptor.HttpRequestInterceptor
import com.crush.rongyun.RongConfigUtil
import com.crush.util.SystemUtils
import com.crush.video.PrepareView
import com.crush.video.StandardVideoController
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.MVPBaseActivity
import com.custom.base.util.ToastUtil
import com.gyf.immersionbar.ImmersionBar
import com.sunday.eventbus.SDBaseEvent
import io.rong.imkit.SpName
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.utils.JsonUtils
import org.json.JSONObject
import xyz.doikki.videoplayer.player.BaseVideoView
import xyz.doikki.videoplayer.player.VideoView
import java.io.IOException
import java.util.Calendar


/**
 * 作者：
 * 时间：
 * 描述：启动页
 */

class StartActivity : MVPBaseActivity<StartContract.View, StartPresenter>(), StartContract.View {

    private var retryNum=0
    override fun setFullScreen(): Boolean {
        return true
    }

    override fun bindLayout(): Int {
        return R.layout.act_start
    }
    override fun initView() {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        if (month == 11 && dayOfMonth<27 && dayOfMonth>22) {
            christmasCountdownContainer.visibility=View.VISIBLE
            val totalTime = 4000L // 倒计时总时长，单位为毫秒
            val interval = 1000L // 倒计时间隔，单位为毫秒

            val countDownTimer = object : CountDownTimer(totalTime, interval) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = millisUntilFinished / 1000
//                    if (seconds > 0) {
                        christmasCountdown.text = "Skip $seconds"
//                    }
                }

                override fun onFinish() {
                    splash()
                }
            }
            countDownTimer.start()
            christmasCountdown.setOnClickListener {
                countDownTimer.cancel()
                splash()
                christmasCountdownContainer.visibility=View.GONE
            }
        }else{
            splash()
        }
        //防止用户杀进程再进来，需要重新把这个订单事件id置为空
        BaseConfig.getInstance.setString(SpName.orderEventId,"")
    }

    private fun splash() {
        val channel = BaseConfig.getInstance.getString(SpName.channel, "")
        if (channel != "" && JsonUtils.isJSON(channel)) {
            val jsonObject = JSONObject(channel)
            if (jsonObject.has("af_status")) {
                val afStatus = jsonObject.getString("af_status")
                if (!afStatus.equals("Organic",true)){
                    startVideo()
                }else{
                    imgOrganic.visibility = View.VISIBLE
                }
            }
        } else {
            imgOrganic.visibility = View.VISIBLE
        }


        //测试环境下不上报
//        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        setRongConfig()


        if (!BaseConfig.getInstance.getBoolean(
                SpName.profileComplete,
                false
            ) || BaseConfig.getInstance.getString(
                SpName.token, ""
            ) == ""
        ) {
            UserUtil.startLogin(mActivity)
        } else {
            if (SystemUtils.isConnected()) {
                networkErrorContainer.visibility = View.GONE
                getIMToken()
            } else {
                imgOrganic.visibility = View.GONE
                networkErrorContainer.visibility = View.VISIBLE
                networkErrorTryAgain.setOnClickListener {
                    if (SystemUtils.isConnected()) {
                        getIMToken()
                    } else {
                        ToastUtil.toast(getString(R.string.ooops_network_error))
                    }
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
    }

    private fun startVideo() {
        videoView.visibility=View.VISIBLE
        val controller = StandardVideoController(mActivity)
        //根据屏幕方向自动进入/退出全屏
        controller.setEnableOrientation(false)
        val prepareView = PrepareView(mActivity) //准备播放界面
        prepareView.setClickStart()
        val thumb = prepareView.findViewById<ImageView>(R.id.thumb) //封面图
//        thumb.setImageResource(R.mipmap.icon_video_cover)
        controller.addControlComponent(prepareView)
        controller.setDoubleTapTogglePlayEnabled(false)
        //如果你不想要UI，不要设置控制器即可
        videoView.setVideoController(controller)
        //ExoPlayer
        val am = resources.assets
        var afd: AssetFileDescriptor? = null
        try {
            afd = am.openFd("start.mp4")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        videoView.setAssetFileDescriptor(afd)
        //播放状态监听
        videoView.addOnStateChangeListener(mOnStateChangeListener)
        videoView.isMute = true
        videoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP)
        videoView.start()
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

    private fun getIMToken() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.im_token_Url)
            }
        }, object : SDOkHttpResoutCallBack<IMTokenGetEntity>() {
            override fun onSuccess(entity: IMTokenGetEntity) {
                getTrafficFrom(entity)
            }

            override fun onFailure(code: Int, msg: String) {
                if (code == 700) {
                    showToast(mActivity.getString(R.string.account_logout_tip))
                    UserUtil.out(mActivity)
                }else{
                    if (retryNum < 3){
                        getIMToken()
                        retryNum++
                    }
                }
            }
        },true)
        HttpRequestInterceptor.addInterceptor()
    }

    /**
     * 获取加白控制判断
     */
    private fun getTrafficFrom(tokenEntity: IMTokenGetEntity) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.traffic_from_url)
            }
        }, object : SDOkHttpResoutCallBack<TrafficEntity>() {
            override fun onSuccess(entity: TrafficEntity) {
                BaseConfig.getInstance.setInt(SpName.trafficSource,entity.data.trafficSource?:0)
                RongConfigUtil.connectIM(tokenEntity.data.token,mActivity)
            }

            override fun onFailure(code: Int, msg: String) {
                BaseConfig.getInstance.setInt(SpName.trafficSource,0)
                RongConfigUtil.connectIM(tokenEntity.data.token,mActivity)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.pause()
        videoView.release()
    }

    //实现“onRequestPermissionsResult”函数接收校验权限结果
    override fun onEventMainThread(event: SDBaseEvent) {
        when (EnumEventTag.valueOf(event.tagInt)) {
            EnumEventTag.START_VIDEO_STOP -> {
                if (videoView.currentPlayState == VideoView.STATE_PLAYING) {
                    videoView.pause()
                }
            }
            EnumEventTag.START_REQUEST -> {
                if (BaseConfig.getInstance.getBoolean(SpName.profileComplete, false) &&
                    BaseConfig.getInstance.getString(SpName.token, "") == ""
                ) {
                    getIMToken()
                }

            }
            else->{

            }
        }
    }

    fun setRongConfig() {
        RongConfigUtil.updatePortrait()
        RongConfigUtil.configurationRong()
    }


    override val imgOrganic: ImageView
        get() = findViewById(R.id.img_organic)
    override val networkErrorContainer: LinearLayout
        get() = findViewById(R.id.network_error_container)
    override val networkErrorTryAgain: TextView
        get() = findViewById(R.id.network_error_try_again)
    override val christmasCountdown: TextView
        get() = findViewById(R.id.christmas_countdown)
    override val christmasCountdownContainer: ConstraintLayout
        get() = findViewById(R.id.christmas_countdown_container)
    override val videoView: VideoView
        get() = findViewById(R.id.video_view)
}