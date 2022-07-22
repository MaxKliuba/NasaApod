package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.adapters.HomeApodPagerAdapter
import com.android.maxclub.nasaapod.data.ApodDate
import com.android.maxclub.nasaapod.databinding.FragmentHomeViewPagerBinding
import com.android.maxclub.nasaapod.utils.getNextDate
import com.android.maxclub.nasaapod.utils.getPrevDate
import com.android.maxclub.nasaapod.viewmodels.HomeViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class HomeViewPagerFragment : Fragment(), MenuProvider {
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeViewPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagerAdapter: HomeApodPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)

        pagerAdapter = HomeApodPagerAdapter(this, viewModel.apodDates.value)
        binding.viewPager.apply {
            adapter = pagerAdapter
            currentItem = viewModel.currentPosition
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.currentPosition = position
                }
            })
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)

        lifecycleScope.launch {
            launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.apodDates.collect { apodDates ->
                        pagerAdapter.submitList(apodDates)
                    }
                }
            }

            launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.lastLoadedDate.collect { currentDate ->
                        currentDate?.let { date ->
                            val position = viewModel.currentPosition
                            if (position == 0) {
                                val prevDate = getPrevDate(date)
                                viewModel.addNewApodDate(ApodDate.From(prevDate))
                            }
                            if (position == pagerAdapter.itemCount - 1) {
                                val nextDate = getNextDate(date)
                                viewModel.addNewApodDate(ApodDate.From(nextDate))
                            }
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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_home_view_pager, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        when (menuItem.itemId) {
            R.id.random_date -> {
                viewModel.replaceAllWithNewApodDate(ApodDate.Random())
                true
            }
            R.id.select_date -> {
                showDatePickerDialog()
                true
            }
            else -> false
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
                    viewModel.replaceAllWithNewApodDate(ApodDate.From(Date(date)))
                }
            }
            .show(childFragmentManager, "DATE_PICKER")
    }
}