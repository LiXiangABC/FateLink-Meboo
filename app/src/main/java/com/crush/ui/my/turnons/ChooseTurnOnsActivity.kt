package com.crush.ui.my.turnons

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import io.rong.imkit.event.EnumEventTag
import com.crush.mvp.MVPBaseActivity
import com.sunday.eventbus.SDEventManager


class ChooseTurnOnsActivity : MVPBaseActivity<ChooseTurnOnsContract.View, ChooseTurnOnsPresenter>(), ChooseTurnOnsContract.View {

    override fun setFullScreen(): Boolean {
        return false
    }

    override fun getTitleTextView(tv: TextView) {
        tv.text=getString(R.string.choose_your_turn_ons)
    }
    override fun bindLayout(): Int {
        return R.layout.act_choose_turn_ons
    }

    override fun initView() {
    }

    override fun onDestroy() {
        super.onDestroy()
        val boolean = intent.extras?.getBoolean("move",false)
        if (boolean == true) {
            SDEventManager.post(true, EnumEventTag.INDEX_LIKE_SWIPED.ordinal)
        }
        mPresenter?.onDestroy()
    }


    override val turnOnsList: RecyclerView
        get() = findViewById(R.id.turn_ons_list)

}