package com.example.museraya

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Bind UI elements
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        signupBtn = findViewById(R.id.signup_btn)

        loginBtn.setOnClickListener {
            val email = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        signupBtn.setOnClickListener {
            // Replace MainActivity content with SignUpFragment
            val fragment = SignUpFragment()
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(android.R.id.content, fragment)
            transaction.addToBackStack(null) // Optionally add to back stack
            transaction.commit()
        }
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null && user.email == "museraya123@gmail.com") {
                        Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show()

                        // Navigate to AdminActivity
                        val intent = Intent(this, AdminActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
