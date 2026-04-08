package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ProdukActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
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

        drawerLayout = findViewById(R.id.drawerLayout)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)

        val menuProfile = findViewById<LinearLayout>(R.id.menuProfile)
        val menuLaporan = findViewById<LinearLayout>(R.id.menuLaporan)
        val menuManajemen = findViewById<LinearLayout>(R.id.menuManajemen)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        // SIDEBAR
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        menuProfile.setOnClickListener {
            Toast.makeText(this, "Membuka Profile...", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuLaporan.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            showPinPopup(LaporanActivity::class.java)
        }

        menuManajemen.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            showPinPopup(ProdukActivity::class.java)
        }

        // LOGOUT
        btnLogout.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Iya") { _, _ ->

                    Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                    finish()

                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // TOMBOL TAMBAH PRODUK
        btnTambah.setOnClickListener {
            showTambahProdukPopup()
        }

        // RECYCLERVIEW
        recyclerManajemenProduk.layoutManager = GridLayoutManager(this, 4)

        adapter = ManajemenProdukAdapter(productList)
        recyclerManajemenProduk.adapter = adapter

        // EDGE TO EDGE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainProduk)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // BACK BUTTON
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })
    }

    // POPUP PIN
    private fun showPinPopup(targetActivity: Class<*>) {

        val view = LayoutInflater.from(this).inflate(R.layout.popup_pin, null)

        val tvPin = view.findViewById<TextView>(R.id.tvPinIndicator)

        val btn1 = view.findViewById<TextView>(R.id.btn1)
        val btn2 = view.findViewById<TextView>(R.id.btn2)
        val btn3 = view.findViewById<TextView>(R.id.btn3)
        val btn4 = view.findViewById<TextView>(R.id.btn4)
        val btn5 = view.findViewById<TextView>(R.id.btn5)
        val btn6 = view.findViewById<TextView>(R.id.btn6)
        val btn7 = view.findViewById<TextView>(R.id.btn7)
        val btn8 = view.findViewById<TextView>(R.id.btn8)
        val btn9 = view.findViewById<TextView>(R.id.btn9)
        val btn0 = view.findViewById<TextView>(R.id.btn0)

        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var pin = ""
        val correctPin = "112233"

        fun updatePin() {
            tvPin.text = "●".repeat(pin.length)
        }

        fun addNumber(number: String) {

            if (pin.length < 6) {
                pin += number
                updatePin()
            }

            if (pin.length == 6) {

                if (pin == correctPin) {

                    Toast.makeText(this, "PIN Benar", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                    val intent = Intent(this, targetActivity)
                    startActivity(intent)

                } else {

                    Toast.makeText(this, "PIN Salah", Toast.LENGTH_SHORT).show()
                    pin = ""
                    updatePin()

                }
            }
        }

        btn1.setOnClickListener { addNumber("1") }
        btn2.setOnClickListener { addNumber("2") }
        btn3.setOnClickListener { addNumber("3") }
        btn4.setOnClickListener { addNumber("4") }
        btn5.setOnClickListener { addNumber("5") }
        btn6.setOnClickListener { addNumber("6") }
        btn7.setOnClickListener { addNumber("7") }
        btn8.setOnClickListener { addNumber("8") }
        btn9.setOnClickListener { addNumber("9") }
        btn0.setOnClickListener { addNumber("0") }

        btnDelete.setOnClickListener {
            if (pin.isNotEmpty()) {
                pin = pin.dropLast(1)
                updatePin()
            }
        }
    }


    // POPUP TAMBAH PRODUK
    private fun showTambahProdukPopup() {

        val view = LayoutInflater.from(this)
            .inflate(R.layout.popup_tambah_produk, null)

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

        // klik gambar
        imgProduct.setOnClickListener {

            // sementara pakai gambar default dulu
            selectedImageRes = R.drawable.buket1

            imgProduct.setImageResource(selectedImageRes)

            Toast.makeText(this, "Gambar dipilih", Toast.LENGTH_SHORT).show()
        }

        // tombol simpan
        btnSave.setOnClickListener {

            val nama = etName.text.toString()
            val harga = etPrice.text.toString().toIntOrNull() ?: 0
            val desc = etDesc.text.toString()

            if (nama.isEmpty()) {
                Toast.makeText(this, "Nama produk harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val produkBaru = Product(
                nama,
                harga,
                selectedImageRes,
                desc
            )

            productList.add(produkBaru)

            adapter.notifyItemInserted(productList.size - 1)

            Toast.makeText(this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }
    }
}