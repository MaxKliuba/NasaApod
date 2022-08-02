package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.databinding.FragmentApodBinding
import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.data.ApodDate
import com.android.maxclub.nasaapod.data.MediaType
import com.android.maxclub.nasaapod.uistates.ApodUiState
import com.android.maxclub.nasaapod.utils.formatDate
import com.android.maxclub.nasaapod.viewmodels.ApodViewModel
import com.android.maxclub.nasaapod.viewmodels.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

private const val LOG_TAG = "ApodFragment"
private const val ARG_DATE = "date"

@AndroidEntryPoint
class ApodFragment : BaseApodFragment() {
    private val viewModel: ApodViewModel by viewModels()
    private val parentViewModel: HomeViewModel by viewModels({ requireParentFragment() })

    private var _binding: FragmentApodBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.currentApodDate = arguments?.getSerializable(ARG_DATE) as ApodDate
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
                    viewModel.refreshCurrentApod()
                }
            }
            errorLayout.retryButton.setOnClickListener {
                viewModel.refreshCurrentApod()
            }

            imageView.setOnClickListener { view ->
                tryOpenApodMedia(viewModel.currentApod, view)
            }
            placeholderImageView.setOnClickListener { view ->
                tryOpenApodMedia(viewModel.currentApod, view)
            }

            favoriteFab.setOnClickListener {
                viewModel.currentApod?.let { apod ->
                    if (apod.isFavorite) {
                        viewModel.removeFromFavorites(apod)
                    } else {
                        viewModel.addToFavorites(apod)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is ApodUiState.Initializing -> viewModel.refreshCurrentApod()
                        is ApodUiState.Loading -> showLoading()
                        is ApodUiState.Success -> {
                            (viewModel.currentApodDate as? ApodDate.From)?.let { apodDate ->
                                parentViewModel.updateApodDate(apodDate)
                            }
                            showData(uiState.apod)
                        }
                        is ApodUiState.Error -> {
                            uiState.lastApod?.let { data ->
                                showData(data)
                            } ?: showErrorMessage(uiState.exception)
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
        inflater.inflate(R.menu.fragment_apod, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.share -> {
                if (viewModel.currentApod != null) {
                    shareApod(viewModel.currentApod!!)
                    true
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.share_error_message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    false
                }
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun showLoading() {
        binding.apply {
            contentLayout.alpha = 0.0f
            errorLayout.root.isVisible = false
            progressIndicator.isVisible = true
        }
    }

    private fun showData(apod: Apod) {
        binding.apply {
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

    private fun showErrorMessage(exception: Throwable) {
        Log.e(LOG_TAG, "showErrorMessage()", exception)
        binding.apply {
            if (viewModel.currentApod == null) {
                contentLayout.alpha = 0.0f
                progressIndicator.isVisible = false
                errorLayout.root.isVisible = true
            } else {
                Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(apodDate: ApodDate) =
            ApodFragment().apply {
                arguments = bundleOf(
                    ARG_DATE to apodDate,
                )
            }
    }
}