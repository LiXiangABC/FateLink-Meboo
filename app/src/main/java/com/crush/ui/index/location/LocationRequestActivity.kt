package com.crush.ui.index.location

import android.widget.ImageView
import android.widget.LinearLayout
import com.crush.R
import com.crush.mvp.MVPBaseActivity


class LocationRequestActivity : MVPBaseActivity<LocationRequestContract.View, LocationRequestPresenter>(), LocationRequestContract.View {

    override fun setFullScreen(): Boolean {
        return true
    }
    override fun bindLayout(): Int {
        return R.layout.act_loaction_request
    }

    override fun initView() {
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mPresenter?.getIMToken(mActivity)
    }

    override val requestLocationContainer: LinearLayout
        get() = findViewById(R.id.request_location_container)
    override val requestLocationClose: ImageView
        get() = findViewById(R.id.request_location_close)
    override val outSideView: ImageView
        get() = findViewById(R.id.out_side_view)

}