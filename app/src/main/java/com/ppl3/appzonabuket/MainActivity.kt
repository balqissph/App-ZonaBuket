package com.ppl3.appzonabuket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerProduct: RecyclerView
    private lateinit var tabKatalog: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var db: FirebaseFirestore
    private val productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 1. Inisialisasi View Utama
        recyclerProduct = findViewById(R.id.recyclerProduk)
        tabKatalog = findViewById(R.id.tabKatalog)
        drawerLayout = findViewById(R.id.drawerLayout)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnCart = findViewById<ImageView>(R.id.btnCart)

        // 2. Setup Sidebar
        setupSidebar()

        // 3. Logika Klik Navigasi Sidebar
        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        // 4. Setup RecyclerView
        recyclerProduct.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = ProductAdapter(productList)
        recyclerProduct.adapter = adapter

        // 5. Ambil Data Firestore
        db = FirebaseFirestore.getInstance()
        fetchDataFromFirestore()

        // 6. Navigasi Antar Tab
        tabKatalog.setOnClickListener {
            startActivity(Intent(this, KatalogActivity::class.java))
            finish()
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, KeranjangActivity::class.java))
        }

        // 7. Handle Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSidebar() {
        // Inisialisasi Menu Sidebar
        val menuProfile = findViewById<LinearLayout>(R.id.menuProfile)
        val menuLaporan = findViewById<LinearLayout>(R.id.menuLaporan)
        val menuManajemen = findViewById<LinearLayout>(R.id.menuManajemen)
        val menuWaitingPayment = findViewById<LinearLayout>(R.id.menuWaitingPayment)
        val menuManajemenAdmin = findViewById<LinearLayout>(R.id.menuManajemenAdmin)
        val menuResetPin = findViewById<LinearLayout>(R.id.menuResetPin)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        // Logika Role & Visibilitas
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userRole = sharedPref.getString("role", "admin")

        // Menu yang KHUSUS untuk Owner
        if (userRole == "owner") {
            menuManajemenAdmin.visibility = View.VISIBLE
            menuResetPin.visibility = View.VISIBLE
        } else {
            menuManajemenAdmin.visibility = View.GONE
            menuResetPin.visibility = View.GONE
        }

        menuWaitingPayment.visibility = View.VISIBLE

        // Klik Menu Profil
        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, ProfileActivity::class.java))
            }, 250)
        }

        // Klik Menu Laporan (Dengan PIN Global)
        menuLaporan.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            ambilPinFirebase { pinGlobal ->
                showPinDialog("Masukan PIN Anda", pinGlobal) {
                    startActivity(Intent(this, LaporanActivity::class.java))
                }
            }
        }

        // Klik Menu Manajemen (Dengan PIN Global)
        menuManajemen.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            ambilPinFirebase { pinGlobal ->
                showPinDialog("Masukan PIN Anda", pinGlobal) {
                    startActivity(Intent(this, ProdukActivity::class.java))
                }
            }
        }

        // Klik Menu Waiting Payment
        menuWaitingPayment.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, WaitingPaymentActivity::class.java))
            }, 250)
        }

        // Klik Menu Manajemen Admin
        menuManajemenAdmin.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, ManageAdminActivity::class.java))
            }, 250)
        }

        // Klik Menu Reset PIN (Terkoneksi ke Firebase)
        menuResetPin.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)

            ambilPinFirebase { pinGlobal ->
                showPinDialog("Masukkan PIN Lama", pinGlobal) { _ ->
                    showPinDialog("Masukkan PIN Baru", null) { pinBaru ->
                        showPinDialog("Konfirmasi PIN Baru", pinBaru) { pinKonfirmasi ->

                            val db = FirebaseFirestore.getInstance()
                            val dataPin = hashMapOf("app_pin" to pinKonfirmasi)

                            db.collection("settings").document("security").set(dataPin)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "PIN berhasil diubah untuk semua perangkat!", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Gagal mengubah PIN.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }
        }

        // Klik Logout
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Iya") { _, _ ->
                    sharedPref.edit().clear().apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun ambilPinFirebase(onSuccess: (String) -> Unit) {
        val firestoreDb = FirebaseFirestore.getInstance()
        firestoreDb.collection("settings").document("security").get()
            .addOnSuccessListener { document ->
                val pin = document.getString("app_pin") ?: "123456"
                onSuccess(pin)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menghubungi server untuk cek PIN", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchDataFromFirestore() {
        db.collection("produk")
            .get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (document in result) {
                    val nama = document.getString("nama_produk") ?: "Produk Tanpa Nama"
                    val harga = document.getLong("harga")?.toInt() ?: 0
                    val deskripsi = document.getString("deskripsi") ?: ""
                    val namaGambar = document.getString("gambar")?.trim() ?: "buket1"
                    var gambarId = resources.getIdentifier(namaGambar, "drawable", packageName)
                    if (gambarId == 0) gambarId = R.drawable.buket1
                    productList.add(Product(nama, harga, gambarId, deskripsi))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPinDialog(customTitle: String, expectedPin: String?, onSuccess: (String) -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_pin, null)

        val tvTitlePin = dialogView.findViewById<TextView>(R.id.tvTitlePin)
        if (tvTitlePin != null) {
            tvTitlePin.text = customTitle
        }

        val tvPinIndicator = dialogView.findViewById<TextView>(R.id.tvPinIndicator)
        val btnDelete = dialogView.findViewById<ImageButton>(R.id.btnDelete)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var enteredPin = ""

        val numberButtons = listOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9)

        for (id in numberButtons) {
            dialogView.findViewById<TextView>(id).setOnClickListener { view ->
                if (enteredPin.length < 6) {
                    enteredPin += (view as TextView).text.toString()
                    tvPinIndicator.text = "●".repeat(enteredPin.length)

                    if (enteredPin.length == 6) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (expectedPin == null || enteredPin == expectedPin) {
                                dialog.dismiss()
                                onSuccess(enteredPin)
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}