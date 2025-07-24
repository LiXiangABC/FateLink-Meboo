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
        // 仅当没有保存状态时才初始化Fragment
        if (savedInstanceState == null) {
            adFragment = ADFragment()
            photoFragment = PhotoFragment()
            turnOnsFragment = TurnOnsFragment()
        } else {
            // 从FragmentManager恢复已存在的Fragment
            adFragment = supportFragmentManager.findFragmentByTag(getFragmentTag(0)) as? ADFragment
            photoFragment = supportFragmentManager.findFragmentByTag(getFragmentTag(1)) as? PhotoFragment
            turnOnsFragment = supportFragmentManager.findFragmentByTag(getFragmentTag(2)) as? TurnOnsFragment
        }

        // 确保Fragment非空
        adFragment = adFragment ?: ADFragment()
        photoFragment = photoFragment ?: PhotoFragment()
        turnOnsFragment = turnOnsFragment ?: TurnOnsFragment()

        mFragments.clear()
        mFragments.add(adFragment!!)
        mFragments.add(photoFragment!!)

        // 立即初始化Adapter
        val adapter = ProfilePagerAdapter(supportFragmentManager, mFragments, mTitles)
        profileViewPager.adapter = adapter
        profileTabLayout.setViewPager(profileViewPager)
        profileViewPager.offscreenPageLimit = 2 // 设置为固定值


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

                val bundle = Bundle().apply {
                    putSerializable("entity", entity.data)
                }

                // 安全设置参数
                adFragment?.arguments = adFragment?.arguments ?: bundle
                photoFragment?.arguments = photoFragment?.arguments ?: bundle

                // 通知Fragment数据已更新
                (adFragment as? ADFragment)?.onDataUpdated(entity.data)
                (photoFragment as? PhotoFragment)?.onDataUpdated(entity.data)

                if (CollectionUtils.isNotEmpty(entity.data.turnOnsList)) {
//                    mFragments.add(turnOnsFragment!!)
//                    turnOnsFragment?.arguments = bundle

                    // 确保只添加一次
                    if (!mFragments.contains(turnOnsFragment)) {
                        mTitles.add(mActivity.getString(R.string.turns_ons))
                        turnOnsFragment?.arguments = turnOnsFragment?.arguments ?: bundle
                        mFragments.add(turnOnsFragment!!)

                        // 更新Adapter
                        (profileViewPager.adapter as? ProfilePagerAdapter)?.updateFragments(mFragments, mTitles)
                        profileViewPager.offscreenPageLimit = mFragments.size - 1
                    }
                }
//                val profilePagerAdapter = ProfilePagerAdapter(supportFragmentManager, mFragments, mTitles)
//                profileViewPager.adapter = profilePagerAdapter
//                profileTabLayout.setViewPager(profileViewPager)
                // 创建或更新Adapter
//                if (profileViewPager.adapter == null) {
//                    val adapter = ProfilePagerAdapter(supportFragmentManager, mFragments, mTitles)
//                    profileViewPager.adapter = adapter
//                    profileTabLayout.setViewPager(profileViewPager)
//                    profileViewPager.offscreenPageLimit = mFragments.size - 1
//
//                } else {
//                    (profileViewPager.adapter as? ProfilePagerAdapter)?.apply {
//                        updateFragments(mFragments, mTitles)
//                    }
//                }
                // 刷新TabLayout
                profileTabLayout.setViewPager(profileViewPager)


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


    private fun getFragmentTag(position: Int): String {
        // 使用ViewPager的标准标签格式
        return "android:switcher:${R.id.profile_view_pager}:$position"
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
            .statusBarDarkFont(false)
            .init()
    }
}