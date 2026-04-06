package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class KeranjangActivity : AppCompatActivity() {

    lateinit var recyclerCart: RecyclerView
    lateinit var adapter: CartAdapter

    lateinit var tabRekomendasi: TextView
    lateinit var tabKatalog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_keranjang)

        recyclerCart = findViewById(R.id.recyclerCart)

        tabRekomendasi = findViewById(R.id.tabRekomendasi)
        tabKatalog = findViewById(R.id.tabKatalog)

        recyclerCart.layoutManager =
            LinearLayoutManager(this)

        adapter = CartAdapter(CartManager.cartItems)

        recyclerCart.adapter = adapter

        // Klik TAB REKOMENDASI → MainActivity
        tabRekomendasi.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

        // Klik TAB KATALOG → KatalogActivity
        tabKatalog.setOnClickListener {

            val intent = Intent(this, KatalogActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()

        adapter.notifyDataSetChanged()
    }
}