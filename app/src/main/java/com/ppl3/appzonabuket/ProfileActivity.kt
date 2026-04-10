package com.ppl3.appzonabuket

import android.content.Intent
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnEdit.setOnClickListener {
            showEditPasswordDialog()
        }
    }

    private fun showEditPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.edit_password, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val etPasswordLama = dialogView.findViewById<android.widget.EditText>(R.id.etPasswordLama)
        val etPasswordBaru = dialogView.findViewById<android.widget.EditText>(R.id.etPasswordBaru)
        val etKonfirmasiPassword = dialogView.findViewById<android.widget.EditText>(R.id.etKonfirmasiPassword)

        val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btnSimpan)

        btnSimpan.setOnClickListener {

            val passwordLama = etPasswordLama.text.toString().trim()
            val passwordBaru = etPasswordBaru.text.toString().trim()
            val konfirmasiPassword = etKonfirmasiPassword.text.toString().trim()

            // cek apakah ada field kosong
            if (passwordLama.isEmpty() || passwordBaru.isEmpty() || konfirmasiPassword.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // cek apakah password baru sama dengan konfirmasi
            if (passwordBaru != konfirmasiPassword) {
                Toast.makeText(this, "Konfirmasi password tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // jika semua valid
            Toast.makeText(this, "Password berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}