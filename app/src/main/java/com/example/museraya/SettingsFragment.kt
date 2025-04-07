package com.example.museraya

import android.app.AlertDialog
import android.content.ActivityNotFoundException // Import for exception handling
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider // Needed for FileProvider
import androidx.fragment.app.Fragment
import com.example.museraya.databinding.FragmentSettingsBinding
import java.io.File // Needed for File operations
import java.io.FileOutputStream // Needed for writing file
import java.io.IOException // Needed for IO exceptions


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Define the PDF filename (must match the file in assets)
    private val TUTORIAL_PDF_FILENAME = "tutorial.pdf" // <<< CHANGE THIS if your filename is different

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Logout Row ---
        binding.rowLogout.setOnClickListener {
            Log.d("SettingsFragment", "Logout clicked")
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    logoutUser()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // --- Push Notifications Switch ---
        binding.switchPushNotifications.setOnCheckedChangeListener { _, isChecked ->
            Log.d("SettingsFragment", "Push Notifications Toggled: $isChecked")
            // TODO: Save the preference state (e.g., using SharedPreferences)
            showToast("Push Notifications ${if (isChecked) "Enabled" else "Disabled"}")
        }

        // --- FAQ Row Click --- (Updated)
        binding.rowFaq.setOnClickListener {
            Log.d("SettingsFragment", "FAQ clicked")
            // Show "No FAQs yet" message
            showInfoDialog("FAQ", "No FAQs yet.") // <<< CHANGED MESSAGE
        }

        // --- Tutorial Row Click --- (Updated to open PDF)
        binding.rowTutorial.setOnClickListener {
            Log.d("SettingsFragment", "Tutorial clicked")
            openPdfFromAssets(TUTORIAL_PDF_FILENAME) // <<< CALL PDF OPENER
        }

        // --- Contact Us Row Click --- (Keep as is)
        binding.rowContactUs.setOnClickListener {
            Log.d("SettingsFragment", "Contact Us clicked")
            val email = "Museraya123@gmail.com"
            val mailto = "mailto:${email}"

            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(mailto)
            }

            try {
                if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(emailIntent)
                } else {
                    showToast("No email application found.")
                }
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error starting email intent", e)
                showToast("Could not open email application.")
            }
        }

        // --- Terms & Privacy Row Click --- (Keep as is)
        binding.rowTermsPrivacy.setOnClickListener {
            Log.d("SettingsFragment", "Terms & Privacy clicked")

            val termsAndPolicyText = """
                Terms of Service

                Last Updated: March 30, 2025

                Welcome to Museraya. These Terms of Service govern your use of the App, provided by Us. By using the App, you agree to these Terms. If you do not agree, please do not use the App.

                1. Use of the App
                You must be at least 13 years old to use the App.
                The App provides an augmented reality (AR) museum experience, allowing users to view 3D models of vintage artifacts.
                You agree to use the App only for lawful purposes and in compliance with all applicable laws.

                2. Intellectual Property
                All content in the App, including 3D models, images, and designs, are owned by our licensors.
                You may not copy, modify, distribute, or sell any content from the App without our permission.

                3. Limitation of Liability
                We provide the App "as is" without warranties of any kind.
                We are not responsible for any damages, data loss, or issues arising from the use of the App.

                4. Changes to the Terms
                We may update these Terms at any time. Continued use of the App after changes constitutes acceptance of the new Terms.

                5. Contact
                For questions about these Terms, contact us at museraya123@gmail.com.


                Privacy Policy

                Last Updated: March 30, 2025

                Museraya respects your privacy. This Privacy Policy explains how we collect, use, and protect your information.

                1. Information We Collect
                Usage Data: We may collect anonymized data on how you interact with the App.
                Device Information: We may collect information such as device type and operating system.
                AR Data: The App uses ARCore to detect surfaces for placing 3D models. This data is not stored or shared.

                2. How We Use Your Information
                To improve the App and user experience.
                To troubleshoot and prevent technical issues.
                We do not sell or share your personal data with third parties.

                3. Data Security
                We implement security measures to protect your data, but we cannot guarantee complete security.

                4. Your Rights
                You can request to delete any personal data we have collected (if applicable).

                5. Changes to This Policy
                We may update this Privacy Policy. Continued use of the App after changes means you accept the updated Policy.

                6. Contact
                For privacy-related inquiries, contact us at museraya123@gmail.com.
            """.trimIndent()


            showInfoDialog("Terms & Privacy", termsAndPolicyText)
        }

        // --- App Version & Developer Info --- (Keep as is)
        try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val version = pInfo.versionName
            binding.textAppVersion.text = "App version $version"
        } catch (e: Exception) {
            binding.textAppVersion.text = "App version ?"
            Log.e("SettingsFragment", "Error getting app version", e) // Added logging
        }
        binding.textDeveloper.text = "Developed by Museraya Team"
    }

    // --- Helper Function to Open PDF from Assets --- (New)
    private fun openPdfFromAssets(assetFileName: String) {
        val context = requireContext()
        val file = File(context.cacheDir, assetFileName) // Create file in cache directory

        // Check if file already exists in cache, if not, copy from assets
        if (!file.exists()) {
            try {
                context.assets.open(assetFileName).use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Log.d("SettingsFragment", "Copied $assetFileName from assets to cache.")
            } catch (e: IOException) {
                Log.e("SettingsFragment", "Error copying $assetFileName from assets", e)
                showToast("Error loading tutorial. File might be missing.")
                return // Stop if copy failed
            }
        } else {
            Log.d("SettingsFragment", "$assetFileName already exists in cache.")
        }

        // Get URI using FileProvider
        val uri: Uri? = try {
            // Ensure authority matches the one in AndroidManifest.xml
            val authority = "${context.packageName}.provider"
            FileProvider.getUriForFile(context, authority, file)
        } catch (e: IllegalArgumentException) {
            Log.e("SettingsFragment", "Error getting URI via FileProvider. Check authority configuration.", e)
            showToast("Error preparing tutorial file.")
            null
        }

        if (uri == null) return // Stop if URI is null

        // Create Intent to view PDF
        val pdfIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant temporary read permission
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) // Optional: PDF viewer doesn't stay in back stack
        }

        try {
            // Verify that an app exists to handle the PDF intent
            if (pdfIntent.resolveActivity(context.packageManager) != null) {
                startActivity(pdfIntent)
            } else {
                showToast("No application found to open PDF files.")
            }
        } catch (e: ActivityNotFoundException) {
            Log.e("SettingsFragment", "ActivityNotFoundException for PDF Intent", e)
            showToast("No application found to open PDF files.")
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error starting PDF intent", e)
            showToast("Could not open tutorial PDF.")
        }
    }

    // --- Existing Helper Functions ---
    private fun showInfoDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showToast(message: String) {
        // Ensure context is available before showing toast
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun logoutUser() {
        val activity = requireActivity()
        val intent = Intent(activity, MainActivity::class.java) // Assuming MainActivity is your login/entry screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish() // Close the current activity (containing SettingsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up binding
    }
}