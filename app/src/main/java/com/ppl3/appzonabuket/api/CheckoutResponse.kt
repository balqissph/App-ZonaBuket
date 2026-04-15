package com.ppl3.appzonabuket.api

import com.google.gson.annotations.SerializedName

data class CheckoutResponse(
    val status: String,
    @SerializedName("order_id")
    val orderId: String?,
    @SerializedName("snap_token")
    val snapToken: String?,
    val message: String?
)