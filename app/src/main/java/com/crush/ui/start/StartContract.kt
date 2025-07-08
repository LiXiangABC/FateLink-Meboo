package com.crush.ui.start

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView
import xyz.doikki.videoplayer.player.VideoView

/**
 * 作者：
 * 时间：
 * 描述：
 */

class StartContract {
    interface View : BaseView {
        val imgOrganic:ImageView
        val networkErrorContainer: LinearLayout
        val networkErrorTryAgain: TextView
        val christmasCountdown: TextView
        val christmasCountdownContainer: ConstraintLayout
        val videoView: VideoView

    }

    internal interface Presenter : BasePresenter<View> {
    }
}
