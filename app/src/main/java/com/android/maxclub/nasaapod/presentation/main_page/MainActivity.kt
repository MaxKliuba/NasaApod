package com.android.maxclub.nasaapod.presentation.main_page

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.databinding.ActivityMainBinding
import com.android.maxclub.nasaapod.presentation.home_pager.apod_pager.HomeViewPagerFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    private val tabIndicesToFragmentIds = mapOf(
        HOME_TAB_INDEX to setOf(R.id.home_view_pager_fragment),
        FAVORITES_TAB_INDEX to setOf(
            R.id.favorites_list_fragment,
            R.id.favorites_view_pager_fragment
        ),
    )

    private val destinationChangedListener: NavController.OnDestinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            viewModel.onEvent(MainEvent.OnDestinationChanged(destination.id))
        }

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            viewModel.onEvent(MainEvent.OnTabSelected(tab.position))
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            viewModel.onEvent(MainEvent.OnTabReselected(tab.position))
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            setSupportActionBar(this)
            setNavigationOnClickListener {
                viewModel.onEvent(MainEvent.OnNavigationIconClick)
            }
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(destinationChangedListener)

        binding.tabLayout.addOnTabSelectedListener(tabSelectedListener)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    updateTabState(uiState.currentTabIndex, uiState.badgeValue)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is MainUiEvent.OnNavigationIconClick -> openNasaWebsite()
                        is MainUiEvent.OnDestinationChanged -> selectTabByDestinationId(uiEvent.destinationId)
                        is MainUiEvent.OnTabSelected -> onTabSelected(uiEvent.tabIndex)
                        is MainUiEvent.OnTabReselected -> onTabReselected(uiEvent.tabIndex)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(destinationChangedListener)
        binding.tabLayout.removeOnTabSelectedListener(tabSelectedListener)
    }

    private fun updateTabState(currentTabIndex: Int, badgeValue: Int) {
        binding.tabLayout.apply {
            removeOnTabSelectedListener(tabSelectedListener)
            getTabAt(currentTabIndex)?.select()
            addOnTabSelectedListener(tabSelectedListener)
            getTabAt(FAVORITES_TAB_INDEX)?.let { tab ->
                if (badgeValue > 0) {
                    tab.orCreateBadge.number = badgeValue
                } else {
                    tab.removeBadge()
                }
            }
        }
    }

    private fun openNasaWebsite() {
        val params = CustomTabColorSchemeParams.Builder()
            .setNavigationBarColor(ContextCompat.getColor(this, R.color.color_background))
            .setNavigationBarDividerColor(ContextCompat.getColor(this, R.color.color_background))
            .setToolbarColor(ContextCompat.getColor(this, R.color.color_action_bar))
            .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.color_action_bar))
            .build()

        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setDefaultColorSchemeParams(params)
            .build()
            .launchUrl(this@MainActivity, Uri.parse(NASA_URL))
    }

    private fun selectTabByDestinationId(destinationId: Int) {
        tabIndicesToFragmentIds.entries.firstOrNull { tabIndexToFragmentId ->
            tabIndexToFragmentId.value.contains(destinationId)
        }?.key?.let { tabIndex ->
            binding.tabLayout.apply {
                removeOnTabSelectedListener(tabSelectedListener)
                getTabAt(tabIndex)?.select()
                addOnTabSelectedListener(tabSelectedListener)
                viewModel.onEvent(MainEvent.OnTabChanged(tabIndex))
            }
        }
    }

    private fun onTabSelected(tabIndex: Int) {
        viewModel.onEvent(MainEvent.OnTabChanged(tabIndex))
        when (tabIndex) {
            HOME_TAB_INDEX -> navController.popBackStack(R.id.home_view_pager_fragment, false)
            FAVORITES_TAB_INDEX -> navController.navigate(R.id.favorites_list_fragment)
        }
    }

    private fun onTabReselected(tabIndex: Int) {
        when (tabIndex) {
            HOME_TAB_INDEX -> navHostFragment.childFragmentManager
                .primaryNavigationFragment.let { fragment ->
                    (fragment as? HomeViewPagerFragment)?.resetState()
                }
            FAVORITES_TAB_INDEX -> navController.popBackStack(R.id.favorites_list_fragment, false)
        }
    }

    companion object {
        private const val NASA_URL = "https://www.nasa.gov/"

        private const val HOME_TAB_INDEX = 0
        private const val FAVORITES_TAB_INDEX = 1
    }
}