package com.ppl3.appzonabuket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(private val items: List<Product>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgCartProduct)
        val name = view.findViewById<TextView>(R.id.tvCartName)
        val price = view.findViewById<TextView>(R.id.tvCartPrice)
        val qty = view.findViewById<TextView>(R.id.tvQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val product = items[position]

        Glide.with(holder.itemView.context)
            .load(product.image)
            .placeholder(android.R.color.darker_gray) // warna abu-abu pas loading
            .into(holder.img)
        holder.name.text = product.name
        holder.price.text = "Rp. ${product.price}"
        holder.qty.text = product.qty.toString()
    }

    override fun getItemCount(): Int = items.size
}