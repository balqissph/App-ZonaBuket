package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_katalog)

        val recyclerKatalog = findViewById<RecyclerView>(R.id.recyclerKatalog)
        val tabRekomendasi = findViewById<TextView>(R.id.tabRekomendasi)
        val btnCart = findViewById<ImageView>(R.id.btnCart)

        drawerLayout = findViewById(R.id.drawerLayout)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)

        val menuProfile = findViewById<LinearLayout>(R.id.menuProfile)
        val menuLaporan = findViewById<LinearLayout>(R.id.menuLaporan)
        val menuManajemen = findViewById<LinearLayout>(R.id.menuManajemen)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        // SIDEBAR
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        menuProfile.setOnClickListener {
            Toast.makeText(this, "Membuka Profile...", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        menuLaporan.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            showPinPopup(LaporanActivity::class.java)
        }

        menuManajemen.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            showPinPopup(ProdukActivity::class.java)
        }

        // LOGOUT
        btnLogout.setOnClickListener {

            val builder = AlertDialog.Builder(this)

            builder.setTitle("Konfirmasi Logout")
            builder.setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")

            builder.setPositiveButton("Iya") { _, _ ->

                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            builder.create().show()
        }

        // RECYCLERVIEW GRID
        recyclerKatalog.layoutManager = GridLayoutManager(this, 3)

        val productList = listOf(
            Product("Buket Mawar", 150000, R.drawable.buket1, "Buket Mawar"),
            Product("Buket Wisuda", 200000, R.drawable.buket2, "Buket Wisuda"),
            Product("Buket Ulang Tahun", 180000, R.drawable.buket3, "Buket Ulang Tahun"),
            Product("Buket Anniversary", 220000, R.drawable.buket4, "Buket Anniversary"),
            Product("Buket Baby", 170000, R.drawable.buket5, "Buket Baby"),
            Product("Buket Graduation", 210000, R.drawable.buket6, "Buket Graduation"),
            Product("Buket Pink", 190000, R.drawable.buket7, "Buket Pink"),
            Product("Buket Lily", 230000, R.drawable.buket8, "Buket Lily")
        )

        val adapter = ProductAdapter(productList)
        recyclerKatalog.adapter = adapter

        // TAB PINDAH HALAMAN
        tabRekomendasi.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnCart.setOnClickListener {
            startActivity(Intent(this, KeranjangActivity::class.java))
        }

        // EDGE TO EDGE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainKatalog)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // BACK BUTTON
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }

            }
        })
    }

    // POPUP PIN
    private fun showPinPopup(targetActivity: Class<*>) {

        val view = LayoutInflater.from(this).inflate(R.layout.popup_pin, null)

        val tvPin = view.findViewById<TextView>(R.id.tvPinIndicator)

        val btn1 = view.findViewById<TextView>(R.id.btn1)
        val btn2 = view.findViewById<TextView>(R.id.btn2)
        val btn3 = view.findViewById<TextView>(R.id.btn3)
        val btn4 = view.findViewById<TextView>(R.id.btn4)
        val btn5 = view.findViewById<TextView>(R.id.btn5)
        val btn6 = view.findViewById<TextView>(R.id.btn6)
        val btn7 = view.findViewById<TextView>(R.id.btn7)
        val btn8 = view.findViewById<TextView>(R.id.btn8)
        val btn9 = view.findViewById<TextView>(R.id.btn9)
        val btn0 = view.findViewById<TextView>(R.id.btn0)

        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var pin = ""
        val correctPin = "112233"

        fun updatePin() {
            tvPin.text = "●".repeat(pin.length)
        }

        fun addNumber(number: String) {

            if (pin.length < 6) {
                pin += number
                updatePin()
            }

            if (pin.length == 6) {

                if (pin == correctPin) {

                    Toast.makeText(this, "PIN Benar", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                    val intent = Intent(this, targetActivity)
                    startActivity(intent)

                } else {

                    Toast.makeText(this, "PIN Salah", Toast.LENGTH_SHORT).show()
                    pin = ""
                    updatePin()

                }
            }
        }

        btn1.setOnClickListener { addNumber("1") }
        btn2.setOnClickListener { addNumber("2") }
        btn3.setOnClickListener { addNumber("3") }
        btn4.setOnClickListener { addNumber("4") }
        btn5.setOnClickListener { addNumber("5") }
        btn6.setOnClickListener { addNumber("6") }
        btn7.setOnClickListener { addNumber("7") }
        btn8.setOnClickListener { addNumber("8") }
        btn9.setOnClickListener { addNumber("9") }
        btn0.setOnClickListener { addNumber("0") }

        btnDelete.setOnClickListener {
            if (pin.isNotEmpty()) {
                pin = pin.dropLast(1)
                updatePin()
            }
        }
    }
}