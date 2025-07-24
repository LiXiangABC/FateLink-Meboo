package com.crush.ui.my.settings

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.R
import com.crush.mvp.MVPBaseActivity
import com.gyf.immersionbar.ImmersionBar
import com.yalantis.ucrop.util.DensityUtil

class SettingActivity : MVPBaseActivity<SettingContact.View, SettingPresenter>(), SettingContact.View{

    override fun bindLayout(): Int {
       return R.layout.act_setting
    }
    override fun setFullScreen(): Boolean {
        return true
    }
    override fun getTitleTextView(tv: TextView) {
        tv.text=getString(R.string.title_settings)
    }

    override fun setTitleBgColor(): Int {
        return Color.parseColor("#00FFFFFF")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<ConstraintLayout>(R.id.viewMain).setPadding(
            0,
            DensityUtil.getStatusBarHeight(this@SettingActivity),
            0,
            0
        )

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .init()
    }

    override val imgUserAvatar: ImageView
        get() = findViewById(R.id.img_user_avatar)
    override val txtVersionCode: TextView
        get() = findViewById(R.id.txt_version_code)
    override val containerMyAccount: LinearLayout
        get() = findViewById(R.id.container_my_account)
    override val containerPrivacyPolicy: LinearLayout
        get() = findViewById(R.id.container_privacy_policy)
    override val containerTerms: LinearLayout
        get() = findViewById(R.id.container_terms)
}