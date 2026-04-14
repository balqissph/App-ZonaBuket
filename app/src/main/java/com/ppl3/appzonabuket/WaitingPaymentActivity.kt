package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class WaitingPaymentActivity : AppCompatActivity() {

    private lateinit var adapter: WaitingPaymentAdapter
    private val waitingList = mutableListOf<WaitingPayment>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_payment)

        val btnBack = findViewById<ImageView>(R.id.btnBackWP)
        val recycler = findViewById<RecyclerView>(R.id.recyclerWaiting)

        btnBack.setOnClickListener {
            // Kembali ke Beranda
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        recycler.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter dengan aksi tombol Lunas
        adapter = WaitingPaymentAdapter(waitingList) { idDokumen ->
            tampilkanDialogKonfirmasi(idDokumen)
        }
        recycler.adapter = adapter

        ambilDataAntrean()
    }

    private fun ambilDataAntrean() {
        // Hanya ambil yang statusnya Menunggu Pembayaran
        db.collection("pesanan")
            .whereEqualTo("status", "Menunggu Pembayaran")
            .get()
            .addOnSuccessListener { result ->
                waitingList.clear()

                // Sortir manual dari yang terbaru
                val sortedResult = result.documents.sortedByDescending {
                    it.getTimestamp("tanggal_pesanan")
                }

                for (doc in sortedResult) {
                    val idDokumen = doc.id

                    val date = doc.getTimestamp("tanggal_pesanan")?.toDate()
                    val format = SimpleDateFormat("HH:mm / d-M-yyyy", Locale.getDefault())
                    val timestampStr = date?.let { format.format(it) } ?: "-"

                    val metode = doc.getString("metode_pembayaran") ?: "-"
                    val noVa = doc.getString("nomor_va") ?: "-"
                    val total = doc.getLong("total_harga") ?: 0

                    waitingList.add(
                        WaitingPayment(idDokumen, timestampStr, metode, noVa, total.toString())
                    )
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun tampilkanDialogKonfirmasi(idDokumen: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Pembayaran")
        builder.setMessage("Apakah pelanggan sudah membayar tagihan ini?")

        builder.setPositiveButton("Sudah (Lunas)") { dialog, _ ->
            updateStatusKeLunas(idDokumen)
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun updateStatusKeLunas(idDokumen: String) {
        // Update field status di Firebase
        db.collection("pesanan").document(idDokumen)
            .update("status", "Lunas")
            .addOnSuccessListener {
                Toast.makeText(this, "Pembayaran Berhasil Dikonfirmasi!", Toast.LENGTH_SHORT).show()
                // Refresh list
                ambilDataAntrean()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal konfirmasi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}