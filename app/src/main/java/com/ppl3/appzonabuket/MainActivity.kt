package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
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
            Product("Buket Uang 100k", "150000", R.drawable.buket1),
            Product("Buket Uang 50k", "200000", R.drawable.buket2),
            Product("Buket Bunga Biru Hitam", "180000", R.drawable.buket3),
            Product("Buket Biru Wisuda", "220000", R.drawable.buket4),
            Product("Buket Silver", "250000", R.drawable.buket5)
        )

        val adapter = ProductAdapter(productList)
        recyclerProduct.adapter = adapter

        // Klik tab katalog
        tabKatalog.setOnClickListener {
            val intent = Intent(this, KatalogActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}