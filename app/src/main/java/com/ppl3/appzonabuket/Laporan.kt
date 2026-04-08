package com.ppl3.appzonabuket

data class Laporan(
    val timestamp: String,
    val namaProduk: String,
    val harga: String,
    val jumlah: Int,
    val total: String,
    val pembayaran: String
)