package com.ppl3.appzonabuket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class AdminAdapter(
    private val listAdmin: List<Admin>,
    private val onEditClick: (Admin) -> Unit) :
    RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

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

        holder.btnEdit.setOnClickListener {onEditClick(admin)}
        holder.btnHapus.setOnClickListener { /* Logika Hapus */ }
    }

    override fun getItemCount(): Int = listAdmin.size
}