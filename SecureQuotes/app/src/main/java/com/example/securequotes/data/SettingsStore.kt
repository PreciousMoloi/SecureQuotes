package com.example.securequotes.data

import android.content.Context

/** Persists app settings and the logged-in session using SharedPreferences. */
class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var darkMode: Boolean
        get() = prefs.getBoolean("dark_mode", false)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()

    var showAuthor: Boolean
        get() = prefs.getBoolean("show_author", true)
        set(value) = prefs.edit().putBoolean("show_author", value).apply()

    /** Text scale: 0.85 (small), 1.0 (normal), 1.25 (large). */
    var textScale: Float
        get() = prefs.getFloat("text_scale", 1.0f)
        set(value) = prefs.edit().putFloat("text_scale", value).apply()

    var sessionUser: String?
        get() = prefs.getString("session_user", null)
        set(value) = prefs.edit().putString("session_user", value).apply()
}
