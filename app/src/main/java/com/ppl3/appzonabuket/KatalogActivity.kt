package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.google.firebase.firestore.FirebaseFirestore

class KatalogActivity : AppCompatActivity() {

    // Deklarasi variabel global
    lateinit var drawerLayout: DrawerLayout
    private lateinit var db: FirebaseFirestore
    private val productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_katalog)

        // Inisialisasi View Bawaan
        val recyclerKatalog = findViewById<RecyclerView>(R.id.recyclerKatalog)
        val tabRekomendasi = findViewById<TextView>(R.id.tabRekomendasi)
        val btnCart = findViewById<ImageView>(R.id.btnCart)

        // Inisialisasi Drawer dan Tombol Menu
        drawerLayout = findViewById(R.id.drawerLayout)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)

        // Inisialisasi View dari Sidebar (Menu Samping)
        val menuProfile = findViewById<LinearLayout>(R.id.menuProfile)
        val menuLaporan = findViewById<LinearLayout>(R.id.menuLaporan)
        val menuManajemen = findViewById<LinearLayout>(R.id.menuManajemen)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        // ==========================================
        // LOGIKA SIDEBAR & MENU
        // ==========================================

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this@KatalogActivity, ProfileActivity::class.java))
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
                showPinDialog(ProdukActivity::class.java)
            }, 250)
        }

        btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this@KatalogActivity)
            builder.setTitle("Konfirmasi Logout")
            builder.setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")

            builder.setPositiveButton("Iya") { dialog, which ->
                Toast.makeText(this@KatalogActivity, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@KatalogActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Batal") { dialog, which ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        // ==========================================
        // KODE RECYCLERVIEW & FIREBASE FIRESTORE
        // ==========================================

        recyclerKatalog.layoutManager = GridLayoutManager(this, 4)

        adapter = ProductAdapter(productList)
        recyclerKatalog.adapter = adapter

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance()

        db.collection("produk")
            .get()
            .addOnSuccessListener { result ->
                productList.clear()

                for (document in result) {
                    val nama = document.getString("nama") ?: ""
                    val harga = document.getLong("harga")?.toInt() ?: 0
                    val deskripsi = document.getString("deskripsi") ?: ""

                    // Ambil nama file gambar dari Firestore (misal: "buket1")
                    val namaGambar = document.getString("gambar") ?: "buket1"

                    // Ubah nama file teks tersebut menjadi Resource ID (Int) bawaan drawable
                    var gambarId = resources.getIdentifier(namaGambar, "drawable", packageName)

                    // Kalau namanya salah ketik atau tidak ditemukan, beri gambar default
                    if (gambarId == 0) {
                        gambarId = R.drawable.buket1
                    }

                    // Menyesuaikan dengan parameter di Product.kt kamu
                    productList.add(Product(name = nama, price = harga, image = gambarId, description = deskripsi))
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@KatalogActivity, "Gagal mengambil data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // ==========================================
        // KLIK TAB & TOMBOL LAINNYA
        // ==========================================

        tabRekomendasi.setOnClickListener {
            val intent = Intent(this@KatalogActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this@KatalogActivity, KeranjangActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainKatalog)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // --- FUNGSI UNTUK MENAMPILKAN POPUP PIN CUSTOM KEYPAD ---
    private fun showPinDialog(targetActivity: Class<*>) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_pin, null)
        val tvPinIndicator = dialogView.findViewById<TextView>(R.id.tvPinIndicator)
        val btnDelete = dialogView.findViewById<ImageButton>(R.id.btnDelete)

        val builder = AlertDialog.Builder(this@KatalogActivity)
        builder.setView(dialogView)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var enteredPin = ""
        val correctPin = "123456"

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
                                Toast.makeText(this@KatalogActivity, "Akses Diberikan", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                startActivity(Intent(this@KatalogActivity, targetActivity))
                            } else {
                                Toast.makeText(this@KatalogActivity, "PIN Salah!", Toast.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}