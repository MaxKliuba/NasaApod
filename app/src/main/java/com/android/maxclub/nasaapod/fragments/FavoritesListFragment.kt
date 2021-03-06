package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import android.util.Log
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
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.adapters.FavoritesAdapter
import com.android.maxclub.nasaapod.adapters.FavoritesDiffCallback
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.databinding.FragmentFavoritesListBinding
import com.android.maxclub.nasaapod.uistates.FavoriteListUiState
import com.android.maxclub.nasaapod.viewmodels.FavoriteListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val LOG_TAG = "FavoritesListFragment"

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
            swipeRefreshLayout.apply {
                setColorSchemeResources(R.color.color_primary)
                setProgressBackgroundColorSchemeResource(R.color.color_action_bar)
                setOnRefreshListener {
                    isRefreshing = false
                    viewModel.fetchFavoriteApods()
                }
            }
            errorLayout.retryButton.setOnClickListener {
                viewModel.fetchFavoriteApods()
            }
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
                        is FavoriteListUiState.Success -> showData(uiState.data)
                        is FavoriteListUiState.Error -> showErrorMessage(uiState.exception)
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
            errorLayout.root.isVisible = false
            progressIndicator.isVisible = true
        }
    }

    private fun showData(favoriteApods: List<FavoriteApod>) {
        binding.apply {
            progressIndicator.isVisible = false
            errorLayout.root.isVisible = false
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

    private fun showErrorMessage(exception: Throwable) {
        Log.e(LOG_TAG, "showErrorMessage()", exception)
        binding.apply {
            contentLayout.alpha = 0.0f
            progressIndicator.isVisible = false
            errorLayout.root.isVisible = false
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
            val fromItem = favoritesAdapter.currentList[viewHolder.adapterPosition]
            val toItem = favoritesAdapter.currentList[target.adapterPosition]
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
                binding.swipeRefreshLayout.isEnabled = false
                (viewHolder as? FavoritesAdapter.FavoriteApodViewHolder)?.isDragging = true
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            (viewHolder as? FavoritesAdapter.FavoriteApodViewHolder)?.apply {
                if (isDragging) {
                    _binding?.swipeRefreshLayout?.isEnabled = true
                    isDragging = false
                    viewModel.updateFavoriteApods(*favoritesAdapter.currentList.toTypedArray())
                }
            }
        }
    }
}