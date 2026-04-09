package com.ppl3.appzonabuket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class AdminAdapter(
    // Ubah menjadi MutableList agar item bisa dihapus
    private val listAdmin: MutableList<Admin>,
    private val onEditClick: (Admin) -> Unit,
    // Tambahkan parameter untuk aksi hapus
    private val onDeleteClick: (Admin, Int) -> Unit
) : RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

    class AdminViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNamaAdmin)
        val btnEdit: MaterialButton = view.findViewById(R.id.btnEditAdmin)
        val btnHapus: MaterialButton = view.findViewById(R.id.btnHapusAdmin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val admin = listAdmin[position]
        holder.tvNama.text = admin.nama

        holder.btnEdit.setOnClickListener { onEditClick(admin) }

        // Logika Hapus, mengirimkan data admin dan posisi (index)-nya
        holder.btnHapus.setOnClickListener {
            onDeleteClick(admin, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = listAdmin.size
}