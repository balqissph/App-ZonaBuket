package com.ppl3.appzonabuket

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class OwnerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner)

        // Ambil tombol Edit dari layout utama
        val btnEdit: MaterialButton = findViewById(R.id.btnEdit)

        // Set listener ketika tombol Edit ditekan
        btnEdit.setOnClickListener {
            tampilkanPopupUbahPassword()
        }
    }

    private fun tampilkanPopupUbahPassword() {
        // 1. Inflate layout XML
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_owner, null)

        // 2. Buat AlertDialog Builder
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        // 3. Buat dan tampilkan Dialog
        val alertDialog = builder.create()

        // Membuat background bawaan dialog menjadi transparan
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()

        // 4. Logika untuk tombol Simpan di dalam popup
        val btnSimpanDialog: MaterialButton = dialogView.findViewById(R.id.btnSimpanDialog)
        btnSimpanDialog.setOnClickListener {
            // Tambahkan logika simpan data/validasi di sini

            // Tutup popup setelah ditekan
            alertDialog.dismiss()
        }
    }
}