package com.crush.ui.like

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crush.Constant
import com.crush.adapter.ILikeAdapter
import io.rong.imkit.entity.WLMListBean
import com.crush.entity.ILikeEntity
import com.crush.view.Loading.LoadingDialog
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.entity.PageModel
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.utils.RongUtils
import io.rong.imkit.utils.StatusBarUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ILikePresenter : BasePresenterImpl<ILikeContract.View>(), ILikeContract.Presenter {
    var page = PageModel()
    private var data: ILikeEntity? = null
    var list = arrayListOf<WLMListBean>()
    var iLikeAdapter: ILikeAdapter? = null

    override fun initBundle(bundle: Bundle) {
        mView?.apply {
            toolbarTitle.setPadding(0, StatusBarUtil.getStatusBarHeight(mActivity), 0, 0)
            iLikeBackLayout.setOnClickListener { finish() }
            txtSeeYourAdmirers.setOnClickListener {
                finish()
                SDEventManager.post("like",EnumEventTag.INDEX_TO_INDEX.ordinal)
            }
            iLikeRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    page.initialRefresh()
                    getData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    page.nextPage()
                    getData()
                }

            })

            iLikeAdapter = ILikeAdapter(list, mActivity)
            iLikeList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            (iLikeList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            iLikeList.adapter = iLikeAdapter
            LoadingDialog.showLoading(mActivity)
            page.request(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_like_url)
                    requestBody.addPage(page)
                }
            }, object : SDOkHttpResoutCallBack<ILikeEntity>() {
                override fun onSuccess(entity: ILikeEntity) {
                    LoadingDialog.dismissLoading(mActivity)
                    data = entity
                    initData(data)

                }

                override fun onFinish() {
                    LoadingDialog.dismissLoading(mActivity)
                    page.onRefreshComplete()
                }
            })
        }
    }

    val scope = MainScope()

    private fun ILikeContract.View.initData(entity: ILikeEntity?) {
        entity?.apply {
            if (page.page == 1) {
                list.clear()
            }
            scope.launch {
                for (i in 0 until entity.data.size) {
                    if (!RongUtils.isDestroy(mActivity)) {
                        Glide.with(mActivity)
                            .load(entity.data[i].avatarUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .preload()
                    }
                }
            }


            list.addAll(entity.data)
            containerEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            topTips.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            iLikeRefreshLayout.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            iLikeRefreshLayout.setNoMoreData(entity.data.size != page.pageSize)
            iLikeRefreshLayout.setEnableFooterFollowWhenNoMoreData(true)
            iLikeAdapter!!.notifyDataSetChanged()

        }

    }


    fun getData() {
        mView?.apply {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_like_url)
                    requestBody.addPage(page)
                }
            }, object : SDOkHttpResoutCallBack<ILikeEntity>() {
                override fun onSuccess(entity: ILikeEntity) {
                    if (page.page == 1) {
                        list.clear()
                    }
                    if (entity.data.isNotEmpty()) {
                        scope.launch {
                            for (i in 0 until entity.data.size) {
                                if (!RongUtils.isDestroy(mActivity)){
                                    Glide.with(mActivity)
                                        .load(entity.data[i].avatarUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .preload()
                                }
                            }
                        }

                        list.addAll(entity.data)
                        iLikeAdapter?.notifyDataSetChanged()
                    }
                    iLikeRefreshLayout.setNoMoreData(entity.data.size != page.pageSize)
                    iLikeRefreshLayout.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                    topTips.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                    containerEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE


                }

                override fun onFinish() {
                    if (!mActivity.isDestroyed) {
                        if (page.page == 1) {
                            iLikeRefreshLayout.finishRefresh(920)
                        } else {
                            iLikeRefreshLayout.finishLoadMore(1000)
                        }
                    }
                }
            })
        }
    }

    fun removeItem(position:Int){
        mView?.apply {
            list.removeAt(position)
            iLikeAdapter!!.notifyDataSetChanged()

            if (list.isEmpty()){
                iLikeRefreshLayout.visibility = View.GONE
                topTips.visibility = View.GONE
                containerEmpty.visibility = View.VISIBLE
            }

        }
    }
}