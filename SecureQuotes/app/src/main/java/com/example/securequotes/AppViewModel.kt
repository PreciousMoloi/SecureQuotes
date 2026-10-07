package com.example.securequotes

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.securequotes.data.SettingsStore
import com.example.securequotes.data.User
import com.example.securequotes.data.UserDb
import com.example.securequotes.network.ApiClient
import com.example.securequotes.network.Quote
import com.example.securequotes.security.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Screen { Login, Register, Home, Settings }

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val db = UserDb(app)
    private val store = SettingsStore(app)

    var screen by mutableStateOf(Screen.Login)
        private set
    var user by mutableStateOf<User?>(null)
        private set
    var busy by mutableStateOf(false)
        private set

    var message by mutableStateOf<String?>(null)
        private set
    var messageIsError by mutableStateOf(true)
        private set

    // Settings
    var darkModeEnabled by mutableStateOf(store.darkMode)
        private set
    var isAuthorVisible by mutableStateOf(store.showAuthor)
        private set
    var currenttextScale by mutableStateOf(store.textScale)
        private set

    // REST API data
    var quote by mutableStateOf<Quote?>(null)
        private set
    var quoteLoading by mutableStateOf(false)
        private set
    var quoteError by mutableStateOf<String?>(null)
        private set

    init {
        // Restore a previous session, if any.
        store.sessionUser?.let { name ->
            db.findUser(name)?.let {
                user = it
                screen = Screen.Home
                loadQuote()
            }
        }
    }

    fun go(target: Screen) {
        message = null
        screen = target
    }

    private fun showMessage(text: String, isError: Boolean) {
        message = text
        messageIsError = isError
    }

    // ---------- Register / Login ----------

    fun register(username: String, displayName: String, password: String, confirm: String) {
        val u = username.trim()
        val name = displayName.trim()
        when {
            u.length < 3 -> return showMessage("Username must be at least 3 characters.", true)
            name.isEmpty() -> return showMessage("Please enter a display name.", true)
            password.length < 8 -> return showMessage("Password must be at least 8 characters.", true)
            password != confirm -> return showMessage("Passwords do not match.", true)
        }
        viewModelScope.launch {
            busy = true
            val created = withContext(Dispatchers.IO) {
                db.register(u, name, PasswordHasher.hash(password))
            }
            busy = false
            if (created) {
                screen = Screen.Login
                showMessage("Account created. Please log in.", false)
            } else {
                showMessage("That username is already taken.", true)
            }
        }
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isEmpty()) {
            return showMessage("Enter your username and password.", true)
        }
        viewModelScope.launch {
            busy = true
            val found = withContext(Dispatchers.IO) {
                db.findUser(username.trim())?.takeIf { PasswordHasher.verify(password, it.passwordHash) }
            }
            busy = false
            if (found == null) {
                showMessage("Incorrect username or password.", true)
            } else {
                user = found
                store.sessionUser = found.username
                message = null
                screen = Screen.Home
                loadQuote()
            }
        }
    }

    fun logout() {
        user = null
        quote = null
        store.sessionUser = null
        go(Screen.Login)
    }

    // ---------- Settings ----------

    fun setDarkMode(value: Boolean) { darkModeEnabled = value; store.darkMode = value }
    fun setShowAuthor(value: Boolean) { isAuthorVisible = value; store.showAuthor = value }
    fun setTextScale(value: Float) { currenttextScale = value; store.textScale = value }

    fun updateDisplayName(newName: String) {
        val current = user ?: return
        val name = newName.trim()
        if (name.isEmpty()) return showMessage("Display name cannot be empty.", true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) { db.updateDisplayName(current.username, name) }
            user = current.copy(displayName = name)
            showMessage("Display name updated.", false)
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        val current = user ?: return
        if (newPassword.length < 8) {
            return showMessage("New password must be at least 8 characters.", true)
        }
        viewModelScope.launch {
            busy = true
            val ok = withContext(Dispatchers.IO) {
                if (PasswordHasher.verify(oldPassword, current.passwordHash)) {
                    val newHash = PasswordHasher.hash(newPassword)
                    db.updatePasswordHash(current.username, newHash)
                    newHash
                } else null
            }
            busy = false
            if (ok == null) {
                showMessage("Current password is incorrect.", true)
            } else {
                user = current.copy(passwordHash = ok)
                showMessage("Password changed.", false)
            }
        }
    }

    // ---------- REST API ----------

    fun loadQuote() {
        viewModelScope.launch {
            quoteLoading = true
            quoteError = null
            try {
                quote = ApiClient.api.randomQuote()
            } catch (e: Exception) {
                quoteError = "Couldn't load a quote. Check your internet connection."
            } finally {
                quoteLoading = false
            }
        }
    }
}
