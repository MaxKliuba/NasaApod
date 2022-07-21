package com.android.maxclub.nasaapod.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.maxclub.nasaapod.data.ApodDate
import com.android.maxclub.nasaapod.fragments.ApodFragment

class HomeApodPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    var currentList: List<ApodDate> = emptyList()
        private set

    override fun getItemCount(): Int = currentList.size

    override fun createFragment(position: Int): Fragment =
        ApodFragment.newInstance(currentList[position])

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<ApodDate>) {
        currentList = list
        notifyDataSetChanged()
    }
}