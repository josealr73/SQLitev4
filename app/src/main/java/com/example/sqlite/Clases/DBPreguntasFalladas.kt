package com.example.sqlite.Clases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBPreguntasFalladas(context: Context): SQLiteOpenHelper(context, "preguntas_falladas.sqlite", null, 1) {
    private val CREATE_TABLE_PREGUNTAS = "CREATE TABLE falladas (id INTEGER PRIMARY KEY AUTOINCREMENT, pregunta TEXT, resp1 TEXT, resp2 TEXT, resp3 TEXT, resp4 TEXT, correct TEXT)"

    override fun onCreate(db: SQLiteDatabase?) {
        // TODO("Not yet implemented")
        db?.execSQL(CREATE_TABLE_PREGUNTAS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}