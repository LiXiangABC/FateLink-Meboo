package com.crush.ui.my.profile.info.fragment

import android.widget.TextView
import androidx.core.view.isVisible
import com.crush.Constant
import com.crush.R
import io.rong.imkit.entity.IMTagBean
import com.crush.bean.UserWantOrYouAcceptBean
import com.crush.dialog.CustomSelectorDialog
import com.crush.dialog.SelectInterestDialog
import com.crush.dialog.UpdateAboutMeDialog
import com.crush.dialog.UpdateDateDialog
import com.crush.dialog.UpdateHeightDialog
import com.crush.dialog.UpdateNickNameDialog
import com.crush.dialog.UpdateSeekingDialog
import com.crush.entity.UserProfileEntity
import com.crush.view.TagCloudView
import com.custom.base.config.BaseConfig
import io.rong.imkit.event.EnumEventTag
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.BasePresenterImpl
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import java.util.stream.Collectors


class ADPresenter : BasePresenterImpl<ADContract.View>(), ADContract.Presenter {
    var interestsTags = mutableListOf<String>()
    var lookingFor = 0
    var hideHeight = false
    var data: UserProfileEntity.Data? = null
    fun updateUI(data: UserProfileEntity.Data) {
        mView?.apply {
            data?.apply {
                this@ADPresenter.hideHeight = hideHeight

                userProfileNickname?.text = nickName
                txtBrithOfDate?.text = birthday
                txtUserGender?.text =
                    when (gender) {
                        1 -> Activities.get().top?.getString(R.string.male)
                        2 -> Activities.get().top?.getString(
                            R.string.female
                        )

                        else -> Activities.get().top?.getString(R.string.gender_queer)
                    }
                txtSeeking?.text =
                    when (lookingFor) {
                        1 -> Activities.get().top?.getString(R.string.male)
                        2 -> Activities.get().top?.getString(
                            R.string.female
                        )

                        else -> Activities.get().top?.getString(R.string.gender_queer)
                    }
                txtAboutMe?.text = aboutMe
                imgGenderSymbol?.setImageResource(if (gender == 1) R.mipmap.icon_gender_male_small else if (gender == 2) R.mipmap.icon_gender_female_small else R.mipmap.icon_gender_unknown_small)
                imgLookingSymbol?.setImageResource(if (lookingFor == 1) R.mipmap.icon_gender_male_small else if (lookingFor == 2) R.mipmap.icon_gender_female_small else R.mipmap.icon_gender_unknown_small)
                this@ADPresenter.lookingFor = lookingFor
                txtMyHeight?.text = inchHeight

                if (interests != null) {
                    interestsTags = interests
                }
                upTagCloud(interestsTags, interestsTagCloud, tvEmptyInterestsTagCloud)

                upDateNowIwant(data?.userWant, tvNowIwant, tvEmptyNowIWant)
                upDateYouAccpet(data?.youAccept, tagYouAccept, tvEmptyTagYouAccept)

            }
            containerModifyNickname?.setOnClickListener {
                Activities.get().top?.let { it1 ->
                    val updateNickNameDialog =
                        UpdateNickNameDialog(
                            it1,
                            userProfileNickname?.text.toString(),
                            object : UpdateNickNameDialog.onCallBack {
                                override fun onCallback(name: String) {
                                    userProfileNickname?.text = name
                                    saveProfileInfo(nickName = name)
                                }

                            })
                    updateNickNameDialog.showPopupWindow()
                }
            }

            containerSelectDate?.setOnClickListener {

                Activities.get().top?.let { it1 ->
                    UpdateDateDialog(
                        it1,
                        txtBrithOfDate?.text.toString(),
                        object : UpdateDateDialog.onCallBack {
                            override fun onCallback(value: String) {
                                txtBrithOfDate?.text = value
                                saveProfileInfo(birthday = value)
                            }

                        }).showPopupWindow()
                }
            }

            containerSeeking?.setOnClickListener {
                Activities.get().top?.let { it1 ->
                    UpdateSeekingDialog(it1, lookingFor, object : UpdateSeekingDialog.onCallBack {
                        override fun onCallback(value: String) {
                            txtSeeking?.text =
                                when (value) {
                                    "1" -> {
                                        lookingFor = 1
                                        Activities.get().top?.getString(R.string.male)
                                    }

                                    "2" -> {
                                        lookingFor = 2
                                        Activities.get().top?.getString(R.string.female)
                                    }

                                    else -> {
                                        lookingFor = 0
                                        Activities.get().top?.getString(R.string.gender_queer)
                                    }
                                }
                            imgLookingSymbol?.setImageResource(if (value == "1") R.mipmap.icon_gender_male_small else if (value == "2") R.mipmap.icon_gender_female_small else R.mipmap.icon_gender_unknown_small)
                            saveProfileInfo(lookingFor = value)
                        }

                    }).showPopupWindow()
                }
            }
            containerMyHeight?.setOnClickListener {
                Activities.get().top?.let { it1 ->
                    UpdateHeightDialog(
                        it1,
                        txtMyHeight?.text.toString(),
                        hideHeight,
                        object : UpdateHeightDialog.onCallBack {
                            override fun onCallback(value: String, hideHeight: Boolean) {
                                if (value == "0") {
                                    this@ADPresenter.hideHeight = hideHeight
                                    saveProfileInfo(height = value, hideHeight = hideHeight)
                                } else {
                                    saveProfileInfo(height = value)
                                    txtMyHeight?.text = value
                                }
                            }

                        }).showPopupWindow()
                }
            }

            imgAboutModify?.setOnClickListener {
                Activities.get().top?.let { it1 ->
                    UpdateAboutMeDialog(
                        it1,
                        txtAboutMe?.text.toString(),
                        object : UpdateAboutMeDialog.onCallBack {
                            override fun onCallback(value: String) {
                                txtAboutMe?.text = value
                                saveProfileInfo(aboutMe = value)
                            }

                        }).showPopupWindow()
                }
            }

            imgInterestsAdd?.setOnClickListener {
                Activities.get().top?.let { it1 ->
                    SelectInterestDialog(
                        it1,
                        interestsTags,
                        object : SelectInterestDialog.onCallBack {
                            override fun onCallback(tags: MutableList<IMTagBean>) {
                                val checkedTags: MutableList<String> = ArrayList()
                                val checkedTagsBean: MutableList<IMTagBean> = ArrayList()
                                repeat(tags.size) {
                                    if (tags[it].check) {
                                        checkedTags.add(tags[it].interest)
                                        checkedTagsBean.add(tags[it])
                                    }
                                }
                                interestsTags = checkedTags
                                upTagCloud(checkedTags, interestsTagCloud, tvEmptyInterestsTagCloud)

                                saveProfileInfo(interests = checkedTagsBean)
                            }

                        })
                }


            }
            ivNowIwantIn?.setOnClickListener {
                Activities.get().top?.let { it1 ->
                    CustomSelectorDialog()
                        .buildConfig(it1)
                        .setTitle(it1.getString(R.string.now_i_want))
                        .setDatas(data?.userWant)
                        .setMultipleChoice(false)
                        .setOnClickListener(object : CustomSelectorDialog.OnClickListener {
                            override fun save(
                                dialog: CustomSelectorDialog,
                                allDataList: MutableList<UserWantOrYouAcceptBean>?,
                                selectDataList: MutableList<UserWantOrYouAcceptBean>?,
                            ) {
                                selectDataList?.get(0)?.code?.let { it1 ->
                                    saveProfileInfo(userWant = it1) {
                                        data?.userWant?.clear()
                                        allDataList?.stream()?.forEach {
                                            data?.userWant?.add(it)
                                        }
                                        upDateNowIwant(selectDataList, tvNowIwant, tvEmptyNowIWant)
                                    }
                                }
                            }

                            override fun close() {
                            }

                            override fun selected() {
                            }

                        })
                        .show()
                }
            }
            ivYouAccpetIn?.setOnClickListener {
                Activities.get().top?.let { it1 ->
                    CustomSelectorDialog()
                        .buildConfig(it1)
                        .setTitle(it1.getString(R.string.you_accept))
                        .setDatas(data?.youAccept)
                        .setMultipleChoice(true)
                        .setOnClickListener(object : CustomSelectorDialog.OnClickListener {
                            override fun save(
                                dialog: CustomSelectorDialog,
                                allDataList: MutableList<UserWantOrYouAcceptBean>?,
                                selectDataList: MutableList<UserWantOrYouAcceptBean>?,
                            ) {
                                val codes = arrayListOf<Int>()
                                selectDataList?.forEach { codes.add(it.code) }
                                saveProfileInfo(youAccept = codes) {
                                    data?.youAccept?.clear()
                                    allDataList?.stream()?.forEach {
                                        data?.youAccept?.add(it)
                                    }
                                    upDateYouAccpet(selectDataList, tagYouAccept, tvEmptyTagYouAccept)
                                }
                            }

                            override fun close() {
                            }

                            override fun selected() {
                            }

                        })
                        .show()
                }
            }

            containerNowIWant?.isVisible =
                BaseConfig.getInstance.getInt(SpName.trafficSource, 0) != 1
            youAcceptContainer?.isVisible =
                BaseConfig.getInstance.getInt(SpName.trafficSource, 0) != 1
            containerProfileAbout?.isVisible =
                BaseConfig.getInstance.getInt(SpName.trafficSource, 0) != 1
        }
    }


