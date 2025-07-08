package com.crush.ui.like

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class ILikeContract {
    interface View : BaseView {
        val toolbarTitle:ConstraintLayout
        val iLikeRefreshLayout:SmartRefreshLayout
        val containerEmpty:ConstraintLayout
        val iLikeList:RecyclerView
        val txtSeeYourAdmirers:TextView
        val topTips:TextView
        val iLikeBackLayout:ImageView
        val imgWlmEmpty:ImageView
    }

    internal interface Presenter : BasePresenter<View> {

    }
}
