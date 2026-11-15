package com.example.mawachiapp.classes

import java.io.Serializable

data class Product(
    val name: String = "",
    val weight: Int = 0,
    val price: Double = 0.0,
    val location: String = "",
    val age: String = "",
    val amount: Int = 0,
    val imageUrl: String = "",
    val category: String = ""
) : Serializable