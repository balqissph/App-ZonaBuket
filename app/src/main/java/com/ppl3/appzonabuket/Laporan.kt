package com.ppl3.appzonabuket

data class Laporan(
    val timestamp: String,
    val namaProduk: String,
    val notes: String?,      // tambahan untuk catatan
    val harga: String,
    val jumlah: Int,
    val total: String,
    val pembayaran: String,
    val admin: String
)