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

        // --- KONTROL SIDEBAR ---
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, ProfileActivity::class.java))
            }, 250)
        }

        menuLaporan.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            // Kalau kamu udah di Laporan dan klik Laporan lagi, sebenernya gak perlu ngapa-ngapain,
            // tapi tetep aku kasih pop up pin sesuai kodemu biar konsisten.
            Handler(Looper.getMainLooper()).postDelayed({
                showPinDialog(LaporanActivity::class.java)
            }, 250)
        }

        menuManajemen.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                showPinDialog(ProdukActivity::class.java)
            }, 250)
        }

        // --- LOGOUT ---
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Iya") { _, _ ->
                    Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // --- RECYCLERVIEW LAPORAN ---
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

    // --- MENGATUR TOMBOL BACK UNTUK SIDEBAR ---
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}