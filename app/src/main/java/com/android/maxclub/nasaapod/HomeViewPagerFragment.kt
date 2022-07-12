package com.android.maxclub.nasaapod

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.maxclub.nasaapod.databinding.FragmentHomeViewPagerBinding

class HomeViewPagerFragment : Fragment(), MenuProvider {
    private var _binding: FragmentHomeViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)

        binding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@HomeViewPagerFragment) {
                override fun getItemCount(): Int = 2

                override fun createFragment(position: Int): Fragment = ApodFragment()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner)
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
}