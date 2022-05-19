package com.example.sqlite

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import com.example.sqlite.Clases.DBConfig

class SettingsActivity : AppCompatActivity() {
    // Creación
    lateinit var miButtonVolverSettings: ImageView
    lateinit var miButton1second: Button
    lateinit var miButton2second: Button
    lateinit var miButton3second: Button
    lateinit var miButton4second: Button
    lateinit var miButton5second: Button
    lateinit var miCheckSettings: CheckBox
    // Variable booleana que indicará si debemos cambiar de activity
    var cambioActivity: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Inicialización
        miButtonVolverSettings = findViewById(R.id.imageViewVolverSettings)
        miButton1second = findViewById(R.id.button1second)
        miButton2second = findViewById(R.id.button2second)
        miButton3second = findViewById(R.id.button3second)
        miButton4second = findViewById(R.id.button4second)
        miButton5second = findViewById(R.id.button5second)
        miCheckSettings = findViewById(R.id.checkBoxSettings)

        // Establecemos un diseño de botones por defecto
        miButton1second.setBackgroundColor(Color.DKGRAY)
        miButton2second.setBackgroundColor(Color.DKGRAY)
        miButton3second.setBackgroundColor(Color.DKGRAY)
        miButton4second.setBackgroundColor(Color.DKGRAY)
        miButton5second.setBackgroundColor(Color.DKGRAY)

        // Evitamos que se produzca un error en caso de no recibir una confirmación de cambio de activity del Main
        try{
            cambioActivity = intent.extras!!.getBoolean("cambio")
        }catch (e: NullPointerException){
            Log.e("TAG", "Error null")
        }

        // Cargamos publicidad
        cargarDatosCheckPubli()
        // Cargamos los datos de la BD para que aparezca el botón pulsado
        cargarDatosSecond()

        // Listener de imageview para volver al main
        miButtonVolverSettings.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        // Listener que escucha si marcamos o desmarcamos el tema del seguimiento de la publicidad
        // Se comprueba en la BD si está guardada la opción.
        miCheckSettings.setOnClickListener {
            // COnexión con la BD
            val admin = DBConfig(this)
            val db = admin.writableDatabase

            // Comprobamos si el check estña marcado o desmarcado para cambiar el ajuste en la BD
            if (miCheckSettings.isChecked){
                db.execSQL("UPDATE publi SET publi_activated = 1")
            } else {
                db.execSQL("UPDATE publi SET publi_activated = 0")
            }

            // Cerramos conexiones
            admin.close()
            db.close()
        }

        // Listener de los botones para cambiar los segundos que pasan entre pregunta y pregunta fallada
        miButton1second.setOnClickListener {
            if(cambioActivity == true){
                startActivity(Intent(applicationContext, TestActivity::class.java))
            }

            miButton1second.setBackgroundColor(Color.GREEN)
            miButton2second.setBackgroundColor(Color.DKGRAY)
            miButton3second.setBackgroundColor(Color.DKGRAY)
            miButton4second.setBackgroundColor(Color.DKGRAY)
            miButton5second.setBackgroundColor(Color.DKGRAY)

            guardarDatosSecond(1000)
        }

        miButton2second.setOnClickListener {
            if(cambioActivity == true){
                startActivity(Intent(applicationContext, TestActivity::class.java))
            }

            miButton1second.setBackgroundColor(Color.DKGRAY)
            miButton2second.setBackgroundColor(Color.GREEN)
            miButton3second.setBackgroundColor(Color.DKGRAY)
            miButton4second.setBackgroundColor(Color.DKGRAY)
            miButton5second.setBackgroundColor(Color.DKGRAY)

            guardarDatosSecond(2000)
        }

        miButton3second.setOnClickListener {
            if(cambioActivity == true){
                startActivity(Intent(applicationContext, TestActivity::class.java))
            }

            miButton1second.setBackgroundColor(Color.DKGRAY)
            miButton2second.setBackgroundColor(Color.DKGRAY)
            miButton3second.setBackgroundColor(Color.GREEN)
            miButton4second.setBackgroundColor(Color.DKGRAY)
            miButton5second.setBackgroundColor(Color.DKGRAY)

            guardarDatosSecond(3000)
        }

