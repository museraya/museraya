package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Locale

class BookingHistoryFragment : Fragment() {

    private lateinit var bookingHistoryLayout: LinearLayout
    private lateinit var noBookingsMessage: TextView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var scrollView: ScrollView // Added ScrollView reference

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val inflater: LayoutInflater by lazy { LayoutInflater.from(requireContext()) } // Efficient inflater

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking_history, container, false)

        // Bind UI elements
        bookingHistoryLayout = view.findViewById(R.id.booking_history_layout)
        noBookingsMessage = view.findViewById(R.id.no_bookings_message)
        loadingProgress = view.findViewById(R.id.loading_progress)
        scrollView = view.findViewById(R.id.scrollView) // Bind ScrollView

        fetchUserBookings()

        return view
    }

    private fun fetchUserBookings() {
        val currentUser = auth.currentUser
        if (currentUser?.email != null) {
            val userEmail = currentUser.email!! // Safe call because we checked for null
            showLoading(true)
            fetchBookingHistoryByEmail(userEmail)
        } else {
            Toast.makeText(requireContext(), "Could not get user email. Are you logged in?", Toast.LENGTH_LONG).show()
            showLoading(false)
            showNoBookingsMessage(true, "Login required to view history.") // Show specific message
        }
    }

    // --- Improved Firestore Query ---
    private fun fetchBookingHistoryByEmail(email: String) {
        firestore.collection("booking")
            .whereEqualTo("email", email) // Query by email field directly
            .orderBy("date_created", Query.Direction.DESCENDING) // Order by creation date, newest first
            .get()
            .addOnSuccessListener { documents ->
                showLoading(false)
                if (documents == null || documents.isEmpty) {
                    showNoBookingsMessage(true)
                } else {
                    showNoBookingsMessage(false)
                    displayBookingHistory(documents)
                }
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                showNoBookingsMessage(true, "Error fetching booking history.") // Show error
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayBookingHistory(snapshot: QuerySnapshot) {
        bookingHistoryLayout.removeAllViews() // Clear previous views if any

        for (document in snapshot.documents) {
            // Inflate the list item layout for each booking
            val bookingView = inflater.inflate(R.layout.list_item_booking, bookingHistoryLayout, false)

            // Find views within the inflated layout
            val nameTextView = bookingView.findViewById<TextView>(R.id.booking_name)
            val dateTimeTextView = bookingView.findViewById<TextView>(R.id.booking_datetime)
            val quantityTextView = bookingView.findViewById<TextView>(R.id.booking_quantity)
            val statusTextView = bookingView.findViewById<TextView>(R.id.booking_status)
            val dateCreatedTextView = bookingView.findViewById<TextView>(R.id.booking_date_created)

            // Extract data with null checks and defaults
            val name = document.getString("name") ?: "N/A"
            val timestamp = document.getTimestamp("date")
            val dateTime = timestamp?.toDate()?.let { formatDateTime(it) } ?: "N/A"
            val quantity = document.getLong("quantity")?.toString() ?: "N/A"
            val status = document.getString("status") ?: "N/A"
            val dateCreatedTimestamp = document.getTimestamp("date_created")
            val dateCreated = dateCreatedTimestamp?.toDate()?.let { formatDateTime(it) } ?: "N/A"

            // Populate the views
            nameTextView.text = "Name: $name" // Or just name, depending on preference
            dateTimeTextView.text = "Date & Time: $dateTime"
            quantityTextView.text = "Quantity: $quantity"
            statusTextView.text = status.uppercase(Locale.getDefault()) // Show status in uppercase
            dateCreatedTextView.text = "Created: $dateCreated"

            // Set status background color
            setStatusBackground(statusTextView, status)

            // Add the populated item view to the main layout
            bookingHistoryLayout.addView(bookingView)
        }
    }

    private fun setStatusBackground(textView: TextView, status: String) {
        val backgroundDrawableId = when (status.lowercase(Locale.getDefault())) {
            "pending" -> R.drawable.status_background_pending
            "accepted" -> R.drawable.status_background_accepted
            "declined" -> R.drawable.status_background_declined
            else -> R.drawable.status_background_other
        }
        // Use ContextCompat for backward compatibility
        textView.background = ContextCompat.getDrawable(textView.context, backgroundDrawableId)
    }


    private fun formatDateTime(date: java.util.Date): String {
        // Consider making the format slightly more concise if needed
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return formatter.format(date)
    }

    private fun showLoading(isLoading: Boolean) {
        loadingProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        // Hide content while loading
        scrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
        noBookingsMessage.visibility = View.GONE // Ensure message is hidden when loading starts
    }

    private fun showNoBookingsMessage(show: Boolean, message: String = "No bookings found for this account.") {
        noBookingsMessage.text = message
        noBookingsMessage.visibility = if (show) View.VISIBLE else View.GONE
        // Hide the scroll view if the message is shown
        scrollView.visibility = if (show) View.GONE else View.VISIBLE
    }
}