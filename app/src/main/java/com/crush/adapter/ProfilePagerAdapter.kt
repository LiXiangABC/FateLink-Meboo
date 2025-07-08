package com.crush.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ProfilePagerAdapter(
    fm: FragmentManager,
    var mFragments: List<Fragment>,
    var mTitles: List<String>
) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles[position]
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }
}