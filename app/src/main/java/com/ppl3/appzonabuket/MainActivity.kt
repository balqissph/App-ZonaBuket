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

class MainActivity : AppCompatActivity() {

    lateinit var recyclerProduct: RecyclerView
    lateinit var tabKatalog: TextView
    lateinit var drawerLayout: DrawerLayout

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
                showPinDialog(ProdukActivity::class.java)
            }, 250)
        }

        // --- POPUP KONFIRMASI LOGOUT ---
        btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Konfirmasi Logout")
            builder.setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            builder.setPositiveButton("Iya") { dialog, which ->
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton("Batal") { dialog, which -> dialog.dismiss() }
            builder.show()
        }

        // --- KODE RECYCLERVIEW & TAB ---
        recyclerProduct.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val productList = listOf(
            Product("Buket Uang 100k", 150000, R.drawable.buket1, "Berikan kejutan paling berkesan..."),
            Product("Buket Uang 50k", 200000, R.drawable.buket2, "Cari hadiah yang pasti disukai?..."),
            Product("Buket Bunga Biru Hitam", 180000,  R.drawable.buket3, "Tampil beda dengan Buket..."),
            Product("Buket Biru Wisuda", 220000, R.drawable.buket4, "Rayakan momen kelulusan..."),
            Product("Buket Silver", 250000, R.drawable.buket5, "Simbol kemewahan dan cinta...")
        )

        val adapter = ProductAdapter(productList)
        recyclerProduct.adapter = adapter

        tabKatalog.setOnClickListener {
            startActivity(Intent(this, KatalogActivity::class.java))
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, KeranjangActivity::class.java))
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

        val builder = AlertDialog.Builder(this)
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
                    tvPinIndicator.text = "*".repeat(enteredPin.length)

                    // Jika PIN sudah 6 digit, otomatis cek kebenaran
                    if (enteredPin.length == 6) {
                        // Beri jeda sedikit agar user bisa melihat bintang ke-6 muncul
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (enteredPin == correctPin) {
                                Toast.makeText(this, "Akses Diberikan", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                startActivity(Intent(this, targetActivity))
                            } else {
                                Toast.makeText(this, "PIN Salah!", Toast.LENGTH_SHORT).show()
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
                tvPinIndicator.text = "*".repeat(enteredPin.length)
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