package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class BookingHistoryFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_booking_history, container, false)

        // Bind UI elements
        val bookingHistoryTextView = view.findViewById<TextView>(R.id.booking_history_text)

        // Fetch the currently logged-in user's email
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email
            if (!userEmail.isNullOrEmpty()) {
                fetchBookingHistoryByEmail(userEmail, bookingHistoryTextView)
            } else {
                Toast.makeText(requireContext(), "Unable to fetch user email.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No user is logged in.", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun fetchBookingHistoryByEmail(email: String, bookingHistoryTextView: TextView) {
        val stringBuilder = StringBuilder()

        // Loop through appointment1 to appointment50
        for (i in 1..50) {
            val documentRef = firestore.collection("booking").document("appointment$i")

            documentRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        // Check if the email field matches the logged-in user's email
                        val appointmentEmail = document.getString("email")
                        if (appointmentEmail?.equals(email, ignoreCase = true) == true) {
                            // If email matches, retrieve and display the appointment data
                            val name = document.getString("name") ?: "N/A"
                            val timestamp = document.getTimestamp("date")
                            val dateTime = timestamp?.toDate()?.let { formatDateTime(it) } ?: "N/A"
                            val quantity = document.getLong("quantity")?.toString() ?: "N/A"
                            val status = document.getString("status") ?: "N/A"
                            val dateCreatedTimestamp = document.getTimestamp("date_created")
                            val dateCreated = dateCreatedTimestamp?.toDate()?.let { formatDateTime(it) } ?: "N/A"

                            stringBuilder.append("Name: $name\n")
                                .append("Date & Time: $dateTime\n")
                                .append("Quantity: $quantity\n")
                                .append("Status: $status\n")
                                .append("Date Created: $dateCreated\n\n")
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Error fetching booking history", Toast.LENGTH_SHORT).show()
                }

                // After checking all appointments, update the TextView with the result
                if (i == 50) {
                    if (stringBuilder.isEmpty()) {
                        stringBuilder.append("No bookings found for this account.")
                    }
                    bookingHistoryTextView.text = stringBuilder.toString()
                }
            }
        }
    }

    private fun formatDateTime(date: java.util.Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return formatter.format(date)
    }
}
