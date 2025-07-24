package com.crush


import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.adjust.sdk.OnAttributionChangedListener
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieConfig
import com.crush.dot.DotLogUtil
import com.crush.entity.ADEntity
import com.crush.service.AdjustLifecycleCallbacks
import com.crush.util.ChannelUtil
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.zhy.autolayout.config.AutoLayoutConifg
import io.rong.dot.DotInit
import io.rong.dot.listener.ImDotExecuteListener
import io.rong.imkit.IMCenter
import io.rong.imkit.SpName
import io.rong.imkit.utils.JsonUtils
import io.rong.imlib.IRongCoreEnum
import io.rong.imlib.RongCoreClient
import io.rong.imlib.model.InitOption
import io.rong.imlib.model.InitOption.AreaCode
import io.rong.push.RongPushClient
import io.rong.push.pushconfig.PushConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.util.Locale


/**
 * 作者：
 * 日期：2022/3/1
 * 说明：
 */
class App : Application() {
    private var isDebug = false
    private var source = ""

    companion object {
        var appInterface: Application? = null
        fun applicationContext(): Context? {
            return appInterface?.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        appInterface = this
        AutoLayoutConifg.getInstance().useDeviceSize()
        initTextAndFormal(resources.getString(R.string.type))
        val appKey = getString(R.string.rongyun_key)
        val areaCode = AreaCode.NA

        val config: PushConfig = PushConfig.Builder()
            .enableFCM(true)
            .build()
        RongPushClient.setPushConfig(config)
        val initOption = InitOption.Builder()
            .setAreaCode(areaCode)
            .enablePush(true)
            .setNaviServer("nav.us-light-edge.com")
            .build()
        RongCoreClient.setServerInfo("nav.us-light-edge.com", null)
        RongCoreClient.getInstance().setPushLanguageCode(IRongCoreEnum.PushLanguage.EN_US.name,null)
        IMCenter.init(this, appKey, initOption)
        BaseConfig.getInstance.init(
            this,
            debug = isDebug,
            printLog = isDebug,
            debugUrl = "https://api-dev.usalovehere.top",
            url = "https://bff.fatelink123.top",
            successCode = "0",
            successCodeName = "code",
            successMsgName = "msg",
            pageName = "pageNum",
            pageSizeName = "pageSize",
            isHttpFailToast = true,
            titleBold = true,
            URLDecoder = false
        )

        BaseConfig.getInstance.setString(SpName.source, source)
        val jsonObject = JSONObject()
        jsonObject.put("timestamp", System.currentTimeMillis())
        jsonObject.put("channel", ChannelUtil.getChannel())
        jsonObject.put("appsFlyerUID", "")
        jsonObject.put("appCode", "fatelink")
        jsonObject.put("applicationID", BuildConfig.APPLICATION_ID)
        jsonObject.put("appVersion", BuildConfig.VERSION_CODE)
        jsonObject.put("token", BaseConfig.getInstance.getString(SpName.token, ""))
        jsonObject.put("appVersionName", BuildConfig.VERSION_NAME)
        jsonObject.put("appSource", "1")
        jsonObject.put("countryName", Locale.getDefault().country)
        BaseConfig.getInstance.addHead("basicParams", jsonObject.toString())
        initDotInit()
        Fresco.initialize(this)


        Lottie.initialize(
            LottieConfig.Builder()
                .setEnableSystraceMarkers(true)
                .setEnableNetworkCache(false).build()
        )


        FirebaseApp.initializeApp(this)
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)

        // 启动协程来请求接口
        CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(500) // 延迟2秒
                getADorAF()
            } catch (e: Exception) {
            }
        }


    }

    private fun getADorAF() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_config_url)
                requestBody.add("code", 9)
            }
        }, object : SDOkHttpResoutCallBack<ADEntity>() {
            override fun onSuccess(entity: ADEntity) {
                entity.data.ad?.let {
                    setAdjust(it)
                }
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })

    }

    fun setAdjust(token:String){
        val environment = AdjustConfig.ENVIRONMENT_PRODUCTION
        val config = AdjustConfig(this, token, environment)
        config.setLogLevel(LogLevel.VERBOSE)
        config.onAttributionChangedListener = OnAttributionChangedListener { attribution ->
            attribution?.apply {
                val jsonObject = JSONObject()
                jsonObject.put("trackerToken", this.trackerToken)
                jsonObject.put("trackerName", this.trackerName)
                jsonObject.put("network", this.network)
                jsonObject.put("campaign", this.campaign)
                jsonObject.put("adgroup", this.adgroup)
                jsonObject.put("creative", this.creative)
                jsonObject.put("clickLabel", this.clickLabel)
                jsonObject.put("costType", this.costType)
                jsonObject.put("fbInstallReferrer", this.fbInstallReferrer)
                BaseConfig.getInstance.setString(SpName.adChannel, jsonObject.toString())
                val channel = BaseConfig.getInstance.getString(SpName.channel, "")
                if (channel != "" && JsonUtils.isJSON(channel)) {
                    val jsonObject = JSONObject(channel)
                    if (jsonObject.has("af_status")) {
                        val afStatus = jsonObject.getString("af_status")
                        if (afStatus.equals("Organic",true)){
                            jsonObject.put("af_status",this.trackerName)
                        }
                    }
                }else{
                    val jsonObject = JSONObject()
                    jsonObject.put("af_status",this.trackerName)
                    BaseConfig.getInstance.setString(SpName.channel, jsonObject.toString())
                }
            }
        }
        Adjust.initSdk(config)
        Adjust.getGoogleAdId(this) {
            it?.let {
                BaseConfig.getInstance.setString(SpName.googleAdId,it)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())
        }

    }


    /**
     * 判断当前进程是否是主进程
     */
    private fun isMainProcessName(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val processes = manager.runningAppProcesses

        val pid = Process.myPid()
        var processName = ""

        if (processes == null) {
            return false
        }
        for (process in processes) {
            if (process.pid == pid) {
                processName = process.processName
                break
            }
        }
        return processName == BuildConfig.APPLICATION_ID

    }

    /**
     * 获取到adjust归因跟踪链接 上传channel信息
     */

    private fun initDotInit() {
        DotInit.init().setImDotExecuteListener(object : ImDotExecuteListener {
            override fun execute(eventName: String, jsonObject: JSONObject?) {
                DotLogUtil.setEventName(eventName).addJSONObject(jsonObject).commit()
            }
        })
    }


    /**
     * 根据bundle初始化
     * @param string
     */
    private fun initTextAndFormal(string: String) {
        Log.e("initTextAndFormal", string)
        isDebug = string == "yyb"
        source = string
    }
}
