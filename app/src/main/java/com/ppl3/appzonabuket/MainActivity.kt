package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    lateinit var recyclerProduct: RecyclerView
    lateinit var tabKatalog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerProduct = findViewById(R.id.recyclerProduct)
        tabKatalog = findViewById(R.id.tabKatalog)

        // RecyclerView horizontal
        recyclerProduct.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // DATA PRODUK
        val productList = listOf(
            Product("Buket Uang 100k", 150000, R.drawable.buket1, "Berikan kejutan paling berkesan dengan Buket Uang 100k kami yang super mewah! Dibuat dengan lembaran uang pecahan Rp100.000 baru yang disusun rapi dan presisi, buket ini memancarkan kesan eksklusif dan elegan. Sangat cocok untuk hadiah ulang tahun, anniversary, atau kejutan spesial untuk orang terkasih."),
            Product("Buket Uang 50k", 200000, R.drawable.buket2, "Cari hadiah yang pasti disukai? Buket Uang pecahan Rp50.000 ini adalah jawabannya! Warna biru dari uang 50 ribuan memberikan kesan visual yang sangat estetik, kalem, dan eye-catching. Cocok untuk hadiah sahabat, pacar, atau keluarga tercinta."),
            Product("Buket Bunga Biru Hitam", 180000, R.drawable.buket3, "Tampil beda dengan Buket Bunga Satin perpaduan warna Biru dan Hitam! Kombinasi warna ini menciptakan aura yang misterius, edgy, sekaligus sangat elegan. Sangat direkomendasikan untuk hadiah pria, atau bagi mereka yang menyukai gaya anti-mainstream dan berkelas."),
            Product("Buket Biru Wisuda", 220000, R.drawable.buket4, "Rayakan momen kelulusan orang terdekat dengan Buket Biru spesial Wisuda! Warna biru melambangkan kecerdasan, kepercayaan diri, dan masa depan yang cerah. Buket ini dirancang khusus untuk membuat foto wisuda menjadi lebih hidup dan fotogenik."),
            Product("Buket Silver", 250000, R.drawable.buket5, "Simbol kemewahan dan cinta yang tak lekang oleh waktu. Buket Bunga Satin berwarna Silver ini memantulkan cahaya dengan indah, memberikan kesan glamor kelas atas. Pilihan sempurna untuk momen-momen sakral seperti anniversary pernikahan, pertunangan, atau kado Hari Ibu.")
        )

        val adapter = ProductAdapter(productList)
        recyclerProduct.adapter = adapter

        // Klik tab katalog
        tabKatalog.setOnClickListener {
            val intent = Intent(this, KatalogActivity::class.java)
            startActivity(intent)
        }

        val btnCart = findViewById<ImageView>(R.id.btnCart)
        btnCart.setOnClickListener {
            startActivity(Intent(this, KeranjangActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}