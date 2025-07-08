package com.crush.ui.index.match

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.view.CircleImageView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class MatchUserContract {
    interface View : BaseView {
        val imgUserMatch:CircleImageView
        val imgUserMatchRight:CircleImageView
        val containerBackArrow:ImageView
        val emojiWave: TextView
        val emojiWinkingFace:TextView
        val emojiHeart:TextView
        val emojiLecherous:TextView
        val editUserMatch:EditText
        val txtMatchSend:TextView
        val editContainer:ConstraintLayout
        val imgMatchIconOne:ImageView
        val imgMatchIconTwo:ImageView
        val imgMatchIconThree:ImageView
        val imgMatchIconFour:ImageView
    }

    internal interface Presenter : BasePresenter<View> {

    }
}
