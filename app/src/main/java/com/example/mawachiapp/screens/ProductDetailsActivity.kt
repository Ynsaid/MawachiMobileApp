package com.example.mawachiapp.screens

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.bumptech.glide.Glide
import com.example.mawachiapp.classes.Item
import com.example.mawachiapp.classes.Product
import com.example.mawachiapp.databinding.ActivityProductDetailsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private var count = 1
    private var currentProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentProduct = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("product", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("product") as? Product
        }

        currentProduct?.let { product ->
            binding.productName.text = product.name
            binding.price.text = "${product.price} د.ج"
            binding.locationText.text = product.location
            binding.weightText.text = "${product.weight} كغ"
            binding.ageText.text = product.age
            binding.amountText.text = "${product.amount} رأس"
            if (product.imageUrl.isNotEmpty()) {
                Glide.with(this).load(product.imageUrl).into(binding.productImage)
            }
        }

        binding.quantityText.text = count.toString()

        binding.minusButton.setOnClickListener {
            if (count > 1) {
                count--
                binding.quantityText.text = count.toString()
            }
        }
        binding.plusButton.setOnClickListener {
            count++
            binding.quantityText.text = count.toString()
        }
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.addToCartBtn.setOnClickListener {
            val name = binding.productName.text.toString()
            val price = binding.price.text.toString()
            val quantity = binding.quantityText.text.toString()
            val location = binding.locationText.text.toString()
            val weight = binding.weightText.text.toString()
            val age = binding.ageText.text.toString()
            val amount = binding.amountText.text.toString()
            val image = currentProduct?.imageUrl ?: ""

            val item = Item(name, price, quantity, location, weight, age, amount, image)

            val sharedPreferences = getSharedPreferences("cart", MODE_PRIVATE)
            sharedPreferences.edit {
                val gson = Gson()
                val cartList = getCartList(sharedPreferences)
                cartList.add(item)
                val json = gson.toJson(cartList)
                putString("cart_items", json)
            }
            Toast.makeText(this, "تمت الإضافة إلى السلة", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCartList(sharedPreferences: SharedPreferences): MutableList<Item> {
        val gson = Gson()
        val json = sharedPreferences.getString("cart_items", null)
        val type = object : TypeToken<MutableList<Item>>() {}.type
        return if (json != null) gson.fromJson(json, type) else mutableListOf()
    }
}