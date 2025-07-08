package com.crush.ui.my.settings

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.crush.mvp.BaseView

class SettingContact {
    interface View : BaseView {
        val imgUserAvatar:ImageView
        val txtVersionCode:TextView
        val containerMyAccount:LinearLayout
        val containerPrivacyPolicy:LinearLayout
        val containerTerms:LinearLayout
    }
}