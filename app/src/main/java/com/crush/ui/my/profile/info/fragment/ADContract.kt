package com.crush.ui.my.profile.info.fragment

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.view.TagCloudView
import com.crush.mvp.BasePresenter
import com.crush.mvp.BaseView

class ADContract {
    interface View : BaseView {

        val interestsTagCloud: TagCloudView?
        val imgInterestsAdd: ImageView?
        val containerModifyNickname:ConstraintLayout?
        val userProfileNickname:TextView?
        val containerSelectDate:ConstraintLayout?
        val txtBrithOfDate:TextView?
        val containerSeeking:ConstraintLayout?
        val txtUserGender:TextView?
        val txtSeeking:TextView?
        val imgLookingSymbol:ImageView?

        val imgGenderSymbol:ImageView?
        val imgAboutModify:ImageView?
        val txtAboutMe:TextView?

        val txtMyHeight:TextView?
        val containerMyHeight:ConstraintLayout?

        val tvNowIwant:TextView?
        val tvEmptyNowIWant:TextView?
        val tvEmptyTagYouAccept:TextView?
        val tvEmptyInterestsTagCloud:TextView?
        val ivNowIwantIn:ImageView?
        val ivYouAccpetIn:ImageView?
        val tagYouAccept: TagCloudView?

        val containerNowIWant:ConstraintLayout?
        val youAcceptContainer:ConstraintLayout?
        val containerProfileAbout:ConstraintLayout?
    }

    internal interface Presenter : BasePresenter<View> {
    }

}