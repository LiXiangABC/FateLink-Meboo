package com.crush.ui.my.account

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class MyAccountContract {
    interface View : BaseView {
        val txtLogOut: TextView
        val txtDeleteAccount: TextView
        val imgLoginLogo: ImageView
        val txtLoginWay: TextView
        val googleContainer: ConstraintLayout
        val txtGoogleBindStatus: TextView
        val facebookContainer: ConstraintLayout
        val txtFacebookBindStatus: TextView
        val phoneNumberContainer: ConstraintLayout
        val txtPhoneNumberBindStatus: TextView

    }

    internal interface Presenter : BasePresenter<View> {

    }
}
