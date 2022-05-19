package com.example.sqlite

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.sqlite.Clases.DBConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


class MainActivity : AppCompatActivity() {
    // Creación
    private lateinit var miImageViewStart: ImageView
    private lateinit var miImageViewSettings: ImageView
    private lateinit var miImageViewAnswers: ImageView
    private lateinit var miAdViewBanner: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Iniciamos el servicio de anuncio
        MobileAds.initialize(this)

        // Inicializamos los botones
        miImageViewStart = findViewById(R.id.imageViewStart)
        miImageViewSettings = findViewById(R.id.imageViewSettings)
        miImageViewAnswers = findViewById(R.id.imageViewAnswers)
        miAdViewBanner = findViewById(R.id.adViewBanner)

        // Llamamos a la función que muestra el banner
        cargarBanner()

        // Listener que escucha cuándo pulsamos el botón para cambiar de activity
        miImageViewStart.setOnClickListener {
            comprobarConfig()
        }

        miImageViewSettings.setOnClickListener {
            val i = Intent(applicationContext, SettingsActivity::class.java)
            i.putExtra("cambio", false)
            startActivity(i)
        }

        miImageViewAnswers.setOnClickListener {
            startActivity(Intent(applicationContext, AnswersActivity::class.java))
        }
    }

    /*
     * Función que carga un anuncio de tipo banner
     */
    private fun cargarBanner(){
        val adRequest: AdRequest = AdRequest.Builder().build()
        miAdViewBanner.loadAd(adRequest)
    }

    private fun comprobarConfig(){
        val admin = DBConfig(this)
        val db = admin.readableDatabase

        val i: Intent

        val cursor = db.rawQuery("select * from seconds", null)

        if(cursor.count == 0){
            i = Intent(applicationContext, SettingsActivity::class.java)
            i.putExtra("cambio", true)
            startActivity(i)
        }else{
            startActivity(Intent(applicationContext, TestActivity::class.java))
        }
    }
}