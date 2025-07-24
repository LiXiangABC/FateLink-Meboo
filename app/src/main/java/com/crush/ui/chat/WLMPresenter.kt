package com.crush.ui.chat

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crush.Constant
import com.crush.R
import com.crush.adapter.WhoLikeMeAdapter
import io.rong.imkit.entity.WLMListBean
import io.rong.imkit.widget.EmptyCallBack
import com.crush.callback.WLMSwipedCallBack
import com.crush.dialog.WlmTipsDialog
import com.crush.entity.WhoLikeMeEntity
import io.rong.imkit.utils.ktl.WlmClick
import com.crush.ui.index.match.MatchUserActivity
import com.crush.util.CollectionUtils
import com.crush.view.SwipeCardCallback
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.entity.PageModel
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.custom.base.manager.SDActivityManager
import com.crush.mvp.BasePresenterImpl
import com.crush.util.IntentUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import io.rong.imkit.dialog.MemberBuyDialog
import io.rong.imkit.entity.MemberSubscribeEntity
import io.rong.imkit.entity.OrderCreateEntity
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.event.FirebaseEventTag
import io.rong.imkit.pay.EmptySuccessCallBack
import io.rong.imkit.pay.PayUtils
import io.rong.imkit.utils.FirebaseEventUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import razerdp.basepopup.BasePopupWindow


class WLMPresenter : BasePresenterImpl<WLMContract.View>(), WLMContract.Presenter {
    private var data: WhoLikeMeEntity.Data? = null
    private var member: Boolean = false
    private var isEnableLoadMore: Boolean = false
    var list = arrayListOf<WLMListBean>()
    var page = PageModel()
    var whoLikeMeAdapter: WhoLikeMeAdapter? = null
    var wlmNum = 0
    var userCanUseWlmNum = 0//用户可使用的wlm次数
    var message = ""
    var content = ""
    var lastVisibleMember = 0