    private fun upTagCloud(
        checkedTags: MutableList<String>,
        interestsTagCloud: TagCloudView?,
        tvEmptyInterestsTagCloud: TextView?,
    ) {
        tvEmptyInterestsTagCloud?.isVisible = checkedTags.size <= 0
        tvEmptyInterestsTagCloud?.text = if (BaseConfig.getInstance.getInt(
                SpName.trafficSource,
                0
            ) == 1
        ) Activities.get().top?.getString(R.string.like_minded_sweetheart_white) else Activities.get().top?.getString(
            R.string.like_minded_sweetheart
        )
        interestsTagCloud?.setTags(checkedTags)
    }

    private fun upDateNowIwant(
        dataList: MutableList<UserWantOrYouAcceptBean>?,
        tvNowIwant: TextView?,
        tvEmptyNowIWant: TextView?,
    ) {
        val userWant =
            dataList?.stream()?.filter { v -> v.selected == 1 }?.collect(
                Collectors.toList()
            )
        if (userWant != null && userWant.size > 0) {
            tvNowIwant?.text = userWant[0].value
            tvNowIwant?.isVisible = true
            tvEmptyNowIWant?.isVisible = false
        } else {
            tvNowIwant?.isVisible = false
            tvEmptyNowIWant?.isVisible = true
        }
    }

