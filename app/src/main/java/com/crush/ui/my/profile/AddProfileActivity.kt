package com.crush.ui.my.profile

import android.Manifest
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.adjust.sdk.Adjust
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.crush.Constant
import com.crush.R
import com.crush.adapter.GenderPreferAdapter
import com.crush.bean.GenderPreferListBean
import com.crush.dot.AFDotLogUtil
import com.crush.dot.DotLogEventName
import com.crush.dot.DotLogUtil
import com.crush.entity.IMTokenGetEntity
import com.crush.entity.RegisterConfigEntity
import com.crush.entity.TrafficEntity
import com.crush.entity.UserProfileEntity
import com.crush.rongyun.RongConfigUtil
import com.crush.ui.index.location.LocationRequestActivity
import com.crush.util.CollectionUtils
import com.crush.util.DensityUtil
import com.crush.util.SoftInputUtils
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.MVPBaseActivity
import com.crush.util.IntentUtil
import com.crush.util.PermissionUtil
import com.crush.util.appContext
import com.custom.base.util.ToastUtil
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.crush.view.DateWheelLayout
import com.crush.view.EnglishDateFormatter
import com.crush.view.TagCloudView
import com.gyf.immersionbar.ImmersionBar
import io.rong.imkit.RongIM
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import io.rong.imkit.utils.JsonUtils
import io.rong.imkit.utils.RongUtils
import io.rong.imlib.model.UserInfo
import org.json.JSONObject
import java.util.Calendar


