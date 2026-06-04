package com.example.rutaupt

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import com.example.rutaupt.screens.HomeAdminScreen
import com.example.rutaupt.screens.HomeChoferScreen
import com.example.rutaupt.screens.HomeEstudianteScreen
import com.example.rutaupt.screens.LoginScreen

@Composable
fun App() {

    var pantallaActual by remember {
        mutableStateOf("login")
    }

    MaterialTheme {

        when (pantallaActual) {

            "login" -> LoginScreen(
                onLoginSuccess = {
                    pantallaActual = it
                }
            )

            "admin" -> HomeAdminScreen()

            "chofer" -> HomeChoferScreen()

            "estudiante" -> HomeEstudianteScreen()
        }
    }
}