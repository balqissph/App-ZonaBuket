package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ProdukActivity : AppCompatActivity() {

    lateinit var recyclerManajemenProduk: RecyclerView
    lateinit var adapter: ManajemenProdukAdapter

    var selectedImageRes = R.drawable.tambah_gambar
    val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_produk)

        recyclerManajemenProduk = findViewById(R.id.recyclerManajemenProduk)
        val btnTambah = findViewById<MaterialButton>(R.id.btnTambahProduk)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        recyclerManajemenProduk.layoutManager = GridLayoutManager(this, 4)

        adapter = ManajemenProdukAdapter(productList) { product, position ->
            showTambahProdukPopup(product, position)
        }

        recyclerManajemenProduk.adapter = adapter

        btnTambah.setOnClickListener {
            showTambahProdukPopup()
        }
    }

    // POPUP TAMBAH PRODUK + EDIT
    private fun showTambahProdukPopup(product: Product? = null, position: Int = -1) {

        val view = LayoutInflater.from(this).inflate(R.layout.popup_tambah_produk, null)

        val imgProduct = view.findViewById<ImageView>(R.id.ivProductImage)
        val etName = view.findViewById<EditText>(R.id.etProductName)
        val etPrice = view.findViewById<EditText>(R.id.etProductPrice)
        val etDesc = view.findViewById<EditText>(R.id.etProductDesc)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveProduct)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // MODE EDIT
        if (product != null) {
            etName.setText(product.name)
            etPrice.setText(product.price.toString())
            etDesc.setText(product.description)
            imgProduct.setImageResource(product.image)
            selectedImageRes = product.image
        }

        // PILIH GAMBAR
        imgProduct.setOnClickListener {
            selectedImageRes = R.drawable.buket4
            imgProduct.setImageResource(selectedImageRes)
            imgProduct.scaleType = ImageView.ScaleType.CENTER_CROP

            Toast.makeText(this, "Gambar dipilih", Toast.LENGTH_SHORT).show()
        }

        // TOMBOL SIMPAN
        btnSave.setOnClickListener {

            val nama = etName.text.toString()
            val harga = etPrice.text.toString().toIntOrNull() ?: 0
            val desc = etDesc.text.toString()

            if (nama.isEmpty()) {
                Toast.makeText(this, "Nama produk harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (product == null) {
                // TAMBAH PRODUK
                val produkBaru = Product(
                    nama,
                    harga,
                    selectedImageRes,
                    desc
                )

                productList.add(produkBaru)
                adapter.notifyItemInserted(productList.size - 1)

                Toast.makeText(this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()

            } else {
                // EDIT PRODUK
                val updatedProduct = Product(
                    nama,
                    harga,
                    selectedImageRes,
                    desc
                )

                productList[position] = updatedProduct
                adapter.notifyItemChanged(position)

                Toast.makeText(this, "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }
    }
}