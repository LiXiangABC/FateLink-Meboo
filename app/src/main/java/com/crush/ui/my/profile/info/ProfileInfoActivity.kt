package com.crush.ui.my.profile.info

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crush.Constant
import com.crush.R
import com.crush.adapter.ProfilePagerAdapter
import com.crush.entity.UserProfileEntity
import com.crush.ui.my.profile.info.fragment.ADFragment
import com.crush.ui.my.profile.info.fragment.PhotoFragment
import com.crush.ui.my.profile.info.fragment.TurnOnsFragment
import com.crush.util.CollectionUtils
import com.custom.base.config.BaseConfig
import com.custom.base.entity.OkHttpBodyEntity
import com.custom.base.http.OkHttpFromBoy
import com.custom.base.http.OkHttpManager
import com.custom.base.http.SDOkHttpResoutCallBack
import com.crush.mvp.MVPBaseActivity
import com.flyco.tablayout.SlidingTabLayout
import com.flyco.tablayout.listener.OnTabSelectListener
import com.gyf.immersionbar.ImmersionBar
import com.yalantis.ucrop.util.DensityUtil
import io.rong.imkit.SpName
import io.rong.imkit.utils.RongUtils

class ProfileInfoActivity : MVPBaseActivity<ProfileInfoContact.View, ProfileInfoPresenter>(),
    ProfileInfoContact.View {
    var adFragment: Fragment? = null
    var photoFragment: Fragment? = null
    var turnOnsFragment: Fragment? = null
    private val mFragments = arrayListOf<Fragment>()
    private val mTitles = arrayListOf("AD", "Photo")
    override fun bindLayout(): Int {
        return R.layout.act_profile_info
    }

    override fun setFullScreen(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        findViewById<ConstraintLayout>(R.id.viewMain).setPadding(
            0,
            DensityUtil.getStatusBarHeight(this@ProfileInfoActivity),
            0,
            0
        )
        savedInstanceState?.let {
            if (supportFragmentManager.findFragmentByTag(ADFragment::class.java.simpleName) != null) {
                adFragment =
                    supportFragmentManager.findFragmentByTag(ADFragment::class.java.simpleName) as ADFragment
            }
            if (supportFragmentManager.getFragment(
                    savedInstanceState,
                    PhotoFragment::class.java.simpleName
                ) != null
            ) {
                photoFragment =
                    supportFragmentManager.findFragmentByTag(PhotoFragment::class.java.simpleName) as PhotoFragment
            }
            if (supportFragmentManager.findFragmentByTag(TurnOnsFragment::class.java.simpleName) != null) {
                turnOnsFragment =
                    supportFragmentManager.findFragmentByTag(TurnOnsFragment::class.java.simpleName) as TurnOnsFragment
            }
        }
        if (adFragment == null) {
            adFragment = ADFragment()
        }
        if (photoFragment == null) {
            photoFragment = PhotoFragment()
        }
        if (turnOnsFragment == null) {
            turnOnsFragment = TurnOnsFragment()
        }

        if (!mFragments.contains(adFragment)) {
            mFragments.add(adFragment as ADFragment)
        }
        if (!mFragments.contains(photoFragment)) {
            mFragments.add(photoFragment as PhotoFragment)
        }

        OkHttpManager.instance.requestInterface(object : OkHttpFromBoy {
            override fun addBody(requestBody: OkHttpBodyEntity) {
                requestBody.setPost(Constant.user_info_url)
            }
        }, object : SDOkHttpResoutCallBack<UserProfileEntity>() {
            override fun onSuccess(entity: UserProfileEntity) {
                for (i in 0 until entity.data.images.size) {
                    if (!RongUtils.isDestroy(mActivity)) {
                        Glide.with(mActivity)
                            .load(entity.data.images[i])
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .preload()
                    }
                }
                if (CollectionUtils.isNotEmpty(entity.data.turnOnsList)) {
                    repeat(entity.data.turnOnsList.size) {
                        if (!RongUtils.isDestroy(mActivity)) {
                            Glide.with(mActivity)
                                .load(entity.data.turnOnsList[it].imageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .preload()
                        }
                    }
                }


                BaseConfig.getInstance.setString(SpName.userCode, entity.data.userCode)

                val bundle = Bundle()
                bundle.putSerializable("entity", entity.data)
                adFragment?.arguments = bundle
                photoFragment?.arguments = bundle
                if (CollectionUtils.isNotEmpty(entity.data.turnOnsList)) {
                    mTitles.add(mActivity.getString(R.string.turns_ons))
                    if (!mFragments.contains(turnOnsFragment)) {
                        mFragments.add(turnOnsFragment as TurnOnsFragment)
                        turnOnsFragment?.arguments = bundle
                    }
                }
                val profilePagerAdapter =
                    ProfilePagerAdapter(supportFragmentManager, mFragments, mTitles)
                profileViewPager.adapter = profilePagerAdapter
                profileTabLayout.setViewPager(profileViewPager)
                profileViewPager.offscreenPageLimit = mFragments.size - 1
            }

            override fun onFailure(code: Int, msg: String) {

            }
        })
        profileTabLayout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                profileViewPager.currentItem = position
            }

            override fun onTabReselect(position: Int) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (photoFragment != null) {
            photoFragment!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    override val profileTabLayout: SlidingTabLayout
        get() = findViewById(R.id.profile_tab_layout)
    override val profileViewPager: ViewPager
        get() = findViewById(R.id.profile_view_pager)

    override fun onResume() {
        super.onResume()
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .init()
    }
}