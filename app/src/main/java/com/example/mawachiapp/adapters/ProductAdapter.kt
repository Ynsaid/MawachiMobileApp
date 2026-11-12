package com.example.mawachiapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mawachiapp.classes.Product
import com.example.mawachiapp.screens.ProductDetailsActivity
import com.example.mawachiapp.R

class ProductAdapter(private var products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.sheepName)
        val price = itemView.findViewById<TextView>(R.id.sheepPrice)
        val image = itemView.findViewById<ImageView>(R.id.sheepImage)
        val location = itemView.findViewById<TextView>(R.id.sheepLocation)
        val weight = itemView.findViewById<TextView>(R.id.sheepWeight)
        val age = itemView.findViewById<TextView>(R.id.sheepAge)
        val amount = itemView.findViewById<TextView>(R.id.sheepAmount)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
            return ProductViewHolder(view)
    }

    override fun getItemCount(): Int  = products.size
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val product = products[position]
            holder.name.text = product.name
            holder.price.text = "${product.price} دج"
            holder.amount.text = "${product.amount}رأس "
            holder.location.text = product.location
            holder.weight.text = "${product.weight} كغ"
            holder.age.text = product.age
            Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailsActivity::class.java)
            intent.putExtra("product", products[holder.adapterPosition]) // or product if passed in bind()
            holder.itemView.context.startActivity(intent)
        }

    }
    fun updateList(newList: List<Product>) {
        products = newList.toMutableList()
        notifyDataSetChanged()
    }
}