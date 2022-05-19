package com.example.sqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val i: Intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(i)
        /*
        // Mantenemos la pantalla unos segundos y después cambia al main

        Handler(Looper.getMainLooper()).postDelayed({
            // Your Code
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }, 5000)*/
    }
}