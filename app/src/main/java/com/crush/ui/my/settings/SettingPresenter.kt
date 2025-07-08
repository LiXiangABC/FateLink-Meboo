package com.crush.ui.my.settings

import android.os.Bundle
import com.crush.BuildConfig
import com.crush.Constant
import com.crush.R
import com.crush.entity.AgreementEntity
import com.crush.ui.my.account.MyAccountActivity
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import com.crush.util.IntentUtil
import io.rong.imkit.activity.RongWebviewActivity

class SettingPresenter : BasePresenterImpl<SettingContact.View>() {

    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            txtVersionCode.text =
                "${mActivity.getString(R.string.app_name)} v${BuildConfig.VERSION_NAME}"

            containerMyAccount.setOnClickListener {
               IntentUtil.startActivity(MyAccountActivity::class.java)
            }


            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_config_url)
                    requestBody.add("code", 2)
                }
            }, object : SDOkHttpResoutCallBack<AgreementEntity>() {
                override fun onSuccess(entity: AgreementEntity) {
                    containerPrivacyPolicy.setOnClickListener {
                        val b = Bundle()
                        b.putString("url", entity.data.privacyPolicy)
                        b.putString("title", mActivity.resources.getString(R.string.privacy_policy))
                        startActivity(RongWebviewActivity::class.java, b)
                    }

                    containerTerms.setOnClickListener {
                        val b = Bundle()
                        b.putString("url", entity.data.terms)
                        b.putString("title", mActivity.resources.getString(R.string.terms))
                        startActivity(RongWebviewActivity::class.java, b)

                    }
                }
            })
        }
    }
}