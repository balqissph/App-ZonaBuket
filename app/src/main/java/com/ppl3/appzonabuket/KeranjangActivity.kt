package com.ppl3.appzonabuket

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class KeranjangActivity : AppCompatActivity() {

    lateinit var recyclerCart: RecyclerView
    lateinit var adapter: CartAdapter
    lateinit var tabRekomendasi: TextView
    lateinit var tabKatalog: TextView

    // Variabel untuk menyimpan metode yang sedang dipilih
    private var metodeTerpilih: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_keranjang)

        recyclerCart = findViewById(R.id.recyclerCart)
        tabRekomendasi = findViewById(R.id.tabRekomendasi)
        tabKatalog = findViewById(R.id.tabKatalog)
        val btnCheckout = findViewById<Button>(R.id.btnCheckout)

        // Inisialisasi Card Metode Pembayaran
        val btnQris = findViewById<MaterialCardView>(R.id.btnQris)
        val btnBca = findViewById<MaterialCardView>(R.id.btnBca)
        val btnMandiri = findViewById<MaterialCardView>(R.id.btnMandiri)
        val btnTunai = findViewById<MaterialCardView>(R.id.btnTunai)

        // Masukkan ke dalam list untuk mempermudah perubahan warna
        val listMetode = listOf(btnQris, btnBca, btnMandiri, btnTunai)

        // --- LOGIKA PILIH METODE (Ubah warna kartu saat diklik) ---
        btnQris.setOnClickListener {
            metodeTerpilih = "QRIS"
            updateWarnaSeleksi(btnQris, listMetode)
        }
        btnBca.setOnClickListener {
            metodeTerpilih = "BCA"
            updateWarnaSeleksi(btnBca, listMetode)
        }
        btnMandiri.setOnClickListener {
            metodeTerpilih = "MANDIRI"
            updateWarnaSeleksi(btnMandiri, listMetode)
        }
        btnTunai.setOnClickListener {
            metodeTerpilih = "TUNAI"
            updateWarnaSeleksi(btnTunai, listMetode)
        }

        // --- LOGIKA CHECKOUT (Munculkan Popup) ---
        btnCheckout.setOnClickListener {
            if (metodeTerpilih != null) {
                prosesPembayaran(metodeTerpilih!!)
            } else {
                // Beri peringatan jika user belum memilih metode pembayaran
                Toast.makeText(this, "Pilih metode pembayaran dulu ya!", Toast.LENGTH_SHORT).show()
            }
        }

        // --- SETUP RECYCLER VIEW & NAVIGASI ---
        recyclerCart.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(CartManager.cartItems)
        recyclerCart.adapter = adapter

        tabRekomendasi.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        tabKatalog.setOnClickListener {
            startActivity(Intent(this, KatalogActivity::class.java))
        }
    }

    // Fungsi untuk mengubah warna latar kartu yang dipilih menjadi abu-abu
    private fun updateWarnaSeleksi(selected: MaterialCardView, allCards: List<MaterialCardView>) {
        for (card in allCards) {
            if (card == selected) {
                card.setCardBackgroundColor(Color.parseColor("#D3D3D3")) // Warna abu-abu saat dipilih
            } else {
                card.setCardBackgroundColor(Color.parseColor("#F4F4F4")) // Warna putih keabuan (default)
            }
        }
    }

    // Fungsi untuk menampilkan popup dan menjalankan timer animasi
    private fun prosesPembayaran(metode: String) {
        val dialogView = layoutInflater.inflate(R.layout.pembayaran, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()

        // Buat background bawaan dialog transparan agar sudut melengkung CardView terlihat
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgPayment = dialogView.findViewById<ImageView>(R.id.imgPayment)
        val layoutVa = dialogView.findViewById<LinearLayout>(R.id.layoutVa)
        val tvVaNumber = dialogView.findViewById<TextView>(R.id.tvVaNumber)

        // 1. Atur tampilan awal popup berdasarkan metode yang dipilih di Checkout
        when (metode) {
            "QRIS" -> {
                imgPayment.setImageResource(R.drawable.qr_code) // Pastikan nama gambar sesuai dengan di drawable-mu
                imgPayment.visibility = View.VISIBLE
                layoutVa.visibility = View.GONE
            }
            "BCA", "MANDIRI" -> {
                imgPayment.visibility = View.GONE
                layoutVa.visibility = View.VISIBLE
                tvVaNumber.text = if (metode == "BCA") "6289 7262 9198 5526 82" else "1370 0123 4567 8900"
            }
            "TUNAI" -> {
                imgPayment.setImageResource(R.drawable.done) // Langsung tampilkan centang
                imgPayment.visibility = View.VISIBLE
                layoutVa.visibility = View.GONE
            }
        }

        dialog.show()

        // 2. Jalankan Logika Timer menggunakan Handler
        val handler = Handler(Looper.getMainLooper())

        if (metode == "TUNAI") {
            // Jika Tunai: Tunggu 5 detik lalu langsung tutup
            handler.postDelayed({
                if (dialog.isShowing) {
                    dialog.dismiss()
                    Toast.makeText(this, "Pesanan Berhasil!", Toast.LENGTH_SHORT).show()
                }
            }, 3000)
        } else {
            // Jika QRIS/BCA/Mandiri: Tunggu 5 detik, lalu ganti tampilan menjadi centang hijau
            handler.postDelayed({
                if (dialog.isShowing) {
                    layoutVa.visibility = View.GONE
                    imgPayment.visibility = View.VISIBLE
                    imgPayment.setImageResource(R.drawable.done)
                }
            }, 3000)

            // Tunggu 5 detik lagi setelah centang muncul (total 10 detik dari awal), lalu tutup
            handler.postDelayed({
                if (dialog.isShowing) {
                    dialog.dismiss()
                    Toast.makeText(this, "Pembayaran Berhasil!", Toast.LENGTH_SHORT).show()
                }
            }, 8000)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}