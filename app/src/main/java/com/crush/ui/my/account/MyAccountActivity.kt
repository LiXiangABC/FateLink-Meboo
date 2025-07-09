package com.crush.ui.my.account

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.R
import io.rong.imkit.event.EnumEventTag
import com.crush.mvp.MVPBaseActivity
import com.gyf.immersionbar.ImmersionBar
import com.sunday.eventbus.SDBaseEvent
import com.yalantis.ucrop.util.DensityUtil


class MyAccountActivity : MVPBaseActivity<MyAccountContract.View, MyAccountPresenter>(), MyAccountContract.View {

    override fun setFullScreen(): Boolean {
        return true
    }

    override fun getTitleTextView(tv: TextView) {
        tv.text=getString(R.string.my_account)
    }

    override fun setTitleBgColor(): Int {
        return Color.parseColor("#1A1B1E")
    }

    override fun bindLayout(): Int {
        return R.layout.act_my_account
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<LinearLayout>(R.id.viewMain).setPadding(
            0,
            DensityUtil.getStatusBarHeight(this@MyAccountActivity),
            0,
            0
        )
    }
    override fun initView() {
        ImmersionBar.with(this)
            .barAlpha(0f).statusBarDarkFont(false)
            .init()
    }

    //实现“onRequestPermissionsResult”函数接收校验权限结果
    override fun onEventMainThread(event: SDBaseEvent) {
        when (EnumEventTag.valueOf(event.tagInt)) {
            EnumEventTag.MY_ACCOUNT_REFRESH -> {
                mPresenter?.getData()
            }
            else -> {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter?.onActivityResult(requestCode,resultCode, data)
    }

    override val txtLogOut: TextView
        get() = findViewById(R.id.txt_log_out)
    override val txtDeleteAccount: TextView
        get() = findViewById(R.id.txt_delete_account)
    override val imgLoginLogo: ImageView
        get() = findViewById(R.id.img_login_logo)
    override val txtLoginWay: TextView
        get() = findViewById(R.id.txt_login_way)
    override val googleContainer: ConstraintLayout
        get() = findViewById(R.id.google_container)
    override val txtGoogleBindStatus: TextView
        get() = findViewById(R.id.txt_google_bind_status)
    override val facebookContainer: ConstraintLayout
        get() = findViewById(R.id.facebook_container)
    override val txtFacebookBindStatus: TextView
        get() = findViewById(R.id.txt_facebook_bind_status)
    override val phoneNumberContainer: ConstraintLayout
        get() = findViewById(R.id.phone_number_container)
    override val txtPhoneNumberBindStatus: TextView
        get() = findViewById(R.id.txt_phone_number_bind_status)
}