package com.multistoryparking.app.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.google.android.material.snackbar.Snackbar
import com.multistoryparking.app.App
import com.multistoryparking.app.R
import com.multistoryparking.app.databinding.ActivityMainBinding
import com.multistoryparking.app.di.AppContractor
import com.multistoryparking.app.utils.ThemeHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var inputManager: InputMethodManager

    private val medium: AppContractor by lazy { App.appContractor }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        navController = host.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.reserveFragment,
                R.id.profileFragment
            )
        )

        viewControllers()

        networking()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> navController.popBackStack(R.id.homeFragment, false)

                R.id.reserveFragment -> navController.popBackStack(R.id.reserveFragment, false)

                R.id.profileFragment -> navController.popBackStack(R.id.profileFragment, false)
            }
            NavigationUI.onNavDestinationSelected(it, navController)
        }

        binding.bottomNavigationView.setOnApplyWindowInsetsListener(null)
        binding.bottomNavigationView.setOnItemReselectedListener {}

        updateTheme()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    private fun viewControllers() {
        bottomNavViewVisible.observe(this, {
            binding.bottomNavVisible = it ?: true
        })
        progressbarVisible.observe(this, {
            binding.progressbarVisible = it ?: true
        })
    }

    override fun onSupportNavigateUp() =
        findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)

    @SuppressLint("ShowToast")
    private fun networking() {
        Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    setAction("Open Settings") { startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)) }
                else setAction("OK") { dismiss() }
            }
    }

    private fun updateTheme() {
        themeState.observe(this, medium.themeHelper::updateForTheme)
        themeState.postValue(medium.themeHelper.currentTheme)
    }

    companion object {

        private val progressbarVisible = MutableLiveData(false)
        fun progressbarVisible(visible: Boolean = true) = progressbarVisible.postValue(visible)

        // Bars
        private val bottomNavViewVisible = MutableLiveData(false)
        fun mainParams(
            isBottomNavigationViewVisible: Boolean = false
        ) {
            bottomNavViewVisible.postValue(isBottomNavigationViewVisible)
        }

        private val medium: AppContractor by lazy { App.appContractor }
        val preference get() = medium.preference

        /* Theme State */
        val themeState: MutableLiveData<ThemeHelper.Companion.Themes> = MutableLiveData()
    }
}