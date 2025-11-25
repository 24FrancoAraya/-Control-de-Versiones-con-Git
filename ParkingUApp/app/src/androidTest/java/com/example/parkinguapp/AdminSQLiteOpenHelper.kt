package com.example.parkinguapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    // Se ejecuta la primera vez que abres la app para crear la tabla
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table reservas(id integer primary key autoincrement, patente text, espacio text, sede text, fecha text, hora_inicio text, hora_termino text)")
    }

    // Se usa si cambias la versión de la base de datos (no lo usaremos en el prototipo)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Sin cambios para el prototipo
    }
}