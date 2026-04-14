package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class LaporanActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout

    private lateinit var adapter: LaporanAdapter
    private val laporanList = mutableListOf<Laporan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        val recyclerLaporan = findViewById<RecyclerView>(R.id.recyclerLaporan)
        val btnSavePDF = findViewById<MaterialButton>(R.id.btnSavePDF)

        drawerLayout = findViewById(R.id.drawerLayout)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        recyclerLaporan.layoutManager = LinearLayoutManager(this)

        adapter = LaporanAdapter(laporanList)
        recyclerLaporan.adapter = adapter

        ambilDataDariFirebase()

        btnSavePDF.setOnClickListener {
            Toast.makeText(
                this,
                "Laporan berhasil disimpan sebagai PDF",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // --- FUNGSI DIPERBARUI: FILTER HANYA STATUS "Lunas" ---
    private fun ambilDataDariFirebase() {
        val db = FirebaseFirestore.getInstance()

        // Ambil data pesanan yang HANYA berstatus "Lunas"
        db.collection("pesanan")
            .whereEqualTo("status", "Lunas")
            .get()
            .addOnSuccessListener { resultPesanan ->
                laporanList.clear()

                // Sortir secara manual dari yang terbaru agar tidak error di index Firebase
                val sortedPesanan = resultPesanan.documents.sortedByDescending {
                    it.getTimestamp("tanggal_pesanan")
                }

                for (docPesanan in sortedPesanan) {
                    val idPesanan = docPesanan.id

                    val date = docPesanan.getTimestamp("tanggal_pesanan")?.toDate()
                    val format = SimpleDateFormat("HH:mm / d-M-yyyy", Locale.getDefault())
                    val timestampStr = date?.let { format.format(it) } ?: "-"

                    val pembayaran = docPesanan.getString("metode_pembayaran") ?: "-"
                    val notes = docPesanan.getString("catatan") ?: ""
                    val totalHargaStruk = docPesanan.getLong("total_harga") ?: 0

                    val notesFinal = if (notes.isBlank()) null else notes

                    // Ambil detail produk berdasarkan ID Pesanan tersebut
                    db.collection("detail_pesanan")
                        .whereEqualTo("id_pesanan", idPesanan)
                        .get()
                        .addOnSuccessListener { resultDetail ->

                            // Siapkan list kosong untuk menampung gabungan teks
                            val listNama = mutableListOf<String>()
                            val listHarga = mutableListOf<String>()
                            val listJumlah = mutableListOf<String>()

                            for (docDetail in resultDetail) {
                                val namaProduk = docDetail.getString("nama_produk") ?: "-"
                                val hargaSatuan = docDetail.getLong("harga_satuan") ?: 0
                                val jumlah = docDetail.getLong("jumlah") ?: 0

                                // Masukkan ke dalam list penampung
                                listNama.add(namaProduk)
                                listHarga.add("Rp.$hargaSatuan")
                                listJumlah.add(jumlah.toString()) // Jadikan string agar bisa digabung
                            }

                            // Masukkan ke Data Class Laporan
                            val laporanItem = Laporan(
                                idPesanan = "ID: ${idPesanan.take(8)}",
                                timestamp = timestampStr,
                                namaProduk = listNama.joinToString("\n"),
                                notes = notesFinal,
                                harga = listHarga.joinToString("\n"),
                                jumlah = listJumlah.joinToString("\n"),
                                total = "Rp.$totalHargaStruk",
                                pembayaran = pembayaran,
                                admin = "Admin 1",
                                status = "Lunas" // <-- Wajib ditambahkan agar sesuai dengan Data Class Laporan.kt yang baru
                            )

                            laporanList.add(laporanItem)
                            adapter.notifyDataSetChanged()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengambil laporan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPinDialog(targetActivity: Class<*>) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_pin, null)
        val tvPinIndicator = dialogView.findViewById<TextView>(R.id.tvPinIndicator)
        val btnDelete = dialogView.findViewById<ImageButton>(R.id.btnDelete)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var enteredPin = ""
        val correctPin = "123456"

        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (id in numberButtons) {
            dialogView.findViewById<TextView>(id).setOnClickListener { view ->
                if (enteredPin.length < 6) {
                    val number = (view as TextView).text.toString()
                    enteredPin += number
                    tvPinIndicator.text = "●".repeat(enteredPin.length)

                    if (enteredPin.length == 6) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (enteredPin == correctPin) {
                                Toast.makeText(this, "Akses Diberikan", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                startActivity(Intent(this, targetActivity))
                            } else {
                                Toast.makeText(this, "PIN Salah!", Toast.LENGTH_SHORT).show()
                                enteredPin = ""
                                tvPinIndicator.text = ""
                            }
                        }, 200)
                    }
                }
            }
        }

        btnDelete.setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin = enteredPin.dropLast(1)
                tvPinIndicator.text = "●".repeat(enteredPin.length)
            }
        }

        dialog.show()
    }
}