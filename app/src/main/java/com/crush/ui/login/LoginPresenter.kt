package com.crush.ui.login

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.adjust.sdk.Adjust
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crush.BuildConfig
import com.crush.Constant
import com.crush.R
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import io.rong.imkit.SpName
import com.crush.entity.AgreementEntity
import com.crush.entity.IMTokenGetEntity
import com.crush.entity.LoginParamEntity
import com.crush.entity.RegisterConfigEntity
import com.crush.entity.TrafficEntity
import com.crush.rongyun.RongConfigUtil
import com.crush.ui.my.profile.AddProfileActivity
import com.crush.util.ChannelUtil
import com.google.gson.JsonObject
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import com.crush.mvp.BasePresenterImpl
import com.crush.util.IntentUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.rong.imkit.activity.RongWebviewActivity
import io.rong.imkit.utils.JsonUtils
import org.json.JSONObject
import java.util.Locale


/**
 * 作者：
 * 时间：
 * 描述：登录
 */
class LoginPresenter : BasePresenterImpl<LoginContract.View>(), LoginContract.Presenter {
    var mEntity: RegisterConfigEntity? = null
    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            val jsonObject = JSONObject()
            jsonObject.put("timestamp", System.currentTimeMillis())
            jsonObject.put("channel", ChannelUtil.getChannel())
            jsonObject.put("appsFlyerUID", "")
            jsonObject.put("appCode", "fatelink")
            jsonObject.put("applicationID", BuildConfig.APPLICATION_ID)
            jsonObject.put("appVersion", BuildConfig.VERSION_CODE)
            jsonObject.put("token", "")
            jsonObject.put("appVersionName", BuildConfig.VERSION_NAME)
            jsonObject.put("appSource", "1")
            jsonObject.put("countryName", Locale.getDefault().country)
            BaseConfig.getInstance.addHead("basicParams", jsonObject.toString())

            val loginWay = bundle.getString("loginWay") ?: ""
            if (loginWay != "") {
                if (JsonUtils.isJSON(loginWay)) {//是json格式
                    txtLoginPhone.visibility = if (JSONObject(loginWay).getString("loginChannel")
                            .contains("phone")
                    ) View.VISIBLE else View.GONE
                }
            }

