package com.example.mawachiapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mawachiapp.classes.Item
import com.example.mawachiapp.adapters.CartAdapter
import com.example.mawachiapp.databinding.FragmentCartBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class Cart : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var adapter: CartAdapter
    private val cartItems = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout and initialize binding
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        binding.itemsList.layoutManager = LinearLayoutManager(requireContext())
        adapter = CartAdapter(cartItems)
        binding.itemsList.adapter = adapter


        loadCartItems()
        // ✅ Now check cart state AFTER loading
        if (cartItems.isEmpty()) {
            binding.buyNowBtn.visibility = View.GONE
            binding.totalPriceTxt.visibility = View.GONE
            binding.totalTxt.visibility = View.GONE
            binding.line.visibility = View.GONE
            binding.emptyCartTxt.visibility = View.VISIBLE
        } else {
            binding.buyNowBtn.visibility = View.VISIBLE
            binding.totalPriceTxt.visibility = View.VISIBLE
            binding.totalTxt.visibility = View.VISIBLE
            binding.line.visibility = View.VISIBLE
            binding.emptyCartTxt.visibility = View.GONE
        }
        attachSwipeToDelete()


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadCartItems() {
        binding.loadingSpinner.visibility = View.VISIBLE

        val sharedPreferences = requireContext().getSharedPreferences("cart", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("cart_items", null)
        val type = object : TypeToken<List<Item>>() {}.type
        val savedItems: List<Item> = gson.fromJson(json, type) ?: emptyList()

        cartItems.clear()
        cartItems.addAll(savedItems)
        adapter.notifyDataSetChanged()

        updateCartVisibility()

        // Hide spinner after loading
        binding.loadingSpinner.visibility = View.GONE
    }

    // Attach swipe-to-delete functionality
    private fun attachSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition


                // Remove from local list
                cartItems.removeAt(position)
                adapter.notifyItemRemoved(position)

                // Save updated list to SharedPreferences
                val sharedPreferences = requireContext().getSharedPreferences("cart", Context.MODE_PRIVATE)
                sharedPreferences.edit {
                    val gson = Gson()
                    val updatedJson = gson.toJson(cartItems)
                    putString("cart_items", updatedJson)
                }

                // Refresh UI (spinner optional)
                updateCartVisibility()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.itemsList)
    }
    private fun updateCartVisibility() {
        if (cartItems.isEmpty()) {
            binding.buyNowBtn.visibility = View.GONE
            binding.totalPriceTxt.visibility = View.GONE
            binding.totalTxt.visibility = View.GONE
            binding.line.visibility = View.GONE
            binding.emptyCartTxt.visibility = View.VISIBLE
        } else {
            binding.buyNowBtn.visibility = View.VISIBLE
            binding.totalPriceTxt.visibility = View.VISIBLE
            binding.line.visibility = View.VISIBLE
            binding.totalTxt.visibility = View.VISIBLE
            binding.emptyCartTxt.visibility = View.GONE
        }
    }

}