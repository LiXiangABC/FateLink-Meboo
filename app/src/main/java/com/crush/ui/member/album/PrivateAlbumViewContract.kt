package com.crush.ui.member.album

import android.widget.ImageView
import android.widget.TextView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class PrivateAlbumViewContract {
    interface View : BaseView {
        val privateAlbumView:ImageView
        val imgDone:TextView
    }

    internal interface Presenter : BasePresenter<View> {

    }
}