            val animationEnlarge: Animation =
                AnimationUtils.loadAnimation(mActivity, R.anim.anim_transfer_1000)
            loginContainer.startAnimation(animationEnlarge)
            if (!SDActivityManager.instance.isEmpty) {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(Constant.user_config_url)
                        requestBody.add("code", 2)
                        Adjust.getAdid{adid->
                            requestBody.add("adid", adid)
                        }
                        requestBody.add(
                            "channel",
                            BaseConfig.getInstance.getString(SpName.channel, "")
                        )
                    }
                }, object : SDOkHttpResoutCallBack<AgreementEntity>() {
                    override fun onSuccess(entity: AgreementEntity) {
                        val style = SpannableStringBuilder()
                        style.append(mActivity.getString(R.string.login_protocol))

                        style.setSpan(object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                val b = Bundle()
                                b.putString("url", entity.data.terms)
                                b.putString(
                                    "title",
                                    mActivity.resources.getString(R.string.user_agreement)
                                )
                                startActivity(RongWebviewActivity::class.java, b)
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                ds.isUnderlineText = true
                                ds.color = Color.BLACK
                                ds.clearShadowLayer()
                            }
                        }, 34, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        style.setSpan(object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                val b = Bundle()
                                b.putString("url", entity.data.privacyPolicy)
                                b.putString(
                                    "title",
                                    mActivity.resources.getString(R.string.privacy_items)
                                )
                                startActivity(RongWebviewActivity::class.java, b)
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                ds.isUnderlineText = true
                                ds.color = Color.BLACK
                                ds.clearShadowLayer()
                            }
                        }, 42, 56, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        txtLoginProtocol.text = style
                        txtLoginProtocol.movementMethod = LinkMovementMethod.getInstance()
                    }
                })
            }
            val source = bundle.getBoolean("isDeleteAccount")
            val recallContent = BaseConfig.getInstance.getString(SpName.recallLogingHintContent, "")
            if (source && recallContent.isNotEmpty()) {
                viewRecallHint.isVisible = source
                tvRecallContent.text = recallContent
                Handler().postDelayed(Runnable {
                   viewRecallHint.isVisible = false
                }, 6000)
            }

            txtLoginPhone.setOnClickListener {
                DotLogUtil.setEventName(DotLogEventName.LOGING).add("type", "phoneLogin")?.commit()
                val bundle = Bundle()
                bundle.putSerializable("entity", mEntity)
                IntentUtil.startActivity(PhoneLoginActivity::class.java, bundle)
            }
        }
    }

    fun getLoginToken(user: GoogleSignInAccount, type: String) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.login_Url)
                user.account?.name?.let { requestBody.add("account", it) }
                user.email?.let { requestBody.add("email", it) }
                requestBody.add("loginType", type)
                user.displayName?.let { requestBody.add("userName", it) }
            }
        }, object : SDOkHttpResoutCallBack<LoginParamEntity>() {
            override fun onSuccess(entity: LoginParamEntity) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("applicationID", BuildConfig.APPLICATION_ID)
                jsonObject.addProperty("timestamp", System.currentTimeMillis())
                jsonObject.addProperty("appsFlyerUID", "")
                jsonObject.addProperty("channel", ChannelUtil.getChannel())
                jsonObject.addProperty("appCode", "fatelink")
                jsonObject.addProperty("appVersion", BuildConfig.VERSION_CODE)
                jsonObject.addProperty("token", entity.data.token)
                jsonObject.addProperty("appVersionName", BuildConfig.VERSION_NAME)
                jsonObject.addProperty("appSource", "1")
                jsonObject.addProperty("countryName", Locale.getDefault().country)
                BaseConfig.getInstance.addHead("basicParams", jsonObject.toString())
                BaseConfig.getInstance.setString(SpName.token, entity.data.token)
                BaseConfig.getInstance.setString(SpName.userCode, entity.data.userCode)
                if (entity.data.nickName != null) {
                    BaseConfig.getInstance.setString(SpName.nickName, entity.data.nickName)
                }
                if (entity.data.avatarUrl != null) {
                    BaseConfig.getInstance.setString(SpName.avatarUrl, entity.data.avatarUrl)
                    Glide.with(mActivity)
                        .load(entity.data.avatarUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .preload()
                }
                BaseConfig.getInstance.setBoolean(SpName.profileComplete, entity.data.isUserValid)
                if (entity.data.isUserValid) {
                    getTrafficFrom(entity)
                } else {
                    BaseConfig.getInstance.setInt(SpName.trafficSource,entity.data.trafficControlEnableSwitch?:0)
                    startActivity(AddProfileActivity::class.java)
                    finish()
                }
            }

            override fun onFailure(code: Int, msg: String) {
                mView?.apply {
                    showToast(msg)
                    actionLoading.visibility = View.GONE
                    actionLoading.pauseAnimation()
                }
            }

            override fun onFinish() {
                user.idToken?.let { BaseConfig.getInstance.setString(SpName.googleToken, it) }
            }
        })
    }

    /**
     * 获取加白控制判断
     */
    private fun getTrafficFrom(loginEntity: LoginParamEntity) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.traffic_from_url)
            }
        }, object : SDOkHttpResoutCallBack<TrafficEntity>() {
            override fun onSuccess(entity: TrafficEntity) {
                BaseConfig.getInstance.setInt(SpName.trafficSource,entity.data.trafficSource?:0)
                getToken(loginEntity)
            }

            override fun onFailure(code: Int, msg: String) {
                BaseConfig.getInstance.setInt(SpName.trafficSource,0)
                getToken(loginEntity)
            }
        })
    }

    private fun getToken(loginEntity: LoginParamEntity) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.im_token_Url)
            }
        }, object : SDOkHttpResoutCallBack<IMTokenGetEntity>() {
            override fun onSuccess(parms: IMTokenGetEntity) {
                RongConfigUtil.connectIMLogin(
                    parms.data.token,
                    loginEntity.data.userCode,
                    loginEntity.data.nickName,
                    loginEntity.data.avatarUrl ?: "",
                    mActivity
                )
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }


}