package com.example.sqlite

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.sqlite.Clases.DBEstadisticas
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : AppCompatActivity() {
    // Creación
    lateinit var miTextViewAciertos: TextView
    lateinit var miTextViewFallos: TextView
    lateinit var miImageViewVolverResult: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Request para cargar el anuncio
        val adRequest = AdRequest.Builder().build()

        // Inicialización
        miTextViewAciertos = findViewById(R.id.textViewAciertos)
        miTextViewFallos = findViewById(R.id.textViewFallos)
        miImageViewVolverResult = findViewById(R.id.imageViewVolverResult)

        // Cargamos el anuncio llamando a la función
        cargarAnuncio(adRequest)

        // Recuperamos los datos que recibimos de la anterior activity
        val aciertos: Int = intent.extras!!.getInt("aciertos")
        val fallos: Int = intent.extras!!.getInt("fallos")
        val date = SimpleDateFormat("dd/MM/yyyy")
        val actualDate = date.format(Date()).toString()

        // Seteamos los textview con los resultados
        miTextViewAciertos.setText("Aciertos: " +aciertos.toString())
        miTextViewFallos.setText("Fallos: " +fallos.toString())

        // Guardamos la información en la base de datos
        guardarDatosEstadisticos(aciertos, fallos, actualDate)

        // Listener el botón que vuelve al menú
        miImageViewVolverResult.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }

    // Impedimos que el usuario pueda volver a la activity anterior pulsando el botón atrás
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    /**
     * Función que guarda los datos en la base de datos automáticamente al entrar en la activity
     */
    fun guardarDatosEstadisticos(aciertos: Int, fallos: Int, actualDate: String){
        val admin = DBEstadisticas(this)
        val db = admin.writableDatabase

        db.execSQL("Insert into stadistics_data (aciertos, fallos, fecha) values("+aciertos+", " +fallos+", "+ actualDate+")")

        admin.close()
        db.close()
    }

    /**
     * Función que carga el anuncio intersticial nada más entrar en la activity
     */
    fun cargarAnuncio(adRequest: AdRequest){
        var mInterstitialAd: InterstitialAd? = null

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("TAG", "Ad was loaded.")
                mInterstitialAd = interstitialAd

                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(this@ResultActivity)
                } else {
                    Log.e(TAG, "The interstitial ad wasn't ready yet.")
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("TAG", adError.message)
                mInterstitialAd = null
            }
        })
    }
}