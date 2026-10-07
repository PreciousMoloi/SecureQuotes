package com.example.securequotes.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class User(val username: String, val displayName: String, val passwordHash: String)

/** Local SQLite database that stores registered users (with hashed passwords only). */
class UserDb(context: Context) : SQLiteOpenHelper(context, "securequotes.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                display_name TEXT NOT NULL,
                password_hash TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // No migrations yet.
    }

    /** Returns false if the username is already taken. */
    fun register(username: String, displayName: String, passwordHash: String): Boolean {
        val values = ContentValues().apply {
            put("username", username.lowercase())
            put("display_name", displayName)
            put("password_hash", passwordHash)
        }
        return writableDatabase.insert("users", null, values) != -1L
    }

    fun findUser(username: String): User? {
        readableDatabase.query(
            "users",
            arrayOf("username", "display_name", "password_hash"),
            "username = ?",
            arrayOf(username.lowercase()),
            null, null, null
        ).use { c ->
            return if (c.moveToFirst()) User(c.getString(0), c.getString(1), c.getString(2)) else null
        }
    }

    fun updateDisplayName(username: String, displayName: String) {
        val values = ContentValues().apply { put("display_name", displayName) }
        writableDatabase.update("users", values, "username = ?", arrayOf(username.lowercase()))
    }

    fun updatePasswordHash(username: String, passwordHash: String) {
        val values = ContentValues().apply { put("password_hash", passwordHash) }
        writableDatabase.update("users", values, "username = ?", arrayOf(username.lowercase()))
    }
}
