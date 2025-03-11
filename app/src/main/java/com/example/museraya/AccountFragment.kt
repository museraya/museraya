package com.example.museraya

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.imageview.ShapeableImageView

class AccountFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val email = user?.email

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        val profileNameTextView: TextView = view.findViewById(R.id.name)
        val avatarImageView: ShapeableImageView = view.findViewById(R.id.avatar_image)
        val changeProfileButton: Button = view.findViewById(R.id.change_name_button)
        val changePasswordButton: Button = view.findViewById(R.id.change_password_button)
        val logoutButton: Button = view.findViewById(R.id.logout_button)

        // Fetch user data from Firestore based on the email
// Fetch user data from Firestore based on the email
        if (email != null) {
            db.collection("users").document(email).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        val username = document.getString("username")
                        val email = document.getString("email") // This is redundant as you're already using `user?.email`
                        val contact = document.getString("contact") // Fetch the contact info

                        profileNameTextView.text = name
                        val userUsernameTextView: TextView = view.findViewById(R.id.user_username)
                        val userEmailTextView: TextView = view.findViewById(R.id.user_email)
                        val userContactTextView: TextView = view.findViewById(R.id.user_contact)

                        userUsernameTextView.text = username
                        userEmailTextView.text = email
                        userContactTextView.text = contact
                    } else {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Set up Change Profile button
        changeProfileButton.setOnClickListener {
            showChangeProfileDialog(profileNameTextView)
        }

        // Set up Change Password button
        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        // Set up Logout button
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.finish() // Close current activity
        }

        return view
    }

    private fun showChangeProfileDialog(profileNameTextView: TextView) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change Profile Information")

        // Set up input fields for new name and username
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val nameInput = EditText(requireContext())
        nameInput.hint = "Enter new name"
        layout.addView(nameInput)

        val usernameInput = EditText(requireContext())
        usernameInput.hint = "Enter new username"
        layout.addView(usernameInput)

        builder.setView(layout)

        builder.setPositiveButton("OK") { _, _ ->
            val newName = nameInput.text.toString()
            val newUsername = usernameInput.text.toString()

            if (newName.isNotBlank() && newUsername.isNotBlank()) {
                // Update Firestore with the new profile data
                val updatedData = mapOf(
                    "name" to newName,
                    "username" to newUsername
                )

                if (email != null) {
                    db.collection("users").document(email).update(updatedData)
                        .addOnSuccessListener {
                            profileNameTextView.text = newName
                            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change Password")

        // Set up input field for new password
        val passwordInput = EditText(requireContext())
        passwordInput.hint = "Enter new password"
        builder.setView(passwordInput)

        builder.setPositiveButton("OK") { _, _ ->
            val newPassword = passwordInput.text.toString()

            if (newPassword.isNotBlank()) {
                // Update Firebase Authentication password
                user?.updatePassword(newPassword)
                    ?.addOnSuccessListener {
                        Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}
