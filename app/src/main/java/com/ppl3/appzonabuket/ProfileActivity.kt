package com.ppl3.appzonabuket

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etName: EditText
    private lateinit var etPassword: EditText

    private var currentUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // 1. Inisialisasi View (Sudah disamakan dengan ID di XML kamu)
        etName = findViewById(R.id.etNama)
        etPassword = findViewById(R.id.etPassword)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnEdit = findViewById<MaterialButton>(R.id.btnEdit)

        auth = FirebaseAuth.getInstance()

        // 2. Ambil nama langsung dari SharedPreferences (yang disimpan saat login)
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        currentUsername = sharedPref.getString("username", null)

        // 3. Tampilkan data ke layar
        if (currentUsername != null) {
            etName.setText(currentUsername) // Nama langsung terisi!

            // Firebase Auth tidak mengizinkan kita membaca password asli pengguna
            // Jadi kita tampilkan simbol bintang-bintang saja di layar
            etPassword.setText("******")
        } else {
            Toast.makeText(this, "Sesi tidak ditemukan! Silakan Login ulang.", Toast.LENGTH_LONG).show()
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        btnEdit.setOnClickListener {
            if (currentUsername != null) {
                showEditPasswordDialog()
            } else {
                Toast.makeText(this, "Sesi tidak valid...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.edit_password, null)
        val builder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val etOld = dialogView.findViewById<EditText>(R.id.etPasswordLama)
        val etNew = dialogView.findViewById<EditText>(R.id.etPasswordBaru)
        val etConfirm = dialogView.findViewById<EditText>(R.id.etKonfirmasiPassword)
        val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btnSimpan)

        btnSimpan.setOnClickListener {
            val oldInput = etOld.text.toString().trim()
            val newInput = etNew.text.toString().trim()
            val confirmInput = etConfirm.text.toString().trim()

            // Validasi Kosong
            if (oldInput.isEmpty() || newInput.isEmpty() || confirmInput.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi Konfirmasi
            if (newInput != confirmInput) {
                Toast.makeText(this, "Konfirmasi password baru tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userAuth = auth.currentUser
            // Merangkai kembali email dummy sesuai trik di LoginActivity
            val dummyEmail = "$currentUsername@zonabuket.com"

            if (userAuth != null && userAuth.email == dummyEmail) {
                btnSimpan.isEnabled = false
                btnSimpan.text = "Memverifikasi..."

                // 4. Proses penting: Cocokkan password lama ke Firebase Auth
                val credential = EmailAuthProvider.getCredential(dummyEmail, oldInput)

                userAuth.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        btnSimpan.text = "Menyimpan..."

                        // 5. Update password di sistem Authentication
                        userAuth.updatePassword(newInput).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(this, "Password berhasil diubah!", Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                            } else {
                                Toast.makeText(this, "Gagal mengubah: ${updateTask.exception?.message}", Toast.LENGTH_LONG).show()
                                btnSimpan.isEnabled = true
                                btnSimpan.text = "Simpan"
                            }
                        }
                    } else {
                        Toast.makeText(this, "Password lama yang Anda ketik salah!", Toast.LENGTH_SHORT).show()
                        btnSimpan.isEnabled = true
                        btnSimpan.text = "Simpan"
                    }
                }
            } else {
                Toast.makeText(this, "Terjadi kesalahan pada sesi akun.", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
}