        miButton4second.setOnClickListener {
            if(cambioActivity == true){
                startActivity(Intent(applicationContext, TestActivity::class.java))
            }

            miButton1second.setBackgroundColor(Color.DKGRAY)
            miButton2second.setBackgroundColor(Color.DKGRAY)
            miButton3second.setBackgroundColor(Color.DKGRAY)
            miButton4second.setBackgroundColor(Color.GREEN)
            miButton5second.setBackgroundColor(Color.DKGRAY)

            guardarDatosSecond(4000)
        }

        miButton5second.setOnClickListener {
            if(cambioActivity == true){
                startActivity(Intent(applicationContext, TestActivity::class.java))
            }

            miButton1second.setBackgroundColor(Color.DKGRAY)
            miButton2second.setBackgroundColor(Color.DKGRAY)
            miButton3second.setBackgroundColor(Color.DKGRAY)
            miButton4second.setBackgroundColor(Color.DKGRAY)
            miButton5second.setBackgroundColor(Color.GREEN)

            guardarDatosSecond(5000)
        }
    }

    /**
     * Función que marca o desmarca el check de la publicidad basada en intereses de Google.
     * Se conecta a la BD de la configuración.
     */
    private fun cargarDatosCheckPubli(){
        // Conexión con la base de datos
        val admin = DBConfig(this)
        val db = admin.readableDatabase

        // Cursor donde se almacenan datos
        val cursor = db.rawQuery("select * from publi", null)

        // Marcamos o desmarcamos el check
        if(cursor.moveToFirst()){
            if(cursor.getString(cursor.getColumnIndex("publi_activated").toInt()).toInt() == 1){
                miCheckSettings.isChecked = true
            } else {
                miCheckSettings.isChecked = false
            }
        }

        // Cerramos conexiones.
        cursor.close()
        db.close()
    }

    /**
     * Función que colorea en verde uno de los botones de los segundos. Se colorea en función de los segundos guardados en la BD.
     *
     */
    private fun cargarDatosSecond(){
        // Conexión con la base de datos
        val admin = DBConfig(this)
        val db = admin.readableDatabase

        // Cursor donde se almacenan datos
        val cursor = db.rawQuery("select * from seconds", null)

        // Movemos el cursor a la primera posición
        cursor.moveToFirst()

        // Comprobamos si el cursor está vacío
        // Si tiene datos se prodece a colorear los botones en verde
        if(cursor.count == 0){

        }else{
            when (cursor.getString(cursor.getColumnIndex("tiempo_error").toInt()).toInt()){
                1000 -> miButton1second.setBackgroundColor(Color.GREEN)

                2000 -> miButton2second.setBackgroundColor(Color.GREEN)

                3000 -> miButton3second.setBackgroundColor(Color.GREEN)

                4000 -> miButton4second.setBackgroundColor(Color.GREEN)

                5000 -> miButton5second.setBackgroundColor(Color.GREEN)
            }
        }

        // Cerramos conexiones.
        admin.close()
        db.close()
    }

    /**
     * Función que actualiza en la BD el tiempo que pasará entre pregunta y pregunta fallada.
     */
    private fun guardarDatosSecond(seconds: Int){
        // Conexión con la BD
        val admin = DBConfig(this)
        val db = admin.writableDatabase

        // Guardamos en un cursor la consulta
        val cursor = db.rawQuery("select * from seconds", null)

        // Movemos el cursor a la primera posición
        cursor.moveToFirst()

        // Si el cursor está vacío insertamos el dato en la BD
        // Si hay datos en la BD actualizamos la información
        if(cursor.count == 0){
            db.execSQL("INSERT INTO seconds (tiempo_error) values("+seconds+")")
        } else{
            db.execSQL("UPDATE seconds SET tiempo_error = " + seconds)
        }

        // Cerramos conexiones.
        admin.close()
        cursor.close()
        db.close()
    }
}