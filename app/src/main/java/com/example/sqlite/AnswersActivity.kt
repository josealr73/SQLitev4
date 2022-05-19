package com.example.sqlite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sqlite.Clases.DBPreguntasFalladas
import com.example.sqlite.Clases.Preguntas

class AnswersActivity : AppCompatActivity() {
    // Creación
    private lateinit var miImageViewVolverAnswers: ImageView
    private lateinit var miImageViewDeleteAnswer: ImageView
    private lateinit var miImageViewDeleteAll: ImageView
    private lateinit var miButtonAnswerCorrect: Button
    private lateinit var miButtonSiguiente: Button
    private lateinit var miTextViewContador: TextView
    private lateinit var miTextViewContador2: TextView
    private lateinit var miTextViewPregunta: TextView
    private lateinit var listaPreguntasFallas: ArrayList<Preguntas>
    // Variable que guarda la posición que debe cargar en la lista de preguntas. Se va incrementando.
    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answers)

        // Inicialización
        miImageViewVolverAnswers = findViewById(R.id.imageViewVolverAnswers)
        miImageViewDeleteAnswer = findViewById(R.id.imageViewDelAnswer)
        miImageViewDeleteAll = findViewById(R.id.imageViewDelAll)
        miButtonAnswerCorrect = findViewById(R.id.buttonAnswerCorrect)
        miButtonSiguiente = findViewById(R.id.buttonNextAnswer)
        miTextViewContador = findViewById(R.id.textViewContador)
        miTextViewContador2 = findViewById(R.id.textViewContador2)
        miTextViewPregunta = findViewById(R.id.textViewPreguntaAnswer)

        // Lista donde se almacenan las preguntas falladas de la base de datos
        listaPreguntasFallas = ArrayList<Preguntas>()

        // Desactivamos el botón Siguiente por defecto. En función del número de preguntas falladas se activará o no.
        miButtonSiguiente.isEnabled = false

        // Cargamos en la lista las preguntas falladas.
        cargarPreguntasMal()

        // Listener del imageview para volver hacia atrás
        miImageViewVolverAnswers.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        // Comprobamos si la lista está vacía y si tiene más de una pregunta.
        // Si está vacía se desactiva el botón Siguiente.
        // Si solo tiene una pregunta, también se desactiva. Mostramos la pregunta.
        // Si la lista tiene más de 1 pregunta se activa el botón Siguiente y mostramos las preguntas.
        if(listaPreguntasFallas.isEmpty()){
            miButtonSiguiente.isEnabled = false
        } else if (listaPreguntasFallas.size == 1){
            miButtonSiguiente.isEnabled = false
            mostrarPreguntas(count)
        } else{
            miButtonSiguiente.isEnabled = true
            mostrarPreguntas(count)
        }

        // Listener del botón siguiente, que va incrementando el contador.
        // Comprueba si la lista está llena o vacía.
        // Al pulsarlo aumenta el contrador, carga la pregunta y edita el contador de preguntas.
        miButtonSiguiente.setOnClickListener {
            count++

            if(count < listaPreguntasFallas.size){
                mostrarPreguntas(count)
            } else {
                count = 0
                mostrarPreguntas(count)
            }

            setContador(count + 1, listaPreguntasFallas.size)
        }

        // Listener del imageview que eliminará la pregunta guardada que se muestra
        // Si la lista tiene preguntas, se borrará la pregunta y se setea el contador de preguntas.
        // Si no hay preguntas, no hace nada
        miImageViewDeleteAnswer.setOnClickListener {
            if(listaPreguntasFallas.size > 0){
                borrarPregunta(listaPreguntasFallas.get(count))
                setContador(count + 1, listaPreguntasFallas.size)
            } else{
                Log.i("TAG", "No hay preguntas")
            }
        }

        // Listener de la imagen que borra todas las preguntas falladas.
        // Únicamente vaciará la lista si hay preguntas en la lista.
        miImageViewDeleteAll.setOnClickListener {
            if(listaPreguntasFallas.size > 0){
                borrarTodo()
            }
        }
    }

    /**
     * Carga en la lista todas las preguntas de la base de datos
     */
    private fun cargarPreguntasMal(){
        // Conexión con la BD
        val admin = DBPreguntasFalladas(this)
        val db = admin.writableDatabase
        val cursor = db.rawQuery("select * from falladas order by id asc", null)

        // Vaciamos la base de datos
        listaPreguntasFallas.clear()

        // Movemos el cursor a la primera posición
        cursor.moveToFirst()

        // Si el cursor no recoge datos pondremos un texto por defecto
        // Si recoge datos prodecemos a recorrer el cursor y guardar las preguntas en la BD
        if(cursor.count == 0){
            miTextViewPregunta.setText(R.string.no_preguntas)
        } else {
            listaPreguntasFallas.clear()

            do {
                val id: Int = cursor.getString(cursor.getColumnIndex("id").toInt()).toInt()
                val pregunta: String = cursor.getString(cursor.getColumnIndex("pregunta").toInt())
                val respuesta1: String = cursor.getString(cursor.getColumnIndex("resp1").toInt())
                val respuesta2: String = cursor.getString(cursor.getColumnIndex("resp2").toInt())
                val respuesta3: String = cursor.getString(cursor.getColumnIndex("resp3").toInt())
                val respuesta4: String = cursor.getString(cursor.getColumnIndex("resp4").toInt())
                val respuestaCorrecta: String = cursor.getString(cursor.getColumnIndex("correct").toInt())

                val preguntaObject: Preguntas = Preguntas(id, pregunta, respuesta1, respuesta2, respuesta3, respuesta4, respuestaCorrecta)

                listaPreguntasFallas.add(preguntaObject)
            }while (cursor.moveToNext())
        }

        // Si la lista está vacía seteamos el contador de preguntas a 0
        // Si hay preguntas en la lista seteamos el contador con el número de preguntas
        if(listaPreguntasFallas.isEmpty()){
            setContador(count, listaPreguntasFallas.size)
        } else {
            setContador(count + 1, listaPreguntasFallas.size)
        }

        // Cerramos conexiones
        admin.close()
        db.close()
        cursor.close()
    }

    /**
     * Funcion que carga en el textview y el button el contenido del objeto de tipo pregunta.
     * Recibe un número que se va incrementando que apunta a la posición que debe cargar de la lista.
     */
    private fun mostrarPreguntas(pos: Int){
        miTextViewPregunta.setText(listaPreguntasFallas.get(pos).a0)
        miButtonAnswerCorrect.setText(listaPreguntasFallas.get(pos).e)
    }

    /**
     * Funcion que borra la pregunta que se muestra en pantalla. La borra de la BD y de la lista de preguntas
     * Recibe un objeto de tipo Preguntas
     */
    private fun borrarPregunta(p: Preguntas){
        // Conexión con la BD
        val admin = DBPreguntasFalladas(this)
        val db = admin.writableDatabase

        // Ejecutamos la consulta que elimina la pregunta de la BD teniendo en cuenta el id del objeto
        db.execSQL("delete from falladas where id = "+p.id)

        // Volvemos a llenar la lista
        cargarPreguntasMal()

        // Establecemos el contador a 0
        count = 0

        // Comprobamos si en la lista hay preguntas para desactivar o activar el botón siguiente y setear el contador.
        // Si la lista está vacía, seteamos el textview con un texto por defecto y desactivamos el botón Siguiente.
        // Si solo guarda un elemento, se desactiva el botón Siguiente
        // Si en la lista hay más de un elemento, se muestran las preguntas.
        if (listaPreguntasFallas.size == 0){
            miTextViewPregunta.setText(R.string.no_preguntas)
            miButtonAnswerCorrect.setText("")

            miButtonSiguiente.isEnabled = false
        } else if(listaPreguntasFallas.size == 1){
            miButtonSiguiente.isEnabled = false
            mostrarPreguntas(count)
        } else if(listaPreguntasFallas.size > 1){
            mostrarPreguntas(count)
        }

        // Cerramos conexiones.
        admin.close()
        db.close()
    }

    /**
     * Función que borra todas las preguntas de la BD y de la lista de preguntas.
     */
    private fun borrarTodo(){
        // COnexión con la BD
        val admin = DBPreguntasFalladas(this)
        val db = admin.writableDatabase

        // Ejecutamos la consulta que elimina todas las filas de la BD
        db.execSQL("delete from falladas")

        // Desactivamos el botón Siguiente, ya que no hay preguntas.
        miButtonSiguiente.isEnabled = false

        // Vaciamos la lista de preguntas falladas
        listaPreguntasFallas.clear()

        // Seteamos el textview y button a un texto por defecto
        miTextViewPregunta.setText(R.string.no_preguntas)
        miButtonAnswerCorrect.setText("")

        // Seteamos el contador de preguntas a 0
        setContador(0, 0)

        // Cerramos conexiones.
        admin.close()
        db.close()
    }

    /**
     * Función que escribe en los textview que cuentan las preguntas disponibles y la posición de la pregunta que se está mostrando por pantalla
     */
    private fun setContador(cont1: Int, cont2: Int){
        miTextViewContador.setText(cont1.toString())
        miTextViewContador2.setText("/" + cont2.toString())
    }
}