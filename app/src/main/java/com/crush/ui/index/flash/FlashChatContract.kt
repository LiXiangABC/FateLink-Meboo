package com.crush.ui.index.flash

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.makeramen.roundedimageview.RoundedImageView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class FlashChatContract {
    interface View : BaseView {
        val  flashChatUserTitle:TextView
        val flashChatUserAvatar:RoundedImageView
        val loLightningLove:LottieAnimationView
        val startFlashChatContainer:LinearLayout
        val imgClose:ImageView
    }

    internal interface Presenter : BasePresenter<View> {

    }
}
