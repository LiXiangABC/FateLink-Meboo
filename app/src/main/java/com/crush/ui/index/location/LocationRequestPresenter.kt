package com.crush.ui.index.location

import android.Manifest
import android.app.Activity
import com.crush.Constant
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.entity.IMTokenGetEntity
import com.crush.rongyun.RongConfigUtil
import com.crush.view.Loading.LoadingDialog
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import com.crush.util.PermissionUtil
import io.rong.imkit.activity.Activities


class LocationRequestPresenter : BasePresenterImpl<LocationRequestContract.View>(),
    LocationRequestContract.Presenter {


    override fun init() {
        mView?.apply {
            requestLocationContainer.setOnClickListener {
                Activities.get().top?.let { it1 ->
                    PermissionUtil.requestPermissionCallBack(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, activity = it1
                    ) {
                        if (it) {
                            getIMToken(mActivity)
                            DotLogUtil.setEventName(DotLogEventName.LOCATION_PERMISSION_GRANTED)
                                .setRemark("400").commit(mActivity)
                        } else {
                            getIMToken(mActivity)
                            DotLogUtil.setEventName(DotLogEventName.LOCATION_PERMISSION_GRANTED)
                                .setRemark("400").commit(mActivity)
                        }
                    }
                }
            }
            requestLocationClose.setOnClickListener {
                getIMToken(mActivity)
            }

            DotLogUtil.setEventName(DotLogEventName.NEW_REGISTER_USER_LOCATION_PERMISSION_GRANT_PAGE).commit(mActivity)
        }
    }

    fun getIMToken(mActivity: Activity) {
        LoadingDialog.showLoading(this.mActivity)
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.im_token_Url)
            }
        }, object : SDOkHttpResoutCallBack<IMTokenGetEntity>() {
            override fun onSuccess(parms: IMTokenGetEntity) {
                RongConfigUtil.connectIM(parms.data.token,mActivity)
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }

}