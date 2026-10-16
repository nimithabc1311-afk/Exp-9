package com.example.experiment9

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// One row of the "notes" table
data class Note(val id: Long, val text: String)

/**
 * SQLiteOpenHelper creates the database file the first time it is needed
 * and gives us simple CRUD (Create, Read, Update, Delete) methods.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "experiment9.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS notes")
        onCreate(db)
    }

    // CREATE
    fun insertNote(text: String): Long {
        val values = ContentValues().apply { put("text", text) }
        return writableDatabase.insert("notes", null, values)
    }

    // READ
    fun getAllNotes(): List<Note> {
        val list = mutableListOf<Note>()
        readableDatabase.rawQuery("SELECT id, text FROM notes ORDER BY id DESC", null).use { c ->
            while (c.moveToNext()) {
                list.add(Note(c.getLong(0), c.getString(1)))
            }
        }
        return list
    }

    // UPDATE
    fun updateNote(id: Long, text: String): Int {
        val values = ContentValues().apply { put("text", text) }
        return writableDatabase.update("notes", values, "id = ?", arrayOf(id.toString()))
    }

    // DELETE one
    fun deleteNote(id: Long): Int =
        writableDatabase.delete("notes", "id = ?", arrayOf(id.toString()))

    // DELETE all
    fun deleteAll(): Int = writableDatabase.delete("notes", null, null)
}
