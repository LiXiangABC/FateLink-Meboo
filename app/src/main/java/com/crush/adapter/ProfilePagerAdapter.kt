package com.crush.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class ProfilePagerAdapter(
    fm: FragmentManager,
    var mFragments: List<Fragment>,
    var mTitles: List<String>
) : FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // 添加更新方法
    fun updateFragments(newFragments: List<Fragment>, newTitles: List<String>) {
        mFragments = newFragments
        mTitles = newTitles
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles[position]
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }
    // 关键：防止重复添加Fragment
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        if (fragment != mFragments[position]) {
            // 如果系统已恢复Fragment，使用恢复的实例
            mFragments = mFragments.toMutableList().apply { set(position, fragment) }
        }
        return fragment
    }

}