package com.example.securequotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.securequotes.ui.HomeScreen
import com.example.securequotes.ui.LoginScreen
import com.example.securequotes.ui.RegisterScreen
import com.example.securequotes.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: AppViewModel = viewModel()
            val density = LocalDensity.current
            // Apply the user's text-size setting app-wide.
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, density.fontScale * vm.textScale)
            ) {
                MaterialTheme(colorScheme = if (vm.darkModeEnabled) darkColorScheme() else lightColorScheme()) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        when (vm.screen) {
                            Screen.Login -> LoginScreen(vm)
                            Screen.Register -> RegisterScreen(vm)
                            Screen.Home -> HomeScreen(vm)
                            Screen.Settings -> SettingsScreen(vm)
                        }
                    }
                }
            }
        }
    }
}
