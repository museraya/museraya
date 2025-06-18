package com.example.museraya

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.android.material.imageview.ShapeableImageView
import kotlin.random.Random // Import Kotlin's Random

class AccountFragment : Fragment() {

    // Firestore and Auth instances
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val email = user?.email // Get email safely

    // --- Lists for Random Images ---
    private val backgroundImages = listOf(
        R.drawable.bg_profile_1, R.drawable.bg_profile_2, R.drawable.bg_profile_3,
        R.drawable.bg_profile_4, R.drawable.bg_profile_5
    )
    private val profilePictures = listOf(
        R.drawable.pfp_1, R.drawable.pfp_2, R.drawable.pfp_3,
        R.drawable.pfp_4, R.drawable.pfp_5
    )

    // --- View References --- (Initialize in onViewCreated)
    private lateinit var profileNameTextView: TextView
    private lateinit var userUsernameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userContactTextView: TextView
    private lateinit var avatarImageView: ShapeableImageView
    private lateinit var headerBackgroundImageView: ImageView
    private lateinit var editProfileLayout: LinearLayout
    private lateinit var changePasswordLayout: LinearLayout
    private lateinit var logoutLayout: LinearLayout

    // --- Variables to store current user data ---
    private var currentName: String? = null
    private var currentUsername: String? = null
    private var currentProfileImageIndex: Int = 0
    private var currentBackgroundImageIndex: Int = 0 // <-- Added

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the NEW layout
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Initialize View References ---
        profileNameTextView = view.findViewById(R.id.name)
        userUsernameTextView = view.findViewById(R.id.user_username)
        userEmailTextView = view.findViewById(R.id.user_email)
        userContactTextView = view.findViewById(R.id.user_contact)
        avatarImageView = view.findViewById(R.id.avatar_image)
        headerBackgroundImageView = view.findViewById(R.id.header_background_image)
        editProfileLayout = view.findViewById(R.id.edit_profile_layout)
        changePasswordLayout = view.findViewById(R.id.change_password_layout)
        logoutLayout = view.findViewById(R.id.logout_layout)

        // --- Fetch User Data ---
        fetchAndDisplayUserData()

