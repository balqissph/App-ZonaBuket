package com.ppl3.appzonabuket

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

// Tambahan Import Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class KeranjangActivity : AppCompatActivity() {

    private lateinit var recyclerCart: RecyclerView
    private lateinit var adapter: CartAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var tvTotal: TextView
    private lateinit var etNotes: EditText
    private lateinit var tvLabelNotes: TextView

    private var metodeTerpilih: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_keranjang)

        // 1. Inisialisasi View
        drawerLayout = findViewById(R.id.drawerLayoutKeranjang)
        recyclerCart = findViewById(R.id.recyclerCart)
        tvTotal = findViewById(R.id.tvTotal)
        etNotes = findViewById(R.id.etNotes)
        tvLabelNotes = findViewById(R.id.tvLabelNotes)

        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnCheckout = findViewById<Button>(R.id.btnCheckout)
        val tabRekomendasi = findViewById<TextView>(R.id.tabRekomendasi)
        val tabKatalog = findViewById<TextView>(R.id.tabKatalog)

        // 2. Setup Sidebar
        setupSidebar()

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 3. Setup RecyclerView & Adapter
        recyclerCart.layoutManager = LinearLayoutManager(this)

        // Mengirimkan MutableList dan Callback untuk update total otomatis
        adapter = CartAdapter(CartManager.cartItems) {
            updateTampilanKeranjang()
        }
        recyclerCart.adapter = adapter

        // Update tampilan awal
        updateTampilanKeranjang()

        // 4. Logika Pilih Metode Pembayaran
        val btnQris = findViewById<MaterialCardView>(R.id.btnQris)
        val btnBca = findViewById<MaterialCardView>(R.id.btnBca)
        val btnMandiri = findViewById<MaterialCardView>(R.id.btnMandiri)
        val btnTunai = findViewById<MaterialCardView>(R.id.btnTunai)
        val listMetode = listOf(btnQris, btnBca, btnMandiri, btnTunai)

        btnQris.setOnClickListener { metodeTerpilih = "QRIS"; updateWarnaSeleksi(btnQris, listMetode) }
        btnBca.setOnClickListener { metodeTerpilih = "BCA"; updateWarnaSeleksi(btnBca, listMetode) }
        btnMandiri.setOnClickListener { metodeTerpilih = "MANDIRI"; updateWarnaSeleksi(btnMandiri, listMetode) }
        btnTunai.setOnClickListener { metodeTerpilih = "TUNAI"; updateWarnaSeleksi(btnTunai, listMetode) }

        // 5. Logika Checkout
        btnCheckout.setOnClickListener {
            if (CartManager.cartItems.isEmpty()) {
                Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show()
            } else if (metodeTerpilih == null) {
                Toast.makeText(this, "Pilih metode pembayaran dulu!", Toast.LENGTH_SHORT).show()
            } else {
                prosesPembayaran(metodeTerpilih!!)
            }
        }

        // 6. Navigasi Tab
        tabRekomendasi.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        tabKatalog.setOnClickListener {
            startActivity(Intent(this, KatalogActivity::class.java))
            finish()
        }
    }

    private fun updateTampilanKeranjang() {
        // Hitung Total
        var total = 0
        for (item in CartManager.cartItems) {
            total += (item.price * item.qty)
        }
        tvTotal.text = "Total : Rp. $total"

        // Visibility Notes khusus "Buket Uang"
        val adaBuketUang = CartManager.cartItems.any {
            it.name.contains("Buket Uang", ignoreCase = true)
        }

        if (adaBuketUang) {
            tvLabelNotes.visibility = View.VISIBLE
            etNotes.visibility = View.VISIBLE
        } else {
            tvLabelNotes.visibility = View.GONE
            etNotes.visibility = View.GONE
            etNotes.text.clear()
        }
    }

    private fun updateWarnaSeleksi(selected: MaterialCardView, allCards: List<MaterialCardView>) {
        for (card in allCards) {
            if (card == selected) {
                card.setCardBackgroundColor(Color.parseColor("#D3D3D3"))
                card.setStrokeColor(Color.parseColor("#3BE000"))
                card.strokeWidth = 4
            } else {
                card.setCardBackgroundColor(Color.parseColor("#F4F4F4"))
                card.strokeWidth = 0
            }
        }
    }

    private fun prosesPembayaran(metode: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.pembayaran, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgPayment = dialogView.findViewById<ImageView>(R.id.imgPayment)
        val layoutVa = dialogView.findViewById<LinearLayout>(R.id.layoutVa)
        val tvVaNumber = dialogView.findViewById<TextView>(R.id.tvVaNumber)

        when (metode) {
            "QRIS" -> {
                imgPayment.setImageResource(R.drawable.qr_code)
                imgPayment.visibility = View.VISIBLE
                layoutVa.visibility = View.GONE
            }
            "BCA", "MANDIRI" -> {
                imgPayment.visibility = View.GONE
                layoutVa.visibility = View.VISIBLE
                tvVaNumber.text = if (metode == "BCA") "6289 7262 9198 55" else "1370 0123 4567 89"
            }
            "TUNAI" -> {
                imgPayment.setImageResource(R.drawable.done)
                imgPayment.visibility = View.VISIBLE
                layoutVa.visibility = View.GONE
            }
        }

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (metode != "TUNAI") {
                layoutVa.visibility = View.GONE
                imgPayment.visibility = View.VISIBLE
                imgPayment.setImageResource(R.drawable.done)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                // Memanggil fungsi simpan ke Firebase setelah animasi selesai
                simpanPesananKeFirebase(metode, dialog)
            }, 2000)
        }, 3000)
    }

    // --- FUNGSI BARU UNTUK MENYIMPAN KE FIREBASE ---
    private fun simpanPesananKeFirebase(metode: String, dialog: AlertDialog) {
        val db = FirebaseFirestore.getInstance()

        // 1. Hitung total harga
        var totalBelanja = 0
        for (item in CartManager.cartItems) {
            totalBelanja += (item.price * item.qty)
        }

        // 2. Siapkan data master pesanan
        val pesananData = hashMapOf(
            "metode_pembayaran" to metode,
            "status_pembayaran" to "Lunas",
            "total_harga" to totalBelanja,
            "tanggal_pesanan" to Timestamp.now(), // Menggunakan waktu dari server
            "catatan" to etNotes.text.toString()
        )

        // 3. Simpan data ke koleksi 'pesanan'
        db.collection("pesanan").add(pesananData)
            .addOnSuccessListener { documentRef ->
                // Mendapatkan ID Document pesanan yang baru dibuat
                val idPesananBaru = documentRef.id

                // 4. Simpan setiap item di keranjang ke koleksi 'detail_pesanan'
                for (item in CartManager.cartItems) {
                    val detailData = hashMapOf(
                        "id_pesanan" to idPesananBaru, // Foreign key ke tabel pesanan
                        "nama_produk" to item.name,
                        "jumlah" to item.qty,
                        "harga_satuan" to item.price,
                        "total_harga_item" to (item.price * item.qty)
                    )
                    db.collection("detail_pesanan").add(detailData)
                }

                // 5. Tutup dialog, bersihkan keranjang, dan pindah ke Beranda
                dialog.dismiss()
                Toast.makeText(this, "Pesanan Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                CartManager.cartItems.clear()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                dialog.dismiss()
                Toast.makeText(this, "Gagal membuat pesanan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSidebar() {
        findViewById<LinearLayout>(R.id.menuProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        findViewById<LinearLayout>(R.id.menuLaporan).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                showPinDialog(LaporanActivity::class.java)
            }, 250)
        }

        findViewById<LinearLayout>(R.id.menuManajemen).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                showPinDialog(ProdukActivity::class.java)
            }, 250)
        }

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showPinDialog(targetActivity: Class<*>) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_pin, null)
        val tvPinIndicator = dialogView.findViewById<TextView>(R.id.tvPinIndicator)
        val btnDelete = dialogView.findViewById<ImageButton>(R.id.btnDelete)

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
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
                    enteredPin += (view as TextView).text.toString()
                    tvPinIndicator.text = "●".repeat(enteredPin.length)

                    if (enteredPin.length == 6) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (enteredPin == correctPin) {
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}