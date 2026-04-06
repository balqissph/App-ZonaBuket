package com.ppl3.appzonabuket

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class KeranjangActivity : AppCompatActivity() {

    lateinit var recyclerCart: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_keranjang)

        recyclerCart = findViewById(R.id.recyclerCart)

        // layout list vertikal
        recyclerCart.layoutManager = LinearLayoutManager(this)

        // adapter keranjang
        recyclerCart.adapter = CartAdapter(CartManager.cartItems)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainKeranjang)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}