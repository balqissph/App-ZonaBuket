package com.ppl3.appzonabuket

object CartManager {

    val cartItems = mutableListOf<Product>()

    fun addToCart(product: Product) {

        val existing = cartItems.find { it.name == product.name }

        if (existing != null) {
            existing.qty++
        } else {
            cartItems.add(product.copy())
        }
    }

}