package com.crush.ui.my.profile.info.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.crush.Constant
import com.crush.adapter.UserProfileTurnOnsAdapter
import com.crush.bean.TurnOnsListBean
import com.crush.entity.UserProfileEntity
import com.crush.util.CollectionUtils
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import io.rong.imkit.activity.Activities


class TurnOnsPresenter : BasePresenterImpl<TurnOnsContract.View>(), TurnOnsContract.Presenter {
    private lateinit var turnOnsAdapter: UserProfileTurnOnsAdapter
    private var turnOnsData = arrayListOf<TurnOnsListBean>()
    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            Activities.get().top?.let {
                val data = bundle.getSerializable("entity") as UserProfileEntity.Data
                if (CollectionUtils.isNotEmpty(data.turnOnsList)) {
                    turnOnsData = data.turnOnsList
                }
                turnOnsList?.layoutManager = GridLayoutManager(it, 2)
                (turnOnsList?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                turnOnsAdapter = UserProfileTurnOnsAdapter(
                    turnOnsData,
                    it,
                    object : UserProfileTurnOnsAdapter.OnListener {
                        override fun onListener(position: Int) {
                            turnOnsData[position].selected = if (turnOnsData[position].selected == 0) 1 else 0
                            turnOnsAdapter.notifyItemChanged(position)
                            saveProfileInfo(turnOns = turnOnsData)
                        }

                    })
                turnOnsList?.adapter = turnOnsAdapter
            }

        }
    }


    fun saveProfileInfo(
        turnOns: ArrayList<TurnOnsListBean> = arrayListOf(),
    ) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_user_change_url)
                if (turnOns.isNotEmpty()) {
                    val list = arrayListOf<Int>()
                    repeat(turnOns.size) {
                        if (turnOns[it].selected == 1) {
                            list.add(turnOns[it].turnOnsCode)
                        }
                    }
                    requestBody.add("turnOnsList", list)
                }
            }
        }, object : SDOkHttpResoutCallBack<UserProfileEntity>() {
            override fun onSuccess(entity: UserProfileEntity) {

            }

            override fun onFailure(code: Int, msg: String) {
                showToast(msg)
            }
        })
    }
}
