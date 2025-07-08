package com.crush.ui.my.benefit

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.mvp.BaseView
import com.kenny.separatededittext.SeparatedEditText

/**
 * @Author ct
 * @Date 2024/4/12 11:50
 */
class PasswordExchangeContact {
    interface View : BaseView {
        val conPwdExchangeMain:ConstraintLayout
        val conPwdExchangeContainers:ConstraintLayout
        val editPwdExchanges:SeparatedEditText
        val txtPwdExchangeNexts:TextView
        val ivBack:ImageView
    }
}