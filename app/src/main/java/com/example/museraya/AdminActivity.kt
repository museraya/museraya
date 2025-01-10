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
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

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
            logout()
        }

        // Fetch data for appointments 1 to 50
        fetchAppointmentsData()
    }

    private fun fetchAppointmentsData() {
        val db = FirebaseFirestore.getInstance()

        for (i in 1..50) {
            val documentRef = db.collection("booking").document("appointment$i")

            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "N/A"
                        val quantity = document.getLong("quantity") ?: 0
                        val date = document.getTimestamp("date")
                        val formattedDate = date?.toDate()?.let { formatDateTime(it) } ?: "N/A"
                        val status = document.getString("status") ?: "N/A"
                        val email = document.getString("email") ?: "N/A"
                        val dateCreated = document.getTimestamp("date_created")
                        val formattedDateCreated =
                            dateCreated?.toDate()?.let { formatDateTime(it) } ?: "N/A"

                        // Create UI elements for the appointment
                        createAppointmentUI(
                            i, name, quantity, formattedDate, formattedDateCreated, status, email
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("AdminActivity", "Error fetching appointment$i", exception)
                }
        }
    }

    private fun createAppointmentUI(
        appointmentNumber: Int,
        name: String,
        quantity: Long,
        date: String,
        dateCreated: String,
        status: String,
        email: String
    ) {
        // Create a new TextView for appointment data
        val textViewData = TextView(this)
        textViewData.text = """
            Appointment $appointmentNumber:
            Name: $name
            Quantity: $quantity
            Email: $email
            Date: $date
            Appointment Created: $dateCreated
            Status: $status
        """.trimIndent()
        appointmentsLayout.addView(textViewData)

        // Create an Accept button
        val acceptButton = Button(this)
        acceptButton.text = "Accept"
        acceptButton.setOnClickListener {
            updateAppointmentStatus(appointmentNumber, email, "accepted")
        }
        appointmentsLayout.addView(acceptButton)

        // Create a Reject button
        val rejectButton = Button(this)
        rejectButton.text = "Reject"
        rejectButton.setOnClickListener {
            updateAppointmentStatus(appointmentNumber, email, "rejected")
        }
        appointmentsLayout.addView(rejectButton)
    }

    private fun updateAppointmentStatus(appointmentNumber: Int, contact: String, status: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("booking").document("appointment$appointmentNumber")

        documentRef.update("status", status)
            .addOnSuccessListener {
                Log.d("AdminActivity", "Appointment $appointmentNumber status updated to $status successfully")
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

    private fun formatDateTime(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return formatter.format(date)
    }
}
