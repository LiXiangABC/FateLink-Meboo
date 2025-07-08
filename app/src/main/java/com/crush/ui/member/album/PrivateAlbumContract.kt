package com.crush.ui.member.album

import androidx.recyclerview.widget.RecyclerView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class PrivateAlbumContract {
    interface View : BaseView {
        val privateAlbumList:RecyclerView
    }

    internal interface Presenter : BasePresenter<View> {

    }
}
