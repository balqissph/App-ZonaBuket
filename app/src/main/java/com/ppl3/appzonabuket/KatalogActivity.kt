package com.ppl3.appzonabuket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class KatalogActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var db: FirebaseFirestore
    private val productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_katalog)

        // 1. Inisialisasi View Utama
        val recyclerKatalog = findViewById<RecyclerView>(R.id.recyclerKatalog)
        val tabRekomendasi = findViewById<TextView>(R.id.tabRekomendasi)
        val btnCart = findViewById<ImageView>(R.id.btnCart)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        drawerLayout = findViewById(R.id.drawerLayout)

        // 2. Setup Sidebar
        setupSidebar()

        // 3. Setup Klik Navigasi Sidebar
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 4. Setup RecyclerView & Firestore
        recyclerKatalog.layoutManager = GridLayoutManager(this, 4)
        adapter = ProductAdapter(productList)
        recyclerKatalog.adapter = adapter
        db = FirebaseFirestore.getInstance()
        fetchDataFromFirestore()

        // 5. Navigasi Antar Tab
        tabRekomendasi.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, KeranjangActivity::class.java))
        }

        // 6. Handle Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainKatalog)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSidebar() {
        val menuProfile = findViewById<LinearLayout>(R.id.menuProfile)
        val menuLaporan = findViewById<LinearLayout>(R.id.menuLaporan)
        val menuManajemen = findViewById<LinearLayout>(R.id.menuManajemen)
        val menuWaitingPayment = findViewById<LinearLayout>(R.id.menuWaitingPayment)
        val menuManajemenAdmin = findViewById<LinearLayout>(R.id.menuManajemenAdmin)
        val menuResetPin = findViewById<LinearLayout>(R.id.menuResetPin)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

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

        // Menu yang BEBAS
        menuWaitingPayment.visibility = View.VISIBLE

        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, ProfileActivity::class.java))
            }, 250)
        }

        menuLaporan.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            ambilPinFirebase { pinGlobal ->
                showPinDialog("Masukan PIN Anda", pinGlobal) {
                    startActivity(Intent(this, LaporanActivity::class.java))
                }
            }
        }

        menuManajemen.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            ambilPinFirebase { pinGlobal ->
                showPinDialog("Masukan PIN Anda", pinGlobal) {
                    startActivity(Intent(this, ProdukActivity::class.java))
                }
            }
        }

        menuWaitingPayment.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, WaitingPaymentActivity::class.java))
            }, 250)
        }

        menuManajemenAdmin.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, ManageAdminActivity::class.java))
            }, 250)
        }

        menuResetPin.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)

            ambilPinFirebase { pinGlobal ->
                showPinDialog("Masukkan PIN Lama", pinGlobal) { _ ->
                    showPinDialog("Masukkan PIN Baru", null) { pinBaru ->
                        showPinDialog("Konfirmasi PIN Baru", pinBaru) { pinKonfirmasi ->

                            val dataPin = hashMapOf("app_pin" to pinKonfirmasi)

                            db.collection("settings").document("security").set(dataPin)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "PIN berhasil diubah!", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Gagal mengubah PIN.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }
        }

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
        // Karena db sudah diinisialisasi di onCreate, kita bisa langsung pakai
        val firestoreDb = FirebaseFirestore.getInstance()
        firestoreDb.collection("settings").document("security").get()
            .addOnSuccessListener { document ->
                val pin = document.getString("app_pin") ?: "123456"
                onSuccess(pin)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
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