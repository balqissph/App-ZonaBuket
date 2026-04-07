package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog // Pastikan ini di-import
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
            Toast.makeText(this, "Membuka Profile...", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(GravityCompat.START)
            // startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Aksi klik menu Laporan Penjualan
        menuLaporan.setOnClickListener {
            Toast.makeText(this, "Membuka Laporan Penjualan...", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(GravityCompat.START)
            // startActivity(Intent(this, LaporanActivity::class.java))
        }

        // Aksi klik menu Manajemen Produk
        menuManajemen.setOnClickListener {
            Toast.makeText(this, "Membuka Manajemen Produk...", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(GravityCompat.START)
            // startActivity(Intent(this, ManajemenActivity::class.java))
        }

        // ==========================================
        // POPUP KONFIRMASI LOGOUT
        // ==========================================
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

    // Fungsi tambahan: Jika menu terbuka dan tombol back HP ditekan, tutup menu dulu
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}