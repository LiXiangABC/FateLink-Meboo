package com.crush.ui.chat.profile

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.crush.view.TagCloudView
import com.crush.view.delay.DelayClickImageView
import com.custom.base.config.BaseConfig
import com.crush.mvp.MVPBaseActivity
import com.gyf.immersionbar.ImmersionBar
import com.youth.banner.Banner
import io.rong.imkit.SpName


/**
 * 设置app
 */
class UserProfileInfoActivity : MVPBaseActivity<UserProfileInfoContract.View, UserProfileInfoPresenter>(), UserProfileInfoContract.View {

    override fun setFullScreen(): Boolean {
        return true
    }
    override fun bindLayout(): Int {
        return R.layout.act_user_profile_info
    }

    override fun initView() {
        containerBackArrow.setOnClickListener {
            onBackPressed()
        }
        setWhiteUserVis()
    }

    override fun onResume() {
        super.onResume()
        ImmersionBar.with(mActivity)
            .statusBarDarkFont(true)
            .init()
    }

    override fun onStop() {
        super.onStop()
        //停止轮播
        banner.stop()
    }
    override fun onDestroy() {
        super.onDestroy()
        if (banner!= null) {
            banner.destroy()
        }
    }

    /**
     * 加白用户隐藏about me
     */
    private fun setWhiteUserVis(){
        userProfileIntroduction.isVisible=BaseConfig.getInstance.getInt(SpName.trafficSource,0)!=1
        userProfileAboutMeTitle.isVisible=BaseConfig.getInstance.getInt(SpName.trafficSource,0)!=1
        userProfileUserWant.isVisible=BaseConfig.getInstance.getInt(SpName.trafficSource,0)!=1
    }



    override val banner: Banner<*, *>
        get() = findViewById(R.id.user_profile_info_banner)
    override val scrollView: NestedScrollView
        get() = findViewById(R.id.scroll_view)
    override val userProfileOnline: TextView
        get() = findViewById(R.id.user_profile_online)
    override val userProfilePositioning: TextView
        get() = findViewById(R.id.user_profile_positioning)
    override val userProfileName: TextView
        get() = findViewById(R.id.user_profile_name)
    override val userProfileLocation: TextView
        get() = findViewById(R.id.user_profile_location)
    override val userProfileUserWant: TextView
        get() = findViewById(R.id.txt_user_want)
    override val userProfileHeight: TextView
        get() = findViewById(R.id.txt_user_height)
    override val userProfileStarSign: TextView
        get() =findViewById(R.id. user_profile_star_sign)
    override val userProfileIntroduction: TextView
        get() = findViewById(R.id.user_profile_introduction)
    override val userProfileAboutMeTitle: TextView
        get() = findViewById(R.id.user_profile_about_me_title)
    override val userProfileTagCloud: TagCloudView
        get() = findViewById(R.id.user_profile_tag_cloud)
    override val userProfileTurnOnsTitle: TextView
        get() =findViewById(R.id. user_profile_turn_ons_title)
    override val turnOnsList: RecyclerView
        get() =findViewById(R.id. turn_ons_list)
    override val containerBackArrow: ImageView
        get() = findViewById(R.id.container_back_arrow)
    override val containerMoreOperation: DelayClickImageView
        get() = findViewById(R.id.container_more_operation)
    override val userProfileDislike: DelayClickImageView
        get() = findViewById(R.id.user_profile_dislike)
    override val userProfileLike: DelayClickImageView
        get() = findViewById(R.id.user_profile_like)
    override val userProfileChat: DelayClickImageView
        get() = findViewById(R.id.user_profile_chat)
    override val outsideContainer: ConstraintLayout
        get() = findViewById(R.id.outside_container)
}