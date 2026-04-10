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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    lateinit var recyclerProduct: RecyclerView
    lateinit var tabKatalog: TextView
    lateinit var drawerLayout: DrawerLayout

    // Deklarasi untuk Firebase dan Adapter
    private lateinit var db: FirebaseFirestore
    private val productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerProduct = findViewById(R.id.recyclerProduk)
        tabKatalog = findViewById(R.id.tabKatalog)
        drawerLayout = findViewById(R.id.drawerLayout)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnCart = findViewById<ImageView>(R.id.btnCart)

        val menuProfile = findViewById<LinearLayout>(R.id.menuProfile)
        val menuLaporan = findViewById<LinearLayout>(R.id.menuLaporan)
        val menuManajemen = findViewById<LinearLayout>(R.id.menuManajemen)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        // --- LOGIKA SIDEBAR ---
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
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

        // --- POPUP KONFIRMASI LOGOUT ---
        btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Konfirmasi Logout")
            builder.setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            builder.setPositiveButton("Iya") { dialog, which ->
                Toast.makeText(this@MainActivity, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton("Batal") { dialog, which -> dialog.dismiss() }
            builder.show()
        }

        // --- KODE RECYCLERVIEW & FIREBASE FIRESTORE ---
        recyclerProduct.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        adapter = ProductAdapter(productList)
        recyclerProduct.adapter = adapter

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance()

        // Mengambil data dari Firestore
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

                    productList.add(Product(name = nama, price = harga, image = gambarId, description = deskripsi))
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@MainActivity, "Gagal mengambil data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // --- TAB & LAINNYA ---
        tabKatalog.setOnClickListener {
            startActivity(Intent(this@MainActivity, KatalogActivity::class.java))
            // Opsional: tambahkan finish() di sini jika tidak ingin MainActivity menumpuk di belakang
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this@MainActivity, KeranjangActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(dialogView)
        val dialog = builder.create()

        // Membuat background dialog menjadi transparan agar sudut lengkung (bg_popup_pin) terlihat rapi
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var enteredPin = ""
        val correctPin = "123456" // Ganti PIN di sini

        // Daftar ID tombol angka
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        // Memberikan aksi klik pada semua tombol angka
        for (id in numberButtons) {
            dialogView.findViewById<TextView>(id).setOnClickListener { view ->
                if (enteredPin.length < 6) {
                    val number = (view as TextView).text.toString()
                    enteredPin += number

                    // Menampilkan indikator bintang (*) sesuai jumlah PIN yang diinput
                    tvPinIndicator.text = "●".repeat(enteredPin.length)

                    // Jika PIN sudah 6 digit, otomatis cek kebenaran
                    if (enteredPin.length == 6) {
                        // Beri jeda sedikit agar user bisa melihat bintang ke-6 muncul
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (enteredPin == correctPin) {
                                Toast.makeText(this@MainActivity, "Akses Diberikan", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                startActivity(Intent(this@MainActivity, targetActivity))
                            } else {
                                Toast.makeText(this@MainActivity, "PIN Salah!", Toast.LENGTH_SHORT).show()
                                enteredPin = ""
                                tvPinIndicator.text = "" // Reset indikator jika salah
                            }
                        }, 200)
                    }
                }
            }
        }

        // Aksi klik tombol Hapus (Delete)
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