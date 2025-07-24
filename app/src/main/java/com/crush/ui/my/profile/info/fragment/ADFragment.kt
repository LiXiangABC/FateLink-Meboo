package com.crush.ui.my.profile.info.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.crush.R
import com.crush.entity.UserProfileEntity
import com.crush.view.TagCloudView
import com.crush.mvp.MVPBaseFragment

class ADFragment : MVPBaseFragment<ADContract.View, ADPresenter>(), ADContract.View {

    companion object {
        fun newInstance() = ADFragment()
    }
    override fun bindLayout(): Int {
        return R.layout.layout_profile_ad
    }

    fun onDataUpdated(data: UserProfileEntity.Data) {
        // 更新参数
        arguments = (arguments ?: Bundle()).apply {
            putSerializable("entity", data)
        }

        Log.e("~~~", "onDataUpdated:$data ")
        // 刷新UI
        mPresenter?.updateUI(data)
    }


    override val interestsTagCloud: TagCloudView?
        get() = view?.findViewById(R.id.interests_tag_cloud)
    override val imgInterestsAdd: ImageView?
        get() = view?.findViewById(R.id.img_interests_add)
    override val containerModifyNickname: ConstraintLayout?
        get() = view?.findViewById(R.id.container_modify_nickname)
    override val userProfileNickname: TextView?
        get() = view?.findViewById(R.id.user_profile_nickname)
    override val containerSelectDate: ConstraintLayout?
        get() = view?.findViewById(R.id.container_select_date)
    override val txtBrithOfDate: TextView?
        get() = view?.findViewById(R.id.txt_brith_of_date)
    override val containerSeeking: ConstraintLayout?
        get() = view?.findViewById(R.id.container_seeking)
    override val txtUserGender: TextView?
        get() = view?.findViewById(R.id.txt_user_gender)
    override val txtSeeking: TextView?
        get() = view?.findViewById(R.id.txt_seeking)
    override val imgLookingSymbol: ImageView?
        get() = view?.findViewById(R.id.img_looking_symbol)
    override val imgGenderSymbol: ImageView?
        get() = view?.findViewById(R.id.img_gender_symbol)
    override val imgAboutModify: ImageView?
        get() = view?.findViewById(R.id.img_about_modify)
    override val txtAboutMe: TextView?
        get() = view?.findViewById(R.id.txt_about_me)
    override val txtMyHeight: TextView?
        get() = view?.findViewById(R.id.txt_my_height)
    override val containerMyHeight: ConstraintLayout?
        get() = view?.findViewById(R.id.container_my_height)
    override val ivNowIwantIn: ImageView?
        get() =view?.findViewById(R.id.iv_now_i_want_in)
    override val ivYouAccpetIn: ImageView?
        get() = view?.findViewById(R.id.iv_you_accept_in)
    override val tvNowIwant: TextView?
        get() = view?.findViewById(R.id.tv_now_i_want)
    override val tvEmptyNowIWant: TextView?
        get() = view?.findViewById(R.id.tv_empty_now_i_want)
    override val tvEmptyTagYouAccept: TextView?
        get() = view?.findViewById(R.id.tv_empty_tag_you_accept)
    override val tvEmptyInterestsTagCloud: TextView?
        get() =view?.findViewById(R.id.tv_empty_interests_tag_cloud)
    override val tagYouAccept: TagCloudView?
        get() =view?.findViewById(R.id.tag_you_accept)
    override val containerNowIWant: ConstraintLayout?
        get() = view?.findViewById(R.id.container_now_i_want)
    override val youAcceptContainer: ConstraintLayout?
        get() = view?.findViewById(R.id.you_accept_container)
    override val containerProfileAbout: ConstraintLayout?
        get() = view?.findViewById(R.id.container_profile_about)

}