package com.crush.ui.login

import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

/**
 * 作者：
 * 时间：
 * 描述：
 */

class LoginContract {
    interface View : BaseView {
        val txtLoginProtocol:TextView
        val cbUserAgreement:CheckBox
        val txtLoginGoogle: LinearLayout
        val txtLoginPhone: LinearLayout
        val agreementContainer: ConstraintLayout
        val loginContainer: ConstraintLayout
        val actionLoading: LottieAnimationView
        val loginRealBg: ImageView
        val imgLoginText: ImageView
        val imgLoginLogo: ImageView
        val viewRecallHint: FrameLayout
        val tvRecallContent: TextView
    }


    internal interface Presenter : BasePresenter<View>
}
