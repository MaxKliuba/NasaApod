package com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod_pager

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.databinding.FragmentFavoritesViewPagerBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesViewPagerFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModels()

    private var _binding: FragmentFavoritesViewPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var pagerAdapter: FavoritesApodPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesViewPagerBinding.inflate(inflater, container, false)

        navController = findNavController()

        pagerAdapter = FavoritesApodPagerAdapter(
            this,
            viewModel.uiState.value.favoriteApods
        ) { favoriteApods ->
            viewModel.onEvent(FavoritesEvent.OnItemInserted(favoriteApods))
        }
        binding.viewPager.apply {
            adapter = pagerAdapter
            currentItem = viewModel.position
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.onEvent(FavoritesEvent.OnPositionChanged(position))
                }
            })
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.favoriteApods.isEmpty()) {
                        navController.popBackStack()
                    } else {
                        pagerAdapter.submitList(uiState.favoriteApods)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is FavoritesUiEvent.OnItemDeleted -> {
                            showDeleteSnackbar(uiEvent.favoriteApod)
                        }
                        is FavoritesUiEvent.OnItemRestored -> {
                            binding.viewPager.setCurrentItem(uiEvent.position, true)
                        }
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

    private fun showDeleteSnackbar(deletedFavoriteApod: FavoriteApod) {
        Snackbar.make(binding.root, R.string.delete_item_message, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo_button_title) {
                viewModel.onEvent(FavoritesEvent.OnItemRestore(deletedFavoriteApod))
            }.show()
    }

    companion object {
        const val ARG_FAVORITE_APOD = "favoriteApod"
    }
}