package com.example.museraya

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import android.telephony.SmsManager  // Import SmsManager for sending SMS

class AdminActivity : AppCompatActivity() {

    private lateinit var appointmentsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Set up edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements
        appointmentsLayout = findViewById(R.id.appointmentsLayout)

        // Set up the Logout button click listener
        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Handle the logout action (finish the current activity)
            logout()
        }

        // Fetch data for appointments 1 to 50
        fetchAppointmentsData()
    }

    private fun fetchAppointmentsData() {
        val db = FirebaseFirestore.getInstance()

        // Loop through appointments 1 to 50
        for (i in 1..50) {
            val documentRef = db.collection("booking").document("appointment$i")

            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "N/A"
                        val quantity = document.getLong("quantity") ?: 0
                        val date = document.getTimestamp("date")?.toDate() ?: "N/A"
                        val status = document.getString("status") ?: "N/A"
                        val email = document.getString("email") ?: "N/A"

                        // Create UI elements for the appointment
                        createAppointmentUI(i, name, quantity, date, status, email)
                    } else {
                        // No such appointment, leave it blank
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("AdminActivity", "Error fetching appointment$i", exception)
                }
        }
    }

    private fun createAppointmentUI(appointmentNumber: Int, name: String, quantity: Long, date: Any, status: String, email: String) {
        // Create a new TextView for appointment data
        val textViewData = TextView(this)
        textViewData.text = """
            Appointment $appointmentNumber:
            Name: $name
            Quantity: $quantity
            Email: $email
            Date: $date
            Status: $status
        """.trimIndent()
        appointmentsLayout.addView(textViewData)

        // Create a new Accept button for each appointment
        val acceptButton = Button(this)
        acceptButton.text = "Accept"
        acceptButton.setOnClickListener {
            updateAppointmentStatus(appointmentNumber, email)
        }
        appointmentsLayout.addView(acceptButton)
    }

    private fun updateAppointmentStatus(appointmentNumber: Int, contact: String) {
        val db = FirebaseFirestore.getInstance()

        // Reference to the appointment document
        val documentRef = db.collection("booking").document("appointment$appointmentNumber")

        // Update the "status" field to "accepted"
        documentRef.update("status", "accepted")
            .addOnSuccessListener {
                Log.d("AdminActivity", "Appointment $appointmentNumber status updated successfully")


                // Optionally, refresh the displayed data
                appointmentsLayout.removeAllViews()
                fetchAppointmentsData()
            }
            .addOnFailureListener { exception ->
                Log.w("AdminActivity", "Error updating status for appointment$appointmentNumber", exception)
            }
    }


    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
