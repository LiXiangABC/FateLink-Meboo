package com.crush.ui.login

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.crush.BuildConfig
import com.crush.Constant
import com.crush.R
import io.rong.imkit.SpName
import com.crush.entity.IMTokenGetEntity
import com.crush.entity.LoginParamEntity
import com.crush.entity.PhoneCodeEntity
import com.crush.entity.RegisterConfigEntity
import com.crush.entity.SMSConfigsEntity
import com.crush.entity.TrafficEntity
import io.rong.imkit.event.EnumEventTag
import com.crush.rongyun.RongConfigUtil
import com.crush.ui.my.profile.AddProfileActivity
import com.crush.util.ChannelUtil
import com.crush.util.SoftInputUtils
import com.crush.util.SystemUtils
import com.crush.view.Loading.LoadingDialog
import com.crush.view.codeview.VerificationCodeView
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import com.custom.base.util.ToastUtil
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import com.sunday.eventbus.SDEventManager
import java.util.Locale
import java.util.concurrent.TimeUnit


class PhoneLoginPresenter : BasePresenterImpl<PhoneLoginContract.View>(), PhoneLoginContract.Presenter {

    private var storedVerificationId: String=""
    private var resendToken: PhoneAuthProvider.ForceResendingToken?=null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mCredential: PhoneAuthCredential?=null
    private var smsCodeShow:Boolean=false
    var auth = Firebase.auth
    private var receiveCode:Boolean=false
    var mBundle: Bundle?=null
    var loginType:Int = 0
    var mEntity: RegisterConfigEntity?=null

    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            mBundle=bundle
            mEntity = if (bundle.getSerializable("entity")!= null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getSerializable("entity", RegisterConfigEntity::class.java)
                } else {
                    bundle.getSerializable("entity") as RegisterConfigEntity
                }
            } else null

            SoftInputUtils.showSoftInput(editPhone, InputType.TYPE_CLASS_NUMBER)
            editPhone.addTextChangedListener {
                phoneNext.isEnabled=editPhone.text.toString().isNotEmpty()
                phoneNextContainer.setBackgroundResource(if (editPhone.text.length >=10)R.drawable.shape_solid_pink_radius_26 else R.drawable.shape_solid_gray_radius_26)
            }
            phoneNextContainer.setOnClickListener {
                if(BuildConfig.DEBUG){
                    loginPhoneOther()
                }else {
                    if (SystemUtils.isConnected()) {
                        if (editPhone.text.toString().length < 10) {
                            showToast(mActivity.getString(R.string.phone_number_error))
                            return@setOnClickListener
                        }
                        textLoginPhone.text = "+1${editPhone.text}"
                        SoftInputUtils.hideSoftInput(editPhone)
                        phoneNextAnim.visibility = View.VISIBLE
                        phoneNextAnim.playAnimation()
                        getSmsConfig()
                    } else {
                        ToastUtil.toast(mActivity.getString(R.string.ooops_network_error))
                    }
                }

            }
            actLoginCode.setOnClickListener {
                resendCodeAnim.visibility=View.VISIBLE
                resendCodeAnim.playAnimation()
                if (resendToken==null){
                    startPhoneNumberVerification(textLoginPhone.text.toString())
                }else{
                    resendVerificationCode(textLoginPhone.text.toString(),resendToken)
                }

            }

            actLoginCode.addTextChangedListener {
                bottomLine.setBackgroundColor(if (actLoginCode.isSend)ContextCompat.getColor(mActivity,R.color.color_999999) else ContextCompat.getColor(mActivity,R.color.color_202323))
            }
            editCode.setInputCompleteListener(object :VerificationCodeView.InputCompleteListener{
                override fun inputComplete() {
                    codeNextContainer.isEnabled=editCode.inputContent.isNotEmpty()
                    codeNext.isEnabled=editCode.inputContent.isNotEmpty()
                    codeNextContainer.setBackgroundResource(if (editCode.inputContent.isNotEmpty())R.drawable.shape_solid_pink_radius_26 else R.drawable.shape_solid_gray_radius_26)

                }

                override fun deleteContent() {
                    codeNextContainer.isEnabled=editCode.inputContent.isNotEmpty()
                    codeNext.isEnabled=editCode.inputContent.isNotEmpty()
                    codeNextContainer.setBackgroundResource(if (editCode.inputContent.isNotEmpty())R.drawable.shape_solid_pink_radius_26 else R.drawable.shape_solid_gray_radius_26)

                }

            })



            codeNextContainer.setOnClickListener{
                if(editCode.inputContent.length<6){
                    showToast("code error")
                    return@setOnClickListener
                }
                codeNextAnim.visibility=View.VISIBLE
                codeNextAnim.playAnimation()
                if (loginType == 1) {
                    if (mCredential != null) {
                        signInWithPhoneAuthCredential(mCredential!!)
                    } else {
                        if (storedVerificationId != "") {
                            val credential = PhoneAuthProvider.getCredential(
                                storedVerificationId,
                                editCode.inputContent
                            )
                            signInWithPhoneAuthCredential(credential)
                        }
                    }
                }else{
                    loginPhoneOther()
                }
                SoftInputUtils.hideSoftInput(editCode)
            }

            callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    val inputStr = credential.smsCode
                    val strArray: Array<String> =
                        (inputStr?.split("".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                            ?: arrayOf())

                    for (i in strArray.indices) {
                        editCode.setText(strArray[i])
                    }
                    mCredential=credential
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("~~~", "onVerificationFailed", e)
                    LoadingDialog.dismissLoading(mActivity)
                    phoneNextAnim.visibility=View.GONE
                    phoneNextAnim.pauseAnimation()
                    resendCodeAnim.visibility=View.GONE
                    when (e) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            // Invalid request
                            showToast(mActivity.getString(R.string.code_expire_error))
                        }

                        is FirebaseTooManyRequestsException -> {
                            // The SMS quota for the project has been exceeded
                            showToast("Failed to send the verification code. Please use another login method")
                        }

                    }

                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    Log.e("~~~", "onCodeSent:$verificationId   ----- $token")
                    receiveCode = true
                    // Save verification ID and resending token so we can use them later
                    storedVerificationId = verificationId
                    resendToken = token
                    actLoginCode.startTime()
                    if (phoneOutsideContainer.isVisible) {
                        phoneOutsideContainer.visibility = View.GONE
                        codeOutsideContainer.visibility = View.VISIBLE
                        phoneNextAnim.visibility=View.GONE
                        phoneNextAnim.pauseAnimation()
                        smsCodeShow=true
                    }else{
                        resendCodeAnim.visibility=View.GONE
                        resendCodeAnim.pauseAnimation()
                    }

                }

                override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                    Log.e("~~~", "onCodeAutoRetrievalTimeOut: $verificationId" )
                }

            }

        }
    }

    private fun loginPhone(user: FirebaseUser?) {
        mView?.apply {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    if (mBundle?.getString("source") ==null) {
                        requestBody.setPost(Constant.login_Url)
                        requestBody.add("loginType", 1)
                    }else {
                        requestBody.setPost(Constant.account_bind_Url)
                        requestBody.add("accountType", 1)
                    }
                    requestBody.add("account", user?.uid ?: "")
                    requestBody.add("mobile", textLoginPhone.text.toString())
                }

            }, object : SDOkHttpResoutCallBack<LoginParamEntity>() {
                override fun onSuccess(entity: LoginParamEntity) {
                    if (mBundle?.getString("source") ==null) {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("applicationID", BuildConfig.APPLICATION_ID)
                        jsonObject.addProperty("appsFlyerUID","")
                        jsonObject.addProperty("timestamp", System.currentTimeMillis())
                        jsonObject.addProperty("channel", ChannelUtil.getChannel())
                        jsonObject.addProperty("appCode", "auramix")
                        jsonObject.addProperty("appVersion", BuildConfig.VERSION_CODE)
                        jsonObject.addProperty("token", entity.data.token)
                        jsonObject.addProperty("appVersionName", BuildConfig.VERSION_NAME)
                        jsonObject.addProperty("appSource", "1")
                        jsonObject.addProperty("countryName", Locale.getDefault().country)
                        BaseConfig.getInstance.addHead("basicParams", jsonObject.toString())
                        BaseConfig.getInstance.setString(SpName.token, entity.data.token)
                        BaseConfig.getInstance.setString(SpName.userCode,entity.data.userCode)
                        if (entity.data.nickName != null) {
                            BaseConfig.getInstance.setString(SpName.nickName, entity.data.nickName)
                        }
                        if (entity.data.avatarUrl!= null) {
                            BaseConfig.getInstance.setString(SpName.avatarUrl, entity.data.avatarUrl)
                        }
                        BaseConfig.getInstance.setBoolean(
                            SpName.profileComplete,
                            entity.data.isUserValid
                        )
                        if (entity.data.isUserValid) {
                            getTrafficFrom(entity)
                        } else {
                            BaseConfig.getInstance.setInt(SpName.trafficSource,entity.data.trafficControlEnableSwitch?:0)
                            startActivity(AddProfileActivity::class.java)
                            finish()
//                            if (mEntity!= null){//当注册流程的数据没有加载成功时，再次请求接口，确保到注册流程时接口有数据
//                                val bundle = Bundle()
//                                bundle.putSerializable("entity",mEntity)
//                                startActivity(AddProfileActivity::class.java,bundle)
//                                finish()
//                            }else{
//                                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
//                                    override fun addBody(requestBody: OkHttpBodyEntity) {
//                                        requestBody.setPost(Constant.register_config_url)
//                                    }
//                                }, object : SDOkHttpResoutCallBack<RegisterConfigEntity>() {
//                                    override fun onSuccess(entity: RegisterConfigEntity) {
//                                        mEntity=entity
//                                        Thread{
//                                            val imageList= arrayListOf<String>()
//                                            for (i in 0 until entity.data.configs.size){
//                                                val split = entity.data.configs[i].backgroundPhoto.split("|")
//                                                imageList.addAll(split)
//                                            }
//                                            for(i in 0 until imageList.size){
//                                                Glide.with(mActivity)
//                                                    .load(imageList[i])
//                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                                    .preload()
//                                            }
//                                        }.start()
//                                        val bundle = Bundle()
//                                        bundle.putSerializable("entity",mEntity)
//                                        startActivity(AddProfileActivity::class.java,bundle)
//                                        finish()
//                                    }
//                                })
//                            }
                        }
                    }else{
                        SDEventManager.post(EnumEventTag.MY_ACCOUNT_REFRESH.ordinal)
                        finish()
                    }
                }

                override fun onFailure(code: Int, msg: String) {
                    mView?.apply {
                        codeNextAnim.visibility=View.GONE
                        codeNextAnim.pauseAnimation()
                    }
                    showToast(msg)
                }

            })
        }

    }
    private fun loginPhoneOther() {
        mView?.apply {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    if (mBundle?.getString("source") ==null) {
                        requestBody.setPost(Constant.login_Url)
                        requestBody.add("loginType", 1)
                    }else {
                        requestBody.setPost(Constant.account_bind_Url)
                        requestBody.add("accountType", 1)
                    }
                    requestBody.add("verificationCode", editCode.inputContent)
                    requestBody.add("mobile","1"+  editPhone.text.toString())
                    requestBody.add("account",editPhone.text.toString())
                }

            }, object : SDOkHttpResoutCallBack<LoginParamEntity>() {
                override fun onSuccess(entity: LoginParamEntity) {
                    if (mBundle?.getString("source") ==null) {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("applicationID", BuildConfig.APPLICATION_ID)
                        jsonObject.addProperty("appsFlyerUID","")
                        jsonObject.addProperty("timestamp", System.currentTimeMillis())
                        jsonObject.addProperty("channel",ChannelUtil.getChannel())
                        jsonObject.addProperty("appCode", "auramix")
                        jsonObject.addProperty("appVersion", BuildConfig.VERSION_CODE)
                        jsonObject.addProperty("token", entity.data.token)
                        jsonObject.addProperty("appVersionName", BuildConfig.VERSION_NAME)
                        jsonObject.addProperty("appSource", "1")
                        jsonObject.addProperty("countryName", Locale.getDefault().country)
                        BaseConfig.getInstance.addHead("basicParams", jsonObject.toString())
                        BaseConfig.getInstance.setString(SpName.token, entity.data.token)
                        BaseConfig.getInstance.setString(SpName.userCode,entity.data.userCode)
                        if (entity.data.nickName != null) {
                            BaseConfig.getInstance.setString(SpName.nickName, entity.data.nickName)
                        }
                        if (entity.data.avatarUrl!= null) {
                            BaseConfig.getInstance.setString(SpName.avatarUrl, entity.data.avatarUrl)
                        }
                        BaseConfig.getInstance.setBoolean(
                            SpName.profileComplete,
                            entity.data.isUserValid
                        )
                        if (entity.data.isUserValid) {
                            getTrafficFrom(entity)
                        } else {
                            BaseConfig.getInstance.setInt(SpName.trafficSource,entity.data.trafficControlEnableSwitch?:0)
                            startActivity(AddProfileActivity::class.java)
                            finish()
                        }
                    }else{
                        SDEventManager.post(EnumEventTag.MY_ACCOUNT_REFRESH.ordinal)
                        finish()
                    }
                }

                override fun onFailure(code: Int, msg: String) {
                    mView?.apply {
                        codeNextAnim.visibility=View.GONE
                        codeNextAnim.pauseAnimation()
                    }
                    showToast(msg)
                }

            })
        }

    }
    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(mActivity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?,
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(mActivity) // (optional) Activity for callback binding
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(mActivity) { task ->
                receiveCode=false
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    loginPhone(user)

                } else {
                    mView?.apply {
                        codeNextAnim.visibility=View.GONE
                        codeNextAnim.pauseAnimation()
                    }

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        if((task.exception as FirebaseAuthInvalidCredentialsException).errorCode=="ERROR_INVALID_VERIFICATION_CODE"){
                            showToast("code error")
                        }else{
                            showToast(task.exception?.message.toString())
                        }
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }


    private fun PhoneLoginContract.View.sendCode() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.login_code_Url)
                requestBody.add("phone", "1"+editPhone.text.toString())
            }
        }, object : SDOkHttpResoutCallBack<PhoneCodeEntity>() {
            override fun onSuccess(entity: PhoneCodeEntity) {
                phoneOutsideContainer.visibility = View.GONE
                codeOutsideContainer.visibility = View.VISIBLE
                phoneNextAnim.visibility=View.GONE
                phoneNextAnim.pauseAnimation()
                smsCodeShow=true
                actLoginCode.startTime()
            }

            override fun onFailure(code: Int, msg: String) {
                phoneNextAnim.visibility=View.GONE
                phoneNextAnim.pauseAnimation()
                when(code){
                    3012->{
                        showToast(msg)
                    }
                    3013->{
                        showToast(mActivity.getString(R.string.code_restrict_error))
                    }

                }
            }
        }, isShowDialog = true, isDelay = false)
    }
    private fun PhoneLoginContract.View.getSmsConfig() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.sms_config_url)
            }
        }, object : SDOkHttpResoutCallBack<SMSConfigsEntity>(false) {
            override fun onSuccess(entity: SMSConfigsEntity) {
                loginType=entity.data
                if (entity.data == 1){
                    if (resendToken==null){
                        startPhoneNumberVerification(textLoginPhone.text.toString())
                    }else{
                        resendVerificationCode(textLoginPhone.text.toString(),resendToken)
                    }
                }else{
                    sendCode()
                }
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }

    fun onBackListener() {
        mView?.apply {
            if (smsCodeShow){
                editCode.clearInputContent()
                phoneOutsideContainer.visibility= View.VISIBLE
                codeOutsideContainer.visibility= View.GONE
                codeNextAnim.visibility=View.GONE
                codeNextAnim.pauseAnimation()
                actLoginCode.isSend=false
                smsCodeShow=false
            }else{
                mActivity.finish()
            }
        }


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