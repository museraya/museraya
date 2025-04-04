package com.example.museraya

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
// import android.util.Log // No longer needed in this file for settings
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
// import androidx.navigation.ui.NavigationUI // Not strictly needed if only using setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
// import com.google.android.material.switchmaterial.SwitchMaterial // No longer needed here
// import android.app.AlertDialog // No longer needed here
// import android.widget.TextView // No longer needed here

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var backButton: ImageButton
    private lateinit var drawerToggle: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        window.statusBarColor = Color.TRANSPARENT

        // Toolbar setup
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize drawer, buttons, and nav controller
        drawerLayout = findViewById(R.id.drawer_layout)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navigationView: NavigationView = findViewById(R.id.navigation_view)

        // Handle Navigation Drawer Item Clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.navigation_home)
                }
                R.id.nav_profile -> {
                    navController.navigate(R.id.navigation_account)
                }
                R.id.nav_settings -> {
                    navController.navigate(R.id.navigation_settings)
                }
                R.id.nav_booking -> {
                    navController.navigate(R.id.bookingFragment)
                }
                R.id.nav_history -> {
                    navController.navigate(R.id.bookingHistoryFragment)
                }
                R.id.nav_logout -> {
                    logoutUser()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.END) // Close the drawer after a click
            true
        }

        // Back Button
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            if (!navController.navigateUp()) {
                finish() // Exit the app if no fragments in the back stack
            }
        }

        // Drawer Toggle
        drawerToggle = findViewById(R.id.drawer_toggle)
        drawerToggle.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        // Dynamically show/hide the Back Button
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Keep this listener for the back button visibility logic
            if (destination.id == R.id.navigation_home) {
                backButton.visibility = ImageButton.INVISIBLE // Keep position but hide the button
            } else {
                backButton.visibility = ImageButton.VISIBLE
            }
        }

        // Bottom Navigation Bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        // *** The entire block for handling settings fragment views has been removed ***

    }

    // *** showAboutDialog() method has been removed ***

    // *** showLegalDialog() method has been removed ***

    private fun logoutUser() {
        // Example Logout Logic: Navigate to login screen
        // Ensure MainActivity::class.java is your correct Login Activity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else if (!navController.navigateUp()) {
            super.onBackPressed() // Call super.onBackPressed() if navigateUp returns false
        }
    }
}