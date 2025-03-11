package com.example.museraya


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Button

import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.CollectionReference

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var etFullName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etContactNumber: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize the EditText fields
        etFullName = view.findViewById(R.id.etFullName)
        etUsername = view.findViewById(R.id.etUsername)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        etContactNumber = view.findViewById(R.id.etContactNumber)

        val signUpButton = view.findViewById<Button>(R.id.btnSignUp)
        signUpButton.setOnClickListener {
            signUpUser()
        }

        return view
    }

    private fun signUpUser() {
        val fullName = etFullName.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val contactNumber = etContactNumber.text.toString().trim()

        // Validate input
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
            TextUtils.isEmpty(password) || TextUtils.isEmpty(contactNumber)
        ) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Create user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // User is successfully registered
                    val user = auth.currentUser
                    if (user != null) {
                        saveUserDataToFirestore(user)
                    }
                } else {
                    // If sign up fails, display a message to the user
                    val exception = task.exception
                    handleSignUpError(exception)
                }
            }
    }

    private fun saveUserDataToFirestore(user: FirebaseUser) {
        val userMap = hashMapOf(
            "name" to etFullName.text.toString().trim(),
            "username" to etUsername.text.toString().trim(),
            "email" to user.email.toString(),
            "password" to etPassword.text.toString().trim(), // You might want to avoid storing the password in Firestore
            "contact" to etContactNumber.text.toString().trim()
        )

        // Save user data to Firestore under the "users" collection with email as document ID
        val userRef: CollectionReference = firestore.collection("users")
        userRef.document(user.email.toString())  // Using email as document ID
            .set(userMap)
            .addOnSuccessListener {
                Toast.makeText(context, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                navigateToHomeActivity()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleSignUpError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> {
                Toast.makeText(context, "This email is already registered", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthException -> {
                Toast.makeText(context, "Sign Up failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "An unknown error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHomeActivity() {
        // Display success toast
        Toast.makeText(context, "Sign Up Successful", Toast.LENGTH_SHORT).show()

        // Navigate to HomeActivity
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Close the current activity or fragment
    }
}
