package com.example.mawachiapp.classes

import java.io.Serializable

data class Item(
    val name: String? = null,
    val price: String? = null,
    var quantity: String? = null,
    val location: String? = null,
    val weight: String? = null,
    val age: String? = null,
    val amount: String? = null,
    val image: String? = null
)  : Serializable