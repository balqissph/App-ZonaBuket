package com.ppl3.appzonabuket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class WaitingPayment(
    val idDokumen: String, // Pastikan ini adalah Order ID (misal: ZONA-12345) yang dikirim ke Midtrans
    val waktu: String,
    val metode: String,
    val noVa: String,
    val total: String,
    val expiredDate: String
)

class WaitingPaymentAdapter(
    private val list: List<WaitingPayment>,
    // 1. Ubah nama dari onLunasKlik menjadi onCekStatusKlik agar lebih sesuai fungsinya
    private val onCekStatusKlik: (String) -> Unit
) : RecyclerView.Adapter<WaitingPaymentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId: TextView = itemView.findViewById(R.id.tvWpId)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWpWaktu)
        val tvMetodeVa: TextView = itemView.findViewById(R.id.tvWpMetodeVa)
        val tvTotal: TextView = itemView.findViewById(R.id.tvWpTotal)
        val tvExpiredDate: TextView = itemView.findViewById(R.id.tvWpExpiredDate)
        // 2. ID tombol tetap sama (asumsi di XML kamu belum diubah)
        val btnCekStatus: Button = itemView.findViewById(R.id.btnKonfirmasiLunas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_waiting_payment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pesanan = list[position]

        holder.tvId.text = "ID: ${pesanan.idDokumen}"
        holder.tvWaktu.text = pesanan.waktu
        holder.tvMetodeVa.text = "${pesanan.metode} - VA: ${pesanan.noVa}"
        holder.tvTotal.text = "Total: Rp.${pesanan.total}"
        holder.tvExpiredDate.text = "Batas Waktu: ${pesanan.expiredDate}"

        // 3. Saat diklik, panggil fungsi untuk mengecek status
        holder.btnCekStatus.setOnClickListener {
            // Kita juga bisa ubah teks tombolnya sementara saat diklik agar ada efek loading
            holder.btnCekStatus.text = "Mengecek..."
            holder.btnCekStatus.isEnabled = false

            onCekStatusKlik(pesanan.idDokumen)

            // Kembalikan tombol seperti semula setelah 2 detik
            holder.btnCekStatus.postDelayed({
                holder.btnCekStatus.text = "Cek Status"
                holder.btnCekStatus.isEnabled = true
            }, 2000)
        }
    }

    override fun getItemCount() = list.size
}