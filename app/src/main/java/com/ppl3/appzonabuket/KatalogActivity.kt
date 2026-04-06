package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class KatalogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_katalog)

        val recyclerKatalog = findViewById<RecyclerView>(R.id.recyclerKatalog)
        val tabRekomendasi = findViewById<TextView>(R.id.tabRekomendasi)

        // Grid 4 kolom
        recyclerKatalog.layoutManager = GridLayoutManager(this, 4)

        // DATA PRODUK KATALOG
        val productList = listOf(
            Product("Buket Mawar", "150000", R.drawable.buket1),
            Product("Buket Wisuda", "200000", R.drawable.buket2),
            Product("Buket Ulang Tahun", "180000", R.drawable.buket3),
            Product("Buket Anniversary", "220000", R.drawable.buket4),
            Product("Buket Baby", "170000", R.drawable.buket5),
            Product("Buket Graduation", "210000", R.drawable.buket6),
            Product("Buket Pink", "190000", R.drawable.buket7),
            Product("Buket Lily", "230000", R.drawable.buket8)
        )

        val adapter = ProductAdapter(productList)
        recyclerKatalog.adapter = adapter

        // Klik tab REKOMENDASI → kembali ke MainActivity
        tabRekomendasi.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainKatalog)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}