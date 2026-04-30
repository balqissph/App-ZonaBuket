package com.ppl3.appzonabuket.api

import com.google.gson.annotations.SerializedName

data class StatusResponse(
    val status: String,
    @SerializedName("order_id")
    val orderId: String?,
    @SerializedName("transaction_status")
    val transactionStatus: String?, // Ini yang berisi: settlement, pending, cancel, dll
    @SerializedName("payment_type")
    val paymentType: String?,
    @SerializedName("va_number")      // TAMBAHAN BARU
    val vaNumber: String?,            // TAMBAHAN BARU
    @SerializedName("expiry_time")    // TAMBAHAN BARU
    val expiryTime: String?,          // TAMBAHAN BARU
    val message: String?
)