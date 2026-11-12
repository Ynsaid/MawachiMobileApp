package com.example.mawachiapp.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mawachiapp.fragments.Cart
import com.example.mawachiapp.fragments.Home
import com.example.mawachiapp.R
import com.example.mawachiapp.fragments.Settings
import com.example.mawachiapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // set Home fragment as default
        loadFragment(Home())
        // set bottom navigation
        binding.bottomNavigation.selectedItemId = R.id.home_page
        // set bottom navigation item selected listener
        binding.bottomNavigation.setOnItemSelectedListener{ item ->
            when(item.itemId) {
                R.id.home_page -> {
                    loadFragment(Home())
                    true
                }
                R.id.cart_page -> {
                    loadFragment(Cart())
                    true
                }
                R.id.settings_page -> {
                    loadFragment(Settings())
                    true
                }
                else -> { false }
            }
        }
    }
    private fun loadFragment(fragment: Fragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
    }
}