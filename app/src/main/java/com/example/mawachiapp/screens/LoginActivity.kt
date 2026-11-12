package com.example.mawachiapp.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mawachiapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.createAccBtn.setOnClickListener {
            goToRegister()
        }
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }
        binding.loginBtn.setOnClickListener{
            val email  = binding.emailEdttxt.text.toString()
            val password = binding.passwordEdttxt.text.toString()
            if(email != null && password != null){
                loginUser(email,password);
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                    }

            }
    }

    private fun goToRegister(){
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }
}