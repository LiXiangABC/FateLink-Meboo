package com.crush.ui.chat

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.crush.bean.WLMListBean
import com.crush.ui.index.helper.IndexHelper
import com.crush.view.ViewMinDown
import com.custom.base.config.BaseConfig
import com.gyf.immersionbar.ImmersionBar
import com.crush.mvp.MVPBaseFragment
import com.google.gson.Gson
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.sunday.eventbus.SDBaseEvent
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import io.rong.imkit.event.EnumEventTag
import io.rong.imkit.utils.JsonUtils
import org.json.JSONObject


class WLMFragment : MVPBaseFragment<WLMContract.View, WLMPresenter>(), WLMContract.View {

    override lateinit var txtToEdit: TextView
    override lateinit var txtWlmNumber: TextView
    override lateinit var containerEmpty: ConstraintLayout
    override lateinit var whoLikeMeList: RecyclerView
    override lateinit var wlmRefreshLayout: SmartRefreshLayout
    override lateinit var txtGetPremiumUnlock: TextView
    override lateinit var topRightContainer: ConstraintLayout
    override lateinit var viewDown: ViewMinDown
    override lateinit var wlmRefreshLayoutFooter: ClassicsFooter
    override lateinit var userCanLikeSizeContainer: ConstraintLayout
    override lateinit var txtWlmSize: TextView
    override lateinit var wlmTipsDialogShow: ImageView
    override lateinit var imgWlmEmpty: ImageView
    override lateinit var txtEmptyTip: TextView

    fun findView() {
        mActivity?.let {
            txtToEdit = it.findViewById(R.id.txt_to_edit)
            txtWlmNumber = it.findViewById(R.id.txt_wlm_number)
            containerEmpty = it.findViewById(R.id.container_empty)
            whoLikeMeList = it.findViewById(R.id.who_like_me_list)
            wlmRefreshLayout = it.findViewById(R.id.wlm_refresh_layout)
            txtGetPremiumUnlock = it.findViewById(R.id.txt_get_premium_unlock)
            topRightContainer = it.findViewById(R.id.top_right_container)
            viewDown = it.findViewById(R.id.view_wlm_dowm)
            wlmRefreshLayoutFooter = it.findViewById(R.id.wlm_refresh_layout_footer)
            userCanLikeSizeContainer = it.findViewById(R.id.user_can_like_size_container)
            txtWlmSize = it.findViewById(R.id.txt_wlm_size)
            wlmTipsDialogShow = it.findViewById(R.id.wlm_tips_dialog_show)
            imgWlmEmpty = it.findViewById(R.id.img_wlm_empty)
            txtEmptyTip = it.findViewById(R.id.txt_empty_tip)
        }

    }

    override fun bindLayout(): Int {
        return R.layout.frag_wlm
    }

    override fun onEventMainThread(event: SDBaseEvent) {
        when (EnumEventTag.valueOf(event.tagInt)) {
            EnumEventTag.WLM_LIKE_SWIPED -> {
                mPresenter?.removeData(event.data.toString().toInt())
            }

            EnumEventTag.WLM_DISLIKE_SWIPED -> {
                mPresenter?.removeData(event.data.toString().toInt())
            }

            EnumEventTag.WLM_REFRESH -> {
                mPresenter?.getData()
            }

            EnumEventTag.WLM_INIT_REFRESH -> {
                mPresenter?.initData()
            }

            EnumEventTag.WLM_REFRESH_ITEM -> {
                if (JsonUtils.isJSON(event.data.toString())) {
                    val jsonObject = JSONObject(event.data.toString())
                    mPresenter?.refreshItem(
                        jsonObject.getString("userCode"),
                        jsonObject.getInt("online")
                    )
                }
            }

            EnumEventTag.WLM_ADD_ITEM -> {
                if (JsonUtils.isJSON(event.data.toString())) {
                    val jsonObject = JSONObject(event.data.toString())
                    val bean = Gson().fromJson(
                        jsonObject.getString("wlmUserInfo"),
                        WLMListBean::class.java
                    )
                    mPresenter?.addItem(bean)
                }
            }

            EnumEventTag.WLM_DISCOUNT_DOWN_SHOW -> {
                showDownTime(event.data.toString().toLong())
            }

            else -> {}
        }
    }

    override fun initView(view: View) {
        super.initView(view)
        findView()
        mPresenter?.initView()

        imgWlmEmpty.setImageResource(
            if (BaseConfig.getInstance.getInt(
                    SpName.trafficSource,
                    0
                ) == 1
            ) R.mipmap.icon_white_empty_wlm else R.mipmap.icon_empty_wlm
        )
        txtEmptyTip.text = (if (BaseConfig.getInstance.getInt(
                SpName.trafficSource,
                0
            ) == 1
        ) getString(R.string.wlm_white_empty_tip) else getString(R.string.wlm_empty_tip))
        txtToEdit.isVisible = BaseConfig.getInstance.getInt(SpName.trafficSource, 0) != 1
        wlmTipsDialogShow.isVisible = BaseConfig.getInstance.getInt(SpName.trafficSource, 0) != 1
    }

    fun showDownTime(time: Long) {
        Activities.get().top?.let {
            viewDown.setDownTime(it, time)
            mPresenter?.setPremiumVisibility()
            IndexHelper.setOnClick(it, viewDown)
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!isAdded || activity == null || requireActivity().isFinishing) {
            return
        }
        if (!hidden) {
            ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .init()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!viewDown.isVisible) {
            IndexHelper.getDiscountPopData(
                BaseConfig.getInstance.getBoolean(
                    SpName.isMember,
                    false
                )
            ) {
                showDownTime(it.popOverTime)
            }
        }
        if (!BaseConfig.getInstance.getBoolean(SpName.wlmTopTipDialogShow, false)) {
            mPresenter?.showWLMTips()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewDown.cancelTime()
    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestroy()
    }
}