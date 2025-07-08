package com.crush.ui.my.turnons

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crush.Constant
import com.crush.adapter.UserProfileTurnOnsAdapter
import com.crush.bean.TurnOnsListBean
import com.crush.entity.UserProfileEntity
import com.crush.util.CollectionUtils
import com.crush.view.Loading.LoadingDialog
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import io.rong.imkit.activity.Activities
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class ChooseTurnOnsPresenter : BasePresenterImpl<ChooseTurnOnsContract.View>(),
    ChooseTurnOnsContract.Presenter {

    private lateinit var turnOnsAdapter: UserProfileTurnOnsAdapter
    private var turnOnsData = arrayListOf<TurnOnsListBean>()

    val scope= MainScope()

    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            LoadingDialog.showLoading(mActivity)
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_info_url)
                }
            }, object : SDOkHttpResoutCallBack<UserProfileEntity>() {
                override fun onSuccess(entity: UserProfileEntity) {
                    LoadingDialog.dismissLoading(mActivity)
                    scope.launch {
                        for (i in 0 until entity.data.images.size) {
                            Activities.get().top?.let {
                                Glide.with(it)
                                    .load(entity.data.images[i])
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .preload()
                            }

                        }

                        if (CollectionUtils.isNotEmpty(entity.data.turnOnsList)) {
                            turnOnsData = entity.data.turnOnsList
                            repeat(entity.data.turnOnsList.size) {
                                Activities.get().top?.let {activity->
                                    Glide.with(activity)
                                        .load(entity.data.turnOnsList[it].imageUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .preload()
                                }

                            }
                            turnOnsList.layoutManager = GridLayoutManager(mActivity, 2)
                            (turnOnsList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                            turnOnsAdapter = UserProfileTurnOnsAdapter(turnOnsData, mActivity,
                                object : UserProfileTurnOnsAdapter.OnListener {
                                    override fun onListener(position: Int) {
                                        turnOnsData[position].selected =
                                            if (turnOnsData[position].selected == 0) 1 else 0
                                        turnOnsAdapter.notifyItemChanged(position)
                                        saveProfileInfo(turnOns = turnOnsData)
                                    }

                                })
                            turnOnsList.adapter = turnOnsAdapter

                        }
                    }

                }

                override fun onFailure(code: Int, msg: String) {

                }
            })


        }
    }

    fun saveProfileInfo(
        turnOns: ArrayList<TurnOnsListBean> = arrayListOf(),
    ) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_user_change_url)
                val list = arrayListOf<Int>()
                repeat(turnOns.size) {
                    if (turnOns[it].selected == 1) {
                        list.add(turnOns[it].turnOnsCode)
                    }
                }
                requestBody.add("turnOnsList", list)
            }
        }, object : SDOkHttpResoutCallBack<UserProfileEntity>() {
            override fun onSuccess(entity: UserProfileEntity) {
            }
        })
    }

    fun onDestroy(){
        scope.cancel()
    }



}