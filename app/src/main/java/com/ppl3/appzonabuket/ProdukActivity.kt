package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

        // --- SIDEBAR ---
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, ProfileActivity::class.java))
            }, 250)
        }

        menuLaporan.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                showPinDialog(LaporanActivity::class.java)
            }, 250)
        }

        menuManajemen.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                // Karena ini sudah di ProdukActivity, kamu bisa sesuaikan targetnya
                // Tapi untuk konsistensi, tetap pakai showPinDialog
                showPinDialog(ProdukActivity::class.java)
            }, 250)
        }

        // --- LOGOUT ---
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Iya") { _, _ ->
                    Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // --- TOMBOL TAMBAH PRODUK ---
        btnTambah.setOnClickListener {
            showTambahProdukPopup()
        }

        // --- RECYCLERVIEW ---
        recyclerManajemenProduk.layoutManager = GridLayoutManager(this, 4)

        adapter = ManajemenProdukAdapter(productList)
        recyclerManajemenProduk.adapter = adapter

        // --- EDGE TO EDGE ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainProduk)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- BACK BUTTON ---
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

    // --- FUNGSI UNTUK MENAMPILKAN POPUP PIN CUSTOM KEYPAD ---
    private fun showPinDialog(targetActivity: Class<*>) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_pin, null)
        val tvPinIndicator = dialogView.findViewById<TextView>(R.id.tvPinIndicator)
        val btnDelete = dialogView.findViewById<ImageButton>(R.id.btnDelete)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        // Membuat background dialog menjadi transparan
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var enteredPin = ""
        val correctPin = "123456" // Ganti PIN di sini

        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (id in numberButtons) {
            dialogView.findViewById<TextView>(id).setOnClickListener { view ->
                if (enteredPin.length < 6) {
                    val number = (view as TextView).text.toString()
                    enteredPin += number

                    tvPinIndicator.text = "●".repeat(enteredPin.length)

                    if (enteredPin.length == 6) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (enteredPin == correctPin) {
                                Toast.makeText(this, "Akses Diberikan", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                startActivity(Intent(this, targetActivity))
                            } else {
                                Toast.makeText(this, "PIN Salah!", Toast.LENGTH_SHORT).show()
                                enteredPin = ""
                                tvPinIndicator.text = ""
                            }
                        }, 200)
                    }
                }
            }
        }

        btnDelete.setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin = enteredPin.dropLast(1)
                tvPinIndicator.text = "●".repeat(enteredPin.length)
            }
        }

        dialog.show()
    }

    // --- POPUP TAMBAH PRODUK ---
    private fun showTambahProdukPopup() {
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