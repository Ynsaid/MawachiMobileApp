package com.example.mawachiapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mawachiapp.classes.Animal
import com.example.mawachiapp.screens.ListOfAnimalsActivity
import com.example.mawachiapp.R

class AnimalAdapter(private val list: List<Animal>) :
    RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {
    class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.animal_name)
        val image: ImageView? = itemView.findViewById(R.id.animal_img)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
            val animal = list[position]
        holder.name?.text = animal.name
        holder.image?.let { Glide.with(holder.itemView.context).load(animal.image).into(it) }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ListOfAnimalsActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = list.size
}