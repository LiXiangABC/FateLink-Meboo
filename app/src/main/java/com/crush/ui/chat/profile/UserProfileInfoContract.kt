package com.crush.ui.chat.profile

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.crush.view.TagCloudView
import com.crush.view.delay.DelayClickImageView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView
import com.youth.banner.Banner

/**
 * 作者：
 * 时间：
 * 描述：
 */

class UserProfileInfoContract {
    interface View : BaseView {
        val banner: Banner<*, *>
        val scrollView:NestedScrollView
        val userProfileOnline:TextView
        val userProfilePositioning:TextView
        val userProfileName:TextView
        val userProfileLocation:TextView
        val userProfileUserWant:TextView
        val userProfileHeight:TextView
        val userProfileStarSign:TextView
        val userProfileAboutMeTitle:TextView
        val userProfileIntroduction:TextView
        val userProfileTagCloud:TagCloudView
        val userProfileTurnOnsTitle:TextView
        val turnOnsList:RecyclerView



        val containerBackArrow:ImageView
        val containerMoreOperation:DelayClickImageView
        val userProfileDislike:DelayClickImageView
        val userProfileLike:DelayClickImageView
        val userProfileChat: DelayClickImageView
        val outsideContainer:ConstraintLayout
    }

    internal interface Presenter : BasePresenter<View> {
    }
}
