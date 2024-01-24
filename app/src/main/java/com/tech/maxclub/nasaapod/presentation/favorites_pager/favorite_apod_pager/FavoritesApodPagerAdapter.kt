package com.tech.maxclub.nasaapod.presentation.favorites_pager.favorite_apod_pager

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tech.maxclub.nasaapod.domain.model.FavoriteApod
import com.tech.maxclub.nasaapod.presentation.favorites_pager.favorite_apod.FavoriteApodFragment

class FavoritesApodPagerAdapter(
    fragment: Fragment,
    initialList: List<FavoriteApod> = emptyList(),
    val onItemInserted: (List<FavoriteApod>) -> Unit,
) : FragmentStateAdapter(fragment) {
    private val _currentList: MutableList<FavoriteApod> = initialList.toMutableList()

    override fun getItemCount(): Int = _currentList.size

    override fun createFragment(position: Int): Fragment =
        FavoriteApodFragment.newInstance(_currentList[position])

    override fun getItemId(position: Int): Long =
        _currentList[position].date.time

    override fun containsItem(itemId: Long): Boolean =
        _currentList.any { it.date.time == itemId }

    fun submitList(newList: List<FavoriteApod>) {
        val callback = FavoritesApodPagerDiffUtil(_currentList, newList)
        val diff = DiffUtil.calculateDiff(callback)
        _currentList.clear()
        _currentList.addAll(newList)
        diff.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
                onItemInserted(_currentList)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
            }
        })
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