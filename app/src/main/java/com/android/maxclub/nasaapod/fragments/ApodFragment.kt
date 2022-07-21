package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.databinding.FragmentApodBinding
import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.data.ApodDate
import com.android.maxclub.nasaapod.uistates.ApodUiState
import com.android.maxclub.nasaapod.utils.formatDate
import com.android.maxclub.nasaapod.viewmodels.ApodViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

private const val ARG_DATE = "date"

@AndroidEntryPoint
class ApodFragment : Fragment(), MenuProvider {
    private val viewModel: ApodViewModel by viewModels()

    private var _binding: FragmentApodBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.currentApodDate = arguments?.getSerializable(ARG_DATE) as ApodDate
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
            imageView.setOnClickListener {
                if (viewModel.isImageLoaded) {
                    // TODO
                    Toast.makeText(context, "Open picture", Toast.LENGTH_SHORT).show()
                }
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

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is ApodUiState.Initializing -> viewModel.fetchInitApod()
                        is ApodUiState.Loading -> showLoading()
                        is ApodUiState.Success -> showData(uiState.data)
                        is ApodUiState.Error -> showErrorMessage()
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_apod, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        when (menuItem.itemId) {
            R.id.share -> {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show()
                // TODO
                true
            }
            else -> false
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

            Picasso.get()
                .load(apod.url)
                .error(R.drawable.ic_image_error_placeholder_24)
                .apply {
                    fetch(object : Callback {
                        override fun onSuccess() {
                            viewModel.isImageLoaded = true
                        }

                        override fun onError(e: Exception?) {
                            viewModel.isImageLoaded = false
                        }
                    })
                }
                .into(imageView)
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

    private fun showErrorMessage() {
        binding.apply {
            contentLayout.alpha = 0.0f
            progressIndicator.isVisible = false
            errorLayout.root.isVisible = true
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