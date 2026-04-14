package com.ppl3.appzonabuket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Data Class khusus untuk halaman Antrean
data class WaitingPayment(
    val idDokumen: String,
    val waktu: String,
    val metode: String,
    val noVa: String,
    val total: String
)

class WaitingPaymentAdapter(
    private val list: List<WaitingPayment>,
    private val onLunasKlik: (String) -> Unit // Fungsi callback ketika tombol diklik
) : RecyclerView.Adapter<WaitingPaymentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId: TextView = itemView.findViewById(R.id.tvWpId)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWpWaktu)
        val tvMetodeVa: TextView = itemView.findViewById(R.id.tvWpMetodeVa)
        val tvTotal: TextView = itemView.findViewById(R.id.tvWpTotal)
        val btnLunas: Button = itemView.findViewById(R.id.btnKonfirmasiLunas)
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

        // Aksi ketika tombol Lunas diklik
        holder.btnLunas.setOnClickListener {
            onLunasKlik(pesanan.idDokumen)
        }
    }

    override fun getItemCount() = list.size
}