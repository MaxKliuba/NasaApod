package com.android.maxclub.nasaapod

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.android.maxclub.nasaapod.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var destinationChangedListener: NavController.OnDestinationChangedListener

    private val tabsFragmentId = listOf(
        setOf(R.id.home_view_pager_fragment),
        setOf(R.id.favorite_list_fragment),
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

        destinationChangedListener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val tabIndex = tabsFragmentId.indexOfFirst { it.contains(destination.id) }
            binding.tabLayout.apply {
                tag = false
                getTabAt(tabIndex)?.select()
                tag = true
            }
        }
        navController.addOnDestinationChangedListener(destinationChangedListener)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (binding.tabLayout.tag != false) {
                    when (tab.position) {
                        0 -> navController.navigate(R.id.home_view_pager_fragment)
                        1 -> navController.navigate(R.id.favorite_list_fragment)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(destinationChangedListener)
    }

    private fun navigationIconOnClick() {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
            .launchUrl(this, Uri.parse(URL))
    }

    companion object {
        const val URL = "https://www.nasa.gov/"
    }
}