package com.crush.ui.login

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.crush.R
import com.crush.view.codeview.VerificationCodeView
import com.crush.mvp.MVPBaseActivity
import com.custom.base.view.SendCodeView
import com.gyf.immersionbar.ImmersionBar
import com.yalantis.ucrop.util.DensityUtil


class PhoneLoginActivity : MVPBaseActivity<PhoneLoginContract.View, PhoneLoginPresenter>(),
    PhoneLoginContract.View {

    override fun setFullScreen(): Boolean {
        return true
    }

    override fun bindLayout(): Int {
        return R.layout.act_phone_login
    }

    override fun getTitleView(toolbar: RelativeLayout) {
        toolbar.setBackgroundColor(Color.parseColor("#FFF7FA"))
    }

    override fun initView() {
        ImmersionBar.with(this).statusBarColor("#FFF7FA")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<RelativeLayout>(R.id.viewMain).setPadding(
            0,
            DensityUtil.getStatusBarHeight(this@PhoneLoginActivity),
            0,
            0
        )
    }
    override fun onBackListener() {
        mPresenter?.onBackListener()
    }

    override val editPhone: EditText
        get() =  findViewById(R.id.edit_phone)
    override val phoneNext: TextView
        get() =  findViewById(R.id.phone_next)
    override val phoneOutsideContainer: ConstraintLayout
        get() =  findViewById(R.id.phone_outside_container)
    override val codeOutsideContainer: ConstraintLayout
        get() =  findViewById(R.id.code_outside_container)
    override val textLoginPhone: TextView
        get() =  findViewById(R.id.text_login_phone)
    override val editCode: VerificationCodeView
        get() =  findViewById(R.id.edit_code)
    override val actLoginCode: SendCodeView
        get() =  findViewById(R.id.act_login_code)
    override val codeNext: TextView
        get() = findViewById(R.id. code_next)
    override val bottomLine: View
        get() =  findViewById(R.id.bottom_line)
    override val phoneNextAnim: LottieAnimationView
        get() =  findViewById(R.id.phone_next_anim)
    override val resendCodeAnim: LottieAnimationView
        get() =  findViewById(R.id.resend_code_anim)
    override val codeNextAnim: LottieAnimationView
        get() =  findViewById(R.id.code_next_anim)
    override val phoneNextContainer: LinearLayout
        get() =  findViewById(R.id.phone_next_container)
    override val codeNextContainer: LinearLayout
        get() =  findViewById(R.id.code_next_container)

    override fun onDestroy() {
        super.onDestroy()
        if (phoneNextAnim != null) {
            phoneNextAnim.cancelAnimation()
        }
        if (resendCodeAnim!=null) {
            resendCodeAnim.cancelAnimation()
        }
        if (codeNextAnim!=null) {
            codeNextAnim.cancelAnimation()
        }
    }
}