package com.ppl3.appzonabuket

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ManageAdminActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout

    // 1. JADIKAN VARIABEL GLOBAL AGAR BISA DIAKSES DI FUNGSI POPUP
    private val dataAdmin = mutableListOf<Admin>()
    private lateinit var adapter: AdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_admin)

        drawerLayout = findViewById(R.id.drawerLayout)

        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // --- KODE MANAGE ADMIN (RECYCLERVIEW) ---
        val rvAdmin: RecyclerView = findViewById(R.id.rvAdmin)
        val btnTambahAdmin: MaterialButton = findViewById(R.id.btnTambahAdmin)

        // 2. ISI DATA AWAL
        if (dataAdmin.isEmpty()) {
            dataAdmin.add(Admin(1, "Jessica Admin 1"))
            dataAdmin.add(Admin(2, "Elio Admin 2"))
            dataAdmin.add(Admin(3, "Eden Admin 3"))
        }

        rvAdmin.layoutManager = LinearLayoutManager(this)

        // 3. INISIALISASI ADAPTER
        adapter = AdminAdapter(
            listAdmin = dataAdmin,
            onEditClick = { adminYangDipilih ->
                tampilkanDialogAdmin(isEdit = true, admin = adminYangDipilih)
            },
            onDeleteClick = { adminYangDihapus, posisi ->
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Hapus Admin")
                builder.setMessage("Apakah Anda yakin ingin menghapus ${adminYangDihapus.nama}?")

                builder.setPositiveButton("Hapus") { dialog, which ->
                    dataAdmin.removeAt(posisi)
                    adapter.notifyItemRemoved(posisi)
                    adapter.notifyItemRangeChanged(posisi, dataAdmin.size)
                    Toast.makeText(this, "${adminYangDihapus.nama} berhasil dihapus", Toast.LENGTH_SHORT).show()
                }

                builder.setNegativeButton("Batal") { dialog, which -> dialog.dismiss() }
                builder.show()
            }
        )

        rvAdmin.adapter = adapter

        // Aksi untuk tombol Tambah Admin
        btnTambahAdmin.setOnClickListener {
            tampilkanDialogAdmin(isEdit = false)
        }
    }

    // --- FUNGSI UNTUK MENAMPILKAN POPUP PIN CUSTOM KEYPAD ---
    private fun showPinDialog(targetActivity: Class<*>) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_pin, null)
        val tvPinIndicator = dialogView.findViewById<TextView>(R.id.tvPinIndicator)
        val btnDelete = dialogView.findViewById<ImageButton>(R.id.btnDelete)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

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
                                Toast.makeText(this, "Akses Diberikan", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                startActivity(Intent(this, targetActivity))
                            } else {
                                Toast.makeText(this, "PIN Salah!", Toast.LENGTH_SHORT).show()
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

    // --- FUNGSI UNTUK MEMUNCULKAN POPUP TAMBAH/EDIT ADMIN ---
    private fun tampilkanDialogAdmin(isEdit: Boolean, admin: Admin? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_admin, null)

        val etNama = dialogView.findViewById<EditText>(R.id.etNamaAdminDialog)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPasswordAdminDialog)
        val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btnSimpanAdminDialog)

        if (isEdit && admin != null) {
            etNama.setText(admin.nama)
            etPassword.setText("******")

            etPassword.isFocusable = false
            etPassword.isClickable = false
            etPassword.isCursorVisible = false
            etPassword.setTextColor(Color.parseColor("#999999"))
        }

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        // 4. LOGIKA TOMBOL SIMPAN
        btnSimpan.setOnClickListener {
            val namaBaru = etNama.text.toString().trim()

            if (namaBaru.isEmpty()) {
                Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEdit && admin != null) {
                // LOGIKA EDIT: Cari posisi admin lama, lalu update dengan data baru
                val index = dataAdmin.indexOf(admin)
                if (index != -1) {
                    // Kita asumsikan format data class adalah Admin(id, nama)
                    dataAdmin[index] = Admin(admin.id, namaBaru)

                    // Beritahu adapter ada perubahan di baris tersebut
                    adapter.notifyItemChanged(index)
                    Toast.makeText(this, "Nama Admin berhasil diubah", Toast.LENGTH_SHORT).show()
                }
            } else {
                // LOGIKA TAMBAH: Buat ID baru dan tambahkan ke list
                val idBaru = if (dataAdmin.isNotEmpty()) dataAdmin.last().id + 1 else 1
                val adminBaru = Admin(idBaru, namaBaru)

                dataAdmin.add(adminBaru)

                // Beritahu adapter ada data baru di baris paling bawah
                adapter.notifyItemInserted(dataAdmin.size - 1)
                Toast.makeText(this, "Admin baru berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            }

            alertDialog.dismiss()
        }
    }
}