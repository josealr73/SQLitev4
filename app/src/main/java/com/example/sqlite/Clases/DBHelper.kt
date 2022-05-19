package com.example.sqlite.Clases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Clase DBHelper para establecer conexión con la base de datos.
 * Lo que hace es crear una base de datos por defecto que se sobreescribe con la base de datos que hay en la carpeta assets
 */
class DBHelper(context: Context): SQLiteOpenHelper(context, "correos.sqlite", null, 1) {
    private val DB_PATH = "/data/data/com.example.sqlite/databases/"
    private val DB_NAME = "correos.sqlite"
    private var myContext: Context? = context

    override fun onCreate(db: SQLiteDatabase) {
        //TODO("Not yet2 implemented")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    /**
     * Función que copia la base de datos de la carpeta assets a la memoria del teléfono
     */
    fun copyDataBase(context: Context) {
        // Creamos una carpeta llamada databases en caso de que esta no exista.
        // En esta carpeta de almacenan las bases de datos dentro del dispositivo
        val directorio = File("data/data/com.example.sqlite/", "databases")
        if(directorio.exists()){

        } else{
            directorio.mkdir()
        }

        //Abrimos el fichero de base de datos como entrada
        val myInput: InputStream = myContext!!.assets.open(DB_NAME)

        //Ruta a la base de datos vacía recién creada
        val outFileName = DB_PATH + DB_NAME

        //Abrimos la base de datos vacía como salida
        val myOutput: OutputStream = FileOutputStream(outFileName)

        //Transferimos los bytes desde el fichero de entrada al de salida
        val buffer = ByteArray(1024)
        var length: Int
        while (myInput.read(buffer).also { length = it } > 0) {
            myOutput.write(buffer, 0, length)
        }

        //Liberamos los streams
        myOutput.flush()
        myOutput.close()
        myInput.close()

        Log.i("Copiado con éxito", "Copiado con éxito")
    }
}