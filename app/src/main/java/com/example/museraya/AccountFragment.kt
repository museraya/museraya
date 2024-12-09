package com.example.museraya

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.material.imageview.ShapeableImageView

class AccountFragment : Fragment() {

    // Sample user data (you can replace it with actual data from your user authentication system)
    private var userName = "John Doe"
    private val userAvatar = R.drawable.dummy_avatar // Replace with actual avatar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Make sure the view is not null before accessing it
        val profileNameTextView: TextView? = view.findViewById(R.id.textView4)
        val avatarImageView: ShapeableImageView? = view.findViewById(R.id.avatar_image)
        val editNameButton: Button? = view.findViewById(R.id.change_name_button)
        val changePasswordButton: Button? = view.findViewById(R.id.change_password_button)
        val notificationsSwitch: SwitchCompat = view.findViewById(R.id.notifications_switch)
        val googleLoginButton: Button? = view.findViewById(R.id.login_google)
        val facebookLoginButton: Button? = view.findViewById(R.id.login_facebook)
        val reportProblemButton: Button? = view.findViewById(R.id.report_problem_button)

        // Check if the views are properly initialized
        profileNameTextView?.text = userName // Use dynamic user data
        avatarImageView?.setImageResource(userAvatar) // Use dynamic user avatar

        // Set up Edit Name button
        editNameButton?.setOnClickListener {
            // Open a dialog to edit the name
            showEditNameDialog(profileNameTextView)
        }

        // Set up change password button
        changePasswordButton?.setOnClickListener {
            Toast.makeText(requireContext(), "Change Password clicked", Toast.LENGTH_SHORT).show()
        }

        // Set up notifications switch
        notificationsSwitch?.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Notifications enabled" else "Notifications disabled"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Set up Google login button
        googleLoginButton?.setOnClickListener {
            Toast.makeText(requireContext(), "Login with Google clicked", Toast.LENGTH_SHORT).show()
        }

        // Set up Facebook login button
        facebookLoginButton?.setOnClickListener {
            Toast.makeText(requireContext(), "Login with Facebook clicked", Toast.LENGTH_SHORT).show()
        }

        // Set up Report a Problem button
        reportProblemButton?.setOnClickListener {
            Toast.makeText(requireContext(), "Report a Problem clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun showEditNameDialog(profileNameTextView: TextView?) {
        // Check if the TextView is not null before showing the dialog
        profileNameTextView?.let { tv ->
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Edit Name")

            // Set up the input field
            val input = EditText(requireContext())
            input.hint = "Enter your new name"
            builder.setView(input)

            // Set up buttons
            builder.setPositiveButton("OK") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotBlank()) {
                    // Update the user name
                    userName = newName
                    // Update the TextView with the new name
                    tv.text = userName
                    // Optionally save the new name to SharedPreferences or a database
                    Toast.makeText(requireContext(), "Name updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        } ?: run {
            Toast.makeText(requireContext(), "Error: Profile name view not found", Toast.LENGTH_SHORT).show()
        }
    }
}
