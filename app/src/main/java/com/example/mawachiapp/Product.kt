package com.example.mawachiapp

import java.io.Serializable

data class Product(
    val name:String ,
    val weight :Int,
    val price :Double,
    val location :String ,
    val age :String,
    val amount:Int,
    val imageUrl : String

) : Serializable