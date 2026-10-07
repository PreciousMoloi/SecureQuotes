# Secure Quotes (Kotlin / Jetpack Compose)

Android app that demonstrates:
1. **Register & log in** - passwords are salted and hashed with PBKDF2-HMAC-SHA256 (never stored in plain text). Users are kept in a local SQLite database.
2. **Settings** - change display name, change password, dark mode, text size, show/hide quote author, log out. Settings persist (SharedPreferences).
3. **REST API** - Retrofit calls the public DummyJSON API (`GET https://dummyjson.com/quotes/random`) to show a quote.

## Run it
1. Android Studio -> File -> Open -> select the `SecureQuotes` folder.
2. Let Gradle sync (needs internet the first time).
3. Run on an emulator or a phone (Android 8.0 / API 26+).

## Demo video checklist
Register -> failed login (wrong password) -> successful login -> quote loads from API -> "New quote" -> Settings: change display name, toggle dark mode, change text size, change password -> log out -> log in with the new password.

## Push to GitHub
```
git init
git add .
git commit -m "Secure Quotes app"
git branch -M main
git remote add origin https://github.com/<your-username>/SecureQuotes.git
git push -u origin main
```
