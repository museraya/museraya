package com.example.museraya

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import android.app.AlertDialog
import android.widget.TextView

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
            if (destination.id == R.id.navigation_home) {
                backButton.visibility = ImageButton.INVISIBLE // Keep position but hide the button
            } else {
                backButton.visibility = ImageButton.VISIBLE
            }
        }

        // Bottom Navigation Bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        // Integration for new settings content
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_settings) {
                val fragmentView = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.view
                fragmentView?.let {
                    val rowProfileSettings = it.findViewById<View>(R.id.row_profile_settings)
                    val switchPushNotifications =
                        it.findViewById<SwitchMaterial>(R.id.switch_push_notifications)
                    val switchEmailNotifications =
                        it.findViewById<SwitchMaterial>(R.id.switch_email_notifications)
                    val rowAbout = it.findViewById<View>(R.id.row_about)
                    val rowLegal = it.findViewById<View>(R.id.row_legal)

                    // Handle Profile Settings click
                    rowProfileSettings?.setOnClickListener {
                        Log.d("SettingsFragment", "Profile Settings clicked")
                        // Navigate to Profile Settings screen or handle the action
                    }

                    // Handle Push Notifications toggle
                    switchPushNotifications?.setOnCheckedChangeListener { _, isChecked ->
                        Log.d("SettingsFragment", "Push Notifications: $isChecked")
                        // Handle Push Notifications toggle
                    }

                    // Handle Email Notifications toggle
                    switchEmailNotifications?.setOnCheckedChangeListener { _, isChecked ->
                        Log.d("SettingsFragment", "Email Notifications: $isChecked")
                        // Handle Email Notifications toggle
                    }

                    // Handle About click
                    rowAbout?.setOnClickListener {
                        Log.d("SettingsFragment", "About clicked")
                        // Navigate to About screen or display an info dialog
                        showAboutDialog()
                    }

                    // Handle Legal click
                    rowLegal?.setOnClickListener {
                        Log.d("SettingsFragment", "Legal clicked")
                        // Navigate to Legal screen or display legal information
                        showLegalDialog()
                    }
                }
            }
        }
    }

    // Dialog for About
    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("About")
            .setMessage("This is an example app demonstrating settings functionality.")
            .setPositiveButton("OK", null)
            .show()
    }

    // Dialog for Legal
    private fun showLegalDialog() {
        AlertDialog.Builder(this)
            .setTitle("Legal")
            .setMessage("Legal information goes here.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun logoutUser() {
        // Example Logout Logic: Navigate to login screen
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else if (!navController.navigateUp()) {
            super.onBackPressed()
        }
    }
}
