package com.ppl3.appzonabuket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Sembunyikan Action Bar
        supportActionBar?.hide()

        // Panggil ImageView menggunakan ID-nya
        val imgLogo: ImageView = findViewById(R.id.imgLogo)

        // Memulai animasi Fade In pada gambar
        imgLogo.animate().alpha(1f).setDuration(1500).withEndAction {

            // Jeda 1 detik lalu pindah ke Login
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        }
    }
}