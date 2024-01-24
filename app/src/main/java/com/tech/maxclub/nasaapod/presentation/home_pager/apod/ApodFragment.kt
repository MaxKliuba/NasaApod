package com.tech.maxclub.nasaapod.presentation.home_pager.apod

import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tech.maxclub.nasaapod.R
import com.tech.maxclub.nasaapod.databinding.FragmentApodBinding
import com.tech.maxclub.nasaapod.domain.model.Apod
import com.tech.maxclub.nasaapod.domain.model.ApodDate
import com.tech.maxclub.nasaapod.domain.model.MediaType
import com.tech.maxclub.nasaapod.presentation.home_pager.apod_pager.HomeEvent
import com.tech.maxclub.nasaapod.presentation.home_pager.apod_pager.HomeViewModel
import com.tech.maxclub.nasaapod.presentation.util.openImageMedia
import com.tech.maxclub.nasaapod.presentation.util.openVideoMedia
import com.tech.maxclub.nasaapod.presentation.util.shareApod
import com.tech.maxclub.nasaapod.util.formatDate
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class ApodFragment : Fragment() {
    private val viewModel: ApodViewModel by viewModels()
    private val parentViewModel: HomeViewModel by viewModels({ requireParentFragment() })

    private var _binding: FragmentApodBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApodBinding.inflate(inflater, container, false)

        binding.apply {
            swipeRefreshLayout.apply {
                setColorSchemeResources(R.color.color_primary)
                setProgressBackgroundColorSchemeResource(R.color.color_action_bar)
                setOnRefreshListener {
                    isRefreshing = false
                    viewModel.onEvent(ApodEvent.OnRefresh)
                }
            }
            errorLayout.retryButton.setOnClickListener {
                viewModel.onEvent(ApodEvent.OnRefresh)
            }

            imageView.setOnClickListener {
                viewModel.onEvent(ApodEvent.OnImageClick)
            }
            placeholderImageView.setOnClickListener {
                viewModel.onEvent(ApodEvent.OnVideoClick)
            }

            favoriteFab.setOnClickListener {
                viewModel.onEvent(ApodEvent.OnFavoritesButtonClick)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is ApodUiState.Loading -> {
                            uiState.cachedApod?.let { apod ->
                                showData(apod)
                            }
                            showLoading(uiState.cachedApod == null)
                        }

                        is ApodUiState.Success -> {
                            showData(uiState.apod)
                        }

                        is ApodUiState.Error -> {
                            uiState.cachedApod?.let { data ->
                                showData(data)
                            }
                            showErrorMessage(uiState.exception, uiState.cachedApod == null)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is ApodUiEvent.OnDateLoaded -> {
                            parentViewModel.onEvent(HomeEvent.OnItemLoaded(uiEvent.apodDate))
                        }

                        is ApodUiEvent.OnShowError -> {
                            showErrorSnackbar()
                        }

                        is ApodUiEvent.OnImageOpen -> {
                            openImageMedia(uiEvent.imageInfo, requireContext())
                        }

                        is ApodUiEvent.OnShowImageError -> {
                            showMediaErrorSnackbar()
                        }

                        is ApodUiEvent.OnVideoOpen -> {
                            openVideoMedia(uiEvent.videoUrl, requireContext())
                        }

                        is ApodUiEvent.OnShowVideoError -> {
                            showMediaErrorSnackbar()
                        }

                        is ApodUiEvent.OnShare -> {
                            shareApod(uiEvent.apod, requireContext())
                        }

                        is ApodUiEvent.OnShowShareError -> {
                            showShareErrorSnackbar()
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

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_apod, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.share -> {
                viewModel.onEvent(ApodEvent.OnShareButtonClick)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun showLoading(isContentEmpty: Boolean) {
        binding.apply {
            errorLayout.root.isVisible = false
            if (isContentEmpty) {
                contentLayout.alpha = 0.0f
                progressIndicator.isVisible = false
                progressIndicatorLayout.isVisible = true
            } else {
                progressIndicatorLayout.isVisible = false
                progressIndicator.isVisible = true
            }
        }
    }

    private fun showData(apod: Apod) {
        binding.apply {
            progressIndicatorLayout.isVisible = false
            progressIndicator.isVisible = false
            errorLayout.root.isVisible = false
            contentLayout.animate()
                .alpha(1.0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .duration = 300

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
            explanationTextView.text = apod.explanation
            copyrightTextView.text = apod.copyright?.let { copyright ->
                getString(R.string.copyright_placeholder, copyright)
            }
            favoriteFab.setImageResource(
                if (apod.isFavorite) R.drawable.ic_favorite_24 else R.drawable.ic_favorite_border_24
            )
        }
    }

    private fun showErrorMessage(exception: Throwable, isContentEmpty: Boolean) {
        binding.apply {
            progressIndicator.isVisible = false
            progressIndicatorLayout.isVisible = false
            if (isContentEmpty) {
                contentLayout.alpha = 0.0f
                errorLayout.root.isVisible = true
            } else {
                errorLayout.root.isVisible = false
                viewModel.onEvent(ApodEvent.OnError(exception))
            }
        }
    }

    private fun showErrorSnackbar() {
        Snackbar.make(
            binding.root,
            R.string.error_message,
            Snackbar.LENGTH_SHORT
        ).setAction(R.string.retry_button_title) {
            viewModel.onEvent(ApodEvent.OnRefresh)
        }.show()
    }

    private fun showMediaErrorSnackbar() {
        Snackbar.make(
            binding.root,
            R.string.show_error_message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showShareErrorSnackbar() {
        Snackbar.make(
            binding.root,
            R.string.share_error_message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    companion object {
        const val ARG_APOD_DATE = "apodDate"

        @JvmStatic
        fun newInstance(apodDate: ApodDate) =
            ApodFragment().apply {
                arguments = bundleOf(
                    ARG_APOD_DATE to apodDate,
                )
            }
    }
}