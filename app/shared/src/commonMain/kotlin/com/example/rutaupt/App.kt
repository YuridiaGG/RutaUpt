package com.example.rutaupt

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.rutaupt.screens.HomeAdminScreen
import com.example.rutaupt.screens.HomeChoferScreen
import com.example.rutaupt.screens.HomeEstudianteScreen
import com.example.rutaupt.screens.LoginScreen
import com.example.rutaupt.screens.RutaScreen

@Composable
fun App() {
    var pantallaActual by remember { mutableStateOf("login") }

    MaterialTheme {
        Surface(color = Color.White) {
            Column {
                Text("RutaUPT Activa", color = Color.Gray)
                when (pantallaActual) {
                    "login" -> LoginScreen(
                        onLoginSuccess = { pantallaActual = it }
                    )
                    "admin" -> HomeAdminScreen(
                        onVerRuta = { pantallaActual = "ruta" }
                    )
                    "chofer" -> HomeChoferScreen()
                    "estudiante" -> HomeEstudianteScreen(
                        onVerRuta = { pantallaActual = "ruta" }
                    )
                    "ruta" -> RutaScreen(
                        onVolver = { pantallaActual = "admin" }
                    )
                }
            }
        }
    }
}
