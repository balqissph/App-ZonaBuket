package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class LaporanActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        val recyclerLaporan = findViewById<RecyclerView>(R.id.recyclerLaporan)

        drawerLayout = findViewById(R.id.drawerLayout)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)

        val menuProfile = findViewById<LinearLayout>(R.id.menuProfile)
        val menuLaporan = findViewById<LinearLayout>(R.id.menuLaporan)
        val menuManajemen = findViewById<LinearLayout>(R.id.menuManajemen)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        menuProfile.setOnClickListener {
            Toast.makeText(this, "Membuka Profile", Toast.LENGTH_SHORT).show()
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

            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Iya") { _, _ ->

                    Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                    finish()

                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        recyclerLaporan.layoutManager = LinearLayoutManager(this)

        val laporanList = listOf(
            Laporan(
                "11:05 / 4-5-2025",
                "Buket Bunga Satin Biru Hitam",
                "Rp.150.000",
                2,
                "Rp.300.000",
                "Qris"
            ),
            Laporan(
                "15:33 / 11-5-2025",
                "Buket Uang Putih",
                "Rp.2.000.000",
                2,
                "Rp.4.000.000",
                "Transfer BCA"
            ),
            Laporan(
                "20:55 / 11-5-2025",
                "Buket Bunga Satin Putih",
                "Rp.100.000",
                1,
                "Rp.100.000",
                "Cash"
            ),
            Laporan(
                "08:24 / 13-5-2025",
                "Buket Bunga Wisuda + Boneka",
                "Rp.200.000",
                7,
                "Rp.1.400.000",
                "Cash"
            ),
            Laporan(
                "09:05 / 13-5-2025",
                "Buket Bunga Pink Gold",
                "Rp.85.000",
                2,
                "Rp.170.000",
                "Transfer Mandiri"
            )
        )

        val adapter = LaporanAdapter(laporanList)
        recyclerLaporan.adapter = adapter
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