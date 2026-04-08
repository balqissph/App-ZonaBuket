package com.ppl3.appzonabuket

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnEdit = findViewById<MaterialButton>(R.id.btnEdit)

        btnBack.setOnClickListener {
            finish()
        }

        btnEdit.setOnClickListener {
            showEditPasswordDialog()
        }
    }

    private fun showEditPasswordDialog() {
        // 1. Inflate layout dialog kustom
        val dialogView = layoutInflater.inflate(R.layout.edit_password, null)

        // 2. Buat builder AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        // 3. Buat dan tampilkan dialog
        val dialog = builder.create()

        // Membuat background bawaan AlertDialog menjadi transparan
        // agar sudut melengkung (rounded corners) dari CardView kita terlihat rapi
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 4. Atur aksi untuk tombol Simpan di dalam popup
        val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btnSimpan)
        btnSimpan.setOnClickListener {
            Toast.makeText(this, "Password berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // Tutup popup setelah diklik
        }

        dialog.show()
    }
}