package com.example.museraya

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import java.util.*

class BookingFragment : Fragment() {

    private lateinit var bookingNameInput: EditText
    private lateinit var guestQuantityInput: EditText
    private lateinit var datePickerButton: Button
    private lateinit var timePickerButton: Button
    private lateinit var selectedDateTime: TextView
    private lateinit var submitButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val calendar = Calendar.getInstance()

    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var userContact: String? = null // To store the user's contact number

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        bookingNameInput = view.findViewById(R.id.booking_name_input)
        guestQuantityInput = view.findViewById(R.id.guest_quantity_input)
        datePickerButton = view.findViewById(R.id.date_picker_button)
        timePickerButton = view.findViewById(R.id.time_picker_button)
        selectedDateTime = view.findViewById(R.id.selected_datetime)
        submitButton = view.findViewById(R.id.submit_button)

        datePickerButton.setOnClickListener { showDatePicker() }
        timePickerButton.setOnClickListener { showTimePicker() }
        submitButton.setOnClickListener { submitBooking() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserData()
    }

    private fun loadUserData() {
        val userEmail = auth.currentUser?.email ?: return // Exit if not logged in

        db.collection("users").document(userEmail).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name")
                    userContact = document.getString("contact") // Store contact for submission
                    bookingNameInput.setText(name) // Autofill the name field
                } else {
                    Toast.makeText(requireContext(), "User profile not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("BookingFragment", "Error fetching user data", e)
                Toast.makeText(requireContext(), "Failed to load user data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDatePicker() {
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = "${month + 1}/$dayOfMonth/$year"
                updateSelectedDateTime()
            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePicker.show()
    }

    private fun showTimePicker() {
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                updateSelectedDateTime()
            },
            currentHour,
            currentMinute,
            true
        )
        timePicker.show()
    }

    private fun updateSelectedDateTime() {
        val formatter = android.text.format.DateFormat.getMediumDateFormat(requireContext())
        val formattedDate = formatter.format(calendar.time)

        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext())
        val formattedTime = timeFormat.format(calendar.time)

        selectedDateTime.text = "Selected Date & Time: $formattedDate $formattedTime"
    }

    private fun submitBooking() {
        val bookingName = bookingNameInput.text.toString().trim()
        val guestQuantity = guestQuantityInput.text.toString()
        val userEmail = auth.currentUser?.email

        if (bookingName.isBlank() || guestQuantity.isBlank() || userEmail == null || selectedDate == null || selectedTime == null) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (userContact == null) {
            Toast.makeText(requireContext(), "User contact missing. Please update profile.", Toast.LENGTH_LONG).show()
            return
        }

        val bookingTimestamp = Timestamp(calendar.time)
        val dateCreatedTimestamp = Timestamp.now()

        db.collection("booking")
            .orderBy("id", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lastAppointmentId = querySnapshot.documents.firstOrNull()?.getString("id")
                val nextId = if (lastAppointmentId != null) {
                    val lastNumber = lastAppointmentId.removePrefix("appointment").toInt()
                    "appointment${lastNumber + 1}"
                } else {
                    "appointment1"
                }

                val booking = hashMapOf(
                    "id" to nextId,
                    "name" to bookingName, // Use name from the editable field
                    "contact" to (userContact ?: ""), // Use stored contact
                    "quantity" to guestQuantity.toInt(),
                    "email" to userEmail,
                    "date" to bookingTimestamp,
                    "date_created" to dateCreatedTimestamp,
                    "status" to "pending"
                )

                db.collection("booking")
                    .document(nextId)
                    .set(booking)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Booking successful!", Toast.LENGTH_SHORT).show()
                        Log.d("BookingFragment", "Document added with ID: $nextId")
                        resetInputs()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Booking failed!", Toast.LENGTH_SHORT).show()
                        Log.e("BookingFragment", "Error adding document", e)
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to generate booking ID", Toast.LENGTH_SHORT).show()
                Log.e("BookingFragment", "Error fetching booking ID", e)
            }
    }

    private fun resetInputs() {
        // Don't clear the name, just the other fields. Re-fetch to reset any edits.
        loadUserData()
        guestQuantityInput.text.clear()
        selectedDate = null
        selectedTime = null
        selectedDateTime.text = "Selected Date & Time:"
    }
}