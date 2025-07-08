package com.crush.ui.like

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.custom.base.config.BaseConfig
import com.crush.mvp.MVPBaseActivity
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.sunday.eventbus.SDBaseEvent
import io.rong.imkit.SpName
import io.rong.imkit.event.EnumEventTag


class ILikeActivity : MVPBaseActivity<ILikeContract.View, ILikePresenter>(), ILikeContract.View {


    override fun setFullScreen(): Boolean {
        return true
    }
    override fun bindLayout(): Int {
        return R.layout.act_i_like
    }



    override fun initView() {
        imgWlmEmpty.setImageResource(if(BaseConfig.getInstance.getInt(SpName.trafficSource,0)==1)R.mipmap.icon_empty_i_like_white else R.mipmap.icon_empty_i_like)
    }

    override fun onEventMainThread(event: SDBaseEvent) {
        when (EnumEventTag.valueOf(event.tagInt)) {
            EnumEventTag.FLASH_CHAT_REMOVE -> {
                mPresenter?.removeItem(event.data.toString().toInt())
            }


            else -> {}
        }
    }



    override val toolbarTitle: ConstraintLayout
        get() = findViewById(R.id.toolbar_title)
    override val iLikeRefreshLayout: SmartRefreshLayout
        get() = findViewById(R.id.i_like_refresh_layout)
    override val containerEmpty: ConstraintLayout
        get() = findViewById(R.id.container_empty)
    override val iLikeList: RecyclerView
        get() =findViewById(R.id. i_like_list)
    override val txtSeeYourAdmirers: TextView
        get() = findViewById(R.id.txt_see_your_admirers)
    override val topTips: TextView
        get() =findViewById(R.id. top_tips)
    override val iLikeBackLayout: ImageView
        get() = findViewById(R.id.i_like_back_layout)
    override val imgWlmEmpty: ImageView
        get() = findViewById(R.id.img_wlm_empty)


}