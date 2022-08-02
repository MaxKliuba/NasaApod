package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.maxclub.nasaapod.adapters.FavoritesAdapter
import com.android.maxclub.nasaapod.adapters.FavoritesDiffCallback
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.databinding.FragmentFavoritesListBinding
import com.android.maxclub.nasaapod.uistates.FavoriteListUiState
import com.android.maxclub.nasaapod.viewmodels.FavoriteListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesListFragment : Fragment() {
    private val viewModel: FavoriteListViewModel by viewModels()

    private var _binding: FragmentFavoritesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesListBinding.inflate(inflater, container, false)

        navController = findNavController()

        binding.apply {
            favoritesRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = FavoritesAdapter(FavoritesDiffCallback()) { favoriteApod ->
                    val directions =
                        FavoritesListFragmentDirections.actionFavoritesListFragmentToFavoritesViewPagerFragment(
                            favoriteApod
                        )
                    navController.navigate(directions)
                }.also { adapter ->
                    favoritesAdapter = adapter
                }
            }.also { recyclerView ->
                ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is FavoriteListUiState.Initializing -> viewModel.fetchFavoriteApods()
                        is FavoriteListUiState.Loading -> showLoading()
                        is FavoriteListUiState.DataChanged -> showData(uiState.favoriteApods)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading() {
        binding.apply {
            contentLayout.alpha = 0.0f
            progressIndicator.isVisible = true
        }
    }

    private fun showData(favoriteApods: List<FavoriteApod>) {
        binding.apply {
            progressIndicator.isVisible = false
            if (favoriteApods.isEmpty()) {
                favoritesRecyclerView.isVisible = false
                emptyListLayout.isVisible = true
            } else {
                emptyListLayout.isVisible = false
                favoritesRecyclerView.isVisible = true
                favoritesAdapter.submitList(favoriteApods)
            }
            contentLayout.animate()
                .alpha(1.0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .duration = 300
        }
    }

    private val itemTouchHelperCallback = object :
        ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or
                    ItemTouchHelper.DOWN,
            0
        ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromItem = favoritesAdapter.currentList[viewHolder.bindingAdapterPosition]
            val toItem = favoritesAdapter.currentList[target.bindingAdapterPosition]
            val fromPosition = fromItem.position
            val toPosition = toItem.position
            val newList = favoritesAdapter.currentList.map { favoriteApod ->
                when (favoriteApod) {
                    fromItem -> fromItem.copy(position = toPosition)
                    toItem -> toItem.copy(position = fromPosition)
                    else -> favoriteApod
                }
            }
            viewModel.updateUiStateFavoriteApods(newList)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                (viewHolder as? FavoritesAdapter.FavoriteApodViewHolder)?.isDragging = true
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            (viewHolder as? FavoritesAdapter.FavoriteApodViewHolder)?.apply {
                if (isDragging) {
                    isDragging = false
                    viewModel.updateFavoriteApods(*favoritesAdapter.currentList.toTypedArray())
                }
            }
        }
    }
}