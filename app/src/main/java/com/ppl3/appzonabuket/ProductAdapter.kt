package com.ppl3.appzonabuket

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class ProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        val txtName: TextView = view.findViewById(R.id.tvProductName)
        val txtPrice: TextView = view.findViewById(R.id.tvPrice)
        val btnAdd: ImageView = view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produk, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        val context = holder.itemView.context

        holder.txtName.text = product.name
        holder.txtPrice.text = "Rp ${product.price}"

        // Memuat gambar menggunakan Glide dari URL Firebase
        Glide.with(context)
            .load(product.image)
            .placeholder(android.R.color.darker_gray) // warna abu-abu saat gambar masih loading
            .into(holder.imgProduct)

        // Tombol (+) tambah ke keranjang (yang ada di layout item_product luar)
        holder.btnAdd.setOnClickListener {
            CartManager.addToCart(product)
            Toast.makeText(context, "Produk ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
        }

        // KETIKA KESELURUHAN ITEM DIKLIK -> TAMPILKAN POPUP DETAIL
        holder.itemView.setOnClickListener {
            tampilkanPopupProduk(context, product)
        }
    }

    // FUNGSI UNTUK MENAMPILKAN POPUP DETAIL
    private fun tampilkanPopupProduk(context: Context, produk: Product) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Memanggil file layout dialog yang sudah kita buat sebelumnya
        dialog.setContentView(R.layout.popup_detail_produk)

        // Membuat background dialog menjadi transparan agar ujung CardView terlihat membulat
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // 1. Inisialisasi komponen dari layout popup
        val tvTitle = dialog.findViewById<TextView>(R.id.tvProductTitle)
        val tvPrice = dialog.findViewById<TextView>(R.id.tvProductPrice)
        val tvDesc = dialog.findViewById<TextView>(R.id.tvProductDesc)
        val ivImage = dialog.findViewById<ImageView>(R.id.ivProductImage)
        val btnAddToCartPopup = dialog.findViewById<MaterialButton>(R.id.btnAddToCart)

        // 2. Set Data produk ke dalam popup
        tvTitle.text = produk.name
        tvDesc.text = produk.description
        tvPrice.text = "Rp ${produk.price}"

        // Load Gambar di Popup pakai Glide
        Glide.with(context)
            .load(produk.image)
            .placeholder(android.R.color.darker_gray)
            .into(ivImage)

        // 3. Aksi Tombol "tambahkan ke keranjang" yang ada di dalam POPUP
        btnAddToCartPopup.setOnClickListener {
            // Memasukkan produk ke keranjang menggunakan CartManager
            CartManager.addToCart(produk)

            Toast.makeText(context, "${produk.name} ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // Menutup popup setelah produk ditambah ke keranjang
        }

        // Tampilkan dialognya
        dialog.show()
    }
}