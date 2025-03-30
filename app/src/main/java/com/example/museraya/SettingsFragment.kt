package com.example.museraya

import android.app.AlertDialog
import android.content.Intent // Needed for email and logout intents
import android.net.Uri // Needed for email intent URI
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
// import androidx.navigation.fragment.findNavController // Keep if you plan other navigation
import com.example.museraya.databinding.FragmentSettingsBinding // Import generated binding class


class SettingsFragment : Fragment() {

    // Declare binding variable
    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root // Return the root view from binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    
        binding.rowLogout.setOnClickListener {
            Log.d("SettingsFragment", "Logout clicked")
            // Add a confirmation dialog (optional but recommended)
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    // Call the actual logout logic
                    logoutUser()
                }
                .setNegativeButton("Cancel", null) // Just dismisses the dialog
                .show()
        }
        // ***************************

        // Push Notifications Switch
        binding.switchPushNotifications.setOnCheckedChangeListener { _, isChecked ->
            Log.d("SettingsFragment", "Push Notifications Toggled: $isChecked")
            // TODO: Save the preference state (e.g., using SharedPreferences)
            showToast("Push Notifications ${if (isChecked) "Enabled" else "Disabled"}")
        }

        // FAQ Row Click
        binding.rowFaq.setOnClickListener {
            Log.d("SettingsFragment", "FAQ clicked")
            // Example: Show a simple dialog for FAQ (Keep as is or add content later)
            showInfoDialog("FAQ", "Here you would display frequently asked questions.")
        }

        // Tutorial Row Click - Show Lorem Ipsum Dialog
        binding.rowTutorial.setOnClickListener {
            Log.d("SettingsFragment", "Tutorial clicked")
            val tutorialText = """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.

                Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
                Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
            """.trimIndent() // Use trimIndent for cleaner multi-line strings
            showInfoDialog("App Tutorial", tutorialText)
        }

        // Contact Us Row Click - Start Email Intent
        binding.rowContactUs.setOnClickListener {
            Log.d("SettingsFragment", "Contact Us clicked")
            val email = "Museraya123@gmail.com"
            val mailto = "mailto:${email}" // No subject needed for direct email link usually

            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(mailto)

            }

            try {
                // Verify that an email app exists before starting the intent
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


        try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val version = pInfo.versionName
            binding.textAppVersion.text = "App version $version"
        } catch (e: Exception) {
            binding.textAppVersion.text = "App version ?"
        }
        binding.textDeveloper.text = "Developed by Museraya Team"
    }


    private fun showInfoDialog(title: String, message: String) {

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }



    private fun logoutUser() {

        val activity = requireActivity()


        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}