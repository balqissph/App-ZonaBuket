package com.ppl3.appzonabuket

data class Laporan(
    val idPesanan: String,
    val timestamp: String,
    val namaProduk: String,
    val notes: String?,
    val harga: String,
    val jumlah: String,
    val total: String,
    val pembayaran: String,
    val admin: String,
    val status: String // TAMBAHKAN INI: "Menunggu Pembayaran" atau "Lunas"
)