package com.android.maxclub.nasaapod

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.android.maxclub.nasaapod.databinding.ActivityMainBinding
import com.android.maxclub.nasaapod.viewmodels.MainViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var destinationChangedListener: NavController.OnDestinationChangedListener

    private val tabIndicesToFragmentIds = mapOf(
        HOME_TAB_INDEX to setOf(R.id.home_view_pager_fragment),
        FAVORITES_TAB_INDEX to setOf(
            R.id.favorites_list_fragment,
            R.id.favorites_view_pager_fragment
        ),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            setSupportActionBar(this)
            setNavigationOnClickListener {
                navigationIconOnClick()
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        destinationChangedListener =
            NavController.OnDestinationChangedListener { _, destination, _ ->
                tabIndicesToFragmentIds.entries
                    .firstOrNull {
                        it.value.contains(destination.id)
                    }?.key?.let { tabIndex ->
                        binding.tabLayout.apply {
                            tag = false
                            getTabAt(tabIndex)?.select()
                            tag = true
                        }
                    }
            }
        navController.addOnDestinationChangedListener(destinationChangedListener)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (binding.tabLayout.tag != false) {
                    when (tab.position) {
                        HOME_TAB_INDEX -> navController.popBackStack(
                            R.id.home_view_pager_fragment,
                            false
                        )
                        FAVORITES_TAB_INDEX -> navController.navigate(R.id.favorites_list_fragment)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                if (binding.tabLayout.tag != false) {
                    when (tab.position) {
                        HOME_TAB_INDEX -> navController.popBackStack(
                            R.id.home_view_pager_fragment,
                            false
                        )
                        FAVORITES_TAB_INDEX -> navController.popBackStack(
                            R.id.favorites_list_fragment,
                            false
                        )
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newFavoritesCount.collect { count ->
                    binding.tabLayout.getTabAt(FAVORITES_TAB_INDEX)?.let { tab ->
                        if (count > 0) {
                            tab.orCreateBadge.number = count
                        } else {
                            tab.removeBadge()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(destinationChangedListener)
    }

    private fun navigationIconOnClick() {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
            .launchUrl(this, Uri.parse(NASA_URL))
    }

    companion object {
        private const val NASA_URL = "https://www.nasa.gov/"

        private const val HOME_TAB_INDEX = 0
        private const val FAVORITES_TAB_INDEX = 1
    }
}