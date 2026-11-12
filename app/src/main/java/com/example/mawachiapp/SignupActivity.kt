package com.example.mawachiapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mawachiapp.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class SignupActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signupBtn.setOnClickListener {
            val name = binding.nameEdttxt.text.toString().trim()
            val email = binding.emailEdttxt.text.toString().trim()
            val number = binding.numberEdttxt.text.toString().trim()
            val password = binding.pswEdttxt.text.toString()
            val passwordr = binding.pswrEdttxt.text.toString()

            // Validation
            if (email.isEmpty() || password.isEmpty() || passwordr.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passwordr) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(email, password)
        }
        binding.loginAccBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val db = Firebase.firestore
                    val userMap = hashMapOf(
                        "name" to binding.nameEdttxt.text.toString(),
                        "email" to binding.emailEdttxt.text.toString(),
                        "number" to binding.numberEdttxt.text.toString()
                    )
                    db.collection("users").document(uid ?: "").set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                            // Redirect to home or login
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save user info: ${e.message}", Toast.LENGTH_SHORT).show()
                        }


                } else {
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
