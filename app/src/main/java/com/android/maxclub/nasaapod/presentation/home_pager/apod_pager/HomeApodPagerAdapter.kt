package com.android.maxclub.nasaapod.presentation.home_pager.apod_pager

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.maxclub.nasaapod.domain.model.ApodDate
import com.android.maxclub.nasaapod.presentation.home_pager.apod.ApodFragment

class HomeApodPagerAdapter(
    fragment: Fragment,
    initialList: List<ApodDate> = emptyList(),
) : FragmentStateAdapter(fragment) {
    private val _currentList: MutableList<ApodDate> = initialList.toMutableList()
    val currentList: List<ApodDate> = _currentList

    override fun getItemCount(): Int = _currentList.size

    override fun createFragment(position: Int): Fragment =
        ApodFragment.newInstance(_currentList[position])

    override fun getItemId(position: Int): Long =
        _currentList[position].id.leastSignificantBits

    override fun containsItem(itemId: Long): Boolean =
        _currentList.any { it.id.leastSignificantBits == itemId }

    fun submitList(newList: List<ApodDate>) {
        val callback = HomeApodPagerDiffUtil(_currentList, newList)
        val diff = DiffUtil.calculateDiff(callback)
        _currentList.clear()
        _currentList.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }
}

class HomeApodPagerDiffUtil(
    private val oldList: List<ApodDate>,
    private val newList: List<ApodDate>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem is ApodDate.From && newItem is ApodDate.From && oldItem.date == newItem.date
    }
}