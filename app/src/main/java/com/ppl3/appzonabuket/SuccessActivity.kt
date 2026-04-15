package com.ppl3.appzonabuket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        // Hilangkan Action Bar atas agar terlihat bersih
        supportActionBar?.hide()

        val btnKembaliBeranda: MaterialButton = findViewById(R.id.btnKembaliBeranda)

        btnKembaliBeranda.setOnClickListener {
            // Pindah kembali ke halaman utama (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            // Bersihkan histori layar sebelumnya agar jika ditekan tombol "Back",
            // tidak kembali ke layar Sukses ini.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}