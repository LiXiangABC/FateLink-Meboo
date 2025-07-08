package com.crush.ui.my.benefit

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.R
import com.crush.ui.discount.TriggerDiscountActivity
import com.crush.mvp.MVPBaseActivity
import com.kenny.separatededittext.SeparatedEditText
import com.yalantis.ucrop.util.DensityUtil

/**
 * @Author ct
 * @Date 2024/4/12 11:49
 * 口令兑换
 */
class PasswordExchangeActivity: MVPBaseActivity<PasswordExchangeContact.View, PasswordExchangePresenter>(),PasswordExchangeContact.View {
    override fun bindLayout(): Int {
        return R.layout.act_custom_pwd_inputview
    }

    override fun setFullScreen(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        conPwdExchangeMain.setPadding(
            0,
            DensityUtil.getStatusBarHeight(this),
            0,
            0
        )
        ivBack.setOnClickListener { finish() }
        txtPwdExchangeNexts.setOnClickListener {
            startActivity(TriggerDiscountActivity::class.java)
        }
    }

    override val conPwdExchangeMain: ConstraintLayout
        get() = findViewById(R.id.conPwdExchangeMain)

    override val conPwdExchangeContainers: ConstraintLayout
        get() = findViewById(R.id.conPwdExchangeContainer)
    override val editPwdExchanges: SeparatedEditText
        get() = findViewById(R.id.editPwdExchange)
    override val txtPwdExchangeNexts: TextView
        get() = findViewById(R.id.txtPwdExchangeNext)
    override val ivBack: ImageView
        get() = findViewById(R.id.ivBack)
}