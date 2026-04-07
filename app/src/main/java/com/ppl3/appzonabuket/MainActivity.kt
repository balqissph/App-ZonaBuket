package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog // Pastikan import ini ada
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

        // Inisialisasi View dari layout utama
        recyclerProduct = findViewById(R.id.recyclerProduct)
        tabKatalog = findViewById(R.id.tabKatalog)
        drawerLayout = findViewById(R.id.drawerLayout)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnCart = findViewById<ImageView>(R.id.btnCart)

        // Inisialisasi View dari Sidebar (Menu Samping)
        val menuProfile = findViewById<LinearLayout>(R.id.menuProfile)
        val menuLaporan = findViewById<LinearLayout>(R.id.menuLaporan)
        val menuManajemen = findViewById<LinearLayout>(R.id.menuManajemen)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        // --- LOGIKA SIDEBAR ---

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

        // --- POPUP KONFIRMASI LOGOUT ---
        btnLogout.setOnClickListener {
            // 1. Buat builder untuk AlertDialog
            val builder = AlertDialog.Builder(this)

            // 2. Atur Judul dan Pesan popup
            builder.setTitle("Konfirmasi Logout")
            builder.setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")

            // 3. Jika tombol "Iya" diklik
            builder.setPositiveButton("Iya") { dialog, which ->
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()

                // Pindah ke halaman Login
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish() // Menutup MainActivity
            }

            // 4. Jika tombol "Batal" diklik
            builder.setNegativeButton("Batal") { dialog, which ->
                // Tutup popup saja tanpa melakukan apa-apa
                dialog.dismiss()
            }

            // 5. Tampilkan dialognya
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        // --- KODE RECYCLERVIEW & TAB ---

        recyclerProduct.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val productList = listOf(
            Product("Buket Uang 100k", 150000, R.drawable.buket1, "Berikan kejutan paling berkesan dengan Buket Uang 100k kami yang super mewah! Dibuat dengan lembaran uang pecahan Rp100.000 baru yang disusun rapi dan presisi, buket ini memancarkan kesan eksklusif dan elegan. Sangat cocok untuk hadiah ulang tahun, anniversary, atau kejutan spesial untuk orang terkasih."),
            Product("Buket Uang 50k", 200000, R.drawable.buket2, "Cari hadiah yang pasti disukai? Buket Uang pecahan Rp50.000 ini adalah jawabannya! Warna biru dari uang 50 ribuan memberikan kesan visual yang sangat estetik, kalem, dan eye-catching. Cocok untuk hadiah sahabat, pacar, atau keluarga tercinta."),
            Product("Buket Bunga Biru Hitam", 180000,  R.drawable.buket3, "Tampil beda dengan Buket Bunga Satin perpaduan warna Biru dan Hitam! Kombinasi warna ini menciptakan aura yang misterius, edgy, sekaligus sangat elegan. Sangat direkomendasikan untuk hadiah pria, atau bagi mereka yang menyukai gaya anti-mainstream dan berkelas."),
            Product("Buket Biru Wisuda", 220000, R.drawable.buket4, "Rayakan momen kelulusan orang terdekat dengan Buket Biru spesial Wisuda! Warna biru melambangkan kecerdasan, kepercayaan diri, dan masa depan yang cerah. Buket ini dirancang khusus untuk membuat foto wisuda menjadi lebih hidup dan fotogenik."),
            Product("Buket Silver", 250000, R.drawable.buket5, "Simbol kemewahan dan cinta yang tak lekang oleh waktu. Buket Bunga Satin berwarna Silver ini memantulkan cahaya dengan indah, memberikan kesan glamor kelas atas. Pilihan sempurna untuk momen-momen sakral seperti anniversary pernikahan, pertunangan, atau kado Hari Ibu.")
        )

        val adapter = ProductAdapter(productList)
        recyclerProduct.adapter = adapter

        tabKatalog.setOnClickListener {
            val intent = Intent(this, KatalogActivity::class.java)
            startActivity(intent)
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, KeranjangActivity::class.java))
        }

        // Setting Padding Sistem Bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Fungsi tambahan: Jika menu terbuka dan tombol back ditekan, tutup menu dulu
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}