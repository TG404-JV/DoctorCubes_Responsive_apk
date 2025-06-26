package com.tvm.doctorcube

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.airbnb.lottie.LottieAnimationView
import com.tvm.doctorcube.customview.CustomBottomNavigationView
import java.util.Objects

class MainActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var callButton: LottieAnimationView? = null
    private var navController: NavController? = null
    private var doubleBackToExitPressedOnce = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }

        val window = window


         // Set status bar icon/text color (light/dark)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = true



        // Initialize Toolbar
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        checkNotNull(toolbar) { "Toolbar not found in activity_main.xml" }
        setSupportActionBar(toolbar)

        // Setup Navigation Component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        checkNotNull(navHostFragment) { "NavHostFragment not found in R.id.nav_host_fragment" }
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.homeFragment, R.id.universityFragment, R.id.settingsHome
        ).build()
        setupWithNavController(toolbar!!, navController!!, appBarConfiguration)

        // Setup back press handling
        setupBackPressHandling()

        // Apply animation to app logo click
        val appLogo = findViewById<View?>(R.id.app_logo)
        appLogo?.setOnClickListener(View.OnClickListener { v: View? ->
            if (Objects.requireNonNull<NavDestination?>(
                    navController!!.currentDestination
                ).id != R.id.settingsHome
            ) {
                applySelectionAnimation(v!!)
                navController!!.navigate(R.id.settingsHome)
            }
        })

        // Initialize call button
        callButton = toolbar!!.findViewById<LottieAnimationView?>(R.id.call_button)
        if (callButton == null) {
            CustomToast.showToast(this, "Call button not found in toolbar")
        } else {
            setupToolbar()
        }

        // Set app title
        val appTitle = findViewById<TextView?>(R.id.app_title)
        appTitle?.text = getString(R.string.app_name)

        // Initialize custom navigation
        setupCustomNavigation()

        // Update toolbar title and navigation item based on destination
        navController!!.addOnDestinationChangedListener(OnDestinationChangedListener { controller: NavController?, destination: NavDestination?, arguments: Bundle? ->
            val bottomNav = findViewById<CustomBottomNavigationView?>(R.id.custom_bottom_navigation)
            if (destination!!.id == R.id.homeFragment) {
                toolbar!!.setTitle("DoctorCubes")
                bottomNav?.setSelectedItem(0)
            } else if (destination.id == R.id.universityFragment) {
                val country =
                    if (arguments != null) arguments.getString("COUNTRY_NAME", "All") else "All"
                toolbar!!.setTitle(if (country == "All") "All Universities" else "Universities in $country")
                bottomNav?.setSelectedItem(1)
            } else if (destination.id == R.id.settingsHome) {
                toolbar!!.setTitle("Settings")
                bottomNav?.setSelectedItem(2)
            } else if (destination.id == R.id.universityDetailsFragment) {
                val universityName =
                    if (arguments != null) arguments.getString("UNIVERSITY_NAME", "") else ""
                toolbar!!.setTitle(universityName)
            } else if (destination.id == R.id.studyMaterialFragment) {
                toolbar!!.setTitle("Study Material")
                bottomNav?.setSelectedItem(1)
            } else {
                toolbar!!.setTitle(getString(R.string.app_name))
            }
        })
    }

    private fun setupBackPressHandling() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (Objects.requireNonNull<NavDestination?>(navController!!.currentDestination).id == R.id.homeFragment) {
                    if (doubleBackToExitPressedOnce) {
                        finish()
                    } else {
                        doubleBackToExitPressedOnce = true
                        Toast.makeText(
                            this@MainActivity,
                            "Press back again to exit",
                            Toast.LENGTH_SHORT
                        ).show()
                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                            doubleBackToExitPressedOnce = false
                        }, 2000)
                    }
                } else {
                    navController!!.navigateUp()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setupToolbar() {
        callButton!!.setOnClickListener(View.OnClickListener { v: View? ->
            applySelectionAnimation(v!!)
            val openMedia = SocialActions()
            openMedia.makeDirectCall(this)
        })

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.setHomeButtonEnabled(true)

            if (toolbar!!.navigationIcon != null) {
                val navIcon = toolbar?.navigationIcon
                navIcon?.let {
                    DrawableCompat.setTint(it, ContextCompat.getColor(this, android.R.color.white))
                    toolbar!!.navigationIcon = it
                }

            }
        }
    }

    private fun setupCustomNavigation() {
        val bottomNav = findViewById<CustomBottomNavigationView>(R.id.custom_bottom_navigation)
        checkNotNull(bottomNav) { "CustomBottomNavigationView not found in activity_main.xml" }

        bottomNav.setOnNavigationItemSelectedListener(CustomBottomNavigationView.OnNavigationItemSelectedListener { position: Int ->
            val currentDestination = Objects.requireNonNull<NavDestination?>(
                navController!!.currentDestination
            ).id
            val targetDestination: Int = when (position) {
                0 -> R.id.homeFragment
                1 -> R.id.studyMaterialFragment
                2 -> R.id.settingsHome
                else -> R.id.homeFragment
            }
            if (currentDestination != targetDestination) {
                // Apply animation to the clicked navigation item
                val navItem = bottomNav.findViewById<View?>(getNavItemId(position))
                if (navItem != null) {
                    applySelectionAnimation(navItem)
                }
                navController!!.navigate(targetDestination)
            }
        })
    }

    private fun getNavItemId(position: Int): Int {
        return when (position) {
            0 -> R.id.nav_home
            1 -> R.id.nav_study
            2 -> R.id.nav_settings
            else -> -1
        }
    }

    private fun applySelectionAnimation(view: View) {
        view.scaleX = 0.9f
        view.scaleY = 0.9f
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setInterpolator(OvershootInterpolator(1.5f))
            .start()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController!!.navigateUp() || super.onSupportNavigateUp()
    }
}