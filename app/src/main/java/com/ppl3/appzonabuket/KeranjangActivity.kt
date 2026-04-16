package com.ppl3.appzonabuket

import android.content.Context
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
import com.ppl3.appzonabuket.api.ApiClient
import com.ppl3.appzonabuket.api.CheckoutRequest
import com.ppl3.appzonabuket.api.CheckoutResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Import Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

// Import Midtrans
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

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
        drawerLayout = findViewById(R.id.drawerLayout) // Sesuaikan ID di XML
        recyclerCart = findViewById(R.id.recyclerCart)
        tvTotal = findViewById(R.id.tvTotal)
        etNotes = findViewById(R.id.etNotes)
        tvLabelNotes = findViewById(R.id.tvLabelNotes)

        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnCheckout = findViewById<Button>(R.id.btnCheckout)
        val tabRekomendasi = findViewById<TextView>(R.id.tabRekomendasi)
        val tabKatalog = findViewById<TextView>(R.id.tabKatalog)

        inisialisasiMidtrans()

        // 2. Setup Sidebar & Logika Role
        setupSidebar()

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 3. Setup RecyclerView & Adapter
        recyclerCart.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(CartManager.cartItems) {
            updateTampilanKeranjang()
        }
        recyclerCart.adapter = adapter

        updateTampilanKeranjang()

        // 4. Logika Pilih Metode Pembayaran (Update ID ke Tunai & Non-Tunai sesuai XML baru)
        val btnTunai = findViewById<MaterialCardView>(R.id.btnTunai)
        val btnNonTunai = findViewById<MaterialCardView>(R.id.btnNonTunai)
        val listMetode = listOf(btnTunai, btnNonTunai)

        btnTunai.setOnClickListener {
            metodeTerpilih = "TUNAI"
            updateWarnaSeleksi(btnTunai, listMetode)
        }
        btnNonTunai.setOnClickListener {
            metodeTerpilih = "NON-TUNAI"
            updateWarnaSeleksi(btnNonTunai, listMetode)
        }

        // 5. Logika Checkout
        btnCheckout.setOnClickListener {
            if (CartManager.cartItems.isEmpty()) {
                Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show()
            } else if (metodeTerpilih == null) {
                Toast.makeText(this, "Pilih metode pembayaran dulu!", Toast.LENGTH_SHORT).show()
            } else {
                if (metodeTerpilih == "TUNAI") {
                    prosesPembayaran(metodeTerpilih!!)
                } else {
                    // --- MINTA TOKEN KE LARAVEL MENGGUNAKAN RETROFIT ---
                    Toast.makeText(this, "Menghubungi server...", Toast.LENGTH_SHORT).show()

                    // Hitung total belanja saat ini
                    var totalBelanja = 0
                    for (item in CartManager.cartItems) {
                        totalBelanja += (item.price * item.qty)
                    }

                    // Siapkan data yang mau dikirim
                    val requestData = CheckoutRequest(totalBelanja)

                    // Panggil API
                    ApiClient.instance.getSnapToken(requestData).enqueue(object : Callback<CheckoutResponse> {
                        override fun onResponse(call: Call<CheckoutResponse>, response: Response<CheckoutResponse>) {
                            if (response.isSuccessful) {
                                val snapToken = response.body()?.snapToken

                                if (snapToken != null) {
                                    // Token sukses didapat, langsung buka UI Midtrans!
                                    mulaiPembayaranMidtrans(snapToken)
                                } else {
                                    Toast.makeText(this@KeranjangActivity, "Error: Token kosong", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Tangkap pesan error asli dari Laravel
                                val errorCode = response.code()
                                val errorMessage = response.errorBody()?.string()

                                Toast.makeText(this@KeranjangActivity, "Error $errorCode: $errorMessage", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                            Toast.makeText(this@KeranjangActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
                }
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

    private fun setupSidebar() {
        // Inisialisasi View Sidebar
        val menuProfile = findViewById<LinearLayout>(R.id.menuProfile)
        val menuLaporan = findViewById<LinearLayout>(R.id.menuLaporan)
        val menuManajemen = findViewById<LinearLayout>(R.id.menuManajemen)
        val menuWaitingPayment = findViewById<LinearLayout>(R.id.menuWaitingPayment)
        val menuManajemenAdmin = findViewById<LinearLayout>(R.id.menuManajemenAdmin)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        // Logika Role
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userRole = sharedPref.getString("role", "admin")

        if (userRole == "owner") {
            menuManajemenAdmin.visibility = View.VISIBLE
        } else {
            menuManajemenAdmin.visibility = View.GONE
        }

        // Waiting payment selalu muncul untuk siapapun
        menuWaitingPayment.visibility = View.VISIBLE

        // Click Listeners Sidebar
        menuProfile.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, ProfileActivity::class.java))
            }, 250)
        }

        menuLaporan.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            showPinDialog(LaporanActivity::class.java)
        }

        menuManajemen.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            showPinDialog(ProdukActivity::class.java)
        }

        menuWaitingPayment.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, WaitingPaymentActivity::class.java))
            }, 250)
        }

        menuManajemenAdmin.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            showPinDialog(ManageAdminActivity::class.java)
        }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Iya") { _, _ ->
                    sharedPref.edit().clear().apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    // --- MIDTRANS HELPERS ---
    private fun inisialisasiMidtrans() {
        SdkUIFlowBuilder.init()
            .setClientKey("Mid-client-u5kXP0SEXFv9L379")
            .setContext(this)
            .setTransactionFinishedCallback { result -> handleHasilPembayaranMidtrans(result) }
            .setMerchantBaseUrl("http://10.70.1.244 :8000/api/")
            .enableLog(true)
            .buildSDK()
    }

    private fun mulaiPembayaranMidtrans(snapToken: String) {
        MidtransSDK.getInstance().startPaymentUiFlow(this, snapToken)
    }

    private fun handleHasilPembayaranMidtrans(result: TransactionResult) {
        when (result.status) {
            TransactionResult.STATUS_SUCCESS, TransactionResult.STATUS_PENDING -> {
                val statusSimpan = if (result.status == TransactionResult.STATUS_SUCCESS) "Lunas" else "Menunggu Pembayaran"
                Toast.makeText(this, "Pembayaran diproses!", Toast.LENGTH_SHORT).show()
                simpanPesananKeFirebase(metodeTerpilih ?: "NON-TUNAI", statusSimpan, "Via Midtrans")
                selesaikanCheckout("Pesanan berhasil masuk sistem!")
            }
            TransactionResult.STATUS_FAILED -> Toast.makeText(this, "Pembayaran Gagal", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "Transaksi Dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTampilanKeranjang() {
        var total = 0
        for (item in CartManager.cartItems) {
            total += (item.price * item.qty)
        }
        tvTotal.text = "Total : Rp $total"
        tvLabelNotes.visibility = View.VISIBLE
        etNotes.visibility = View.VISIBLE
    }

    private fun updateWarnaSeleksi(selected: MaterialCardView, allCards: List<MaterialCardView>) {
        for (card in allCards) {
            if (card == selected) {
                card.setCardBackgroundColor(Color.parseColor("#D3D3D3"))
                card.setStrokeColor(Color.parseColor("#3BE000"))
                card.strokeWidth = 4
            } else {
                card.setCardBackgroundColor(Color.parseColor("#F4F4F4"))
                card.strokeWidth = 1 // strokeWidth 1 sesuai XML
                card.setStrokeColor(Color.parseColor("#E0E0E0"))
            }
        }
    }

    private fun prosesPembayaran(metode: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.pembayaran, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).setCancelable(false).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgPayment = dialogView.findViewById<ImageView>(R.id.imgPayment)
        val statusPesanan = if (metode == "TUNAI") "Lunas" else "Menunggu Pembayaran"

        imgPayment.setImageResource(R.drawable.done)
        imgPayment.visibility = View.VISIBLE

        dialog.show()
        simpanPesananKeFirebase(metode, statusPesanan, "-")

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            selesaikanCheckout("Pesanan Berhasil!")
        }, 2000)
    }

    private fun simpanPesananKeFirebase(metode: String, status: String, nomorVa: String) {
        val db = FirebaseFirestore.getInstance()
        var totalBelanja = 0
        for (item in CartManager.cartItems) { totalBelanja += (item.price * item.qty) }

        val pesananData = hashMapOf(
            "metode_pembayaran" to metode,
            "status" to status,
            "nomor_va" to nomorVa,
            "total_harga" to totalBelanja,
            "tanggal_pesanan" to Timestamp.now(),
            "catatan" to etNotes.text.toString()
        )

        db.collection("pesanan").add(pesananData).addOnSuccessListener { doc ->
            for (item in CartManager.cartItems) {
                val detail = hashMapOf(
                    "id_pesanan" to doc.id,
                    "nama_produk" to item.name,
                    "jumlah" to item.qty,
                    "harga_satuan" to item.price
                )
                db.collection("detail_pesanan").add(detail)
            }
        }
    }

    private fun selesaikanCheckout(pesan: String) {
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
        CartManager.cartItems.clear()
        startActivity(Intent(this, SuccessActivity::class.java))
        finish()
    }

    private fun showPinDialog(targetActivity: Class<*>) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_pin, null)
        val tvPinIndicator = dialogView.findViewById<TextView>(R.id.tvPinIndicator)
        val btnDelete = dialogView.findViewById<ImageButton>(R.id.btnDelete)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var enteredPin = ""
        val correctPin = "123456"
        val numberButtons = listOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9)

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