package com.android.maxclub.nasaapod.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.android.maxclub.nasaapod.adapters.HomeApodPagerAdapter
import com.android.maxclub.nasaapod.databinding.FragmentHomeViewPagerBinding

class HomeViewPagerFragment : Fragment() {
    private var _binding: FragmentHomeViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)

        binding.viewPager.adapter = HomeApodPagerAdapter(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}