    fun initView(){
        mView?.apply {
            wlmRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    page.initialRefresh()
                    getData()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    page.nextPage()
                    getData()
                }

            })

            whoLikeMeAdapter = Activities.get().top?.let {
                WhoLikeMeAdapter(list, it, object : WLMSwipedCallBack {
                    override fun swipedCallback(direction: Int, wlmListBean: WLMListBean) {
                        FirebaseEventUtils.logEvent(if (direction == ItemTouchHelper.START) FirebaseEventTag.WLM_Pass.name else FirebaseEventTag.WLM_Like.name)
                        val type = if (direction == ItemTouchHelper.START) 2 else 1
                        WlmClick.benefitsReduceWLM(
                            wlmListBean.userCodeFriend,
                            if (direction == ItemTouchHelper.START) 2 else 1,
                            object : EmptyCallBack {
                                override fun OnSuccessListener() {
                                    userAction(direction, wlmListBean)
                                }

                                override fun OnFailListener() {
                                    FirebaseEventUtils.logEvent(if (type == 1) FirebaseEventTag.WLM_Like_Sub.name else FirebaseEventTag.WLM_Pass_Sub.name)
                                    WlmClick.openMemberBuyDialog { orderCreateEntity ->
                                        Activities.get().top?.let { it1 ->
                                            WlmClick.openPay(it1, orderCreateEntity, type) { isSuccess ->
                                                if (isSuccess) {
                                                    page.initialRefresh()
                                                    FirebaseEventUtils.logEvent(if (type == 1) FirebaseEventTag.WLM_Like_Subsuccess.name else FirebaseEventTag.WLM_Pass_Subsuccess.name)
                                                    getData()
                                                    BaseConfig.getInstance.setBoolean(
                                                        SpName.isMember,
                                                        true
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                            })
                    }

                    override fun swipedRefresh() {
                        getData()
                    }

                })
            }
            whoLikeMeList.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            (whoLikeMeList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            whoLikeMeList.adapter = whoLikeMeAdapter
            val swipeCardCallback = SwipeCardCallback(whoLikeMeAdapter, object : WLMSwipedCallBack {
                override fun swipedCallback(direction: Int, wlmListBean: WLMListBean) {
                    userAction(direction, wlmListBean)
                }

                override fun swipedRefresh() {
                    getData()
                }
            })
            val helper = ItemTouchHelper(swipeCardCallback)
            helper.attachToRecyclerView(whoLikeMeList)

            page.request(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_wlm_list_url)
                    requestBody.addPage(page)
                }
            }, object : SDOkHttpResoutCallBack<WhoLikeMeEntity>() {
                override fun onSuccess(entity: WhoLikeMeEntity) {
                    data = entity.data
                    initData(data)

                }

                override fun onFinish() {
                    page.onRefreshComplete()
                }
            })
            txtToEdit.setOnClickListener {
                SDEventManager.post(EnumEventTag.INDEX_TO_INDEX.ordinal)
            }

            txtGetPremiumUnlock.setOnClickListener {
                SDEventManager.post(EnumEventTag.CHAT_LIST_ITEM.ordinal)
                FirebaseEventUtils.logEvent(FirebaseEventTag.WLM_Unlock_Sub.name)
                MemberBuyDialog(
                    SDActivityManager.instance.lastActivity,
                    1,
                    object : MemberBuyDialog.ChangeMembershipListener {
                        override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                                override fun addBody(requestBody: OkHttpBodyEntity) {
                                    requestBody.setPost(Constant.user_create_order_url)
                                    requestBody.add("productCode", bean.productCode)
                                    requestBody.add("productCategory", 1)
                                }

                            }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                override fun onSuccess(entity: OrderCreateEntity) {
                                    Activities.get().top?.let {
                                        PayUtils.instance.start(
                                            entity,
                                            it,
                                            object : EmptySuccessCallBack {
                                                override fun OnSuccessListener() {
                                                    page.initialRefresh()
                                                    getData()
                                                    BaseConfig.getInstance.setBoolean(
                                                        SpName.isMember,
                                                        true
                                                    )
                                                    FirebaseEventUtils.logEvent(FirebaseEventTag.WLM_Unlock_Subsuccess.name)

                                                }

                                            })
                                    }

                                }
                            })
                        }

                        override fun closeListener(refreshTime: Long) {

                        }
                    })
            }
        }
    }

    val scope = MainScope()

    private fun WLMContract.View.initData(entity: WhoLikeMeEntity.Data?) {
        entity?.apply {
            if (page.page == 1) {
                list.clear()
            }
            this@WLMPresenter.wlmNum = entity.wlmNum
            this@WLMPresenter.message = entity.message
            this@WLMPresenter.content=entity.content?:""
            txtWlmNumber.text =
                if (entity.isMember) "$wlmNum sexy girls have already liked you \uD83D\uDC97" else "Go Premium for instant matches and unlocked perks!"
            txtWlmNumber.textSize = if (entity.isMember) 15f else 13f
            userCanUseWlmNum= entity.wlmCount?:0
            SDEventManager.post(wlmNum, EnumEventTag.REFRESH_WLM_LIKE_SIZE.ordinal)
            scope.launch {
                for (i in 0 until entity.wlmList.size) {
                    Activities.get().top?.let {
                        Glide.with(it)
                            .load(entity.wlmList[i].avatarUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .preload()
                    }
                }
                BaseConfig.getInstance.setBoolean(
                    SpName.isMember,
                    entity.isMember
                )
            }


            member = entity.isMember
            whoLikeMeAdapter?.setMember(member)
            list.addAll(entity.wlmList)
            containerEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            txtWlmNumber.visibility = if (member){if (list.isEmpty()) View.GONE else View.VISIBLE}else View.VISIBLE
            wlmRefreshLayout.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            userCanLikeSizeContainer.isVisible= !member && userCanUseWlmNum >0 && list.isNotEmpty()
            if (userCanLikeSizeContainer.isVisible){
                txtWlmSize.text="$userCanUseWlmNum"
            }
//            wlmRefreshLayout.setEnableLoadMore(entity.wlmList.size == page.pageSize)
            wlmRefreshLayout.setNoMoreData(entity.wlmList.size != page.pageSize)
            wlmRefreshLayout.setEnableFooterFollowWhenNoMoreData(true)
            isEnableLoadMore = entity.wlmList.size == page.pageSize
            whoLikeMeAdapter!!.notifyDataSetChanged()
            setPremiumVisibility()

            whoLikeMeList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                //滑动到底部出现购买会员弹窗
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    // 获取最后一个可见的 item 位置
                    val lastVisibleItems = layoutManager.findLastVisibleItemPositions(null)
                    val lastVisibleItemPosition =
                        lastVisibleItems.maxOrNull() ?: RecyclerView.NO_POSITION
                    val totalItemCount = layoutManager.itemCount
                    if (dy < 0) {
                        lastVisibleMember = 0
                    }
                    if (lastVisibleItemPosition == totalItemCount - 1 && dy > 100) {
                        // 已经滑动到最底部
                        // 进行相应的操作
                        if (lastVisibleMember == 0 && !member && !isEnableLoadMore) {
                            Handler().postDelayed({
                                MemberBuyDialog(
                                    SDActivityManager.instance.lastActivity,
                                    1,
                                    object : MemberBuyDialog.ChangeMembershipListener {
                                        override fun onListener(bean: MemberSubscribeEntity.Data.ProductDescriptions) {
                                            OkHttpManager.instance.requestInterface(object :
                                                OkHttpFromBoy {
                                                override fun addBody(requestBody: OkHttpBodyEntity) {
                                                    requestBody.setPost(Constant.user_create_order_url)
                                                    requestBody.add("productCode", bean.productCode)
                                                    requestBody.add("productCategory", 1)
                                                }

                                            }, object : SDOkHttpResoutCallBack<OrderCreateEntity>() {
                                                override fun onSuccess(entity: OrderCreateEntity) {
                                                    Activities.get().top?.let {
                                                        PayUtils.instance.start(
                                                            entity,
                                                            it,
                                                            object : EmptySuccessCallBack {
                                                                override fun OnSuccessListener() {
                                                                    page.initialRefresh()
                                                                    getData()
                                                                    BaseConfig.getInstance.setBoolean(
                                                                        SpName.isMember,
                                                                        true
                                                                    )
                                                                    FirebaseEventUtils.logEvent(
                                                                        FirebaseEventTag.WLM_Unlock_Subsuccess.name
                                                                    )

                                                                }

                                                            })
                                                    }

                                                }
                                            })
                                        }

                                        override fun closeListener(refreshTime: Long) {

                                        }
                                    })

                            },100)
                        }
                        lastVisibleMember++
                    }
                }
            })


            wlmTipsDialogShow.setOnClickListener {
                showWLMTips()
            }
        }

    }
    fun showWLMTips(){
        mView?.apply {
            val wlmTipsDialog = Activities.get().top?.let { WlmTipsDialog(it,this@WLMPresenter.content) }
            wlmTipsDialog?.showPopupWindow()
            wlmTipsDialogShow.setImageResource(R.mipmap.icon_wlm_controls_click)
            wlmTipsDialog?.onDismissListener=object : BasePopupWindow.OnDismissListener(){
                override fun onDismiss() {
                    BaseConfig.getInstance.setBoolean(SpName.wlmTopTipDialogShow, true)
                    wlmTipsDialogShow.setImageResource(R.mipmap.icon_wlm_controls)
                }
            }
        }
    }

    private fun userAction(direction: Int, wlmListBean: WLMListBean) {
        mView?.apply {
            wlmNum -= 1
            if (member) {
                txtWlmNumber.text = "$wlmNum sexy girls have already liked you \uD83D\uDC97"
            }
            SDEventManager.post(wlmNum, EnumEventTag.REFRESH_WLM_LIKE_SIZE.ordinal)
            list.remove(wlmListBean)
            whoLikeMeAdapter!!.notifyDataSetChanged()
            if (wlmNum <= 1) {
                getData()
            }
            containerEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            txtWlmNumber.visibility = if (member){if (list.isEmpty()) View.GONE else View.VISIBLE}else View.VISIBLE
            setPremiumVisibility()

            wlmRefreshLayout.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            if (whoLikeMeAdapter?.dataList != null)
                SDEventManager.post(
                    whoLikeMeAdapter?.dataList,
                    EnumEventTag.REFRESH_CHAT_HEAD_WLM.ordinal
                )

            if (direction == ItemTouchHelper.END) {
                userCanUseWlmNumSub()//减去wlm次数
                val bundle = Bundle()
                bundle.putString("userCodeFriend", wlmListBean.userCodeFriend)
                bundle.putString(
                    "avatarUrl",
                    wlmListBean.avatarUrl
                )
                IntentUtil.startActivity(MatchUserActivity::class.java, bundle)
            }
        }
    }

    fun removeData(position: Int) {
        mView?.apply {
            list.removeAt(position)
            whoLikeMeAdapter?.notifyDataSetChanged()
            if (list.isEmpty()) {
                txtGetPremiumUnlock.visibility = View.GONE
                containerEmpty.visibility=View.VISIBLE
            }

        }
    }

    fun addItem(bean: WLMListBean) {
        mView?.apply {
            if (CollectionUtils.isNotEmpty(list)) {
                list.add(0, bean)
                wlmNum = list.size
                if (member) {
                    txtWlmNumber.text = "$wlmNum sexy girls have already liked you \uD83D\uDC97"
                }
                SDEventManager.post(wlmNum, EnumEventTag.REFRESH_WLM_LIKE_SIZE.ordinal)
                whoLikeMeAdapter!!.notifyDataSetChanged()
            } else {
                list.add(0, bean)
                wlmNum = list.size
                if (member) {
                    txtWlmNumber.text = "$wlmNum sexy girls have already liked you \uD83D\uDC97"
                }
                SDEventManager.post(wlmNum, EnumEventTag.REFRESH_WLM_LIKE_SIZE.ordinal)

                setPremiumVisibility()
                containerEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                wlmRefreshLayout.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                whoLikeMeAdapter!!.notifyDataSetChanged()
            }

        }

    }

    fun initData() {
        mView?.apply {
            page.initialRefresh()
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_wlm_list_url)
                    requestBody.addPage(page)
                }
            }, object : SDOkHttpResoutCallBack<WhoLikeMeEntity>() {
                override fun onSuccess(entity: WhoLikeMeEntity) {
                    if (page.page == 1) {
                        list.clear()
                    }
                    if (entity.data.wlmList.isNotEmpty()) {
                        scope.launch {
                            for (i in 0 until entity.data.wlmList.size) {
                                Activities.get().top?.let {
                                    Glide.with(it)
                                        .load(entity.data.wlmList[i].avatarUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .preload()
                                }
                            }
                            BaseConfig.getInstance.setBoolean(
                                SpName.isMember,
                                entity.data.isMember
                            )
                        }

                        whoLikeMeAdapter?.setMember(entity.data.isMember)
                        list.addAll(entity.data.wlmList)
                        whoLikeMeAdapter?.notifyDataSetChanged()
                        if (whoLikeMeAdapter?.dataList != null)
                            SDEventManager.post(
                                whoLikeMeAdapter?.dataList,
                                EnumEventTag.REFRESH_CHAT_HEAD_WLM.ordinal
                            )
                    }
                    this@WLMPresenter.content=entity.data.content?:""
                    member = entity.data.isMember
                    whoLikeMeAdapter?.setMember(member)
//                    wlmRefreshLayout.setEnableLoadMore(entity.data.wlmList.size == page.pageSize)
                    wlmRefreshLayout.setNoMoreData(entity.data.wlmList.size != page.pageSize)

                    isEnableLoadMore = entity.data.wlmList.size == page.pageSize
                    userCanUseWlmNum= entity.data.wlmCount?:0

                    txtWlmNumber.visibility = if (member){if (list.isEmpty()) View.GONE else View.VISIBLE}else View.VISIBLE
                    containerEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    wlmRefreshLayout.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                    wlmNum = entity.data.wlmNum
                    if (member) {
                        txtWlmNumber.text = "$wlmNum sexy girls have already liked you \uD83D\uDC97"
                    }
                    userCanLikeSizeContainer.isVisible= !member && userCanUseWlmNum >0 && list.isNotEmpty()//右下角悬浮窗
                    if (userCanLikeSizeContainer.isVisible){
                        txtWlmSize.text="$userCanUseWlmNum"
                    }

                    SDEventManager.post(wlmNum, EnumEventTag.REFRESH_WLM_LIKE_SIZE.ordinal)

                    setPremiumVisibility()


                }

                override fun onFinish() {
                    if (wlmRefreshLayout != null) {
                        if (page.page == 1) {
                            wlmRefreshLayout.finishRefresh(920)
                        } else {
                            wlmRefreshLayout.finishLoadMore(1000)
                        }
                    }
                }
            })
        }
    }

    fun getData() {
        mView?.apply {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_wlm_list_url)
                    requestBody.addPage(page)
                }
            }, object : SDOkHttpResoutCallBack<WhoLikeMeEntity>() {
                override fun onSuccess(entity: WhoLikeMeEntity) {
                    if (page.page == 1) {
                        list.clear()
                    }
                    if (entity.data.wlmList.isNotEmpty()) {
                        scope.launch {
                            for (i in 0 until entity.data.wlmList.size) {
                                Activities.get().top?.let {
                                    Glide.with(it)
                                        .load(entity.data.wlmList[i].avatarUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .preload()
                                }
                            }
                            BaseConfig.getInstance.setBoolean(
                                SpName.isMember,
                                entity.data.isMember
                            )
                        }

                        whoLikeMeAdapter?.setMember(entity.data.isMember)
                        list.addAll(entity.data.wlmList)
                        whoLikeMeAdapter?.notifyDataSetChanged()
                        if (whoLikeMeAdapter?.dataList != null)
                            SDEventManager.post(
                                whoLikeMeAdapter?.dataList,
                                EnumEventTag.REFRESH_CHAT_HEAD_WLM.ordinal
                            )

                    }
                    member = entity.data.isMember
                    whoLikeMeAdapter?.setMember(member)
                    userCanUseWlmNum= entity.data.wlmCount?:0
                    this@WLMPresenter.content=entity.data.content?:""
//                    wlmRefreshLayout.setEnableLoadMore(entity.data.wlmList.size == page.pageSize)
                    wlmRefreshLayout.setNoMoreData(entity.data.wlmList.size != page.pageSize)
                    containerEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    wlmRefreshLayout.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                    txtWlmNumber.visibility = if (member){if (list.isEmpty()) View.GONE else View.VISIBLE}else View.VISIBLE


                    userCanLikeSizeContainer.isVisible= !member && userCanUseWlmNum >0 && list.isNotEmpty()//右下角悬浮窗
                    if (userCanLikeSizeContainer.isVisible){
                        txtWlmSize.text="$userCanUseWlmNum"
                    }


                    wlmNum = entity.data.wlmNum
                    if (member) {
                        txtWlmNumber.text = "$wlmNum sexy girls have already liked you \uD83D\uDC97"
                    }
                    SDEventManager.post(wlmNum, EnumEventTag.REFRESH_WLM_LIKE_SIZE.ordinal)
                    isEnableLoadMore = entity.data.wlmList.size == page.pageSize

                    setPremiumVisibility()


                }

                override fun onFinish() {
                    Activities.get().top?.let {
                        if (page.page == 1) {
                            wlmRefreshLayout.finishRefresh(920)
                        } else {
                            wlmRefreshLayout.finishLoadMore(1000)
                        }
                    }
                }
            })
        }
    }

    fun setPremiumVisibility() {
        mView?.apply {
            txtGetPremiumUnlock.visibility = if (member) View.GONE else {
                if (viewDown.isVisible) {
                    View.GONE
                } else {
                    if (CollectionUtils.isNotEmpty(list)) View.VISIBLE else View.GONE
                }
            }
        }
    }

    fun refreshItem(userCode: String, onlineStatus: Int) {
        whoLikeMeAdapter?.let {
            var position = 0
            for (i in 0 until whoLikeMeAdapter!!.dataList.size) {
                if (whoLikeMeAdapter!!.dataList[i].userCodeFriend == userCode) {
                    position = i
                    whoLikeMeAdapter!!.dataList[i].online = onlineStatus
                    break
                }
            }
            whoLikeMeAdapter!!.notifyItemChanged(position)
        }

    }

    fun onDestroy() {
        scope.cancel()
    }

    fun userCanUseWlmNumSub(){
        if (userCanUseWlmNum >0){
            userCanUseWlmNum--
        }
        mView?.apply {
            userCanLikeSizeContainer.isVisible= !member && userCanUseWlmNum >0 && list.isNotEmpty()
            if (userCanLikeSizeContainer.isVisible){
                txtWlmSize.text="$userCanUseWlmNum"
            }
        }
    }
}