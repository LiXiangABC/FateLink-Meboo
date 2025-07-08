package com.crush.ui.login

import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.crush.view.codeview.VerificationCodeView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView
import com.custom.base.view.SendCodeView

class PhoneLoginContract {
    interface View : BaseView {
        val editPhone:EditText
        val phoneNext:TextView
        val phoneOutsideContainer:ConstraintLayout
        val codeOutsideContainer:ConstraintLayout
        val textLoginPhone:TextView
        val editCode: VerificationCodeView
        val actLoginCode: SendCodeView
        val codeNext: TextView
        val bottomLine: android.view.View
        val phoneNextAnim:LottieAnimationView
        val resendCodeAnim:LottieAnimationView
        val codeNextAnim:LottieAnimationView
        val phoneNextContainer:LinearLayout
        val codeNextContainer:LinearLayout
    }

    internal interface Presenter : BasePresenter<View> {

    }
}
