package com.example.sqlite.Clases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class DBEstadisticas(context: Context): SQLiteOpenHelper(context, "estadisticas.sqlite", null, 1) {
    private var CREATE = "CREATE TABLE stadistics_data (id INTEGER, aciertos INTEGER, fallos INTEGER, fecha TEXT)"

    override fun onCreate(db: SQLiteDatabase) {
        //TODO("Not yet2 implemented")
        db.execSQL(CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}