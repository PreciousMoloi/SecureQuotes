@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.securequotes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.securequotes.AppViewModel
import com.example.securequotes.Screen

@Composable
private fun MessageText(vm: AppViewModel) {
    vm.message?.let {
        Text(
            text = it,
            color = if (vm.messageIsError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun PasswordField(value: String, label: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TextField(value: String, label: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

// ---------------------------------------------------------------- Login

@Composable
fun LoginScreen(vm: AppViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Secure Quotes", style = MaterialTheme.typography.headlineLarge)
        Text("Log in to continue", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        TextField(username, "Username") { username = it }
        Spacer(Modifier.height(8.dp))
        PasswordField(password, "Password") { password = it }
        MessageText(vm)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { vm.login(username, password) },
            enabled = !vm.busy,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (vm.busy) "Please wait..." else "Log in") }
        TextButton(onClick = { vm.go(Screen.Register) }) { Text("Create an account") }
    }
}

// ---------------------------------------------------------------- Register

@Composable
fun RegisterScreen(vm: AppViewModel) {
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create account", style = MaterialTheme.typography.headlineLarge)
        Text("Your password is hashed before it is saved.", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))
        TextField(username, "Username") { username = it }
        Spacer(Modifier.height(8.dp))
        TextField(displayName, "Display name") { displayName = it }
        Spacer(Modifier.height(8.dp))
        PasswordField(password, "Password (min 8 characters)") { password = it }
        Spacer(Modifier.height(8.dp))
        PasswordField(confirm, "Confirm password") { confirm = it }
        MessageText(vm)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { vm.register(username, displayName, password, confirm) },
            enabled = !vm.busy,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (vm.busy) "Please wait..." else "Register") }
        TextButton(onClick = { vm.go(Screen.Login) }) { Text("I already have an account") }
    }
}

// ---------------------------------------------------------------- Home

@Composable
fun HomeScreen(vm: AppViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hello, ${vm.user?.displayName ?: ""}") },
                actions = { TextButton(onClick = { vm.go(Screen.Settings) }) { Text("Settings") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Quote of the moment", style = MaterialTheme.typography.titleMedium)
            Text("Loaded from the DummyJSON REST API", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    when {
                        vm.quoteLoading -> Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) { CircularProgressIndicator() }

                        vm.quoteError != null -> Text(
                            vm.quoteError ?: "",
                            color = MaterialTheme.colorScheme.error
                        )

                        vm.quote != null -> {
                            Text("\u201C${vm.quote?.quote}\u201D", style = MaterialTheme.typography.titleLarge)
                            if (vm.isAuthorVisible) {
                                Spacer(Modifier.height(12.dp))
                                Text("\u2014 ${vm.quote?.author}", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = { vm.loadQuote() }, enabled = !vm.quoteLoading) { Text("New quote") }
        }
    }
}

// ---------------------------------------------------------------- Settings

@Composable
fun SettingsScreen(vm: AppViewModel) {
    var displayName by remember { mutableStateOf(vm.user?.displayName ?: "") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { TextButton(onClick = { vm.go(Screen.Home) }) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text("Profile", style = MaterialTheme.typography.titleMedium)
            Text("Signed in as @${vm.user?.username}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            TextField(displayName, "Display name") { displayName = it }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { vm.updateDisplayName(displayName) }) { Text("Save display name") }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Dark mode", modifier = Modifier.weight(1f))
                Switch(checked = vm.darkModeEnabled, onCheckedChange = { vm.setDarkMode(it) })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Show quote author", modifier = Modifier.weight(1f))
                Switch(checked = vm.isAuthorVisible, onCheckedChange = { vm.setShowAuthor(it) })
            }
            Spacer(Modifier.height(8.dp))
            Text("Text size")
            listOf("Small" to 0.85f, "Normal" to 1.0f, "Large" to 1.25f).forEach { (label, scale) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = vm.textScale == scale, onClick = { vm.setTextScale(scale) })
                    Text(label)
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            Text("Change password", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            PasswordField(oldPassword, "Current password") { oldPassword = it }
            Spacer(Modifier.height(8.dp))
            PasswordField(newPassword, "New password (min 8 characters)") { newPassword = it }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    vm.changePassword(oldPassword, newPassword)
                    oldPassword = ""
                    newPassword = ""
                },
                enabled = !vm.busy
            ) { Text("Update password") }

            MessageText(vm)

            HorizontalDivider(Modifier.padding(vertical = 16.dp))
            OutlinedButton(onClick = { vm.logout() }, modifier = Modifier.fillMaxWidth()) { Text("Log out") }
        }
    }
}
