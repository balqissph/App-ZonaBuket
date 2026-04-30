package com.ppl3.appzonabuket

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ManageAdminActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout

    private val dataAdmin = mutableListOf<Admin>()
    private lateinit var adapter: AdminAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_admin)

        drawerLayout = findViewById(R.id.drawerLayout)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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

        val rvAdmin: RecyclerView = findViewById(R.id.rvAdmin)
        val btnTambahAdmin: MaterialButton = findViewById(R.id.btnTambahAdmin)

        rvAdmin.layoutManager = LinearLayoutManager(this)

        adapter = AdminAdapter(
            listAdmin = dataAdmin,
            onEditClick = { adminYangDipilih ->
                tampilkanDialogAdmin(isEdit = true, admin = adminYangDipilih)
            },
            onDeleteClick = { adminYangDihapus, posisi ->
                konfirmasiHapusAdmin(adminYangDihapus, posisi)
            }
        )
        rvAdmin.adapter = adapter

        // Muat data saat halaman dibuka
        loadDataDariFirestore()

        btnTambahAdmin.setOnClickListener {
            tampilkanDialogAdmin(isEdit = false)
        }
    }

    private fun loadDataDariFirestore() {
        // KITA PAKAI KOLEKSI "users" AGAR SINKRON DENGAN PROFILE ACTIVITY
        db.collection("users")
            .whereEqualTo("role", "admin") // Hanya tampilkan yang role-nya admin
            .get()
            .addOnSuccessListener { result ->
                dataAdmin.clear()
                for (document in result) {
                    val idDoc = document.id
                    val nama = document.getString("nama") ?: "Tanpa Nama"
                    dataAdmin.add(Admin(idDoc, nama))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memuat data admin", Toast.LENGTH_SHORT).show()
            }
    }

    private fun konfirmasiHapusAdmin(admin: Admin, posisi: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Hapus Admin")
        builder.setMessage("Akses masuk untuk '${admin.nama}' akan diblokir dari aplikasi ini.\n\nPERHATIAN: Untuk menghapus datanya secara permanen dari server Google, Anda tetap harus menghapusnya di Website Firebase Console.")

        builder.setPositiveButton("Hapus") { dialog, which ->
            db.collection("users").document(admin.id)
                .delete()
                .addOnSuccessListener {
                    dataAdmin.removeAt(posisi)
                    adapter.notifyItemRemoved(posisi)
                    adapter.notifyItemRangeChanged(posisi, dataAdmin.size)
                    Toast.makeText(this, "${admin.nama} berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                }
        }

        builder.setNegativeButton("Batal") { dialog, which -> dialog.dismiss() }
        builder.show()
    }

    private fun tampilkanDialogAdmin(isEdit: Boolean, admin: Admin? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_admin, null)

        val etNama = dialogView.findViewById<EditText>(R.id.etNamaAdminDialog)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPasswordAdminDialog)
        val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btnSimpanAdminDialog)

        if (isEdit && admin != null) {
            // JIKA MODE EDIT: Kunci mati kolom password!
            etNama.setText(admin.nama)
            etPassword.setText("******")
            etPassword.isEnabled = false // Mematikan kolom agar tidak bisa diklik
            etPassword.setTextColor(Color.parseColor("#999999"))
        }

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        btnSimpan.setOnClickListener {
            val namaBaru = etNama.text.toString().trim()
            val passwordBaru = etPassword.text.toString().trim() // Hanya dipakai saat Tambah Admin

            if (namaBaru.isEmpty()) {
                Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEdit && admin != null) {
                // --- LOGIKA EDIT NAMA SAJA ---
                if (namaBaru == admin.nama) {
                    Toast.makeText(this, "Tidak ada perubahan.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                btnSimpan.text = "Memperbarui..."
                btnSimpan.isEnabled = false

                // 1. Ambil password asli diam-diam dari Firestore
                db.collection("users").document(admin.id).get()
                    .addOnSuccessListener { document ->
                        val passwordAsli = document.getString("password") ?: "123456" // Default jaga-jaga
                        val dummyEmailBaru = "$namaBaru@zonabuket.com"

                        // 2. Buat akun Auth baru dengan nama baru & password asli
                        auth.createUserWithEmailAndPassword(dummyEmailBaru, passwordAsli)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // 3. Update nama di database Firestore
                                    db.collection("users").document(admin.id)
                                        .update("nama", namaBaru)
                                        .addOnSuccessListener {
                                            alertDialog.dismiss()
                                            Toast.makeText(this, "Nama berhasil diedit! Untuk keamanan, Anda di-logout.", Toast.LENGTH_LONG).show()
                                            logoutDanKembaliKeLogin()
                                        }
                                } else {
                                    Toast.makeText(this, "Gagal mengedit nama: Email/Nama ini sudah pernah dipakai.", Toast.LENGTH_LONG).show()
                                    btnSimpan.text = "Simpan"
                                    btnSimpan.isEnabled = true
                                }
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal mengambil data database.", Toast.LENGTH_SHORT).show()
                        btnSimpan.text = "Simpan"
                        btnSimpan.isEnabled = true
                    }

            } else {
                // --- LOGIKA TAMBAH ADMIN BARU ---
                if (passwordBaru.length < 6) {
                    Toast.makeText(this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                btnSimpan.text = "Membuat Akun..."
                btnSimpan.isEnabled = false

                val dummyEmail = "$namaBaru@zonabuket.com"

                auth.createUserWithEmailAndPassword(dummyEmail, passwordBaru)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // SIMPAN NAMA, PASSWORD, DAN ROLE KE KOLEKSI "users"
                            val dataBaru = hashMapOf(
                                "nama" to namaBaru,
                                "password" to passwordBaru,
                                "role" to "admin"
                            )

                            db.collection("users").add(dataBaru)
                                .addOnSuccessListener {
                                    alertDialog.dismiss()
                                    Toast.makeText(this, "Admin berhasil dibuat! Untuk keamanan, Anda di-logout.", Toast.LENGTH_LONG).show()
                                    logoutDanKembaliKeLogin()
                                }
                        } else {
                            Toast.makeText(this, "Gagal membuat akun: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            btnSimpan.text = "Simpan"
                            btnSimpan.isEnabled = true
                        }
                    }
            }
        }
    }

    private fun logoutDanKembaliKeLogin() {
        auth.signOut()
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}