package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.databinding.FragmentApodBinding
import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.uistates.ApodUiState
import com.android.maxclub.nasaapod.utils.formatDate
import com.android.maxclub.nasaapod.viewmodels.ApodViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class ApodFragment : Fragment(), MenuProvider {
    private val viewModel: ApodViewModel by viewModels()

    private var _binding: FragmentApodBinding? = null
    private val binding get() = _binding!!

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
            apodImageView.setOnClickListener {
                if (viewModel.isImageLoaded) {
                    // TODO
                    Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show()
                }
            }
            favoriteFab.setOnClickListener {
                viewModel.currentApod?.let { apod ->
                    if (apod.isFavorite) {
                        viewModel.removeFromFavorite(apod)
                    } else {
                        viewModel.addToFavorite(apod)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner)

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is ApodUiState.Initializing -> viewModel.fetchApodOfToday()
                    is ApodUiState.Loading -> showLoading()
                    is ApodUiState.Success -> showData(uiState.data)
                    is ApodUiState.Error -> showErrorMessage(uiState.exception)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_home_view_pager, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        when (menuItem.itemId) {
            R.id.random_date -> {
                viewModel.fetchRandomApod()
                true
            }
            R.id.select_date -> {
                showDatePickerDialog()
                true
            }
            R.id.share -> {
                // TODO
                true
            }
            else -> false
        }

    private fun showLoading() {
        binding.apply {
            contentLayout.alpha = 0.0f
            errorTextView.isVisible = false
            progressIndicator.isVisible = true
        }
    }

    private fun showData(apod: Apod) {
        binding.apply {
            progressIndicator.isVisible = false
            errorTextView.isVisible = false
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
                .into(apodImageView)
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
        binding.apply {
            contentLayout.alpha = 0.0f
            progressIndicator.isVisible = false
            errorTextView.apply {
                text = exception.localizedMessage
                isVisible = true
            }
        }
    }

    private fun showDatePickerDialog() {
        val constraint = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()

        MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraint)
            .build()
            .apply {
                addOnPositiveButtonClickListener { date ->
                    viewModel.fetchApodByDate(Date(date))
                }
            }
            .show(childFragmentManager, "DATE_PICKER")
    }
}