class AddProfileActivity : MVPBaseActivity<AddProfileContact.View, AddProfilePresenter>(),
    AddProfileContact.View {

    private var genderPreferAdapter: GenderPreferAdapter? = null
    var step: Int = 1
    var trafficSource: Int = 0
    private var iwantList: ArrayList<GenderPreferListBean> = arrayListOf(
        GenderPreferListBean(
            248,
            if (trafficSource != 1) "\uD83D\uDC60  Short-term fling" else "Short-term fling",
            "",
            0
        ),
        GenderPreferListBean(
            249,
            if (trafficSource != 1) "\uD83D\uDC91  Long-term commitment" else "Long-term commitment",
            "",
            0
        )
    )//配置默认的now i want参数，当无数据返回时使用
    private var selectDateValue = ""//默认的生日

    override fun bindLayout(): Int {
        return R.layout.act_add_profile
    }

    override fun setFullScreen(): Boolean {
        return true
    }

    override fun onBackListener() {
        backListener()

    }

    override fun onBackPressed() {
        backListener()
    }

    var genderStream: RegisterConfigEntity.ConfigsBean? = null
    var dateStream: RegisterConfigEntity.ConfigsBean? = null
    var nicknameStream: RegisterConfigEntity.ConfigsBean? = null
    var photoStream: RegisterConfigEntity.ConfigsBean? = null
    var wantStream: RegisterConfigEntity.ConfigsBean? = null
    var interestStream: RegisterConfigEntity.ConfigsBean? = null
    val preferArray = arrayListOf<GenderPreferListBean>()
    var manSelectMore = false
    var womanSelectMore = false
    fun initViewConfig(entity: RegisterConfigEntity?) {
        if (entity != null) {
            //年纪选择初始化
            if (CollectionUtils.isNotEmpty(entity.data.configs.filter {
                    it.pageType == 1
                })) {
                dateStream = entity.data.configs.filter {
                    it.pageType == 1
                }[0]
                dateStream?.let {
                    txtShowDateTitle.text = it.firstTitle ?: ""
                    txtShowDateTip.text = it.firstContent ?: ""
                    txtShowDateTip.visibility =
                        if (it.firstContent != null) View.VISIBLE else View.GONE
                    txtDateSaveTip.text = it.additional ?: ""
                    changeOutsideBg(it.backgroundPhoto)
                }

            }
            //昵称初始化
            if (CollectionUtils.isNotEmpty(entity.data.configs.filter {
                    it.pageType == 2
                })) {
                nicknameStream = entity.data.configs.filter {
                    it.pageType == 2
                }[0]
                nicknameStream?.let {
                    txtShowNameTitle.text = it.firstTitle ?: ""
                    txtShowNameTip.text = it.firstContent ?: ""
                    txtShowNameTip.visibility =
                        if (it.firstContent != null) View.VISIBLE else View.GONE
                    txtNicknameSaveTip.text = it.additional ?: ""
                    editNickname.setText(it.nickName ?: "")
                    txtNicknameNext.isEnabled = editNickname.text.toString().isNotEmpty()
                }

            }

            //性别初始化
            if (CollectionUtils.isNotEmpty(entity.data.configs.filter {
                    it.pageType == 3
                })) {
                genderStream = entity.data.configs.filter {
                    it.pageType == 3
                }[0]
                genderStream?.let {
                    txtGenderDefine.text = it.firstTitle ?: ""
                    txtPreferDefine.text = it.secondTitle ?: ""
                    txtPreferDefineTip.text = it.secondContent ?: ""
                    txtPreferDefineTip.visibility =
                        if (it.secondContent != null) View.VISIBLE else View.GONE
                    txtPreferDefineTip.visibility =
                        if (it.secondContent == null) View.GONE else View.VISIBLE
                    if (it.secondTitle != null) {
                        if (it.bodyShapeForMan != null && it.bodyShapeForWoman != null) {
                            Thread {
                                txtGenderNext.isEnabled = false
                                for (i in 0 until it.bodyShapeForMan.size) {
                                    Activities.get().top?.let { activity ->
                                        Glide.with(activity)
                                            .load(it.bodyShapeForMan[i].iconUrl)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .preload()
                                    }

                                }
                                for (i in 0 until it.bodyShapeForWoman.size) {
                                    Activities.get().top?.let { activity ->
                                        Glide.with(activity)
                                            .load(it.bodyShapeForWoman[i].iconUrl)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .preload()
                                    }
                                }
                            }.start()
                            preferList.layoutManager = GridLayoutManager(mActivity, 2)
                            (preferList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                                false
                            genderPreferAdapter =
                                GenderPreferAdapter(
                                    mActivity,
                                    preferArray,
                                    object : GenderPreferAdapter.OnListener {
                                        override fun onListener(position: Int) {
                                            if (mPresenter?.getGender() == 1) {
                                                it.bodyShapeForMan[position].selected =
                                                    if (it.bodyShapeForMan[position].selected == 0) 1 else 0
                                                preferList.adapter?.notifyItemChanged(position)
                                            } else if (mPresenter?.getGender() == 2) {
                                                it.bodyShapeForWoman[position].selected =
                                                    if (it.bodyShapeForWoman[position].selected == 0) 1 else 0
                                                preferList.adapter?.notifyItemChanged(position)
                                            }
                                            getAllGenderSelect()
                                        }

                                    })
                            preferList.adapter = genderPreferAdapter
                            if (mPresenter?.getGender() == 1) {//根据2级标题判断prefer是否展示
                                if (it.bodyShapeForMan.size > 4) {
                                    for (i in 0 until 4) {
                                        preferArray.add(it.bodyShapeForMan[i])
                                    }
                                } else {
                                    preferArray.addAll(it.bodyShapeForMan)
                                }
                                genderPreferAdapter?.notifyDataSetChanged()
                                noPreferencesClickToSelectMore.visibility =
                                    if (it.bodyShapeForMan.size > 4) View.VISIBLE else View.GONE

                            }
                        }
                    } else {
                        txtGenderNext.isEnabled = true
                        genderPreferContainer.visibility = View.GONE
                        noPreferencesClickToSelectMore.visibility = View.GONE
                        noPreferencesClickToSelectMoreWoman.visibility = View.GONE
                    }
                }
            }


            //照片初始化
            //加白不展示跳过
            txtPhotoSkip.isVisible = trafficSource != 1

            if (CollectionUtils.isNotEmpty(entity.data.configs.filter {
                    it.pageType == 4
                })) {
                photoStream = entity.data.configs.filter {
                    it.pageType == 4
                }[0]
                photoStream?.let {
                    photoTitle.text = it.firstTitle ?: ""
                    photoTitleTip.text = it.firstContent ?: ""
                    photoTitleTip.visibility =
                        if (it.firstContent != null) View.VISIBLE else View.GONE
                }

            }

            //want初始化
            if (CollectionUtils.isNotEmpty(entity.data.configs.filter {
                    it.pageType == 5
                })) {
                wantStream = entity.data.configs.filter {
                    it.pageType == 5
                }[0]
                wantStream?.let {
                    txtWantTitle.text = it.firstTitle ?: ""
                    txtWantTip.text = it.firstContent ?: ""
                    txtWantTip.visibility = if (it.firstContent != null) View.VISIBLE else View.GONE
                    txtAccpetTitle.text = it.secondTitle ?: ""
                    setWantInfo()
                }
            }

            if (CollectionUtils.isNotEmpty(entity.data.configs.filter {
                    it.pageType == 7
                })) {
                interestStream = entity.data.configs.filter {
                    it.pageType == 7
                }[0]
                interestStream?.let {
                    txtInterestTitle.text = it.firstTitle ?: ""
                    setInterestInfo()
                }
            }
        } else {
            setWantView(iwantList)
        }
    }

    private fun setInterestInfo(){
        interestStream?.let {
            val interestList = it.interestList
            interestList?.let {
                interestsTagCloud.setRegisterTagBeans(interestList,true)
                interestsTagCloud.setOnTagClickListener {position->
                    if (!interestList[position].check) {
                        var count = 0
                        for (i in 0 until interestList.size) {
                            if (interestList[i].check) {
                                count++
                            }
                        }
                        if (count >= 8) {
                            ToastUtil.toast(getString(R.string.interest_more_tip))
                            return@setOnTagClickListener
                        }
                    }

                    interestList[position].check = !interestList[position].check
                    interestsTagCloud.getTags(
                        position,
                        if (interestList[position].check) R.drawable.shape_interest_select_bg else R.drawable.shape_interest_unselect_bg
                    )
                    if (interestList.find { it.check }!= null){
                        txtInterestNext.setTextColor(ContextCompat.getColor(appContext, R.color.color_001912))
                        txtInterestNextContainer.setBackgroundResource(R.drawable.shape_solid_pink_radius_26)
                        txtInterestNextContainer.isEnabled = true
                    }

                }
            }


            txtInterestNextContainer.setOnClickListener {
                if (interestList?.find { it.check }==null){
                    ToastUtil.toast(getString(R.string.interest_toast))
                    return@setOnClickListener
                }
                actionLoading.visibility = View.VISIBLE
                actionLoading.playAnimation()
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(Constant.user_user_info_init_url)
                        var userCode = BaseConfig.getInstance.getString(SpName.userCode, "")
                        if (userCode.length > 4) {
                            userCode = userCode.substring(userCode.length - 3)
                        }
                        requestBody.add(
                            "nickName",
                            if (editNickname.text.toString() != "") editNickname.text.toString() else "Socialite$userCode"
                        )
                        requestBody.add("birthday", selectDateValue)
                        mPresenter?.let {
                            requestBody.add("gender", if (it.getGender() != -1) it.getGender() else 1)
                            if (CollectionUtils.isNotEmpty(it.getImageShow())) {
                                val list = arrayListOf<String>()
                                repeat(it.getImageShow().size) { it1 ->
                                    if (it.getImageShow()[it1].imageLoadUrl != "") {
                                        list.add(it.getImageShow()[it1].imageLoadUrl)
                                    }
                                }
                                requestBody.add("images", list)
                                requestBody.add("avatarUrl", it.getImageShow()[0].imageLoadUrl)
                            }

                            if (it.getGender() != 0) {
                                genderStream?.apply {
                                    val bodyList = arrayListOf<Long>()
                                    val bodyShapeList =
                                        if (it.getGender() == 1) genderStream!!.bodyShapeForMan else genderStream!!.bodyShapeForWoman
                                    if (bodyShapeList != null) {
                                        for (i in 0 until bodyShapeList.size) {
                                            if (bodyShapeList[i].selected == 1) {
                                                bodyList.add(bodyShapeList[i].code)
                                            }
                                        }
                                        requestBody.add("bodyShape", bodyList)
                                    }
                                }

                            }

                            var userWant: Long = 0
                            for (i in 0 until radioLookingGroup.childCount) {
                                if ((radioLookingGroup[i] as RadioButton).isChecked) {
                                    if (wantStream != null) {
                                        userWant = wantStream!!.iwantList[i].code
                                    } else {
                                        userWant = iwantList[i].code
                                    }
                                }
                            }
                            requestBody.add("userWant", userWant)
                            val acceptList = arrayListOf<Long>()
                            wantStream?.apply {
                                if (this.youAcceptForWomen != null && this.youAcceptForMan != null) {
                                    for (i in 0 until accpetContainer.childCount) {
                                        if ((accpetContainer[i] as CheckBox).isChecked) {
                                            if (it.getGender() == 2) {
                                                acceptList.add(this.youAcceptForWomen[i].code)
                                            } else {
                                                acceptList.add(this.youAcceptForMan[i].code)
                                            }
                                        }
                                    }
                                }
                            }
                            requestBody.add("youAccept", acceptList)
                            requestBody.add(
                                "lookingFor",
                                if (it.getGender() == 1) 2 else if (it.getGender() == 2) 1 else 0
                            )
                            if (interestList.isNotEmpty()) {
                                val filter = interestList.filter { it.check }
                                val list = arrayListOf<Int>()
                                repeat(filter.size) {
                                    list.add(filter[it].code)
                                }
                                requestBody.add("interests", list)
                            }
                        }


                    }
                }, object : SDOkHttpResoutCallBack<UserProfileEntity>() {
                    override fun onSuccess(entity: UserProfileEntity) {
                        AFDotLogUtil().addFirebaseSighUp(if (entity.data.email == null) "email" else "phone")
                        if (CollectionUtils.isNotEmpty(entity.data.images)) {
                            repeat(entity.data.images.size) {
                                if (!RongUtils.isDestroy(mActivity)) {
                                    Activities.get().top?.let { activity ->
                                        Glide.with(activity)
                                            .load(entity.data.images[it])
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .preload()
                                    }
                                }

                            }
                        }
                        BaseConfig.getInstance.setBoolean(SpName.profileComplete, true)
                        BaseConfig.getInstance.setString(
                            SpName.nickName,
                            editNickname.text.toString()
                        )
                        entity.data.avatarUrl?.apply {
                            BaseConfig.getInstance.setString(SpName.avatarUrl, this)
                        }


                        getTrafficFrom(entity)

                    }

                    override fun onFailure(code: Int, msg: String) {
                        actionLoading.visibility = View.GONE
                        actionLoading.pauseAnimation()
                        showToast(msg)
                    }
                })
            }
        }
    }

    private fun setWantInfo() {
        wantStream?.let {
            if (CollectionUtils.isNotEmpty(it.iwantList)) {
                setWantView(wantStream!!.iwantList)
            }
            if (it.secondTitle == null) {
                txtAccpetTitle.visibility = View.GONE
                accpetContainer.visibility = View.GONE
            } else {
                val accpetArray =
                    if (mPresenter?.getGender() == 2) it.youAcceptForWomen else it.youAcceptForMan
                if (CollectionUtils.isNotEmpty(accpetArray)) {
                    txtAccpetTitle.visibility = View.VISIBLE
                    accpetContainer.visibility = View.VISIBLE
                    setAcceptView(accpetArray)
                } else {
                    txtAccpetTitle.visibility = View.GONE
                    accpetContainer.visibility = View.GONE
                }
            }
        }
        textLookingNext.setTextColor(ContextCompat.getColor(mActivity, R.color.color_BBBBBB))
        txtLookingNext.setBackgroundResource(R.drawable.shape_solid_gray_f5f7f9_radius_26)
        txtLookingNext.isEnabled = false

    }

    private fun setWantView(iwantList: ArrayList<GenderPreferListBean>) {
        radioLookingGroup.removeAllViews()
        for (i in 0 until iwantList.size) {
            val radioButton = RadioButton(this)
            val layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dp2px(this, 54f)
            )
            layoutParams.topMargin = DensityUtil.dp2px(this, 20f)
            radioButton.layoutParams = layoutParams
            radioButton.setPadding(DensityUtil.dp2px(this, 31f), 0, 0, 0)
            radioButton.setBackgroundResource(R.drawable.selector_i_want_bg)
            radioButton.setTextColor(Color.BLACK)

            radioButton.setTextColor(ContextCompat.getColor(this, R.color.color_44F3C4))
            radioButton.setOnCheckedChangeListener { compoundButton, b ->
                if (b){
                    radioButton.setTextColor(ContextCompat.getColor(this, R.color.color_001912))
                }else {
                    radioButton.setTextColor(ContextCompat.getColor(this, R.color.color_44F3C4))
                }
            }
            radioButton.isChecked = false
            radioButton.buttonDrawable = null
            radioButton.text = iwantList[i].value
            radioButton.textSize = 18f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                radioButton.typeface = resources.getFont(R.font.intermedium)
            }
            radioLookingGroup.addView(radioButton)
        }
    }

    private fun setAcceptView(acceptArray: ArrayList<GenderPreferListBean>?) {
        acceptArray?.let {
            accpetContainer.removeAllViews()
            for (i in 0 until acceptArray.size) {
                val radioButton = CheckBox(this)
                val layoutParams = RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    DensityUtil.dp2px(this, 54f)
                )
                layoutParams.topMargin = DensityUtil.dp2px(this, 20f)
                radioButton.layoutParams = layoutParams
                radioButton.setPadding(DensityUtil.dp2px(this, 31f), 0, 0, 0)
                radioButton.setBackgroundResource(R.drawable.selector_i_want_bg)


                radioButton.setTextColor(ContextCompat.getColor(this, R.color.color_44F3C4))
                radioButton.setOnCheckedChangeListener { compoundButton, b ->
                    if (b){
                        radioButton.setTextColor(ContextCompat.getColor(this, R.color.color_001912))
                    }else {
                        radioButton.setTextColor(ContextCompat.getColor(this, R.color.color_44F3C4))
                    }
                }

                radioButton.isChecked = false
                radioButton.buttonDrawable = null
                radioButton.text = acceptArray[i].value
                radioButton.textSize = 18f
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    radioButton.typeface = resources.getFont(R.font.intermedium)
                }
                accpetContainer.addView(radioButton)
            }
        }

    }

    /**
     * 设置背景
     */
    fun changeOutsideBg(url: String) {
//        if (trafficSource == 1) {
//            return
//        }
//        Glide.with(this)
//            .load(url)
//            .into(object : CustomTarget<Drawable>() {
//                override fun onResourceReady(
//                    resource: Drawable,
//                    transition: Transition<in Drawable>?,
//                ) {
//                    outSideView.background = resource
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {
//
//                }
//
//            })
    }

    override fun onResume() {
        super.onResume()
        ImmersionBar.with(this).statusBarDarkFont(false).init()
    }

    override fun initView() {
        initAppsflyer()

        imgTopBack.setOnClickListener {
            backListener()
        }
        editNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                txtNicknameLengthTip.visibility =
                    if (editNickname.text.length < 30) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        editNickname.setOnEditorActionListener { v, actionId, event ->
            actionId == EditorInfo.IME_ACTION_DONE
        }

        editNickname.imeOptions = EditorInfo.IME_ACTION_DONE
        editNickname.setRawInputType(InputType.TYPE_CLASS_TEXT)

        txtNicknameNext.setOnClickListener {
            if (Character.isWhitespace(editNickname.text.toString().first())) {
                ToastUtil.toast(mActivity.getString(R.string.the_nickname_is_not_valid))
                return@setOnClickListener
            }

            if (editNickname.text.toString().isEmpty()) {
                return@setOnClickListener
            }
            //修改背景
            genderStream?.let {
                changeOutsideBg(it.backgroundPhoto)
            }
            step = 3
            nicknameContainer.visibility = View.GONE
            genderContainer.visibility = View.VISIBLE
            SoftInputUtils.hideSoftInput(editNickname)
            DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_NICKNAME_ON_NEXT_CLICK)
                .commit(mActivity)
        }



        maleContainer.setOnClickListener {
            mPresenter?.setGender(1)
            imgGenderMale.setImageResource(R.mipmap.icon_press_male)
            maleContainer.setBackgroundResource(R.drawable.shape_stroke_gender_man_select)


            imgGenderFemale.setImageResource(R.mipmap.icon_unpress_female)
            femaleContainer.setBackgroundResource(R.drawable.shape_stroke_gender_unselect)


            imgGenderQueer.setImageResource(R.mipmap.icon_unpress_queer)
            queerContainer.setBackgroundResource(R.drawable.shape_stroke_gender_unselect)

            txtGenderMale.setTextColor(ContextCompat.getColor(this, R.color.color_001912))
            txtGenderFemale.setTextColor(ContextCompat.getColor(this, R.color.color_44F3C4))
            txtGenderQueer.setTextColor(ContextCompat.getColor(this, R.color.color_44F3C4))

            noPreferencesClickToSelectMoreWoman.visibility = View.GONE
            genderStream?.let {
                if (it.secondTitle != null) {//根据2级标题判断prefer是否展示
                    if (it.bodyShapeForMan != null && it.bodyShapeForWoman != null) {
                        preferArray.clear()
                        //点击过选择更多按钮，按钮隐藏，未点击过判断bodyShapeForWoman条数是否大于4来控制是否展示按钮
                        if (!womanSelectMore) {
                            noPreferencesClickToSelectMore.visibility =
                                if (it.bodyShapeForMan.size <= 4) View.GONE else View.VISIBLE
                        } else {
                            noPreferencesClickToSelectMore.visibility = View.GONE

                        }
                        if (noPreferencesClickToSelectMore.visibility == View.VISIBLE) {//默认展示4个
                            if (it.bodyShapeForMan.size > 4) {
                                for (i in 0 until 4) {
                                    preferArray.add(it.bodyShapeForMan[i])
                                }
                            } else {
                                preferArray.addAll(it.bodyShapeForMan)
                            }
                        } else {
                            preferArray.addAll(it.bodyShapeForMan)
                        }
                        genderPreferAdapter?.notifyDataSetChanged()
                    }
                    genderPreferContainer.visibility =
                        if (it.secondTitle != null) View.VISIBLE else View.GONE
                }
            }
            getAllGenderSelect()
        }
        femaleContainer.setOnClickListener {
            mPresenter?.setGender(2)

            imgGenderFemale.setImageResource(R.mipmap.icon_press_female)
            femaleContainer.setBackgroundResource(R.drawable.shape_stroke_gender_woman_select)


            imgGenderMale.setImageResource(R.mipmap.icon_unpress_male)
            maleContainer.setBackgroundResource(R.drawable.shape_stroke_gender_unselect)


            imgGenderQueer.setImageResource(R.mipmap.icon_unpress_queer)
            queerContainer.setBackgroundResource(R.drawable.shape_stroke_gender_unselect)


            txtGenderFemale.setTextColor(ContextCompat.getColor(this, R.color.color_001912))
            txtGenderMale.setTextColor(ContextCompat.getColor(this, R.color.color_44F3C4))
            txtGenderQueer.setTextColor(ContextCompat.getColor(this, R.color.color_44F3C4))

            noPreferencesClickToSelectMore.visibility = View.GONE
            genderStream?.let {
                if (it.secondTitle != null) {//根据2级标题判断prefer是否展示
                    if (it.bodyShapeForMan != null && it.bodyShapeForWoman != null) {
                        preferArray.clear()

                        //点击过选择更多按钮，按钮隐藏，未点击过判断bodyShapeForWoman条数是否大于4来控制是否展示按钮
                        if (!womanSelectMore) {
                            noPreferencesClickToSelectMoreWoman.visibility =
                                if (it.bodyShapeForWoman.size <= 4) View.GONE else View.VISIBLE
                        } else {
                            noPreferencesClickToSelectMoreWoman.visibility = View.GONE

                        }

                        if (noPreferencesClickToSelectMoreWoman.visibility == View.VISIBLE) {//默认展示4个
                            if (it.bodyShapeForWoman.size > 4) {
                                for (i in 0 until 4) {
                                    preferArray.add(it.bodyShapeForWoman[i])
                                }
                            } else {
                                preferArray.addAll(it.bodyShapeForWoman)
                            }
                        } else {
                            preferArray.addAll(it.bodyShapeForWoman)
                        }
                        genderPreferAdapter?.notifyDataSetChanged()
                    }
                    genderPreferContainer.visibility =
                        if (it.secondTitle != null) View.VISIBLE else View.GONE

                }
            }

            getAllGenderSelect()
        }
        queerContainer.setOnClickListener {
            mPresenter?.setGender(0)
            imgGenderQueer.setImageResource(R.mipmap.icon_press_queer)
            queerContainer.setBackgroundResource(R.drawable.shape_stroke_gender_queer_select)
            imgGenderFemale.setImageResource(R.mipmap.icon_unpress_female)
            femaleContainer.setBackgroundResource(R.drawable.shape_stroke_gender_unselect)
            imgGenderMale.setImageResource(R.mipmap.icon_unpress_male)
            maleContainer.setBackgroundResource(R.drawable.shape_stroke_gender_unselect)

            txtGenderQueer.setTextColor(ContextCompat.getColor(this, R.color.color_001912))
            txtGenderMale.setTextColor(ContextCompat.getColor(this, R.color.color_44F3C4))
            txtGenderFemale.setTextColor(ContextCompat.getColor(this, R.color.color_44F3C4))

            getAllGenderSelect()
            noPreferencesClickToSelectMore.visibility = View.GONE
            noPreferencesClickToSelectMoreWoman.visibility = View.GONE

            genderPreferContainer.visibility = View.GONE
        }

        txtGenderNext.setOnClickListener {
            photoStream?.let {
                changeOutsideBg(photoStream!!.backgroundPhoto)
            }
            step = 4
            genderContainer.visibility = View.GONE
            photoContainer.visibility = View.VISIBLE
            DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_GENDER_ON_NEXT_CLICK)
                .commit(mActivity)
        }

        txtPhotoNext.setOnClickListener {
            wantStream?.let {
                changeOutsideBg(wantStream!!.backgroundPhoto)
            }
            step = 5
            setWantInfo()
            photoContainer.visibility = View.GONE
            purposeContainer.visibility = View.VISIBLE
            DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_UPLOAD_ON_NEXT_CLICK)
                .commit(mActivity)
            uploadAppsflyer()

        }
        txtPhotoSkip.setOnClickListener {
            wantStream?.let {
                changeOutsideBg(wantStream!!.backgroundPhoto)
            }
            step = 5
            setWantInfo()
            photoContainer.visibility = View.GONE
            purposeContainer.visibility = View.VISIBLE
            DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_UPLOAD_ON_SKIP_CLICK)
                .commit(mActivity)
            uploadAppsflyer()

        }
        noPreferencesClickToSelectMore.setOnClickListener {
            genderStream?.let {
                if (it.bodyShapeForMan != null) {
                    noPreferencesClickToSelectMore.visibility = View.GONE
                    preferArray.clear()
                    preferArray.addAll(it.bodyShapeForMan)
                    genderPreferAdapter?.notifyDataSetChanged()
                    manSelectMore = true
                    DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_NO_PREFERENCES_CLICK)
                        .commit(mActivity)
                }
            }
        }
        noPreferencesClickToSelectMoreWoman.setOnClickListener {
            genderStream?.let {
                if (it.bodyShapeForWoman != null) {
                    noPreferencesClickToSelectMoreWoman.visibility = View.GONE
                    preferArray.clear()
                    preferArray.addAll(it.bodyShapeForWoman)
                    genderPreferAdapter?.notifyDataSetChanged()
                    womanSelectMore = true
                    DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_NO_PREFERENCES_CLICK)
                        .commit(mActivity)
                }
            }
        }
        noPreferencesClickToSelectMore.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        noPreferencesClickToSelectMoreWoman.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        initDate()

        txtLookingNext.setOnClickListener {
            var selectWant = false
            for (i in 0 until radioLookingGroup.childCount) {
                if ((radioLookingGroup[i] as RadioButton).isChecked) {
                    selectWant = true
                }
            }
            if (!selectWant) {
                return@setOnClickListener
            }

            step = 6
            setInterestInfo()
            purposeContainer.visibility = View.GONE
            interestContainer.visibility = View.VISIBLE
            DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_WYH_ON_NEXT_CLICK)
                .commit(mActivity)

        }


    }

    private fun goActivity(entity: UserProfileEntity) {
        if (PermissionUtil.checkPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) && PermissionUtil.checkPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) && trafficSource != 1) {
            if (entity.data.nickName != null) {
                RongIM.getInstance().refreshUserInfoCache(
                    UserInfo(
                        entity.data.userCode,
                        entity.data.nickName,
                        Uri.parse(entity.data.avatarUrl)
                    )
                )
            }
            IntentUtil.startActivity(LocationRequestActivity::class.java)
            finish()
        } else {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.im_token_Url)
                }
            }, object : SDOkHttpResoutCallBack<IMTokenGetEntity>() {
                override fun onSuccess(parms: IMTokenGetEntity) {
                    RongConfigUtil.connectIMLogin(
                        parms.data.token,
                        entity.data.userCode,
                        entity.data.nickName,
                        entity.data.avatarUrl ?: "", mActivity
                    )
                }

                override fun onFailure(code: Int, msg: String) {
                    actionLoading.visibility = View.GONE
                    actionLoading.pauseAnimation()
                }
            })
        }
    }

    /**
     * 获取加白控制判断
     */
    private fun getTrafficFrom(userEntity: UserProfileEntity) {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setGet(Constant.traffic_from_url)
            }
        }, object : SDOkHttpResoutCallBack<TrafficEntity>() {
            override fun onSuccess(entity: TrafficEntity) {
                BaseConfig.getInstance.setInt(SpName.trafficSource, entity.data.trafficSource ?: 0)
                goActivity(userEntity)
            }

            override fun onFailure(code: Int, msg: String) {
                BaseConfig.getInstance.setInt(SpName.trafficSource, 0)
                goActivity(userEntity)
            }
        })
    }

    /**
     * 返回按钮事件判断
     */
    private fun backListener() {
        imgTopBack.visibility = View.VISIBLE
        when (step) {
            1 -> {
                finish()
            }

            2 -> {
                dateStream?.let {
                    changeOutsideBg(it.backgroundPhoto)
                }
                imgTopBack.visibility = View.INVISIBLE
                step = 1
                dateContainer.visibility = View.VISIBLE
                nicknameContainer.visibility = View.GONE
            }

            3 -> {
                nicknameStream?.let {
                    changeOutsideBg(it.backgroundPhoto)
                }
                step = 2
                nicknameContainer.visibility = View.VISIBLE
                genderContainer.visibility = View.GONE
            }

            4 -> {
                genderStream?.let {
                    changeOutsideBg(it.backgroundPhoto)
                }
                step = 3
                genderContainer.visibility = View.VISIBLE
                photoContainer.visibility = View.GONE
            }

            5 -> {
                photoStream?.let {
                    changeOutsideBg(it.backgroundPhoto)
                }
                step = 4
                photoContainer.visibility = View.VISIBLE
                purposeContainer.visibility = View.GONE

            }
            6 -> {
                wantStream?.let {
                    changeOutsideBg(it.backgroundPhoto)
                }
                step = 5
                purposeContainer.visibility = View.VISIBLE
                interestContainer.visibility = View.GONE

            }
        }
    }

    private fun getRegisterConfig() {
        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.register_config_url)
            }
        }, object : SDOkHttpResoutCallBack<RegisterConfigEntity>(false) {
            override fun onSuccess(entity: RegisterConfigEntity) {
                entity?.let {
                    initViewConfig(it)
                }
            }

            override fun onFailure(code: Int, msg: String) {
                super.onFailure(code, msg)
                initViewConfig(null)
            }
        })
    }

    private fun initAppsflyer() {
        try {
            OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                override fun addBody(requestBody: OkHttpBodyEntity) {
                    requestBody.setPost(Constant.user_addAdjust_info_url)
                    Adjust.getAdid { requestBody.add("adid", it) }
                    requestBody.add("gps_adid", BaseConfig.getInstance.getString(SpName.googleAdId, ""))
                    requestBody.add("channel", BaseConfig.getInstance.getString(SpName.channel, ""))
                }
            }, object : SDOkHttpResoutCallBack<TrafficEntity>() {
                override fun onSuccess(entity: TrafficEntity) {
                    //保存已经上传adjust channel 信息 状态
                    BaseConfig.getInstance.setBoolean(SpName.isAlreadyUpAdjuestChannelInfo, true)

                    trafficSource = entity.data.trafficSource ?: 0
                    //保存已经上传adjust channel 信息 状态
                    getRegisterConfig()
                }

                override fun onFailure(code: Int, msg: String) {
                    super.onFailure(code, msg)
                    trafficSource = BaseConfig.getInstance.getInt(SpName.trafficSource, 0)
                    getRegisterConfig()
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun uploadAppsflyer() {
        try {
            Adjust.getAdid {
                OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
                    override fun addBody(requestBody: OkHttpBodyEntity) {
                        requestBody.setPost(Constant.user_addAdjust_info_url)
                        requestBody.add("adid", it)
                        requestBody.add(
                            "gps_adid",
                            BaseConfig.getInstance.getString(SpName.googleAdId, "")
                        )
                        requestBody.add(
                            "channel",
                            BaseConfig.getInstance.getString(SpName.channel, "")
                        )

                    }
                }, object : SDOkHttpResoutCallBack<TrafficEntity>() {
                    override fun onSuccess(entity: TrafficEntity) {
                        //保存已经上传adjust channel 信息 状态
                        BaseConfig.getInstance.setBoolean(
                            SpName.isAlreadyUpAdjuestChannelInfo,
                            true
                        )
                    }
                })
            }

        } catch (e: Exception) {
        }
    }

    private fun setInfo() {
        //初始化字段
        val entity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("entity", RegisterConfigEntity::class.java)
        } else {
            intent.getSerializableExtra("entity") as RegisterConfigEntity
        }
        entity?.let { initViewConfig(it) }
    }

    /**
     * 判断是否可以点击next
     */
    fun getAllGenderSelect() {
        when (mPresenter?.getGender()) {
            0 -> {
                txtGenderNext.isEnabled = true
            }

            1 -> {
                val filter = genderStream?.bodyShapeForMan?.filter {
                    it.selected == 1
                }
                txtGenderNext.isEnabled =
                    if (genderStream?.secondTitle == null) true else filter?.isNotEmpty() == true
            }

            2 -> {
                val filter = genderStream?.bodyShapeForWoman?.filter {
                    it.selected == 1
                }
                txtGenderNext.isEnabled =
                    if (genderStream?.secondTitle == null) true else filter?.isNotEmpty() == true
            }
        }
    }

    /**
     * 年龄选择
     */
    private fun initDate() {
        //日期控件参数设置
        dateSelectLayout.setDateMode(DateMode.YEAR_MONTH_DAY)
        val c = Calendar.getInstance()
        //设置最小可选年份
        c.add(Calendar.YEAR, -18)
        selectDateValue =
            "${DateEntity.target(c).month}/${EnglishDateFormatter().formatDay(DateEntity.target(c).day)}/${
                DateEntity.target(c).year
            }"
        //设置当前选择的年月日
        dateSelectLayout.setRange(
            DateEntity.target(c.weekYear - 42, 1, 1),
            DateEntity.target(c),
            DateEntity.target(c)
        )
        dateSelectLayout.setVisibleItemCount(5)
        dateSelectLayout.setItemSpace(DensityUtil.dip2px(this, 45f))
        dateSelectLayout.setAtmosphericEnabled(false)
        dateSelectLayout.setIndicatorEnabled(true)
        dateSelectLayout.setIndicatorColor(
            ContextCompat.getColor(
                this,
                R.color.color_44F3C4
            )
        )
        dateSelectLayout.setIndicatorSize(DensityUtil.dp2pxF(this, 1f))
        dateSelectLayout.setTextColor(Color.BLACK)
        dateSelectLayout.setSelectedTextColor(
            ContextCompat.getColor(
                this,
                R.color.color_44F3C4
            )
        )
        //设置字体
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateSelectLayout.dayWheelView.typeface =
                resources.getFont(R.font.cabin_medium)
            dateSelectLayout.monthWheelView.typeface =
                resources.getFont(R.font.cabin_medium)
            dateSelectLayout.yearWheelView.typeface =
                resources.getFont(R.font.cabin_medium)
        }
        //日期选择监听
        dateSelectLayout.setOnDateSelectedListener { year, month, day ->
            selectDateValue = "$month/${EnglishDateFormatter().formatDay(day)}/$year"
        }

        c.add(Calendar.YEAR, -18)
        val spannableString =
            SpannableString(String.format(getString(R.string.you_re_18_years_old), "18"))
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_44F3C4)),
            7,
            9,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        val sizeSpan = RelativeSizeSpan(1.25f)
        spannableString.setSpan(sizeSpan, 7, 9, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
        selectDateTips.text = spannableString
        selectDateTips.visibility = View.VISIBLE
        txtDateNext.setOnClickListener {
            nicknameStream?.let {
                changeOutsideBg(it.backgroundPhoto)
            }
            step = 2
            imgTopBack.visibility = View.VISIBLE
            dateContainer.visibility = View.GONE
            nicknameContainer.visibility = View.VISIBLE
            DotLogUtil.setEventName(DotLogEventName.USER_PROFILE_BIRTHDAY_ON_NEXT_CLICK)
                .commit(mActivity)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        actionLoading.cancelAnimation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter?.onActivityResult(requestCode, resultCode, data)
    }

    override val outSideView: ConstraintLayout
        get() = findViewById(R.id.out_side_view)

    override val nicknameContainer: ConstraintLayout
        get() = findViewById(R.id.nickname_container)
    override val editNickname: EditText
        get() = findViewById(R.id.edit_nickname)
    override val txtShowNameTitle: TextView
        get() = findViewById(R.id.txt_show_name)
    override val txtShowNameTip: TextView
        get() = findViewById(R.id.txt_show_name_tip)
    override val txtNicknameSaveTip: TextView
        get() = findViewById(R.id.txt_nickname_save_tip)
    override val txtNicknameNext: TextView
        get() = findViewById(R.id.txt_nickname_next)
    override val txtNicknameLengthTip: TextView
        get() = findViewById(R.id.txt_nickname_length_tip)

    override val dateContainer: ConstraintLayout
        get() = findViewById(R.id.date_container)
    override val txtDateNext: TextView
        get() = findViewById(R.id.txt_date_next)


    override val genderContainer: ConstraintLayout
        get() = findViewById(R.id.gender_container)
    override val genderPreferContainer: ConstraintLayout
        get() = findViewById(R.id.gender_prefer_container)
    override val maleContainer: LinearLayout
        get() = findViewById(R.id.male_container)
    override val imgGenderMale: ImageView
        get() = findViewById(R.id.img_gender_male)
    override val txtGenderMale: TextView
        get() = findViewById(R.id.txt_gender_male)
    override val imgGenderFemale: ImageView
        get() = findViewById(R.id.img_gender_female)
    override val txtGenderFemale: TextView
        get() = findViewById(R.id.txt_gender_female)
    override val imgGenderQueer: ImageView
        get() = findViewById(R.id.img_gender_queer)
    override val txtGenderQueer: TextView
        get() = findViewById(R.id.txt_gender_queer)
    override val txtGenderDefine: TextView
        get() = findViewById(R.id.txt_gender_define)
    override val txtGenderNext: TextView
        get() = findViewById(R.id.txt_gender_next)
    override val txtPreferDefine: TextView
        get() = findViewById(R.id.txt_prefer_define)
    override val txtPreferDefineTip: TextView
        get() = findViewById(R.id.txt_prefer_define_tip)
    override val preferList: RecyclerView
        get() = findViewById(R.id.prefer_list)
    override val noPreferencesClickToSelectMore: TextView
        get() = findViewById(R.id.no_preferences_click_to_select_more)
    override val noPreferencesClickToSelectMoreWoman: TextView
        get() = findViewById(R.id.no_preferences_click_to_select_more_woman)
    override val femaleContainer: LinearLayout
        get() = findViewById(R.id.female_container)
    override val queerContainer: LinearLayout
        get() = findViewById(R.id.queer_container)

    override val txtPhotoNext: TextView
        get() = findViewById(R.id.txt_photo_next)
    override val txtPhotoSkip: TextView
        get() = findViewById(R.id.txt_photo_skip)
    override val photoContainer: ConstraintLayout
        get() = findViewById(R.id.photo_container)
    override val photoList: RecyclerView
        get() = findViewById(R.id.photo_list)
    override val photoTitle: TextView
        get() = findViewById(R.id.photo_title)
    override val photoTitleTip: TextView
        get() = findViewById(R.id.photo_title_tip)


    override val purposeContainer: ConstraintLayout
        get() = findViewById(R.id.purpose_container)
    override val txtLookingNext: LinearLayout
        get() = findViewById(R.id.txt_looking_next)
    override val radioLookingGroup: RadioGroup
        get() = findViewById(R.id.radio_looking_group)
    override val actionLoading: LottieAnimationView
        get() = findViewById(R.id.action_loading)
    override val textLookingNext: TextView
        get() = findViewById(R.id.text_looking_next)
    override val txtWantTitle: TextView
        get() = findViewById(R.id.txt_want_title)
    override val txtWantTip: TextView
        get() = findViewById(R.id.txt_want_tip)
    override val txtAccpetTitle: TextView
        get() = findViewById(R.id.txt_accept_title)
    override val accpetContainer: LinearLayout
        get() = findViewById(R.id.accpet_container)
    override val imgTopBack: ImageView
        get() = findViewById(R.id.img_top_back)
    override val dateSelectLayout: DateWheelLayout
        get() = findViewById(R.id.date_select_layout)

    override val txtShowDateTitle: TextView
        get() = findViewById(R.id.txt_show_date)
    override val txtShowDateTip: TextView
        get() = findViewById(R.id.txt_show_date_tip)
    override val txtDateSaveTip: TextView
        get() = findViewById(R.id.txt_date_save_tip)
    override val selectDateTips: TextView
        get() = findViewById(R.id.select_date_tips)

    override val interestContainer: ConstraintLayout
        get() = findViewById(R.id.interest_container)
    override val txtInterestTitle: TextView
        get() = findViewById(R.id.txt_interest_title)
    override val interestsTagCloud: TagCloudView
        get() = findViewById(R.id.interests_tag_cloud)
    override val txtInterestNext: TextView
        get() = findViewById(R.id.text_interest_next)
    override val txtInterestNextContainer: LinearLayout
        get() = findViewById(R.id.txt_interest_next_container)


}