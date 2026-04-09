package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etNama: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etNama = findViewById(R.id.etNama)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Hilangkan hint saat kotak diklik
        etNama.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etNama.hint = ""
            } else if (etNama.text.toString().isEmpty()) {
                etNama.hint = "masukan nama anda"
            }
        }

        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etPassword.hint = ""
            } else if (etPassword.text.toString().isEmpty()) {
                etPassword.hint = "masukan password anda"
            }
        }

        btnLogin.setOnClickListener {

            val nama = etNama.text.toString()
            val password = etPassword.text.toString()

            if (nama.isNotEmpty() && password.isNotEmpty()) {

                // pindah ke halaman berikutnya
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            } else {
                // Modifikasi sedikit agar error muncul di tempat yang benar
                if (nama.isEmpty()) {
                    etNama.error = "Isi nama"
                }
                if (password.isEmpty()) {
                    etPassword.error = "Isi password"
                }
            }
        }
    }
}