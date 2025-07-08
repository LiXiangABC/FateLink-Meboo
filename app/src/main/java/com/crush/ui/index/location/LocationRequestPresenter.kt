package com.crush.ui.index.location

import android.Manifest
import android.app.Activity
import android.util.Log
import com.crush.Constant
import com.crush.R
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.entity.IMTokenGetEntity
import com.crush.rongyun.RongConfigUtil
import com.crush.util.PermissionUtils
import com.crush.view.Loading.LoadingDialog
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import io.rong.imkit.SpName
import io.rong.imkit.utils.JsonUtils
import org.json.JSONObject


class LocationRequestPresenter : BasePresenterImpl<LocationRequestContract.View>(),
    LocationRequestContract.Presenter {


    override fun init() {
        mView?.apply {
            requestLocationContainer.setOnClickListener {
                PermissionUtils.requestPermission(mActivity,
                    {
                        getIMToken(mActivity)
//                        HttpRequest.commonNotify(403,"400")
                        DotLogUtil.setEventName(DotLogEventName.LOCATION_PERMISSION_GRANTED).setRemark("400").commit(mActivity)
                    },
                    {
                        getIMToken(mActivity)
//                        HttpRequest.commonNotify(403,"400")
                        DotLogUtil.setEventName(DotLogEventName.LOCATION_PERMISSION_GRANTED).setRemark("400").commit(mActivity)
                    },
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
            requestLocationClose.setOnClickListener {
                getIMToken(mActivity)
            }

            val channel = BaseConfig.getInstance.getString(SpName.channel, "")
            val drawableId = if (channel != "" && JsonUtils.isJSON(channel)) {
                val jsonObject = JSONObject(channel)
                if (jsonObject.has("af_status")) {
                    val afStatus = jsonObject.getString("af_status")
                    if (!afStatus.equals("Organic",true)) {
                        R.mipmap.shape_location_bg
                    } else {
                        R.mipmap.shape_location_no_bg
                    }
                } else {
                    R.mipmap.shape_location_no_bg
                }
            } else {
                R.mipmap.shape_location_no_bg
            }
            Log.e("~~~", "init: $drawableId" )
            outSideView.setImageResource(drawableId)
//            HttpRequest.commonNotify(400,"")
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
                LoadingDialog.dismissLoading(this@LocationRequestPresenter.mActivity)
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }

}