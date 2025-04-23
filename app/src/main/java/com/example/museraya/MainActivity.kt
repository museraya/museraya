package com.example.museraya

import android.Manifest // Keep for permission check
import android.app.AlertDialog // Keep for permission rationale
import android.content.Intent
import android.content.pm.PackageManager // Keep for permission check
import android.os.Build // Keep for permission check
import android.os.Bundle
import android.util.Log // Keep for logging
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts // Keep for permission result
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat // Keep for permission check
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
// ** REMOVED UNUSED IMPORTS for FCM/Firestore Token Saving **
// import com.google.firebase.firestore.FirebaseFirestore
// import com.google.firebase.firestore.SetOptions
// import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button
    private lateinit var firebaseAuth: FirebaseAuth

    // --- Keep Notification Permission Launcher ---
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            // ** REMOVED updateFcmToken() call here **
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        signupBtn = findViewById(R.id.signup_btn)

        loginBtn.setOnClickListener {
            val email = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        signupBtn.setOnClickListener {
            val fragment = SignUpFragment()
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(android.R.id.content, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // --- Keep Asking for Notification Permission on App Start ---
        askNotificationPermission()
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("MainActivity", "signInWithEmail:success")
                    val user = firebaseAuth.currentUser

                    // ** REMOVED updateFcmToken() call here **

                    // Navigate based on user type
                    if (user != null && user.email == "museraya123@gmail.com") {
                        Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, AdminActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.w("MainActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    // --- REMOVED updateFcmToken() function ---
    /*
    private fun updateFcmToken() { ... }
    */

    // --- Keep Notification Permission Request Logic ---
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Permission", "POST_NOTIFICATIONS permission already granted.")
                // ** REMOVED updateFcmToken() call here **
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Log.d("Permission", "Showing rationale for POST_NOTIFICATIONS permission.")
                showPermissionRationaleDialog()
            } else {
                Log.d("Permission", "Requesting POST_NOTIFICATIONS permission.")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            Log.d("Permission", "Notification permission granted by default (API < 33).")
            // ** REMOVED updateFcmToken() call here **
        }
    }

    // --- Keep Rationale Dialog ---
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