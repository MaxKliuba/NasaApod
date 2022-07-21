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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)

        val pagerAdapter = HomeApodPagerAdapter(this)
        binding.viewPager.apply {
            adapter = pagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                }
            })
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apodDates.collect { apodDates ->
                    pagerAdapter.submitList(apodDates)
//                    binding.viewPager.setCurrentItem(pagerAdapter.itemCount, true)
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
                viewModel.replaceAllWithNewDate(ApodDate.Random())
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
            .setSelection(Date().time)
            .build()
            .apply {
                addOnPositiveButtonClickListener { date ->
                    viewModel.addNewDate(ApodDate.From(Date(date)))
                }
            }
            .show(childFragmentManager, "DATE_PICKER")
    }
}