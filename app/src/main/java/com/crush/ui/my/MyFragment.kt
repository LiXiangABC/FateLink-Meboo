package com.crush.ui.my

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.crush.R
import com.crush.dialog.HelpCenterDialog
import com.crush.ui.HomeActivity
import com.crush.ui.index.helper.IndexHelper
import com.crush.ui.like.ILikeActivity
import io.rong.imkit.event.EnumEventTag
import com.crush.ui.my.profile.info.ProfileInfoActivity
import com.crush.ui.my.settings.SettingActivity
import com.crush.util.DateUtils
import com.crush.util.MyCountDownTimer
import com.crush.view.delay.DelayClickConstraintLayout
import com.custom.base.config.BaseConfig
import com.google.android.material.appbar.AppBarLayout
import com.gyf.immersionbar.ImmersionBar
import com.crush.mvp.MVPBaseFragment
import com.makeramen.roundedimageview.RoundedImageView
import com.sunday.eventbus.SDBaseEvent
import com.sunday.eventbus.SDEventManager
import io.rong.imkit.SpName
import io.rong.imkit.activity.Activities
import io.rong.imkit.activity.RongWebviewActivity
import io.rong.imkit.conversation.messgelist.provider.TextMessageItemProvider
import io.rong.imkit.utils.RongUtils
import java.util.Calendar
import kotlin.math.abs

/**
 * 作者：
 * 时间：
 * 描述：我的
 */
class MyFragment : MVPBaseFragment<MyContract.View, MyPresenter>(), MyContract.View {

    override lateinit var imgMyAvatar: RoundedImageView
    override lateinit var txtUserName: TextView

    override lateinit var containerHelpCenter: LinearLayout
    override lateinit var containerSetting: LinearLayout
    override lateinit var editProfileContainer: LinearLayout
    override lateinit var txtGetPremium: TextView
    override lateinit var imgMemberBg: ImageView
    override lateinit var txtFlashChat: TextView
    override lateinit var txtPrivatePhotos: TextView
    override lateinit var txtPrivateVideos: TextView
    override lateinit var conMyGiftPackBox: DelayClickConstraintLayout
    override lateinit var conMyPwdExchangeBox: ConstraintLayout
    override lateinit var boostFlashChat: DelayClickConstraintLayout
    override lateinit var boostPrivatePhoto: DelayClickConstraintLayout
    override lateinit var boostPrivateVideo: DelayClickConstraintLayout
    override lateinit var txtMemberInfo: TextView
    override lateinit var txtMemberContent: TextView
    override lateinit var txtMemberTitle: TextView
    override lateinit var imgMyCrown: ImageView
    override lateinit var privateAlbumContainer: LinearLayout
    override lateinit var ctbScrollView: AppBarLayout
    override lateinit var clMemberContainer: ConstraintLayout
    override lateinit var myChristmasContainer: ConstraintLayout
    override lateinit var imgMyTitleChristmas: ImageView
    override lateinit var btnModifyProfile: ImageView
    override lateinit var specialMemberTip: TextView
    override lateinit var txtMeILikeSize: TextView
    override lateinit var meILikeContainer: LinearLayout


    override fun bindLayout(): Int {
        return R.layout.frag_my
    }

    fun findView() {
        Activities.get().top?.let {
            imgMyAvatar = it.findViewById(R.id.img_my_avatar)
            txtUserName = it.findViewById(R.id.txt_user_name)
            containerHelpCenter = it.findViewById(R.id.container_help_center)
            containerSetting = it.findViewById(R.id.container_setting)
            editProfileContainer = it.findViewById(R.id.edit_profile_container)
            txtGetPremium = it.findViewById(R.id.txt_get_premium)
            imgMemberBg = it.findViewById(R.id.img_member_bg)
            txtFlashChat = it.findViewById(R.id.txt_flash_chat)
            txtPrivatePhotos = it.findViewById(R.id.txt_private_photos)
            txtPrivateVideos = it.findViewById(R.id.txt_private_videos)
            conMyGiftPackBox = it.findViewById(R.id.conMyGiftPackBox)
            conMyPwdExchangeBox = it.findViewById(R.id.conMyPwdExchangeBox)
            boostFlashChat = it.findViewById(R.id.boost_flash_chat)
            boostPrivatePhoto = it.findViewById(R.id.boost_private_photo)
            boostPrivateVideo = it.findViewById(R.id.boost_private_video)
            txtMemberInfo = it.findViewById(R.id.txt_member_info)
            txtMemberContent = it.findViewById(R.id.txt_member_content)
            txtMemberTitle = it.findViewById(R.id.txt_member_title)
            imgMyCrown = it.findViewById(R.id.img_my_crown)
            privateAlbumContainer = it.findViewById(R.id.private_album_container)
            ctbScrollView = it.findViewById(R.id.ctb_scroll_view)
            clMemberContainer = it.findViewById(R.id.cl_member_container)
            myChristmasContainer = it.findViewById(R.id.my_christmas_container)
            imgMyTitleChristmas = it.findViewById(R.id.img_my_title_christmas)
            btnModifyProfile = it.findViewById(R.id.btn_modify_profile)
            specialMemberTip = it.findViewById(R.id.special_member_tip)
            txtMeILikeSize = it.findViewById(R.id.txt_me_i_like_size)
            meILikeContainer = it.findViewById(R.id.me_i_like_container)
        }

    }

