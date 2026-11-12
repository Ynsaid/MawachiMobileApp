package com.example.mawachiapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mawachiapp.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var animalList: ArrayList<Animal>
    private lateinit var filteredList: ArrayList<Animal>
    private lateinit var adapter: AnimalAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Init lists and adapter
        animalList = ArrayList()
        filteredList = ArrayList()
        adapter = AnimalAdapter(filteredList)

        // RecyclerView setup
        binding.listOfItems.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.listOfItems.adapter = adapter

        // Search listener
        binding.searchEdttxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                filterAnimals(text.toString().trim())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        fetchAnimals()

        return binding.root
    }

    private fun fetchAnimals() {
        db.collection("animals")
            .get()
            .addOnSuccessListener { result ->
                animalList.clear()
                for (document in result) {
                    val animal = document.toObject(Animal::class.java)
                    animalList.add(animal)
                }
                filterAnimals("") // Show all by default
            }
            .addOnFailureListener {
                // Handle errors if needed
            }
    }

    private fun filterAnimals(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(animalList)
        } else {
            for (animal in animalList) {
                if (animal.name?.contains(query, ignoreCase = true) == true) {
                    filteredList.add(animal)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}
