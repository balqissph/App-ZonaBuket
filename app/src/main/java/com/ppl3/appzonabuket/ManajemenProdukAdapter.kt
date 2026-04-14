package com.ppl3.appzonabuket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ManajemenProdukAdapter(
    private val productList: MutableList<Product>,
    private val onItemClick: (Product, Int) -> Unit
) : RecyclerView.Adapter<ManajemenProdukAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvProductDesc: TextView = itemView.findViewById(R.id.tvProductDesc)
        val btnSampah: ImageButton = itemView.findViewById(R.id.btnSampah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manajemen_produk, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val product = productList[position]

        holder.imgProduct.setImageResource(product.image)
        holder.tvProductName.text = product.name
        holder.tvPrice.text = "Rp ${product.price}"
        holder.tvProductDesc.text = product.description

        // KLIK ITEM UNTUK EDIT
        holder.itemView.setOnClickListener {
            onItemClick(product, position)
        }

        // TOMBOL HAPUS
        holder.btnSampah.setOnClickListener {

            productList.removeAt(position)

            notifyItemRemoved(position)
            notifyItemRangeChanged(position, productList.size)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}