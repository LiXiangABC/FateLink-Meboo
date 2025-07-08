package com.crush.ui.my.profile

import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.crush.mvp.BaseView
import com.crush.view.DateWheelLayout

class AddProfileContact {
    interface View : BaseView {
        val outSideView: ConstraintLayout

        val nicknameContainer: ConstraintLayout
        val editNickname:EditText
        val txtShowNameTitle:TextView
        val txtShowNameTip:TextView
        val txtNicknameSaveTip:TextView
        val txtNicknameNext:TextView
        val txtNicknameLengthTip:TextView

        val dateContainer: ConstraintLayout
        val txtShowDateTitle: TextView
        val txtShowDateTip: TextView
        val txtDateSaveTip: TextView
        val selectDateTips: TextView
        val txtDateNext: TextView

        val genderContainer: ConstraintLayout
        val genderPreferContainer: ConstraintLayout
        val maleContainer: LinearLayout
        val imgGenderMale: ImageView
        val txtGenderMale: TextView
        val imgGenderFemale: ImageView
        val txtGenderFemale: TextView
        val imgGenderQueer: ImageView
        val txtGenderQueer: TextView
        val txtGenderDefine: TextView
        val txtGenderNext: TextView
        val txtPreferDefine: TextView
        val txtPreferDefineTip: TextView
        val preferList:RecyclerView
        val noPreferencesClickToSelectMore:TextView
        val noPreferencesClickToSelectMoreWoman:TextView

        val femaleContainer: LinearLayout
        val queerContainer: LinearLayout


        val photoList:RecyclerView
        val photoTitle:TextView
        val photoTitleTip:TextView
        val txtPhotoNext:TextView
        val txtPhotoSkip:TextView
        val photoContainer:ConstraintLayout




        val purposeContainer:ConstraintLayout
        val txtLookingNext:LinearLayout
        val radioLookingGroup:RadioGroup
        val actionLoading:LottieAnimationView
        val textLookingNext:TextView
        val txtWantTitle:TextView
        val txtWantTip:TextView
        val txtAccpetTitle:TextView
        val accpetContainer:LinearLayout


        val imgTopBack:ImageView
        val dateSelectLayout:DateWheelLayout

    }
}