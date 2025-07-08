package com.crush.ui.my.profile.info.fragment

import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.crush.mvp.MVPBaseFragment
import com.sunday.eventbus.SDBaseEvent

class TurnOnsFragment : MVPBaseFragment<TurnOnsContract.View, TurnOnsPresenter>(), TurnOnsContract.View {


    override fun bindLayout(): Int {
        return R.layout.layout_user_turn_ons
    }

    override fun onEventMainThread(event: SDBaseEvent) {
//        when (EnumEventTag.valueOf(event.tagInt)) {
//            else -> {}
//        }
    }

    override fun onResume() {
        super.onResume()
//        ImmersionBar.with(this)
//            .statusBarColor("#ffffff")
//            .init()


    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
//            ImmersionBar.with(this)
//                .statusBarColor("#ffffff")
//                .init()
        }
    }

    override val turnOnsList: RecyclerView?
        get() = view?.findViewById(R.id.turn_ons_list)

}