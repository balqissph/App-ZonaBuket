package com.ppl3.appzonabuket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(
    private val items: MutableList<Product>,
    private val onTotalChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgCartProduct)
        val name: TextView = view.findViewById(R.id.tvCartName)
        val price: TextView = view.findViewById(R.id.tvCartPrice)
        val qty: TextView = view.findViewById(R.id.tvQty)
        val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = items[position]

        // Menggunakan Glide untuk memuat gambar dari Resource ID
        Glide.with(holder.itemView.context)
            .load(product.image)
            .placeholder(android.R.color.darker_gray)
            .into(holder.img)

        holder.name.text = product.name
        holder.price.text = "Rp. ${product.price}"
        holder.qty.text = product.qty.toString()

        // Logika Tombol Tambah (+)
        holder.btnPlus.setOnClickListener {
            product.qty++
            notifyItemChanged(holder.adapterPosition)
            onTotalChanged()
        }

        // Logika Tombol Kurang (-)
        holder.btnMinus.setOnClickListener {
            val currentPos = holder.adapterPosition
            if (product.qty > 1) {
                product.qty--
                notifyItemChanged(currentPos)
            } else {
                // Menghapus item jika jumlah jadi 0
                items.removeAt(currentPos)
                notifyItemRemoved(currentPos)
                notifyItemRangeChanged(currentPos, items.size)
            }
            onTotalChanged()
        }
    }

    override fun getItemCount(): Int = items.size
}