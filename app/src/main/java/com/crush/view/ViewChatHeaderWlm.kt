package com.crush.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.crush.adapter.ViewChatHeaderPageLoaderAdapter
import com.crush.bean.WLMListBean
import com.crush.callback.EmptyCallBack
import com.crush.ui.chat.ktl.WlmClick
import com.crush.ui.index.match.MatchUserActivity
import com.crush.util.CollectionUtils
import com.custom.base.config.BaseConfig
import com.crush.util.IntentUtil
import com.sunday.eventbus.SDEventManager
import com.youth.banner.Banner
import com.youth.banner.listener.OnBannerListener
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import io.rong.imkit.event.EnumEventTag


class ViewChatHeaderWlm(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs){
    private var view: View? = null
    private var banner: Banner<WLMListBean, *>? = null
    private var pageLoaderAdapter: ViewChatHeaderPageLoaderAdapter?=null
    private var isMember=false

    init {
        view = LayoutInflater.from(context).inflate(R.layout.view_chat_header_wlm, this)
        initView()
    }

    private fun initView() {
        banner = view?.findViewById(R.id.banner)
        banner?.setOrientation(RecyclerView.VERTICAL)
        banner?.setUserInputEnabled(false)
        banner?.scrollTime = 300
        pageLoaderAdapter = ViewChatHeaderPageLoaderAdapter(context as Activity, isMember, arrayListOf())
        banner?.adapter = pageLoaderAdapter
    }

    fun getBanner():Banner<*, *>?{
        return banner
    }

    fun setMemberStatus(){
        pageLoaderAdapter?.isMember=isMember
        pageLoaderAdapter?.notifyDataSetChanged()
    }
    fun setData(isMember: Boolean, wlmList: ArrayList<WLMListBean>) {
        this.isVisible = !wlmList.isNullOrEmpty()
        if (CollectionUtils.isEmpty(wlmList)){
            return
        }
        banner?.isAutoLoop(!wlmList.isNullOrEmpty() && wlmList.size>1)
        pageLoaderAdapter?.isMember=isMember
        banner?.setDatas(wlmList)
        banner?.setOnBannerListener(object :OnBannerListener<WLMListBean>{
            override fun OnBannerClick(wLMListBean: WLMListBean?, position: Int) {
                if (CollectionUtils.isEmpty(wlmList)){
                    return
                }
                if (position >= wlmList.size){
                    return
                }
                wLMListBean?.apply {
                    WlmClick.benefitsReduceWLM(wLMListBean.userCodeFriend, 1, object :
                        EmptyCallBack {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun OnSuccessListener() {
                            val bundle = Bundle()
                            bundle.putString("userCodeFriend", wLMListBean.userCodeFriend)
                            bundle.putString(
                                "avatarUrl",
                                wLMListBean.avatarUrl
                            )
                            IntentUtil.startActivity(MatchUserActivity::class.java, bundle)

                            wlmList.remove(wLMListBean)
                            banner?.adapter?.notifyDataSetChanged()

//                            SDEventManager.post("$position", EnumEventTag.WLM_DISLIKE_SWIPED.ordinal)
                            SDEventManager.post(wlmList.size, EnumEventTag.REFRESH_WLM_LIKE_SIZE.ordinal)
                        }

                        override fun OnFailListener() {
                            WlmClick.openMemberBuyDialog { orderCreateEntity ->
                                Activities.get().top?.let {
                                    WlmClick.openPay(it, orderCreateEntity, 1) { isSuccess ->
                                        if (isSuccess) {
                                            BaseConfig.getInstance.setBoolean(SpName.isMember, true)
                                            setData(true,wlmList)
                                        }
                                    }
                                }
                            }
                        }

                    })
                }
            }

        })
    }


}
