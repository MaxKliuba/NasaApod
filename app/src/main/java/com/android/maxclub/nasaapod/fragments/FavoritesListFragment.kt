package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.maxclub.nasaapod.R
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

    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesListBinding.inflate(inflater, container, false)

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
                    Toast.makeText(requireContext(), favoriteApod.title, Toast.LENGTH_SHORT).show()
                }.also { adapter ->
                    favoritesAdapter = adapter
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is FavoriteListUiState.Initializing -> viewModel.fetchFavoriteApods()
                    is FavoriteListUiState.Loading -> showLoading()
                    is FavoriteListUiState.Success -> showData(uiState.data)
                    is FavoriteListUiState.Error -> showErrorMessage()
                }
            }
        }
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
            }
            contentLayout.animate()
                .alpha(1.0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .duration = 300

            favoritesAdapter.submitList(favoriteApods)
        }
    }

    private fun showErrorMessage() {
        binding.apply {
            contentLayout.alpha = 0.0f
            progressIndicator.isVisible = false
            errorLayout.root.isVisible = false
        }
    }
}