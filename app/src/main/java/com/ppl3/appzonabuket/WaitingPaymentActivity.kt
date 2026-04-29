package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import com.ppl3.appzonabuket.api.ApiClient
import com.ppl3.appzonabuket.api.StatusResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WaitingPaymentActivity : AppCompatActivity() {
    private lateinit var adapter: WaitingPaymentAdapter
    private val waitingList = mutableListOf<WaitingPayment>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_payment)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val recycler = findViewById<RecyclerView>(R.id.recyclerWaiting)

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        recycler.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter, arahkan klik tombol ke fungsi pengecekan Retrofit
        adapter = WaitingPaymentAdapter(waitingList) { idDokumen ->
            cekStatusPembayaranMidtrans(idDokumen)
        }
        recycler.adapter = adapter

        ambilDataAntrean()
    }

    private fun ambilDataAntrean() {
        db.collection("pesanan")
            .whereEqualTo("status", "Menunggu Pembayaran")
            .get()
            .addOnSuccessListener { result ->
                waitingList.clear()

                val sortedResult = result.documents.sortedByDescending {
                    it.getTimestamp("tanggal_pesanan")
                }

                for (doc in sortedResult) {
                    val idDokumen = doc.id // Asumsi: ID Firebase ini SAMA dengan Order ID di Midtrans

                    val date = doc.getTimestamp("tanggal_pesanan")?.toDate()
                    val format = SimpleDateFormat("HH:mm / d-M-yyyy", Locale.getDefault())
                    val timestampStr = date?.let { format.format(it) } ?: "-"

                    val metode = doc.getString("metode_pembayaran") ?: "-"
                    val noVa = doc.getString("nomor_va") ?: "-"
                    val total = doc.getLong("total_harga") ?: 0
                    val expiredDate = doc.getString("expired_date") ?: "-"

                    waitingList.add(
                        WaitingPayment(idDokumen, timestampStr, metode, noVa, total.toString(),expiredDate)
                    )
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Mengecek status langsung ke Server Laravel -> Midtrans
    private fun cekStatusPembayaranMidtrans(orderId: String) {
        Toast.makeText(this, "Mengecek ke server...", Toast.LENGTH_SHORT).show()

        ApiClient.instance.checkPaymentStatus(orderId).enqueue(object : Callback<StatusResponse> {
            override fun onResponse(call: Call<StatusResponse>, response: Response<StatusResponse>) {
                if (response.isSuccessful) {
                    val statusMidtrans = response.body()?.transactionStatus

                    when (statusMidtrans) {
                        "settlement", "capture" -> {
                            // Pembayaran sukses/lunas
                            updateStatusKeLunas(orderId)
                        }
                        "pending" -> {
                            // Masih belum dibayar
                            Toast.makeText(this@WaitingPaymentActivity, "Pelanggan belum mentransfer dana.", Toast.LENGTH_LONG).show()
                        }
                        "expire", "cancel", "deny" -> {
                            // Pembayaran kadaluarsa atau dibatalkan
                            updateStatusKeBatal(orderId)
                        }
                        else -> {
                            Toast.makeText(this@WaitingPaymentActivity, "Status: $statusMidtrans", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorCode = response.code()
                    Toast.makeText(this@WaitingPaymentActivity, "Pesanan tidak ditemukan di Midtrans (Error $errorCode)", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                Toast.makeText(this@WaitingPaymentActivity, "Koneksi ke server gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateStatusKeLunas(idDokumen: String) {
        db.collection("pesanan").document(idDokumen)
            .update("status", "Lunas")
            .addOnSuccessListener {
                Toast.makeText(this, "Pesanan Lunas! Memperbarui antrean...", Toast.LENGTH_SHORT).show()
                ambilDataAntrean() // Refresh list otomatis
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal update database: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Fungsi Tambahan: Menangani pesanan yang batal/kadaluarsa
    private fun updateStatusKeBatal(idDokumen: String) {
        db.collection("pesanan").document(idDokumen)
            .update("status", "Batal")
            .addOnSuccessListener {
                Toast.makeText(this, "Waktu pembayaran habis / Dibatalkan.", Toast.LENGTH_SHORT).show()
                ambilDataAntrean() // Refresh list otomatis agar hilang dari antrean
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal update database: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}