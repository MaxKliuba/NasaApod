package com.android.maxclub.nasaapod.adapters

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.maxclub.nasaapod.data.ApodDate
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.fragments.ApodFragment

class FavoritesApodPagerAdapter(
    fragment: Fragment,
    initialList: List<FavoriteApod> = emptyList(),
) : FragmentStateAdapter(fragment) {
    private val _currentList: MutableList<FavoriteApod> = initialList.toMutableList()
    val currentList: List<FavoriteApod> = _currentList

    override fun getItemCount(): Int = _currentList.size

    override fun createFragment(position: Int): Fragment =
        ApodFragment.newInstance(ApodDate.From(_currentList[position].date))

    override fun getItemId(position: Int): Long =
        _currentList[position].date.time

    override fun containsItem(itemId: Long): Boolean =
        _currentList.any { it.date.time == itemId }

    fun submitList(newList: List<FavoriteApod>) {
        val sortedList = newList.sortedByDescending { it.position }
        val callback = FavoritesApodPagerDiffUtil(_currentList, sortedList)
        val diff = DiffUtil.calculateDiff(callback)
        _currentList.clear()
        _currentList.addAll(sortedList)
        diff.dispatchUpdatesTo(this)
    }
}

class FavoritesApodPagerDiffUtil(
    private val oldList: List<FavoriteApod>,
    private val newList: List<FavoriteApod>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].date == newList[newItemPosition].date

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}