        // --- Set Up Click Listeners ---
        editProfileLayout.setOnClickListener {
            showChangeProfileDialog()
        }
        changePasswordLayout.setOnClickListener {
            showChangePasswordDialog()
        }
        logoutLayout.setOnClickListener {
            logoutUser()
        }
    }

    private fun fetchAndDisplayUserData() {
        if (email != null) {
            db.collection("users").document(email).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        currentName = document.getString("name")
                        currentUsername = document.getString("username")
                        val fetchedEmail = document.getString("email")
                        val contact = document.getString("contact")

                        // --- Handle Random Images ---
                        var profileIndex = document.getLong("profileImageIndex")?.toInt()
                        var backgroundIndex = document.getLong("backgroundImageIndex")?.toInt()
                        var needsUpdate = false

                        if (profileIndex == null || profileIndex !in profilePictures.indices) {
                            profileIndex = Random.nextInt(profilePictures.size)
                            needsUpdate = true
                        }
                        if (backgroundIndex == null || backgroundIndex !in backgroundImages.indices) {
                            backgroundIndex = Random.nextInt(backgroundImages.size)
                            needsUpdate = true
                        }

                        currentProfileImageIndex = profileIndex
                        currentBackgroundImageIndex = backgroundIndex // <-- Store current BG index

                        // Set images
                        context?.let { ctx ->
                            avatarImageView.setImageDrawable(ContextCompat.getDrawable(ctx, profilePictures[currentProfileImageIndex]))
                            headerBackgroundImageView.setImageDrawable(ContextCompat.getDrawable(ctx, backgroundImages[currentBackgroundImageIndex])) // <-- Set BG Image
                        }

                        // Update TextViews
                        profileNameTextView.text = currentName ?: "N/A"
                        userUsernameTextView.text = currentUsername ?: "N/A"
                        userEmailTextView.text = fetchedEmail ?: email
                        userContactTextView.text = contact ?: "N/A"

                        // Save generated indices back to Firestore if they were missing
                        if (needsUpdate) {
                            val initialImageData = mapOf(
                                "profileImageIndex" to profileIndex,
                                "backgroundImageIndex" to backgroundIndex
                            )
                            db.collection("users").document(email)
                                .set(initialImageData, SetOptions.merge())
                                .addOnFailureListener { e ->
                                    println("Error saving initial image indices: ${e.message}")
                                }
                        }

                    } else {
                        Toast.makeText(requireContext(), "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error fetching profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showChangeProfileDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Profile")

        // Inflate custom layout for dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.edit_name)
        val usernameInput = dialogView.findViewById<EditText>(R.id.edit_username)
        val pfpSelectorsLayout = dialogView.findViewById<LinearLayout>(R.id.image_selectors_layout)
        // *** Get reference to background selectors layout ***
        val bgSelectorsLayout = dialogView.findViewById<LinearLayout>(R.id.background_selectors_layout) // <-- Added


        // Pre-fill current data
        nameInput.setText(currentName)
        usernameInput.setText(currentUsername)

        // Keep track of selections within the dialog
        var selectedProfileIndex = currentProfileImageIndex
        var selectedBackgroundIndex = currentBackgroundImageIndex // <-- Added

        // --- Dynamically create Profile Picture Selectors ---
        pfpSelectorsLayout.removeAllViews()
        profilePictures.forEachIndexed { index, drawableResId ->
            val imageView = ShapeableImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(100, 100).apply { marginEnd = 16 }
                setImageResource(drawableResId)
                id = View.generateViewId() // Assign unique ID for selection state
                shapeAppearanceModel = avatarImageView.shapeAppearanceModel
                isSelected = (index == selectedProfileIndex) // Use isSelected state
                // Use a selector drawable for the background/stroke
                setBackgroundResource(R.drawable.pfp_image_selector) // Create this selector
                setOnClickListener {
                    selectedProfileIndex = index
                    updateSelectionState(pfpSelectorsLayout, id) // Update selection visuals
                }
            }
            pfpSelectorsLayout.addView(imageView)
        }

        // --- *** Dynamically create Background Image Selectors *** ---
        bgSelectorsLayout.removeAllViews()
        backgroundImages.forEachIndexed { index, drawableResId ->
            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(180, 100).apply { // Adjust size for BG preview
                    marginEnd = 16
                }
                setImageResource(drawableResId)
                scaleType = ImageView.ScaleType.CENTER_CROP // Crop for preview
                id = View.generateViewId() // Assign unique ID
                isSelected = (index == selectedBackgroundIndex) // Use isSelected state
                setBackgroundResource(R.drawable.background_image_selector) // Use the border selector
                setOnClickListener {
                    selectedBackgroundIndex = index
                    updateSelectionState(bgSelectorsLayout, id) // Update selection visuals
                }
            }
            bgSelectorsLayout.addView(imageView)
        }


        builder.setView(dialogView)

        builder.setPositiveButton("Save") { _, _ ->
            val newName = nameInput.text.toString().trim()
            val newUsername = usernameInput.text.toString().trim()
            var dataChanged = false // Flag to check if anything actually changed

            // Prepare map for updates
            val updatedData = mutableMapOf<String, Any>()

            if (newName.isNotBlank() && newName != currentName) {
                updatedData["name"] = newName
                dataChanged = true
            }
            if (newUsername.isNotBlank() && newUsername != currentUsername) {
                updatedData["username"] = newUsername
                dataChanged = true
            }
            if (selectedProfileIndex != currentProfileImageIndex) {
                updatedData["profileImageIndex"] = selectedProfileIndex
                dataChanged = true
            }
            // *** Check if background image changed ***
            if (selectedBackgroundIndex != currentBackgroundImageIndex) { // <-- Added
                updatedData["backgroundImageIndex"] = selectedBackgroundIndex
                dataChanged = true
            }

            // Proceed only if name/username are not blank and something changed
            if (newName.isNotBlank() && newUsername.isNotBlank() && dataChanged) {
                if (email != null) {
                    db.collection("users").document(email).update(updatedData)
                        .addOnSuccessListener {
                            // Update local cache and UI immediately
                            currentName = newName
                            currentUsername = newUsername
                            profileNameTextView.text = newName
                            userUsernameTextView.text = newUsername

                            if (selectedProfileIndex != currentProfileImageIndex) {
                                currentProfileImageIndex = selectedProfileIndex
                                context?.let { ctx ->
                                    avatarImageView.setImageDrawable(ContextCompat.getDrawable(ctx, profilePictures[currentProfileImageIndex]))
                                }
                            }
                            // *** Update background image view if changed ***
                            if (selectedBackgroundIndex != currentBackgroundImageIndex) { // <-- Added
                                currentBackgroundImageIndex = selectedBackgroundIndex
                                context?.let { ctx ->
                                    headerBackgroundImageView.setImageDrawable(ContextCompat.getDrawable(ctx, backgroundImages[currentBackgroundImageIndex]))
                                }
                            }

                            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else if (!newName.isNotBlank() || !newUsername.isNotBlank()) {
                Toast.makeText(requireContext(), "Name and username cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                // Nothing changed, just dismiss dialog silently or show message
                // Toast.makeText(requireContext(), "No changes made", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.create().show() // Create and show
    }

    // Helper function to update selection state visually
    private fun updateSelectionState(layout: LinearLayout, selectedViewId: Int) {
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            child.isSelected = (child.id == selectedViewId)
        }
    }


    // --- Change Password Dialog (Unchanged logic, slightly better layout) ---
    private fun showChangePasswordDialog() {
        // ... (Keep the existing showChangePasswordDialog code) ...
        if (user == null) {
            Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change Password")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 20) // Add padding

        val passwordInput = EditText(requireContext())
        passwordInput.hint = "Enter new password"
        passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        layout.addView(passwordInput)

        builder.setView(layout)

        builder.setPositiveButton("Update") { _, _ ->
            val newPassword = passwordInput.text.toString().trim()

            // Add password validation (e.g., minimum length)
            if (newPassword.length >= 6) { // Example: Enforce minimum length
                user.updatePassword(newPassword)
                    .addOnCompleteListener { task -> // Use addOnCompleteListener for detailed feedback
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Password updated successfully.", Toast.LENGTH_SHORT).show()
                        } else {
                            // Provide more specific error if possible (e.g., re-authentication needed)
                            Toast.makeText(requireContext(), "Failed to update password: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            // Consider prompting for re-login if update fails due to recent login requirement
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.create().show() // Create and show
    }

    // --- Logout Function (Unchanged) ---
    private fun logoutUser() {
        // ... (Keep the existing logoutUser code) ...
        auth.signOut()
        // Navigate back to Login/Main activity
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
        startActivity(intent)
        activity?.finish() // Close the current activity (HomeActivity)
    }


}