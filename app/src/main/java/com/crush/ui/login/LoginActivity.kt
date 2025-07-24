package com.crush.ui.login


import android.content.Intent
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.crush.R
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.util.GoogleUtil
import com.crush.util.SystemUtils
import com.crush.mvp.MVPBaseActivity
import com.custom.base.util.ToastUtil
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gyf.immersionbar.ImmersionBar
import com.sunday.eventbus.SDBaseEvent
import io.rong.imkit.event.EnumEventTag


/**
 * 作者：
 * 时间：
 * 描述：登录
 */

class LoginActivity : MVPBaseActivity<LoginContract.View, LoginPresenter>(), LoginContract.View {

    private lateinit var oneTapClient: SignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun bindLayout(): Int {
        return R.layout.act_login
    }

    override fun setFullScreen(): Boolean {
        return true

    }

    //实现“onRequestPermissionsResult”函数接收校验权限结果
    override fun onEventMainThread(event: SDBaseEvent) {
        when (EnumEventTag.valueOf(event.tagString)) {
            EnumEventTag.LOGIN_FINISH -> {
                finish()
            }

            else -> {}
        }
    }

    override fun initView() {
        googleSignInClient = GoogleUtil().googleLogin(this)

        auth = Firebase.auth

        oneTapClient = Identity.getSignInClient(this)


        txtLoginGoogle.setOnClickListener {
            if (SystemUtils.isConnected()) {
                actionLoading.visibility = View.VISIBLE
                actionLoading.playAnimation()
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, REQ_ONE_TAP)
            } else {
                ToastUtil.toast(getString(R.string.ooops_network_error))
            }
            DotLogUtil.setEventName(DotLogEventName.LOGING).add("type", "googleLogin")
                ?.commit(mActivity)
        }

        viewRecallHint.setOnClickListener {
            it.isVisible = false
        }
    }


    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity

    override fun onResume() {
        super.onResume()
        ImmersionBar.with(this).statusBarDarkFont(true).init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    mPresenter?.getLoginToken(account, "2")
                } catch (e: ApiException) {
                    actionLoading.visibility = View.GONE
                    actionLoading.pauseAnimation()
                    showToast("Google sign in failed")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        actionLoading.cancelAnimation()
    }

    override val txtLoginProtocol: TextView
        get() = findViewById(R.id.txt_login_protocol)
    override val cbUserAgreement: CheckBox
        get() = findViewById(R.id.cb_user_agreement)
    override val txtLoginGoogle: LinearLayout
        get() = findViewById(R.id.txt_login_google)
    override val txtLoginPhone: LinearLayout
        get() = findViewById(R.id.txt_login_phone)
    override val agreementContainer: ConstraintLayout
        get() = findViewById(R.id.agreement_container)
    override val loginContainer: ConstraintLayout
        get() = findViewById(R.id.login_container)
    override val actionLoading: LottieAnimationView
        get() = findViewById(R.id.action_loading)
    override val imgLoginLogo: ImageView
        get() = findViewById(R.id.img_login_logo)
    override val viewRecallHint: FrameLayout
        get() = findViewById(R.id.viewRecallHint)
    override val tvRecallContent: TextView
        get() = findViewById(R.id.tvRecallContent)

}