    var month = 0
    var dayOfMonth = 0

    var countDownTimer: MyCountDownTimer? = null
    override fun initView(view: View) {
        findView()
        val calendar = Calendar.getInstance()
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)


        containerHelpCenter.setOnClickListener {
            Activities.get().top?.let { it1 -> HelpCenterDialog(it1).showPopupWindow() }
        }
        startActivity(ProfileInfoActivity::class.java, view = editProfileContainer)
        startActivity(ProfileInfoActivity::class.java, view = btnModifyProfile)
        startActivity(SettingActivity::class.java, view = containerSetting)
        startActivity(ILikeActivity::class.java, view = meILikeContainer)

        ctbScrollView.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percentage = abs(verticalOffset).toDouble() / ctbScrollView.height
//            ImmersionBar.with(mActivity)
//                .statusBarDarkFont((percentage > 0.6))
//                .init()
        }

        mPresenter?.getMemberData()
        mPresenter?.setClick()
        Activities.get().top?.let {
            IndexHelper.showDiscountPop(
                false,
                BaseConfig.getInstance.getBoolean(SpName.isMember, false),
                it
            ) { infoEntity ->
                try {
                    if (it is HomeActivity){
                        it.showDiscountDownTime(infoEntity)
                    }
                } catch (e: Exception) {
                }

            }
        }
    }

    fun discountDownTime() {
        IndexHelper.getDiscountPopData(BaseConfig.getInstance.getBoolean(SpName.isMember, false)) {
            if (it.popOverTime < 1) {
                return@getDiscountPopData
            }
            specialMemberTip.visibility = View.VISIBLE
            countDownTimer =
                IndexHelper.convertDownTime(it.popOverTime, 1000, onTick = { hour, minute, second ->
                    if (RongUtils.isDestroy(Activities.get().top)) {
                        countDownTimer?.cancel()
                        return@convertDownTime
                    }
                    txtGetPremium.text =
                        Activities.get().top?.getString(R.string.get_special) + " ${
                            IndexHelper.convertDownTimeStr(
                                hour,
                                minute,
                                second
                            )
                        }"
                }, onFinish = {
                    SDEventManager.post(EnumEventTag.DISCOUNT_COUNTDOWN_END.ordinal)
                })
        }
    }

    override fun onEventMainThread(event: SDBaseEvent) {
        when (EnumEventTag.valueOf(event.tagInt)) {
            EnumEventTag.MY_REFRESH -> {
                mPresenter?.getData()
            }

            EnumEventTag.DISCOUNT_COUNTDOWN_REFRESH -> {
                specialMemberTip.visibility = View.VISIBLE
                txtGetPremium?.text =
                    "Get special ${DateUtils.timeHourConversion(event.data.toString().toLong())}"
            }

            EnumEventTag.DISCOUNT_COUNTDOWN_END -> {
                specialMemberTip.visibility = View.GONE
                txtGetPremium?.text = getString(R.string.get_premium)
            }


            else -> {}
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.getData()
        mPresenter?.getILikeCount()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (!(month == 11 && dayOfMonth < 27)) {
                ImmersionBar.with(this)
                    .statusBarDarkFont(true)
                    .init()
            } else {
                ImmersionBar.with(this)
                    .statusBarDarkFont(true)
                    .transparentBar()
                    .navigationBarEnable(false)
                    .init()
            }
            mPresenter?.getData()
            mPresenter?.getILikeCount()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter?.onActivityResult(requestCode, resultCode, data)
    }

}