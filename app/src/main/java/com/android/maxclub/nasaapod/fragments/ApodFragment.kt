package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.api.ApodService
import com.android.maxclub.nasaapod.databinding.FragmentApodBinding
import com.android.maxclub.nasaapod.model.Apod
import com.android.maxclub.nasaapod.repository.ApodRepository
import com.android.maxclub.nasaapod.utils.DateHelper
import com.android.maxclub.nasaapod.viewmodel.ApodViewModel
import com.android.maxclub.nasaapod.viewmodel.ApodViewModelFactory
import com.squareup.picasso.Picasso
import java.util.*

class ApodFragment : Fragment(), MenuProvider {
    private val apodViewModel by lazy {
        val apodService = ApodService.getInstance()
        val apodRepository = ApodRepository(apodService)
        ViewModelProvider(this, ApodViewModelFactory(apodRepository))[ApodViewModel::class.java]
    }

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

        if (apodViewModel.apod.value == null) {
            fetchApod()
        }
        apodViewModel.apod.observe(viewLifecycleOwner) { apod ->
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
                // TODO
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
        apodViewModel.fetchApod()
    }

    private fun fetchApodByDate(date: Date) {
        binding.contentLayout.alpha = 0.0f
        apodViewModel.fetchApodByDate(date)
    }

    private fun fetchRandomApod() {
        binding.contentLayout.alpha = 0.0f
        apodViewModel.fetchRandomApod()
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
                dateTextView.text = DateHelper.format(getString(R.string.date_format_pattern), date)
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