package com.ppl3.appzonabuket

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ManageAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_admin)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rvAdmin: RecyclerView = findViewById(R.id.rvAdmin)
        val btnTambahAdmin: MaterialButton = findViewById(R.id.btnTambahAdmin)

        val dataAdmin = listOf(
            Admin(1, "Jessica Admin 1"),
            Admin(2, "Elio Admin 2"),
            Admin(3, "Eden Admin 3")
        )

        rvAdmin.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter dengan aksi Edit
        rvAdmin.adapter = AdminAdapter(dataAdmin) { adminYangDipilih ->
            // Munculkan popup mode EDIT
            tampilkanDialogAdmin(isEdit = true, admin = adminYangDipilih)
        }

        // Aksi untuk tombol Tambah Admin di bawah layar
        btnTambahAdmin.setOnClickListener {
            // Munculkan popup mode TAMBAH
            tampilkanDialogAdmin(isEdit = false)
        }
    }

    // Fungsi untuk memunculkan Popup
    private fun tampilkanDialogAdmin(isEdit: Boolean, admin: Admin? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_admin, null)

        val etNama = dialogView.findViewById<EditText>(R.id.etNamaAdminDialog)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPasswordAdminDialog)
        val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btnSimpanAdminDialog)

        // Logika Mode Edit
        if (isEdit && admin != null) {
            etNama.setText(admin.nama)
            etPassword.setText("******") // Sembunyikan password asli

            // Kunci field password agar tidak bisa diedit
            etPassword.isFocusable = false
            etPassword.isClickable = false
            etPassword.isCursorVisible = false
            etPassword.setTextColor(Color.parseColor("#999999")) // Ubah warna teks jadi agak pudar
        }

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        btnSimpan.setOnClickListener {
            // Tempat untuk menyimpan data ke database (nantinya)

            alertDialog.dismiss()
        }
    }
}