package com.crush.dialog

import UserUtil
import android.app.Activity
import android.view.View
import android.widget.TextView
import com.crush.Constant
import com.crush.R
import com.crush.entity.IMTokenGetEntity
import com.crush.view.Loading.LoadingDialog
import com.custom.base.dialog.SDDialogConfirm
import com.custom.base.dialog.SDDialogCustom
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import razerdp.basepopup.BasePopupWindow

class DeletedAccountDialog(var ctx: Activity) :  BasePopupWindow(ctx) {
    init {
        setContentView(R.layout.dialog_my_account)
        initView()
    }

    private fun initView() {
        val dialogCancel = findViewById<TextView>(R.id.dialog_cancel)
        dialogCancel.setOnClickListener {
            dismiss()
        }
        val dialogDeleteAccount = findViewById<TextView>(R.id.dialog_delete_account)
        dialogDeleteAccount.setOnClickListener {
            SDDialogConfirm("Are sure to delete the account? After deletion,you will not be able to retrieve all the account information.").setTextTitle("Delete Account")
                .setTextConfirm("Delete")
                .setTextCancel("Cancel")
                .setTextBackgroundConfirm(R.drawable.shape_solid_red_radius_24)
                .setmListener(object :
                    SDDialogCustom.SDDialogCustomListener {
                    override fun onClickConfirm(v: View, dialog: SDDialogCustom) {
                        LoadingDialog.showLoading(ctx)
                        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                            override fun addBody(requestBody: OkHttpBodyEntity) {
                                requestBody.setPost(Constant.user_logout_url)
                            }
                        },object : SDOkHttpResoutCallBack<IMTokenGetEntity>() {
                            override fun onSuccess(parms: IMTokenGetEntity) {
                                Firebase.auth.currentUser?.delete()
                                    ?.addOnCompleteListener { task ->
                                        LoadingDialog.dismissLoading(ctx)
                                        UserUtil.out(ctx)
                                    }
                            }

                            override fun onFailure(code: Int, msg: String) {
                            }
                        })

                    }
                }).show()
            dismiss()
        }
        setOutSideDismiss(true)
    }
}