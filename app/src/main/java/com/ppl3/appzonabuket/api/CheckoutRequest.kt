package com.ppl3.appzonabuket.api

import com.google.gson.annotations.SerializedName

data class CheckoutRequest(
    @SerializedName("total_harga")
    val totalHarga: Int,
    @SerializedName("order_id")
    val orderId: String
)