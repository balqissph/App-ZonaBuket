package com.ppl3.appzonabuket

data class Product(
    val name: String,
    val price: Int,
    val image: Int,
    var qty: Int = 1
)