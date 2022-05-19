package com.example.sqlite

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sqlite.Clases.DBHelper
import com.example.sqlite.Clases.Preguntas
import com.google.android.material.appbar.AppBarLayout
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random
import com.example.sqlite.Clases.DBConfig
import com.example.sqlite.Clases.DBPreguntasFalladas
import java.sql.SQLException


class TestActivity : AppCompatActivity() {
    // Creación
    // Lista que almacena las preguntas
    lateinit var listaPreguntas: ArrayList<Preguntas>

    // Lista que almacena los números aleatorios generados para evitar que se repita la pregunta
    lateinit var listaRandom: ArrayList<Int>

    // Elementos en pantalla
    lateinit var miTextViewA0: TextView
    lateinit var miButtonA00: Button
    lateinit var miButtonB: Button
    lateinit var miButtonC: Button
    lateinit var miButtonD: Button
    lateinit var miImageViewVolver: ImageView
    lateinit var miAppbarLayout: AppBarLayout
    lateinit var miImageViewImpugnar: ImageView

    // Variables que almacenan los aciertos y errores del usuario
    var aciertos: Int = 0
    var fallos: Int = 0

    // Constantes que almacenan el tiempo que va a estar congelada al app cuando pulsamos una respuesta
    val tiempoAcierto: Long = 1000
    var tiempoFallo: Long = 3000

    // Contraseña que se usa para codificar y descodificar
    val password: String = "Password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Inicialización
        // En esta lista se almacenan los objetos Pregunta
        listaPreguntas = ArrayList<Preguntas>()
        listaRandom = ArrayList<Int>()
        miTextViewA0 = findViewById(R.id.textViewA0)
        miButtonA00 = findViewById(R.id.buttonRespA00)
        miButtonB = findViewById(R.id.buttonRespB)
        miButtonC = findViewById(R.id.buttonRespC)
        miButtonD = findViewById(R.id.buttonRespD)
        miImageViewVolver = findViewById(R.id.imageViewVolver)
        miAppbarLayout = findViewById(R.id.appbar)
        miImageViewImpugnar = findViewById(R.id.imageViewImpugnar)

        // Número de aciertos y fallos
        aciertos = 0
        fallos = 0

        // Copiamos la base de datos a la memoria del teléfono y la codificamos
        copyDatabase()
        codificarTabla()

        // Cargamos las preguntas en la lista
        cargarListaPreguntas()

        setTiempoError()

        // Desactivarmos el botón de impugnar al principio, ya que no habrá ninguna pregunta cargada
        miImageViewImpugnar.isEnabled = false

        cargarPregunta()

