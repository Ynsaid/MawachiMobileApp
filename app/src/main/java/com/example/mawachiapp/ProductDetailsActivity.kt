package com.example.mawachiapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mawachiapp.databinding.ActivityProductDetailsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProductDetailsBinding
    private var count = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.minusButton.setOnClickListener {
            if(count>0){
                count--;
                binding.quantityText.text = count.toString()

            }
        }
        binding.plusButton.setOnClickListener{
            count++;
            binding.quantityText.text = count.toString()
        }
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.addToCartBtn.setOnClickListener {
            // get values of the fields
            val name = binding.productName.text.toString()
            val price = binding.price.text.toString()
            val quantity = binding.quantityText.text.toString()
            val location = binding.locationText.text.toString()
            val weight = binding.weightText.text.toString()
            val age = binding.ageText.text.toString()
            val amount = binding.amountText.text.toString()
            val image = binding.productImage.toString()
            // set up the item object
            val item = Item(name, price, quantity, location, weight, age, amount, image)
            // add the item to the cart
            val sharedPreferences = getSharedPreferences("cart", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            // get the current cart list
            val cartList = getCartList(sharedPreferences)
            cartList.add(item)
            val json = gson.toJson(cartList)
            editor.putString("cart_items", json)
            editor.apply()
        }

    }

    private fun getCartList(sharedPreferences: SharedPreferences): MutableList<Item> {
        val gson = Gson()
        val json = sharedPreferences.getString("cart_items", null)
        val type = object : TypeToken<MutableList<Item>>() {}.type
        return if (json != null) gson.fromJson(json, type) else mutableListOf()
    }

}