package com.crush.ui.chat

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.crush.view.ViewMinDown
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class WLMContract {
    interface View : BaseView {
        val txtToEdit:TextView
        val txtWlmNumber:TextView
        val containerEmpty:ConstraintLayout
        val whoLikeMeList: RecyclerView
        val wlmRefreshLayout: SmartRefreshLayout
        val txtGetPremiumUnlock: TextView
        val topRightContainer:ConstraintLayout
        val viewDown:ViewMinDown
        val wlmRefreshLayoutFooter: ClassicsFooter
        val userCanLikeSizeContainer: ConstraintLayout
        val txtWlmSize: TextView
        val wlmTipsDialogShow: ImageView
        val imgWlmEmpty: ImageView
        val txtEmptyTip: TextView
    }

    internal interface Presenter : BasePresenter<View> {
    }
}
