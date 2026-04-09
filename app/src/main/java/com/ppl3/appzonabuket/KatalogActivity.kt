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

class KatalogActivity : AppCompatActivity() {

    // Deklarasi drawerLayout di sini agar bisa diakses oleh onBackPressed()
    lateinit var drawerLayout: DrawerLayout

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

        // Buka Sidebar saat tombol menu di pojok kiri atas diklik
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Aksi klik menu Profile
        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, ProfileActivity::class.java))
            }, 250)
        }

        // Aksi klik menu Laporan Penjualan (Panggil PIN)
        menuLaporan.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                showPinDialog(LaporanActivity::class.java)
            }, 250)
        }

        // Aksi klik menu Manajemen Produk (Panggil PIN)
        menuManajemen.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                showPinDialog(ProdukActivity::class.java)
            }, 250)
        }

        // POPUP KONFIRMASI LOGOUT
        btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Konfirmasi Logout")
            builder.setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")

            // Tombol Iya
            builder.setPositiveButton("Iya") { dialog, which ->
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            // Tombol Batal
            builder.setNegativeButton("Batal") { dialog, which ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        // ==========================================
        // KODE RECYCLERVIEW & TAB (BAWAAN KAMU)
        // ==========================================

        // Grid 4 kolom
        recyclerKatalog.layoutManager = GridLayoutManager(this, 4)

        // DATA PRODUK KATALOG
        val productList = listOf(
            Product("Buket Mawar", 150000, R.drawable.buket1, "Berikan kejutan paling berkesan dengan Buket Uang 100k kami yang super mewah! Dibuat dengan lembaran uang pecahan Rp100.000 baru yang disusun rapi dan presisi, buket ini memancarkan kesan eksklusif dan elegan. Sangat cocok untuk hadiah ulang tahun, anniversary, atau kejutan spesial untuk orang terkasih."),
            Product("Buket Wisuda", 200000, R.drawable.buket2, "Berikan kejutan paling berkesan dengan Buket Uang 100k kami yang super mewah! Dibuat dengan lembaran uang pecahan Rp100.000 baru yang disusun rapi dan presisi, buket ini memancarkan kesan eksklusif dan elegan. Sangat cocok untuk hadiah ulang tahun, anniversary, atau kejutan spesial untuk orang terkasih."),
            Product("Buket Ulang Tahun", 180000, R.drawable.buket3, "Berikan kejutan paling berkesan dengan Buket Uang 100k kami yang super mewah! Dibuat dengan lembaran uang pecahan Rp100.000 baru yang disusun rapi dan presisi, buket ini memancarkan kesan eksklusif dan elegan. Sangat cocok untuk hadiah ulang tahun, anniversary, atau kejutan spesial untuk orang terkasih."),
            Product("Buket Anniversary", 220000, R.drawable.buket4, "Berikan kejutan paling berkesan dengan Buket Uang 100k kami yang super mewah! Dibuat dengan lembaran uang pecahan Rp100.000 baru yang disusun rapi dan presisi, buket ini memancarkan kesan eksklusif dan elegan. Sangat cocok untuk hadiah ulang tahun, anniversary, atau kejutan spesial untuk orang terkasih."),
            Product("Buket Baby", 170000, R.drawable.buket5, "Berikan kejutan paling berkesan dengan Buket Uang 100k kami yang super mewah! Dibuat dengan lembaran uang pecahan Rp100.000 baru yang disusun rapi dan presisi, buket ini memancarkan kesan eksklusif dan elegan. Sangat cocok untuk hadiah ulang tahun, anniversary, atau kejutan spesial untuk orang terkasih."),
            Product("Buket Graduation", 210000, R.drawable.buket6, "Berikan kejutan paling berkesan dengan Buket Uang 100k kami yang super mewah! Dibuat dengan lembaran uang pecahan Rp100.000 baru yang disusun rapi dan presisi, buket ini memancarkan kesan eksklusif dan elegan. Sangat cocok untuk hadiah ulang tahun, anniversary, atau kejutan spesial untuk orang terkasih."),
            Product("Buket Pink", 190000, R.drawable.buket7, "Berikan kejutan paling berkesan dengan Buket Uang 100k kami yang super mewah! Dibuat dengan lembaran uang pecahan Rp100.000 baru yang disusun rapi dan presisi, buket ini memancarkan kesan eksklusif dan elegan. Sangat cocok untuk hadiah ulang tahun, anniversary, atau kejutan spesial untuk orang terkasih."),
            Product("Buket Lily", 230000, R.drawable.buket8,"Berikan kejutan paling berkesan dengan Buket Uang 100k kami yang super mewah! Dibuat dengan lembaran uang pecahan Rp100.000 baru yang disusun rapi dan presisi, buket ini memancarkan kesan eksklusif dan elegan. Sangat cocok untuk hadiah ulang tahun, anniversary, atau kejutan spesial untuk orang terkasih.")
        )

        val adapter = ProductAdapter(productList)
        recyclerKatalog.adapter = adapter

        // Klik tab REKOMENDASI → kembali ke MainActivity
        tabRekomendasi.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Tutup KatalogActivity agar memori lega
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, KeranjangActivity::class.java))
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

    // Fungsi tambahan: Jika menu terbuka dan tombol back HP ditekan, tutup menu dulu
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}