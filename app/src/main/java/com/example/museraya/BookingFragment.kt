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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import java.util.*

class BookingFragment : Fragment() {

    private lateinit var fullNameInput: EditText
    private lateinit var guestQuantityInput: EditText
    private lateinit var contactInput: EditText
    private lateinit var datePickerButton: Button
    private lateinit var timePickerButton: Button
    private lateinit var selectedDateTime: TextView
    private lateinit var submitButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val calendar = Calendar.getInstance()
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        // Initialize views
        fullNameInput = view.findViewById(R.id.full_name_input)
        guestQuantityInput = view.findViewById(R.id.guest_quantity_input)
        contactInput = view.findViewById(R.id.contact_input)
        datePickerButton = view.findViewById(R.id.date_picker_button)
        timePickerButton = view.findViewById(R.id.time_picker_button)
        selectedDateTime = view.findViewById(R.id.selected_datetime)
        submitButton = view.findViewById(R.id.submit_button)

        // Set up date picker
        datePickerButton.setOnClickListener { showDatePicker() }

        // Set up time picker
        timePickerButton.setOnClickListener { showTimePicker() }

        // Set up submit button
        submitButton.setOnClickListener { submitBooking() }

        return view
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
        val fullName = fullNameInput.text.toString()
        val guestQuantity = guestQuantityInput.text.toString()
        val contactNumber = contactInput.text.toString()

        if (fullName.isBlank() || guestQuantity.isBlank() || contactNumber.isBlank() || selectedDate == null || selectedTime == null) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Use Firestore Timestamp to store the date and time
        val bookingTimestamp = Timestamp(calendar.time)

        // Fetch the next document ID (e.g., appointment1, appointment2, ...)
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

                // Create the booking document with "status" set to "pending"
                val booking = hashMapOf(
                    "id" to nextId,
                    "name" to fullName,
                    "quantity" to guestQuantity.toInt(),
                    "email" to contactNumber,
                    "date" to bookingTimestamp,
                    "status" to "pending"  // Add status field with "pending" value
                )

                db.collection("booking")
                    .document(nextId)
                    .set(booking)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Booking successful!", Toast.LENGTH_SHORT).show()
                        Log.d("BookingFragment", "Document added with ID: $nextId")

                        // Reset inputs after successful booking
                        resetInputs()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Booking failed!", Toast.LENGTH_SHORT).show()
                        Log.e("BookingFragment", "Error adding document", e)
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch document ID", Toast.LENGTH_SHORT).show()
                Log.e("BookingFragment", "Error fetching document ID", e)
            }
    }

    private fun resetInputs() {
        fullNameInput.text.clear()
        guestQuantityInput.text.clear()
        contactInput.text.clear()
        selectedDate = null
        selectedTime = null
        selectedDateTime.text = "Selected Date & Time:"
    }
}
