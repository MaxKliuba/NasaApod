package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.data.*
import com.android.maxclub.nasaapod.databinding.FragmentFavoriteApodBinding
import com.android.maxclub.nasaapod.uistates.ApodUiState
import com.android.maxclub.nasaapod.utils.formatDate
import com.android.maxclub.nasaapod.viewmodels.FavoriteApodViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

private const val LOG_TAG = "FavoriteApodFragment"
private const val ARG_FAVORITE_APOD = "favoriteApod"

@AndroidEntryPoint
class FavoriteApodFragment : BaseApodFragment() {
    private val viewModel: FavoriteApodViewModel by viewModels()

    private var _binding: FragmentFavoriteApodBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.currentFavoriteApod =
            arguments?.getSerializable(ARG_FAVORITE_APOD) as FavoriteApod
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
                    viewModel.fetchCurrentApod()
                }
            }

            imageView.setOnClickListener { view ->
                tryOpenApodMedia(viewModel.currentApod, view)
            }
            placeholderImageView.setOnClickListener { view ->
                tryOpenApodMedia(viewModel.currentApod, view)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is ApodUiState.Initializing -> {
                            viewModel.fetchCurrentApod()
                            showData(viewModel.currentApod)
                        }
                        is ApodUiState.Loading -> {
                            showData(viewModel.currentApod)
                            showLoading()
                        }
                        is ApodUiState.Success -> {
                            showData(uiState.apod)
                        }
                        is ApodUiState.Error -> {
                            showData(viewModel.currentApod)
                            showErrorMessage(uiState.exception)
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                viewModel.removeFavoriteApod(viewModel.currentFavoriteApod)
                true
            }
            R.id.share -> {
                shareApod(viewModel.currentApod)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun showLoading() {
        binding.progressIndicator.isVisible = true
    }

    private fun showData(apod: Apod) {
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
            explanationTextView.isVisible = apod.explanation.isNotEmpty()
            explanationTextView.text = apod.explanation
            copyrightTextView.text = apod.copyright?.let { copyright ->
                getString(R.string.copyright_placeholder, copyright)
            }
        }
    }

    private fun showErrorMessage(exception: Throwable) {
        Log.e(LOG_TAG, "showErrorMessage()", exception)
        binding.apply {
            progressIndicator.isVisible = false
            Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(favoriteApod: FavoriteApod): FavoriteApodFragment =
            FavoriteApodFragment().apply {
                arguments = bundleOf(
                    ARG_FAVORITE_APOD to favoriteApod,
                )
            }
    }
}