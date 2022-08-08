package com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.domain.model.MediaType
import com.android.maxclub.nasaapod.databinding.FragmentFavoriteApodBinding
import com.android.maxclub.nasaapod.domain.model.Apod
import com.android.maxclub.nasaapod.domain.model.FavoriteApod
import com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod_pager.FavoritesEvent
import com.android.maxclub.nasaapod.presentation.favorites_pager.favorite_apod_pager.FavoritesViewModel
import com.android.maxclub.nasaapod.presentation.util.openImageMedia
import com.android.maxclub.nasaapod.presentation.util.openVideoMedia
import com.android.maxclub.nasaapod.presentation.util.shareApod
import com.android.maxclub.nasaapod.util.formatDate
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class FavoriteApodFragment : Fragment() {
    private val viewModel: FavoriteApodViewModel by viewModels()
    private val parentViewModel: FavoritesViewModel by viewModels({ requireParentFragment() })

    private var _binding: FragmentFavoriteApodBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteApodBinding.inflate(inflater, container, false)

        binding.apply {
            swipeRefreshLayout.apply {
                setColorSchemeResources(R.color.color_primary)
                setProgressBackgroundColorSchemeResource(R.color.color_action_bar)
                setOnRefreshListener {
                    isRefreshing = false
                    viewModel.onEvent(FavoriteApodEvent.OnRefresh)
                }
            }

            imageView.setOnClickListener {
                viewModel.onEvent(FavoriteApodEvent.OnImageClick)
            }
            placeholderImageView.setOnClickListener {
                viewModel.onEvent(FavoriteApodEvent.OnVideoClick)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is FavoriteApodUiState.Loading -> {
                            showData(viewModel.currentApod)
                            showLoading()
                        }
                        is FavoriteApodUiState.Success -> {
                            showData(uiState.apod)
                        }
                        is FavoriteApodUiState.Error -> {
                            showData(viewModel.currentApod)
                            viewModel.onEvent(FavoriteApodEvent.OnError(uiState.exception))
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is FavoriteApodUiEvent.OnShowError -> {
                            showErrorSnackbar()
                        }
                        is FavoriteApodUiEvent.OnImageOpen -> {
                            openImageMedia(uiEvent.imageInfo, requireContext())
                        }
                        is FavoriteApodUiEvent.OnShowImageError -> {
                            showMediaErrorSnackbar()
                        }
                        is FavoriteApodUiEvent.OnVideoOpen -> {
                            openVideoMedia(uiEvent.videoUrl, requireContext())
                        }
                        is FavoriteApodUiEvent.OnShowVideoError -> {
                            showMediaErrorSnackbar()
                        }
                        is FavoriteApodUiEvent.OnShare -> {
                            shareApod(uiEvent.apod, requireContext())
                        }
                        is FavoriteApodUiEvent.OnDelete -> {
                            parentViewModel.onEvent(FavoritesEvent.OnItemDeleted(uiEvent.favoriteApod))
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

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_favorite_apod, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.favorite -> {
                viewModel.onEvent(FavoriteApodEvent.OnDelete)
                true
            }
            R.id.share -> {
                viewModel.onEvent(FavoriteApodEvent.OnShareButtonClick)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun showLoading() {
        binding.progressIndicator.isVisible = true
    }

    private fun showData(apod: Apod) {
        viewModel.onEvent(FavoriteApodEvent.OnShowData(apod))
        binding.apply {
            progressIndicator.isVisible = false
            when (apod.mediaType) {
                MediaType.IMAGE -> {
                    placeholderImageView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    Picasso.get()
                        .load(apod.url)
                        .apply {
                            fetch(object : Callback {
                                override fun onSuccess() {
                                }

                                override fun onError(e: Exception?) {
                                    imageView.visibility = View.GONE
                                    placeholderImageView.apply {
                                        visibility = View.VISIBLE
                                        setImageResource(R.drawable.ic_error_image_placeholder_24)
                                    }
                                }
                            })
                        }
                        .into(imageView)
                }
                MediaType.VIDEO -> {
                    imageView.visibility = View.GONE
                    placeholderImageView.apply {
                        visibility = View.VISIBLE
                        setImageResource(R.drawable.ic_video_placeholder_24)
                    }
                }
            }
            dateTextView.text = formatDate(
                apod.date,
                getString(R.string.date_format_pattern),
                Locale(getString(R.string.language), getString(R.string.country))
            )
            titleTextView.text = apod.title
            explanationTextView.isVisible = apod.explanation.isNotBlank()
            explanationTextView.text = apod.explanation
            copyrightTextView.text = apod.copyright?.let { copyright ->
                getString(R.string.copyright_placeholder, copyright)
            }
        }
    }

    private fun showErrorSnackbar() {
        Snackbar.make(
            binding.root,
            R.string.error_message,
            Snackbar.LENGTH_SHORT
        ).setAction(R.string.retry_button_title) {
            viewModel.onEvent(FavoriteApodEvent.OnRefresh)
        }.show()
    }

    private fun showMediaErrorSnackbar() {
        Snackbar.make(
            binding.root,
            R.string.show_error_message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    companion object {
        const val ARG_FAVORITE_APOD = "favoriteApod"

        @JvmStatic
        fun newInstance(favoriteApod: FavoriteApod): FavoriteApodFragment =
            FavoriteApodFragment().apply {
                arguments = bundleOf(
                    ARG_FAVORITE_APOD to favoriteApod,
                )
            }
    }
}