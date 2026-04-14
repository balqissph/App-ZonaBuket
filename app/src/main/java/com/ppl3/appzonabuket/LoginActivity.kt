package com.ppl3.appzonabuket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var etNama: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button

    // Inisialisasi FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        etNama = findViewById(R.id.etNama)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nama.isNotEmpty() && password.isNotEmpty()) {

                // --- TRIK LOGIN PAKAI NAMA ---
                // Kita tambahkan domain buatan secara otomatis di belakang nama
                val dummyEmail = "$nama@zonabuket.com"

                // Proses login ke Firebase tetap pakai fungsi signInWithEmailAndPassword
                auth.signInWithEmailAndPassword(dummyEmail, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            // --- TAMBAHAN LOGIKA PENYIMPANAN ROLE (SESI) ---
                            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                            val editor = sharedPref.edit()

                            // Cek siapa yang login.
                            // Ubah "owner" di bawah ini sesuai dengan nama akun bos/owner kamu di Firebase.
                            if (nama.equals("owner", ignoreCase = true)) {
                                editor.putString("role", "owner")
                            } else {
                                editor.putString("role", "admin")
                            }
                            editor.apply()
                            // -----------------------------------------------

                            // Login sukses!
                            Toast.makeText(this, "Selamat Datang, $nama!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Login gagal
                            Toast.makeText(this, "Nama atau Password Salah!", Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                if (nama.isEmpty()) etNama.error = "Isi Nama"
                if (password.isEmpty()) etPassword.error = "Isi Password"
            }
        }
    }
}