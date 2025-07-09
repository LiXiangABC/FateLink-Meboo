package com.crush.ui.my.account

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.crush.Constant
import com.crush.R
import com.crush.dialog.CommonDialog
import io.rong.imkit.dialog.CustomDialog
import com.crush.dialog.DeleteMemberEquityRemainingDialog
import com.crush.dialog.DeleteMemberExpireDateDialog
import com.crush.dialog.LoginOutDialog
import com.crush.entity.BaseEntity
import com.crush.entity.DeleteHintEntity
import com.crush.entity.IMTokenGetEntity
import com.crush.entity.LoginParamEntity
import com.crush.entity.QueryBenefitsEntity
import com.crush.entity.ResultDataEntity
import com.crush.entity.UserAccountEntity
import com.crush.ui.login.PhoneLoginActivity
import com.crush.util.GoogleUtil
import com.crush.view.Loading.LoadingDialog
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import com.crush.util.IntentUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.rong.imkit.SpName


class MyAccountPresenter : BasePresenterImpl<MyAccountContract.View>(),
    MyAccountContract.Presenter {
    var mEntity: UserAccountEntity? = null
    var queryBenefitsEntity: QueryBenefitsEntity? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity

    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            getData()
            googleSignInClient = GoogleUtil().googleLogin(mActivity)

            auth = Firebase.auth


            txtLogOut.setOnClickListener {
                LoginOutDialog(mActivity, object : LoginOutDialog.OnCallBack {
                    override fun callBack() {
                        UserUtil.out(mActivity)
                    }

                }).showPopupWindow()
            }
            txtDeleteAccount.setOnClickListener {
                queryBenefits()
                getDeleteHintContent {
                        hintContent->
                    CustomDialog(mActivity)
                        .setLayoutId(R.layout.dialog_delete_account)
                        .setControllerListener {
                            run{
                                it.findViewById<TextView>(R.id.tvDeleteAccountHint).text = hintContent
                            }

                        }
                        .setOnClickListener(R.id.tvCancel) { dialog, view ->
                            run {
                                dialog.dismiss()
                            }
                        }
                        .setOnClickListener(R.id.tvDelete) { dialog, view ->
                            dialog.dismiss()
                            if (mEntity?.data?.expiryDate == null) {
                                if (queryBenefitsEntity != null) {
                                    if (queryBenefitsEntity!!.data[0].maxUses > 0 || queryBenefitsEntity!!.data[1].maxUses > 0 || queryBenefitsEntity!!.data[2].maxUses > 0) {
                                        DeleteMemberEquityRemainingDialog(mActivity,
                                            queryBenefitsEntity!!,
                                            object : DeleteMemberEquityRemainingDialog.OnListener {
                                                override fun onListener() {
                                                    deleteAccount()
                                                }
                                            }).showPopupWindow()
                                        return@setOnClickListener
                                    }
                                }
                                deleteAccount()
                                return@setOnClickListener
                            }
                            DeleteMemberExpireDateDialog(
                                mActivity,
                                mEntity?.data?.expiryDate ?: "",
                                object : DeleteMemberExpireDateDialog.OnListener {
                                    override fun onListener() {
                                        if (queryBenefitsEntity != null) {
                                            Handler().postDelayed({
                                                DeleteMemberEquityRemainingDialog(mActivity,
                                                    queryBenefitsEntity!!,
                                                    object :
                                                        DeleteMemberEquityRemainingDialog.OnListener {
                                                        override fun onListener() {
                                                            deleteAccount()
                                                        }
                                                    }).showPopupWindow()
                                            }, 200)

                                        }
                                    }

                                }).showPopupWindow()

                        }.show()
                }
            }
        }
    }
    private fun getDeleteHintContent(message: (noticeMessage: String) -> Unit) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.delete_account_message)
            }
        }, object : SDOkHttpResoutCallBack<ResultDataEntity<DeleteHintEntity>>() {
            override fun onSuccess(entity: ResultDataEntity<DeleteHintEntity>) {
                entity.data?.loginMessage?.let {
                    BaseConfig.getInstance.setString(SpName.recallLogingHintContent, it)
                }
                Log.d("ddddddd",entity.data?.loginMessage!!)
                entity.data?.noticeMessage?.let { message.invoke(it) }
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }

    fun getData() {
        mView?.apply {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_account_url)
                }

            }, object : SDOkHttpResoutCallBack<UserAccountEntity>() {
                override fun onSuccess(entity: UserAccountEntity) {
                    mEntity = entity
                    when (entity.data.loginType) {
                        1 -> {
                            imgLoginLogo.setImageResource(R.mipmap.icon_login_phone)
                            txtLoginWay.text = mActivity.getText(R.string.phone_number)
                        }

                        2 -> {
                            imgLoginLogo.setImageResource(R.mipmap.icon_google)
                            txtLoginWay.text = mActivity.getText(R.string.google)
                        }

                        3 -> {
                            imgLoginLogo.setImageResource(R.mipmap.icon_login_facebook)
                            txtLoginWay.text = mActivity.getText(R.string.facebook)
                        }
                    }
                    //                    txtGoogleBindStatus.visibility=if (entity.data.googleBind) View.VISIBLE else View.GONE
                    txtGoogleBindStatus.setTextColor(
                        if (entity.data.googleBind) ContextCompat.getColor(
                            mActivity,
                            R.color.color_44F3C4
                        ) else ContextCompat.getColor(mActivity, R.color.color_6544F3C4)
                    )
                    txtGoogleBindStatus.text =
                        if (entity.data.googleBind) entity.data.googleName else mActivity.getString(
                            R.string.connect_now
                        )

                    //                    txtFacebookBindStatus.visibility=if (entity.data.facebookBind) View.VISIBLE else View.GONE
                    txtFacebookBindStatus.setTextColor(
                        if (entity.data.facebookBind) ContextCompat.getColor(
                            mActivity,
                            R.color.color_202323
                        ) else ContextCompat.getColor(mActivity, R.color.color_6544F3C4)
                    )
                    txtFacebookBindStatus.text =
                        if (entity.data.facebookBind) entity.data.facebookName else mActivity.getString(
                            R.string.connect_now
                        )

                    //                    txtPhoneNumberBindStatus.visibility=if (entity.data.mobileBind) View.VISIBLE else View.GONE
                    txtPhoneNumberBindStatus.setTextColor(
                        if (entity.data.mobileBind) ContextCompat.getColor(
                            mActivity,
                            R.color.color_44F3C4
                        ) else ContextCompat.getColor(mActivity, R.color.color_6544F3C4)
                    )
                    txtPhoneNumberBindStatus.text =
                        if (entity.data.mobileBind) entity.data.mobile else mActivity.getString(R.string.connect_now)

                    phoneNumberContainer.setOnClickListener {
                        if (entity.data.mobileBind) {
                            val commonDialog = CommonDialog(mActivity)
                            commonDialog.setTitle(mActivity.getString(R.string.change_phone_number_account))
                            commonDialog.setContent(mActivity.getString(R.string.change_phone_number_account_content))
                            commonDialog.setConfirmText(mActivity.getString(R.string.change))
                            commonDialog.setCancelText(mActivity.getString(R.string.cancel))
                            commonDialog.setConfirmTextBackground(R.drawable.shape_stroke_gray_solid_white_radius_24)
                            commonDialog.setConfirmTextColor(
                                ContextCompat.getColor(
                                    mActivity,
                                    R.color.black
                                )
                            )
                            commonDialog.showPopupWindow()
                            commonDialog.setConfirmListener {
                                val phoneBundle = Bundle()
                                phoneBundle.putString("source", "account")
                                IntentUtil.startActivity(
                                    PhoneLoginActivity::class.java,
                                    phoneBundle
                                )
                                commonDialog.dismiss()
                            }
                        } else {
                            val phoneBundle = Bundle()
                            phoneBundle.putString("source", "account")
                            IntentUtil.startActivity(PhoneLoginActivity::class.java, phoneBundle)
                        }
                    }
                    googleContainer.setOnClickListener {
                        if (entity.data.googleBind) {
                            val commonDialog = CommonDialog(mActivity)
                            commonDialog.setTitle(mActivity.getString(R.string.unbind_google_account))
                            commonDialog.setContent(mActivity.getString(R.string.unbind_google_account_content))
                            commonDialog.setConfirmText(mActivity.getString(R.string.unbind))
                            commonDialog.setCancelText(mActivity.getString(R.string.cancel))
                            commonDialog.setConfirmTextBackground(R.drawable.shape_stroke_gray_solid_white_radius_24)
                            commonDialog.setConfirmTextColor(
                                ContextCompat.getColor(
                                    mActivity,
                                    R.color.black
                                )
                            )
                            commonDialog.showPopupWindow()
                            commonDialog.setConfirmListener {
                                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                    override fun addBody(requestBody: OkHttpBodyEntity) {
                                        requestBody.setPost(Constant.account_remove_Url)
                                        requestBody.add("accountType", 2)
                                    }
                                }, object : SDOkHttpResoutCallBack<IMTokenGetEntity>() {
                                    override fun onSuccess(parms: IMTokenGetEntity) {
                                        showToast(mActivity.getString(R.string.unbind_succeeded))
                                        getData()
                                        GoogleUtil().googleLogin(mActivity).signOut()
                                        commonDialog.dismiss()
                                    }

                                    override fun onFailure(code: Int, msg: String) {
                                        showToast(msg)
                                        commonDialog.dismiss()
                                    }
                                })
                            }
                        } else {
                            LoadingDialog.showLoading(mActivity)
                            val signInIntent = googleSignInClient.signInIntent
                            mActivity.startActivityForResult(signInIntent, REQ_ONE_TAP)
                        }
                    }
                }
            })
        }

    }

    private fun deleteAccount() {
        LoadingDialog.showLoading(mActivity)
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_logout_url)
            }
        }, object : SDOkHttpResoutCallBack<BaseEntity>() {
            override fun onSuccess(parms: BaseEntity) {
                LoadingDialog.dismissLoading(mActivity)
                UserUtil.deleteAccount(mActivity,)
            }

            override fun onFailure(code: Int, msg: String) {
                LoadingDialog.dismissLoading(mActivity)
            }
        })
    }

    fun queryBenefits() {
        mView?.apply {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_member_query_benefits_url)
                }
            }, object : SDOkHttpResoutCallBack<QueryBenefitsEntity>(false) {
                override fun onSuccess(entity: QueryBenefitsEntity) {
                    if (entity.data != null) {
                        if (entity.data.size > 2) {
                            queryBenefitsEntity = entity
                        }
                    }
                }
            })
        }

    }

    fun getLogin(user: FirebaseUser, type: String) {
        LoadingDialog.showLoading(mActivity)
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.account_bind_Url)
                requestBody.add("account", user.uid)
                user.email?.let { requestBody.add("email", it) }
                requestBody.add("accountType", type)
                user.displayName?.let { requestBody.add("userName", it) }
            }
        }, object : SDOkHttpResoutCallBack<LoginParamEntity>() {
            override fun onSuccess(entity: LoginParamEntity) {
                LoadingDialog.dismissLoading(mActivity)
                getData()
            }

            override fun onFailure(code: Int, msg: String) {
                showToast(msg)
                LoadingDialog.dismissLoading()
            }
        })
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(mActivity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    LoadingDialog.dismissLoading(mActivity)
                    user?.let { getLogin(it, "2") }
                }
            }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_ONE_TAP -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    LoadingDialog.dismissLoading(mActivity)
                    showToast("Google sign in failed")
                }
            }
        }
    }

}