        miImageViewVolver.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Listener del botón Impugnar
        miImageViewImpugnar.setOnClickListener {
            val address = arrayOf("info@estudionemo.es")
            val subject = "Pregunta impuganada"
            val message = "Impugnar la pregunta: " + miTextViewA0.text

            val intentImpugnar = Intent(Intent.ACTION_SENDTO)
            intentImpugnar.data = Uri.parse("mailto:")
            intentImpugnar.putExtra(Intent.EXTRA_EMAIL, address)
            intentImpugnar.putExtra(Intent.EXTRA_SUBJECT, subject)
            intentImpugnar.putExtra(Intent.EXTRA_TEXT, message)
            if (intentImpugnar.resolveActivity(packageManager) != null) {
                startActivity(intentImpugnar)
            }
        }
    }

    // Impedimos que el usuario pueda volver hacia atrás
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    /**
     * Función que convierte a objetos los elementos de la base de datos y los mete en la lista
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun cargarPregunta(){
        val random = Random
        var numRandom: Int = 0

        // Activamos los botones
        miButtonA00.isEnabled = true
        miButtonB.isEnabled = true
        miButtonC.isEnabled = true
        miButtonD.isEnabled = true
        miImageViewVolver.isEnabled = true
        miImageViewImpugnar.isEnabled = true

        // Volvemos el fondo de los botones transparente
        miButtonA00.setBackgroundColor(Color.TRANSPARENT)
        miButtonB.setBackgroundColor(Color.TRANSPARENT)
        miButtonC.setBackgroundColor(Color.TRANSPARENT)
        miButtonD.setBackgroundColor(Color.TRANSPARENT)

        // Hacemos que el texto de los colores sea negro
        miButtonA00.setTextColor(Color.BLACK)
        miButtonB.setTextColor(Color.BLACK)
        miButtonC.setTextColor(Color.BLACK)
        miButtonD.setTextColor(Color.BLACK)

        // Generamos un número aleatorio, que será utilizado para cargar una pregunta. Se evita que se vuelva a generar el mismo número
        // Los números se guardan en una lista de enteros
        // Si la lista está vacía se genera un número aleatorio. Si está llena se comprueba si la lista de números es del mismo tamaño que la lista de preguntas
        // Si no lo es se genera un número aleatorio y se comprueba si ya existe en la lista
        if(listaRandom.isEmpty()){
            numRandom = random.nextInt(0..listaPreguntas.size)
            listaRandom.add(numRandom)
        } else{
            if (listaRandom.size == listaPreguntas.size){
                val i = Intent(applicationContext, ResultActivity::class.java)

                // Desactivamos los botones
                miButtonA00.isEnabled = false
                miButtonB.isEnabled = false
                miButtonC.isEnabled = false
                miButtonD.isEnabled = false
                miImageViewVolver.isEnabled = false
                miImageViewImpugnar.isEnabled = false

                // Volvemos transparente el fondo de los botones
                miButtonA00.setBackgroundColor(Color.TRANSPARENT)
                miButtonB.setBackgroundColor(Color.TRANSPARENT)
                miButtonC.setBackgroundColor(Color.TRANSPARENT)
                miButtonD.setBackgroundColor(Color.TRANSPARENT)

                // Volvemos el fondo de los botones de color negro
                miButtonA00.setTextColor(Color.BLACK)
                miButtonB.setTextColor(Color.BLACK)
                miButtonC.setTextColor(Color.BLACK)
                miButtonD.setTextColor(Color.BLACK)

                // Pasamos a la otra actividad la cantidad de aciertos y fallos
                i.putExtra("aciertos", aciertos)
                i.putExtra("fallos", fallos)

                startActivity(i)
            } else{
                do{
                    numRandom = random.nextInt(0..listaPreguntas.size)

                }while (listaRandom.contains(numRandom))

                listaRandom.add(numRandom)
            }
        }

        // El objeto aleatorio cargará la pregunta y respuestas en la posición que ha generado numRandom
        miTextViewA0.setText(listaPreguntas.get(numRandom).a0)
        miButtonA00.setText(listaPreguntas.get(numRandom).a00)
        miButtonB.setText(listaPreguntas.get(numRandom).b)
        miButtonC.setText(listaPreguntas.get(numRandom).c)
        miButtonD.setText(listaPreguntas.get(numRandom).d)

        // Si la pregunta está vacía (en caso de que la pregunta solo tenga 3 respuestas) se desactiva el botón
        if (listaPreguntas.get(numRandom).d.equals("")){
            miButtonD.isEnabled = false
        }

        /**
         * En estos Listeners se comprueba si el botón pulsado coincide con la respuesta correcta.
         * Lo que hace es comparar el texto del botón con la respuesta correcta y desactivar los botones
         * Si es correcto, se pone el botón en verde. Si es incorrecta, se pone el botón en rojo y se pone en verde la correcta
         */
        miButtonA00.setOnClickListener {
            if(miButtonA00.text.equals(listaPreguntas.get(numRandom).e)){
                miButtonA00.setBackgroundColor(Color.GREEN)

                aciertos ++

                Handler(Looper.getMainLooper()).postDelayed({
                    cargarPregunta()
                }, tiempoAcierto)
            } else {
                miButtonA00.setBackgroundColor(Color.RED)

                guardarPreguntaFallada(listaPreguntas.get(numRandom))

                fallos++

                if (miButtonB.text.equals(listaPreguntas.get(numRandom).e)){
                    miButtonB.setBackgroundColor(Color.GREEN)
                } else if (miButtonC.text.equals(listaPreguntas.get(numRandom).e)){
                    miButtonC.setBackgroundColor(Color.GREEN)
                } else if (miButtonD.text.equals(listaPreguntas.get(numRandom).e)){
                    miButtonD.setBackgroundColor(Color.GREEN)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    cargarPregunta()
                }, tiempoFallo)
            }

            miButtonA00.isEnabled = false
            miButtonB.isEnabled = false
            miButtonC.isEnabled = false
            miButtonD.isEnabled = false
            miImageViewVolver.isEnabled = false
            miImageViewImpugnar.isEnabled = false
        }

        miButtonB.setOnClickListener {
            if(miButtonB.text.equals(listaPreguntas.get(numRandom).e)){
                miButtonB.setBackgroundColor(Color.GREEN)

                aciertos ++

                Handler(Looper.getMainLooper()).postDelayed({
                    cargarPregunta()
                }, tiempoAcierto)
            } else {
                miButtonB.setBackgroundColor(Color.RED)

                guardarPreguntaFallada(listaPreguntas.get(numRandom))

                fallos++

                if (miButtonA00.text.equals(listaPreguntas.get(numRandom).e)){
                    miButtonA00.setBackgroundColor(Color.GREEN)
                } else if (miButtonC.text.equals(listaPreguntas.get(numRandom).e)){
                    miButtonC.setBackgroundColor(Color.GREEN)
                } else if (miButtonD.text.equals(listaPreguntas.get(numRandom).e)){
                    miButtonD.setBackgroundColor(Color.GREEN)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    cargarPregunta()
                }, tiempoFallo)
            }

            miButtonA00.isEnabled = false
            miButtonB.isEnabled = false
            miButtonC.isEnabled = false
            miButtonD.isEnabled = false
            miImageViewVolver.isEnabled = false
            miImageViewImpugnar.isEnabled = false
        }

        miButtonC.setOnClickListener {
            if(miButtonC.text.equals(listaPreguntas.get(numRandom).e)){
                miButtonC.setBackgroundColor(Color.GREEN)

                aciertos++

                Handler(Looper.getMainLooper()).postDelayed({
                    cargarPregunta()
                }, tiempoAcierto)
            } else {
                miButtonC.setBackgroundColor(Color.RED)

                guardarPreguntaFallada(listaPreguntas.get(numRandom))

                fallos++

                if (miButtonA00.text.equals(listaPreguntas.get(numRandom).e)){
                    this.miButtonA00.setBackgroundColor(Color.GREEN)
                } else if (miButtonB.text.equals(listaPreguntas.get(numRandom).e)){
                    miButtonB.setBackgroundColor(Color.GREEN)
                }else if (miButtonD.text.equals(listaPreguntas.get(numRandom).e)){
                    miButtonD.setBackgroundColor(Color.GREEN)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    cargarPregunta()
                }, tiempoFallo)
            }

            miButtonA00.isEnabled = false
            miButtonB.isEnabled = false
            miButtonC.isEnabled = false
            miButtonD.isEnabled = false
            miImageViewVolver.isEnabled = false
            miImageViewImpugnar.isEnabled = false
        }

        miButtonD.setOnClickListener {
            if(miButtonD.text.equals(listaPreguntas.get(numRandom).e)){
                miButtonD.setBackgroundColor(Color.GREEN)

                aciertos++

                Handler(Looper.getMainLooper()).postDelayed({
                    cargarPregunta()
                }, tiempoAcierto)
            } else {
                miButtonD.setBackgroundColor(Color.RED)

                guardarPreguntaFallada(listaPreguntas.get(numRandom))

                fallos ++

                if (miButtonA00.text.equals(listaPreguntas.get(numRandom).e)){
                    miButtonA00.setBackgroundColor(Color.GREEN)
                } else if (miButtonB.text.equals(listaPreguntas.get(numRandom).e)){
                    miButtonB.setBackgroundColor(Color.GREEN)
                } else if (miButtonC.text.equals(listaPreguntas.get(numRandom).e)){
                    miButtonC.setBackgroundColor(Color.GREEN)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    cargarPregunta()
                }, tiempoFallo)
            }

            // Se desactivan los botones
            miButtonA00.isEnabled = false
            miButtonB.isEnabled = false
            miButtonC.isEnabled = false
            miButtonD.isEnabled = false
            miImageViewVolver.isEnabled = false
            miImageViewImpugnar.isEnabled = false
        }
    }

    /**
     * Función que codifica la tabla de Preguntas
     * Lo que hace es coger los elementos de la tabla, convertirlos a objetos y añadirlos a la tabla
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun codificarTabla(){
        // Conexión con la base de datos
        val admin = DBHelper(this)
        val db = admin.writableDatabase
        // Cursor donde se almacenan datos
        val cursor = db.rawQuery("select * from mixtas Order by Id desc", null)

        // Limpiamos la lista en caso de que contenga algo
        listaPreguntas.clear()

        // Se van convirtiendo los datos de la base de datos a objetos y se añaden a la lista
        if(cursor.moveToFirst()){
            do{
                val id: Int = cursor.getString(cursor.getColumnIndex("id").toInt()).toInt()
                val pregunta: String = codificarDatos(cursor.getString(cursor.getColumnIndex("a0").toInt()), password)
                val respuesta1: String = codificarDatos(cursor.getString(cursor.getColumnIndex("a00").toInt()), password)
                val respuesta2: String = codificarDatos(cursor.getString(cursor.getColumnIndex("b").toInt()), password)
                val respuesta3: String = codificarDatos(cursor.getString(cursor.getColumnIndex("c").toInt()), password)
                val respuesta4: String = codificarDatos(cursor.getString(cursor.getColumnIndex("d").toInt()), password)
                val respuestaCorrecta: String = codificarDatos(cursor.getString(cursor.getColumnIndex("e").toInt()), password)

                val preguntaObject: Preguntas = Preguntas(id, pregunta, respuesta1, respuesta2, respuesta3, respuesta4, respuestaCorrecta)

                db.execSQL("UPDATE mixtas SET a0 = '" + preguntaObject.a0 + "', a00 = '" + preguntaObject.a00 + "', b = '" +
                        preguntaObject.b + "', c = '" + preguntaObject.c + "', d = '" + preguntaObject.d + "', e = '" +
                        preguntaObject.e +"' WHERE Id = " + preguntaObject.id)

                listaPreguntas.add(preguntaObject)

            }while(cursor.moveToNext())
        }

        // Cerramos conexiones
        admin.close()
        cursor.close()
        db.close()
    }

    /**
     * Funcion que encripta el mensaje y lo devuelve desencriptado
     */
    fun descodificarDatos(datos: String, pass: String): String{
        //
        val secretKey: SecretKeySpec = generateKey(pass)
        val cipher: Cipher = Cipher.getInstance("AES")
        // Modo desencriptación
        cipher.init(Cipher.DECRYPT_MODE, secretKey)

        // Array de bytes con datos descodificados
        val datosDescodificados: ByteArray = Base64.decode(datos, Base64.DEFAULT)
        // Array de bytes con datos desencriptados
        val datosDesencriptadosByte: ByteArray = cipher.doFinal(datosDescodificados)

        // Datos desencriptados en String
        val datosDesencriptadosString = String(datosDesencriptadosByte)

        return datosDesencriptadosString
    }

    /**
     * Función que encripta el mensaje y lo devuelve encriptado
     */
    fun codificarDatos(datos: String, pass: String): String {
        //
        val secretKey: SecretKeySpec = generateKey(pass)
        // Algoritmo de encriptación tipo AES
        val cipher: Cipher = Cipher.getInstance("AES")
        // Modo encriptación
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        // Array de datos en bytes
        val datosEncriptadosBytes: ByteArray = cipher.doFinal(datos.toByteArray())
        // Devolverlo en Base64 y se convierte a String
        val datosEncriptadosString: String = Base64.encodeToString(datosEncriptadosBytes, Base64.DEFAULT)

        return datosEncriptadosString
    }

    /**
     * Genera una clave de tipo Sha-256 pasándole una contraseña de usuario
     */
    fun generateKey (pass: String): SecretKeySpec {
        // Genera el algoritmo seguro
        val sha: MessageDigest = MessageDigest.getInstance("SHA-256")
        // Pasar la clave a byte con el estándar UTF-8 en un array de bytes
        var key: ByteArray = pass.toByteArray(Charsets.UTF_8)
        // Llamamos al método digest que completa el cálculo del hash
        key = sha.digest(key)
        // Usamos estándar AES
        val secretKey = SecretKeySpec(key, "AES")

        return secretKey
    }

    /**
     * Función que llama a la función copyDatabase de la clase DBHelper para copiar la base de datos desde la carpeta assets a la memoria del teléfono
     */
    fun copyDatabase(){
        val admin = DBHelper(this)
        admin.copyDataBase(this)

        admin.close()
    }

    /**
     * Función que genera un número aleatorio entre un rango que recibe por parámetro
     */
    fun Random.nextInt(range: IntRange): Int {
        return range.start + nextInt(range.last - range.start)
    }

    /**
     * Función que carga las preguntas en la lista de preguntas
     */
    fun cargarListaPreguntas(){
        // Conexión con la base de datos
        val admin = DBHelper(this)
        val db = admin.writableDatabase

        listaPreguntas.clear()

        // Cursor donde se almacenan datos
        val cursor = db.rawQuery("select * from mixtas Order by id desc", null)

        // Se van convirtiendo los datos de la base de datos a objetos y se añaden a la lista
        // Se recorre el cursor, que es donde se almacenan los datos de la tabla
        // Se comprueba si el botón pulsado coincide con la respuesta correcta y se desactivan los botones
        if(cursor.moveToFirst()){
            do{
                val id: Int = cursor.getString(cursor.getColumnIndex("id").toInt()).toInt()
                val pregunta: String = cursor.getString(cursor.getColumnIndex("a0").toInt())
                val respuesta1: String = cursor.getString(cursor.getColumnIndex("a00").toInt())
                val respuesta2: String = cursor.getString(cursor.getColumnIndex("b").toInt())
                val respuesta3: String = cursor.getString(cursor.getColumnIndex("c").toInt())
                val respuesta4: String = cursor.getString(cursor.getColumnIndex("d").toInt())
                val respuestaCorrecta: String = cursor.getString(cursor.getColumnIndex("e").toInt())

                val preguntaObject: Preguntas = Preguntas(id, descodificarDatos(pregunta, password), descodificarDatos(respuesta1, password),
                    descodificarDatos(respuesta2, password), descodificarDatos(respuesta3, password), descodificarDatos(respuesta4, password),
                    descodificarDatos(respuestaCorrecta, password))

                // Cada objeto creado se añade a la lista de preguntas
                listaPreguntas.add(preguntaObject)
            }while(cursor.moveToNext())
        }

        // Cerramos conexiones
        admin.close()
        cursor.close()
        db.close()
    }

    fun guardarPreguntaFallada(p: Preguntas){
        // Conexión con la base de datos
        val admin = DBPreguntasFalladas(this)
        val db = admin.writableDatabase
        // Cursor
        val cursor = db.rawQuery("select * from falladas", null)

        var repetido = false

        cursor.moveToFirst()

        // Comprobamos si la base de datos está vacía en caso de que el cursor no tenga nada
        if (cursor.count == 0){
            // Insertamos los datos del objeto que nos han pasado
            db.execSQL("INSERT INTO falladas (pregunta, resp1, resp2, resp3, resp4, correct) VALUES ('"+p.a0+"', '"+p.a00+"', '"+p.b+"', '"+p.c+"', '"+p.d+"', '"+p.e+"')")
            Toast.makeText(this, "guardado", Toast.LENGTH_LONG).show()
        } else {
            do{
                try{
                    if(cursor.getString(cursor.getColumnIndex("a0").toInt()).equals(p.a0)){
                        repetido = true
                    }
                }catch (e: Exception){
                    Log.e("TAG", "Error")
                }

            }while (cursor.moveToNext())

            // Si no está repetido en la base de datos, no se guarda
            if(!repetido){
                db.execSQL("INSERT INTO falladas (pregunta, resp1, resp2, resp3, resp4, correct) VALUES ('"+p.a0+"', '"+p.a00+"', '"+p.b+"', '"+p.c+"', '"+p.d+"', '"+p.e+"')")
                Toast.makeText(this, "Guardado", Toast.LENGTH_LONG).show()
            }
        }

        // Cerramos conexiones
        admin.close()
        db.close()
        cursor.close()
    }

    /**
     * Función que busca en la base de datos el tiempo que el usuario ha configurado
     */
    fun setTiempoError(){
        val admin = DBConfig(this)
        val db = admin.readableDatabase

        val cursor = db.rawQuery("select * from seconds", null)

        cursor.moveToFirst()

        try{
            tiempoFallo = cursor.getString(cursor.getColumnIndex("tiempo_error").toInt()).toInt().toLong()
        }catch(e: SQLException){
            Log.e("TAG", e.toString())
        }

        admin.close()
        cursor.close()
        db.close()
    }
}