    private fun upDateYouAccpet(
        dataList: MutableList<UserWantOrYouAcceptBean>?,
        tagYouAccpet: TagCloudView?,
        tvEmptyTagYouAccept: TextView?,
    ) {
        val youAccpet = dataList?.stream()?.filter { v -> v.selected == 1 }?.collect(
            Collectors.toList()
        )
        val youAccpetTags = mutableListOf<String>()
        youAccpet?.stream()?.forEach { it.value?.let { it1 -> youAccpetTags.add(it1) } }
        if (youAccpetTags.size > 0) {
            tagYouAccpet?.setTags(youAccpetTags)
            tvEmptyTagYouAccept?.isVisible = false
        } else {
            tvEmptyTagYouAccept?.isVisible = true
        }

    }


    fun saveProfileInfo(
        nickName: String = "",
        birthday: String = "",
        lookingFor: String = "",
        interests: MutableList<IMTagBean> = arrayListOf(),
        aboutMe: String = "",
        socialConnections: String = "",
        height: String = "",
        hideHeight: Boolean = false,
        userWant: Int = 0,
        youAccept: ArrayList<Int> = arrayListOf(),
        success: (() -> Unit?)? = null,
    ) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_user_change_url)
                if (userWant != 0) {
                    requestBody.add("userWant", userWant)
                }
                if (youAccept.size > 0) {
                    requestBody.add("youAccept", youAccept)
                }
                if (nickName != "") {
                    requestBody.add("nickName", nickName)
                }
                if (birthday != "") {
                    requestBody.add("birthday", birthday)
                }
                if (lookingFor != "") {
                    requestBody.add("lookingFor", lookingFor)
                }

                if (interests.isNotEmpty()) {
                    val list = arrayListOf<Int>()
                    repeat(interests.size) {
                        list.add(interests[it].interestCode)
                    }
                    requestBody.add("interests", list)
                }

                if (aboutMe != "") {
                    requestBody.add("aboutMe", aboutMe)
                }
                if (socialConnections != "") {
                    requestBody.add("socialConnections", socialConnections)
                }
                if (height != "") {
                    if (height == "0") {
                        requestBody.add("hideHeight", hideHeight)
                    } else {
                        requestBody.add("height", height)
                    }
                }

            }
        }, object : SDOkHttpResoutCallBack<UserProfileEntity>() {
            override fun onSuccess(entity: UserProfileEntity) {
                success?.invoke()
                mView?.apply {
                    if (lookingFor != "") {
                        SDEventManager.post(EnumEventTag.INDEX_REFRESH_DATA.ordinal)
                    }
                    if (nickName != "") {
                        SDEventManager.post(EnumEventTag.MY_REFRESH.ordinal)
                    }
                }


            }

            override fun onFailure(code: Int, msg: String) {
                showToast(msg)
            }
        })
    }

}
