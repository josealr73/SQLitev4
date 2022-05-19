package com.example.sqlite.Clases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.nfc.Tag
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class DBConfig(context: Context): SQLiteOpenHelper(context, "config.sqlite", null, 1) {
    private val CREATE_TABLE_PUBLI = "CREATE TABLE publi (id INTEGER, publi_activated INTEGER)"
    private val CREATE_TABLE_SECOND = "CREATE TABLE seconds (id INTEGER, tiempo_error INTEGER)"
    private val INSERT_DEFAULT_DATA = "INSERT INTO publi (id, publi_activated) values (1, 1)"

    override fun onCreate(db: SQLiteDatabase?) {
        // TODO("Not yet implemented")
        db?.execSQL(CREATE_TABLE_PUBLI)
        db?.execSQL(CREATE_TABLE_SECOND)
        db?.execSQL(INSERT_DEFAULT_DATA)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}