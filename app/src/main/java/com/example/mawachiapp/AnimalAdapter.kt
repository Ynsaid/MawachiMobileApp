package com.example.mawachiapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AnimalAdapter(private val list: List<Animal>) :
    RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {
    class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.animal_name)
        val image = itemView.findViewById<ImageView>(R.id.animal_img)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnimalAdapter.AnimalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalAdapter.AnimalViewHolder, position: Int) {
            val animal = list[position]
        holder.name.text = animal.name
        Glide.with(holder.itemView.context).load(animal.image).into(holder.image)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,ListOfAnimalsActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = list.size
}