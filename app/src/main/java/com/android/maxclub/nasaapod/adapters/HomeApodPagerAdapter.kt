package com.android.maxclub.nasaapod.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.maxclub.nasaapod.fragments.ApodFragment

class HomeApodPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int): Fragment = ApodFragment()
}