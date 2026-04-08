package com.ppl3.appzonabuket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LaporanAdapter(
    private val laporanList: List<Laporan>
) : RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        val tvNamaProduk: TextView = itemView.findViewById(R.id.tvNamaProduk)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvJumlah: TextView = itemView.findViewById(R.id.tvJumlah)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        val tvPembayaran: TextView = itemView.findViewById(R.id.tvPembayaran)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_laporan, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val laporan = laporanList[position]

        holder.tvTimestamp.text = laporan.timestamp
        holder.tvNamaProduk.text = laporan.namaProduk
        holder.tvHarga.text = laporan.harga
        holder.tvJumlah.text = laporan.jumlah.toString()
        holder.tvTotal.text = laporan.total
        holder.tvPembayaran.text = laporan.pembayaran

    }

    override fun getItemCount(): Int {
        return laporanList.size
    }
}