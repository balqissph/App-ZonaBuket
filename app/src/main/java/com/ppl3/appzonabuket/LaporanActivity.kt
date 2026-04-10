package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
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
        val btnSavePDF = findViewById<MaterialButton>(R.id.btnSavePDF)

        drawerLayout = findViewById(R.id.drawerLayout)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // --- RECYCLERVIEW LAPORAN ---
        recyclerLaporan.layoutManager = LinearLayoutManager(this)

        val laporanList = listOf(
            Laporan(
                "11:05 / 4-5-2025",
                "Buket Bunga Satin Biru Hitam",
                "warna biru tua",
                "Rp.150.000",
                2,
                "Rp.300.000",
                "Qris",
                "Admin 1"
            ),
            Laporan(
                "15:33 / 11-5-2025",
                "Buket Uang Putih",
                null,
                "Rp.2.000.000",
                2,
                "Rp.4.000.000",
                "Transfer BCA",
                "Admin 2"
            ),
            Laporan(
                "20:55 / 11-5-2025",
                "Buket Bunga Satin Putih",
                "pakai pita emas",
                "Rp.100.000",
                1,
                "Rp.100.000",
                "Cash",
                "Admin 1"
            ),
            Laporan(
                "08:24 / 13-5-2025",
                "Buket Wisuda + Boneka",
                null,
                "Rp.200.000",
                7,
                "Rp.1.400.000",
                "Cash",
                "Admin 3"
            ),
            Laporan(
                "09:05 / 13-5-2025",
                "Buket Pink Gold",
                "untuk ulang tahun",
                "Rp.85.000",
                2,
                "Rp.170.000",
                "Transfer Mandiri",
                "Admin 1"
            )
        )

        val adapter = LaporanAdapter(laporanList)
        recyclerLaporan.adapter = adapter

        // --- TOMBOL SIMPAN PDF ---
        btnSavePDF.setOnClickListener {
            Toast.makeText(
                this,
                "Laporan berhasil disimpan sebagai PDF",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // --- FUNGSI UNTUK MENAMPILKAN POPUP PIN ---
    private fun showPinDialog(targetActivity: Class<*>) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_pin, null)
        val tvPinIndicator = dialogView.findViewById<TextView>(R.id.tvPinIndicator)
        val btnDelete = dialogView.findViewById<ImageButton>(R.id.btnDelete)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var enteredPin = ""
        val correctPin = "123456"

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

                                Toast.makeText(
                                    this,
                                    "Akses Diberikan",
                                    Toast.LENGTH_SHORT
                                ).show()

                                dialog.dismiss()
                                startActivity(Intent(this, targetActivity))

                            } else {

                                Toast.makeText(
                                    this,
                                    "PIN Salah!",
                                    Toast.LENGTH_SHORT
                                ).show()

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
}