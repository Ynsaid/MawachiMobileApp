package com.example.mawachiapp.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mawachiapp.classes.Product
import com.example.mawachiapp.adapters.ProductAdapter
import com.example.mawachiapp.databinding.ActivityListOfAnimalsBinding

class ListOfAnimalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListOfAnimalsBinding
    private var recyclerView: RecyclerView? = null
    private var adapter: ProductAdapter? = null

    // full list
    private lateinit var fullProductList: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListOfAnimalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button
        binding.backIcon.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        // full list of products
        fullProductList = listOf(
            Product(
                name = "كباش اولاد جلال",
                weight = 60,
                price = 50000.0,
                location = "واد سوف",
                age = "8 أشهر",
                amount = 1,
                imageUrl = "https://as2.ftcdn.net/v2/jpg/03/12/95/13/1000_F_312951336_8LxW7gBLHslTnpbOAwxFo5FpD2R5vGxu.jpg"
            ),
            Product(
                name = "كباش سيدي بلعباس",
                weight = 75,
                price = 62000.0,
                location = "سيدي بلعباس",
                age = "10 أشهر",
                amount = 2,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSJYawnj4_yLWUHoi5uU5dI9lcdwKrUxmpc8g&s"
            ),
            Product(
                name = "خروف من تيارت",
                weight = 55,
                price = 48000.0,
                location = "تيارت",
                age = "7 أشهر",
                amount = 3,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPEQ6YyE83ZRRn2ghe9m2FnfmgYQAftg8aRBlWr6mDp6GVPWoervKug--GgITXA0ZKnBU&usqp=CAU"
            ),
            Product(
                name = "كباش من غرداية",
                weight = 68,
                price = 57000.0,
                location = "غرداية",
                age = "9 أشهر",
                amount = 1,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSFDBH4DBqYZLqBUVd4J6z8Jc-urtJNXoSqmw&s"
            )
        )

        recyclerView = binding.productsLists
        recyclerView?.layoutManager = GridLayoutManager(this, 2)
        adapter = ProductAdapter(fullProductList.toMutableList())
        recyclerView?.adapter = adapter

        // Dropdown filter menu
        binding.dropdownButton.setOnClickListener {
            val popup = PopupMenu(this, binding.dropdownButton)
            popup.menu.add("المكان")
            popup.menu.add("الوزن")
            popup.menu.add("العمر")
            popup.menu.add("السعر")

            popup.setOnMenuItemClickListener { item ->
                val selectedItem = item.title.toString()
                binding.dropdownButton.text = selectedItem
                val filteredList = when (selectedItem) {
                    "المكان" -> fullProductList.sortedBy { it.location }
                    "الوزن" -> fullProductList.sortedByDescending { it.weight }
                    "العمر" -> fullProductList.sortedByDescending { it.age }
                    "السعر" -> fullProductList.sortedByDescending { it.price }
                    else -> fullProductList
            }
                adapter = ProductAdapter(filteredList)
                binding.productsLists.adapter = adapter
                true
            }
            popup.show()
        }

        // Search filter
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val query = text.toString().trim()
                val filteredList = fullProductList.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.location.contains(query, ignoreCase = true) ||
                            it.age.contains(query, ignoreCase = true)
                }
                adapter?.updateList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }
}