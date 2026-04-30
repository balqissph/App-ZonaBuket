package com.ppl3.appzonabuket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
                val dummyEmail = "$nama@zonabuket.com"

                // Ubah tampilan tombol saat proses loading
                btnLogin.text = "Memuat..."
                btnLogin.isEnabled = false

                // Proses login ke Firebase Auth
                auth.signInWithEmailAndPassword(dummyEmail, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            // --- CEK STATUS AKUN (BLOKIR ADMIN TERHAPUS) ---
                            if (nama.equals("owner", ignoreCase = true)) {
                                // Owner punya akses bebas hambatan
                                simpanSesiDanMasuk(nama, "owner")
                            } else {
                                // Jika Admin, kita cek dulu apakah namanya masih ada di daftar Firestore
                                val db = FirebaseFirestore.getInstance()

                                // UBAH DI SINI: Sekarang mencari ke koleksi "users"
                                db.collection("users")
                                    .whereEqualTo("nama", nama)
                                    .whereEqualTo("role", "admin") // Ekstra aman: pastikan rolenya admin
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        if (!documents.isEmpty) {
                                            // Nama admin MASIH ADA di daftar, izinkan masuk
                                            simpanSesiDanMasuk(nama, "admin")
                                        } else {
                                            // NAMA SUDAH DIHAPUS OLEH OWNER!
                                            auth.signOut() // Tendang paksa agar sesi Auth-nya mati
                                            Toast.makeText(this, "Akses Ditolak! Akun ini telah dihapus oleh Owner.", Toast.LENGTH_LONG).show()

                                            // Kembalikan tombol seperti semula
                                            btnLogin.text = "Login"
                                            btnLogin.isEnabled = true
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Gagal memverifikasi status admin.", Toast.LENGTH_SHORT).show()
                                        auth.signOut()

                                        // Kembalikan tombol seperti semula
                                        btnLogin.text = "Login"
                                        btnLogin.isEnabled = true
                                    }
                            }
                        } else {
                            // Login gagal (Password salah atau memang tidak terdaftar sama sekali)
                            Toast.makeText(this, "Nama atau Password Salah!", Toast.LENGTH_SHORT).show()
                            btnLogin.text = "Login"
                            btnLogin.isEnabled = true
                        }
                    }

            } else {
                if (nama.isEmpty()) etNama.error = "Isi Nama"
                if (password.isEmpty()) etPassword.error = "Isi Password"
            }
        }
    }

    // --- FUNGSI HELPER UNTUK MENGURANGI KODE BERULANG ---
    private fun simpanSesiDanMasuk(nama: String, role: String) {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString("username", nama)
        editor.putString("role", role)
        editor.apply()

        Toast.makeText(this, "Selamat Datang, $nama!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}