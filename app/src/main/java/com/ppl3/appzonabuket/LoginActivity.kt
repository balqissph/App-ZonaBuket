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

        btnLogin.setOnClickListener {

            val nama = etNama.text.toString()
            val password = etPassword.text.toString()

            if (nama.isNotEmpty() && password.isNotEmpty()) {

                // pindah ke halaman berikutnya
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            } else {
                etNama.error = "Isi nama"
                etPassword.error = "Isi password"
            }
        }
    }
}