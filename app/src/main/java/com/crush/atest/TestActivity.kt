package com.crush.atest

import com.crush.R
import com.crush.mvp.MVPBaseActivity


class TestActivity : MVPBaseActivity<TestContract.View, TestPresenter>(), TestContract.View {

    override fun setFullScreen(): Boolean {
        return true
    }
    override fun bindLayout(): Int {
        return R.layout.act_start
    }

    override fun initView() {
    }

}