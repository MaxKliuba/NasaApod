package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.databinding.FragmentApodBinding
import com.android.maxclub.nasaapod.data.Apod
import com.android.maxclub.nasaapod.utils.formatDate
import com.android.maxclub.nasaapod.viewmodels.ApodViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
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

        binding.swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.color_primary)
            setProgressBackgroundColorSchemeResource(R.color.color_action_bar)
            setOnRefreshListener {
                isRefreshing = false
                fetchApodByDate(Date())
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner)

        if (viewModel.apod.value == null) {
            fetchApod()
        }
        viewModel.apod.observe(viewLifecycleOwner) { apod ->
            updateIU(apod)
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
                fetchRandomApod()
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

    private fun fetchApod() {
        binding.contentLayout.alpha = 0.0f
        viewModel.fetchApod()
    }

    private fun fetchApodByDate(date: Date) {
        binding.contentLayout.alpha = 0.0f
        viewModel.fetchApodByDate(date)
    }

    private fun fetchRandomApod() {
        binding.contentLayout.alpha = 0.0f
        viewModel.fetchRandomApod()
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
                    fetchApodByDate(Date(date))
                }
            }
            .show(childFragmentManager, "DATE_PICKER")
    }

    private fun updateIU(apod: Apod) {
        binding.apply {
            contentLayout.animate()
                .alpha(1.0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .duration = 300

            Picasso.get()
                .load(apod.url)
//                .placeholder(R.drawable.image_placeholder)
//                .fit()
//                .centerCrop()
                .into(apodImageView)

            apod.date?.let { date ->
                dateTextView.text = formatDate(
                    date,
                    getString(R.string.date_format_pattern),
                    Locale(getString(R.string.language), getString(R.string.country))
                )
            }

            titleTextView.text = apod.title
            explanationTextView.text = apod.explanation
            apod.copyright?.let { copyright ->
                copyrightTextView.text = getString(R.string.copyright_placeholder, copyright)
            }

            favoriteFab.setImageResource(
                if (true) {
                    R.drawable.ic_favorite_24
                } else {
                    R.drawable.ic_favorite_border_24
                }
            )
        }
    }
}