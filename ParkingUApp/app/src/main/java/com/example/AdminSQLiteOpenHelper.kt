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

    override fun onCreate(db: SQLiteDatabase) {
        // Aquí creamos la tabla automáticamente la primera vez
        db.execSQL("create table reservas(id integer primary key autoincrement, patente text, espacio text, sede text, fecha text, hora_inicio text, hora_termino text)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // No necesario para este prototipo
    }
}