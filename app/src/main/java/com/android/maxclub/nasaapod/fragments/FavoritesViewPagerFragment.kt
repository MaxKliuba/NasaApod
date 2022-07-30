package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.android.maxclub.nasaapod.adapters.FavoritesApodPagerAdapter
import com.android.maxclub.nasaapod.databinding.FragmentFavoritesViewPagerBinding
import com.android.maxclub.nasaapod.uistates.FavoritesViewPagerUiState
import com.android.maxclub.nasaapod.viewmodels.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesViewPagerFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModels()

    private var _binding: FragmentFavoritesViewPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val args: FavoritesViewPagerFragmentArgs by navArgs()
    private lateinit var pagerAdapter: FavoritesApodPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesViewPagerBinding.inflate(inflater, container, false)

        navController = findNavController()

        val initialList = viewModel.uiState.value.let { uiState ->
            when (uiState) {
                is FavoritesViewPagerUiState.Initializing -> listOf(args.favoriteApod)
                is FavoritesViewPagerUiState.DataChanged -> uiState.data
            }
        }
        pagerAdapter =
            FavoritesApodPagerAdapter(this, initialList)
        binding.viewPager.apply {
            adapter = pagerAdapter
            currentItem = viewModel.currentPosition
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.currentPosition = position
                    pagerAdapter.currentList[position].let { favoriteApod ->
                        if (favoriteApod.isNew) {
                            viewModel.unmarkAsNewFavoriteApod(favoriteApod)
                        }
                    }
                }
            })
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState is FavoritesViewPagerUiState.DataChanged) {
                        if (uiState.data.isEmpty()) {
                            navController.popBackStack()
                        } else {
                            pagerAdapter.submitList(uiState.data)
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
}