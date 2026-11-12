package com.example.mawachiapp

import java.io.Serializable

class Item (
    val name : String,
    val price : String,
    var quantity : String,
    val location :String,
    val weight :String,
    val age :String,
    val amount :String,
    val image : String

) : Serializable