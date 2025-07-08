package com.crush.ui.my

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.view.delay.DelayClickConstraintLayout
import com.google.android.material.appbar.AppBarLayout
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView
import com.makeramen.roundedimageview.RoundedImageView

/**
 * 作者：
 * 时间：
 * 描述：
 */

class MyContract {
    interface View : BaseView {
        val imgMyAvatar: RoundedImageView
        val txtUserName:TextView
        val containerHelpCenter:LinearLayout
        val containerSetting:LinearLayout
        val editProfileContainer:LinearLayout
        val txtGetPremium:TextView?
        val imgMemberBg:ImageView
        val txtFlashChat:TextView
        val txtPrivatePhotos:TextView
        val txtPrivateVideos:TextView
        val conMyPwdExchangeBox: ConstraintLayout
        val conMyGiftPackBox:DelayClickConstraintLayout
        val boostFlashChat:DelayClickConstraintLayout
        val boostPrivatePhoto:DelayClickConstraintLayout
        val boostPrivateVideo: DelayClickConstraintLayout
        val txtMemberInfo:TextView
        val txtMemberContent:TextView
        val txtMemberTitle:TextView
        val imgMyCrown:ImageView
        val privateAlbumContainer:LinearLayout
        val ctbScrollView: AppBarLayout
        val clMemberContainer:ConstraintLayout
        val myChristmasContainer:ConstraintLayout
        val imgMyTitleChristmas:ImageView
        val btnModifyProfile:ImageView
        val specialMemberTip:TextView
        val txtMeILikeSize:TextView
        val meILikeContainer:LinearLayout
    }

    internal interface Presenter : BasePresenter<View> {
    }
}
