package com.example.museraya

import android.Manifest // Keep
import android.app.AlertDialog // Keep
import android.content.Intent
import android.content.pm.PackageManager // Keep
import android.graphics.Color
import android.os.Build // Keep
import android.os.Bundle
import android.util.Log // Keep
import android.view.View
import android.widget.ImageButton
import android.widget.Toast // Keep
import androidx.activity.result.contract.ActivityResultContracts // Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat // Keep
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.example.museraya.NotificationUtils // *** Ensure this import is correct ***

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var backButton: ImageButton
    private lateinit var drawerToggle: ImageButton

    // --- Firestore Listener ---
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var bookingListenerRegistration: ListenerRegistration? = null

    // --- Notification Permission Launcher ---
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // ... (Window, Toolbar, Drawer, Nav setup - unchanged) ...
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        window.statusBarColor = Color.TRANSPARENT
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navController.navigate(R.id.navigation_home)
                R.id.nav_profile -> navController.navigate(R.id.navigation_account)
                R.id.nav_settings -> navController.navigate(R.id.navigation_settings)
                R.id.nav_booking -> navController.navigate(R.id.bookingFragment)
                R.id.nav_history -> navController.navigate(R.id.bookingHistoryFragment)
                R.id.nav_logout -> logoutUser()
            }
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            if (!navController.navigateUp()) {
                finish()
            }
        }
        drawerToggle = findViewById(R.id.drawer_toggle)
        drawerToggle.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_home) {
                backButton.visibility = ImageButton.INVISIBLE
            } else {
                backButton.visibility = ImageButton.VISIBLE
            }
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)


        // --- Request Notification Permission ---
        // It's okay to ask here too, but MainActivity might be better as the primary point
        askNotificationPermission()

        // --- Start Listening for Booking Updates ---
        startListeningForBookingUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        // --- Stop Listening When Activity is Destroyed ---
        stopListeningForBookingUpdates()
    }

    // --- Firestore Listener Logic (Unchanged) ---
    private fun startListeningForBookingUpdates() {
        val currentUserEmail = auth.currentUser?.email
        if (currentUserEmail == null) {
            Log.w("BookingListener", "User not logged in, cannot start listener.")
            return
        }
        Log.d("BookingListener", "Starting listener for user: $currentUserEmail")
        stopListeningForBookingUpdates() // Ensure no duplicates
        val query = db.collection("booking").whereEqualTo("email", currentUserEmail)

        bookingListenerRegistration = query.addSnapshotListener(MetadataChanges.INCLUDE) { snapshots, e ->
            if (e != null) {
                Log.w("BookingListener", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshots == null) {
                Log.w("BookingListener", "Listener returned null snapshot.")
                return@addSnapshotListener
            }
            Log.d("BookingListener", "Received ${snapshots.documentChanges.size} document changes.")
            for (dc in snapshots.documentChanges) {
                if (dc.type == DocumentChange.Type.MODIFIED && !snapshots.metadata.hasPendingWrites()) {
                    val booking = dc.document.data
                    val bookingId = dc.document.id
                    val status = booking["status"] as? String
                    val name = booking["name"] as? String ?: "your Museraya visit"
                    Log.d("BookingListener", "Booking $bookingId modified. Status: $status")
                    if ("accepted".equals(status, ignoreCase = true)) {
                        Log.i("BookingListener", "Booking $bookingId accepted! Triggering notification.")
                        val openAppIntent = Intent(this, HomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            putExtra("navigateTo", "booking_history")
                        }
                        // *** Make sure NotificationUtils is correctly imported/accessible ***
                        NotificationUtils.showSimpleNotification(
                            applicationContext,
                            MyApplication.CHANNEL_ID_GENERAL, // Ensure MyApplication exists or use string directly
                            NotificationUtils.NOTIFICATION_ID_BOOKING_STATUS + bookingId.hashCode(),
                            "Booking Accepted!",
                            "Your booking for $name has been accepted.",
                            openAppIntent
                        )
                    }
                }
            }
        }
    }

    // --- Stop Listener (Unchanged) ---
    private fun stopListeningForBookingUpdates() {
        Log.d("BookingListener", "Stopping listener.")
        bookingListenerRegistration?.remove()
        bookingListenerRegistration = null
    }

    // --- Logout (Unchanged, includes stop listener) ---
    private fun logoutUser() {
        stopListeningForBookingUpdates()
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // --- Back Pressed (Unchanged) ---
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else if (!navController.navigateUp()) {
            super.onBackPressed()
        }
    }

    // --- Notification Permission Request Logic (Kept, ensures permission check) ---
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Notification permission already granted.")
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showPermissionRationaleDialog()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            Log.d("Permission", "Notification permission granted by default (API < 33).")
        }
    }

    // --- Rationale Dialog (Kept) ---
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission Needed")
            .setMessage("To receive updates about your booking status while the app is running, please grant the Notification permission.")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}