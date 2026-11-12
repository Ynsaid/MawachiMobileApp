package com.example.mawachiapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private val cartItems: List<Item>) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemAgeWeightLocation: TextView = itemView.findViewById(R.id.itemAgeWeightLocation)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        val itemQuantity: TextView = itemView.findViewById(R.id.quantityTxt)
        val itemImg: ImageView = itemView.findViewById(R.id.itemImg)
        val itemQuantityPlus :AppCompatButton = itemView.findViewById(R.id.plusBtn)
        val itemQuantityMinus :AppCompatButton = itemView.findViewById(R.id.minusBtn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_bg, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cartItems[position]

        holder.itemName.text = item.name
        holder.itemAgeWeightLocation.text = "${item.location} - ${item.weight} - ${item.age}"
        holder.itemPrice.text = item.price
        holder.itemQuantity.text = item.quantity

        holder.itemQuantityPlus.setOnClickListener {
            val currentQty = item.quantity.toIntOrNull() ?: 1
            val updatedQty = currentQty + 1
            item.quantity = updatedQty.toString() // Make sure quantity is a var in your model
            holder.itemQuantity.text = item.quantity
        }

        // Handle - button
        holder.itemQuantityMinus.setOnClickListener {
            val currentQty = item.quantity.toIntOrNull() ?: 1
            val updatedQty = if (currentQty > 1) currentQty - 1 else 1
            item.quantity = updatedQty.toString()
            holder.itemQuantity.text = item.quantity
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
