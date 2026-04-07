package com.ppl3.appzonabuket

data class Product(
    val name: String = "",
    val price: Int = 0,
    val image: Int = 0, // <-- KEMBALIKAN JADI Int SEMENTARA WAKTU
    val description: String = "",
    var qty: Int = 1
)