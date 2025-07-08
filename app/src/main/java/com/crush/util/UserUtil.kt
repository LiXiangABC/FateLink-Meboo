import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.crush.BuildConfig
import com.crush.Constant
import io.rong.imkit.SpName
import com.crush.entity.BaseStringEntity
import com.crush.ui.login.LoginActivity
import com.crush.util.ChannelUtil
import com.crush.util.GoogleUtil
import com.google.firebase.messaging.FirebaseMessaging
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import com.crush.util.IntentUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.rong.imkit.RongIM
import org.json.JSONObject
import java.util.Locale

object UserUtil {
    /**
     * 退出登录
     */
    fun out(context: Activity) {
        val jsonObject = JSONObject()
        jsonObject.put("timestamp", System.currentTimeMillis())
        jsonObject.put("channel", ChannelUtil.getChannel())
        jsonObject.put("appsFlyerUID", "")
        jsonObject.put("appCode", "auramix")
        jsonObject.put("applicationID", BuildConfig.APPLICATION_ID)
        jsonObject.put("appVersion", BuildConfig.VERSION_CODE)
        jsonObject.put("token", "")
        jsonObject.put("appVersionName", BuildConfig.VERSION_NAME)
        jsonObject.put("appSource", "1")
        jsonObject.put("countryName", Locale.getDefault().country)
        BaseConfig.getInstance.addHead("basicParams", jsonObject.toString())
        BaseConfig.getInstance.setString(SpName.token, "")
        BaseConfig.getInstance.setInt(SpName.buyType,-1)
        FirebaseMessaging.getInstance().deleteToken()
        RongIM.getInstance().logout()
        if (Firebase.auth.uid != null) {
            Firebase.auth.signOut()
        }
        GoogleUtil().googleLogin(context).signOut()
        startLoginOut(context)
    }

    /**
     * 删除账号
     */
    fun deleteAccount(context: Activity) {
        val jsonObject = JSONObject()
        jsonObject.put("timestamp", System.currentTimeMillis())
        jsonObject.put("channel", ChannelUtil.getChannel())
        jsonObject.put("appsFlyerUID", "")
        jsonObject.put("appCode", "auramix")
        jsonObject.put("applicationID", BuildConfig.APPLICATION_ID)
        jsonObject.put("appVersion", BuildConfig.VERSION_CODE)
        jsonObject.put("token", "")
        jsonObject.put("appVersionName", BuildConfig.VERSION_NAME)
        jsonObject.put("appSource", "1")
        jsonObject.put("countryName", Locale.getDefault().country)
        BaseConfig.getInstance.addHead("basicParams", jsonObject.toString())
        BaseConfig.getInstance.setString(SpName.token, "")
        BaseConfig.getInstance.setInt(SpName.buyType,-1)
        FirebaseMessaging.getInstance().deleteToken()
        GoogleUtil().googleLogin(context).signOut()
        RongIM.getInstance().logout()
        if (Firebase.auth.uid != null) {
            Firebase.auth.signOut()
        }
        startLoginOut(context,true)
    }


    fun startLogin(activity: Activity?) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_config_url)
                requestBody.add("code", 4)
            }
        }, object : SDOkHttpResoutCallBack<BaseStringEntity>() {
            override fun onSuccess(entity: BaseStringEntity) {
                val bundle = Bundle()
                bundle.putString("loginWay", entity.data)
                IntentUtil.startActivity(LoginActivity::class.java, bundle, activity = activity)
                activity?.finish()
            }

            override fun onFailure(code: Int, msg: String) {
                IntentUtil.startActivity(LoginActivity::class.java, activity = activity)
            }
        })
    }

    fun startLoginOut(context: Activity, isDelete: Boolean? = false) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_config_url)
                requestBody.add("code", 4)
            }
        }, object : SDOkHttpResoutCallBack<BaseStringEntity>() {
            override fun onSuccess(entity: BaseStringEntity) {
                val bundle = Bundle()
                if (isDelete == true){
                    bundle.putBoolean("isDeleteAccount", isDelete)
                }
                bundle.putString("loginWay", entity.data)
                IntentUtil.startActivity(LoginActivity::class.java, bundle)
                SDActivityManager.instance.finishAllActivityExcept(LoginActivity::class.java)
            }

            override fun onFailure(code: Int, msg: String) {
                IntentUtil.startActivity(LoginActivity::class.java, activity = context)
                SDActivityManager.instance.finishAllActivityExcept(LoginActivity::class.java)
            }
        })
    }

}