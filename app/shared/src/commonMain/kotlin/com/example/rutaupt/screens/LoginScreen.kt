package com.example.rutaupt.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "RutaUPT Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                when (usuario.lowercase()) {
                    "admin" -> onLoginSuccess("admin")
                    "chofer" -> onLoginSuccess("chofer")
                    "estudiante" -> onLoginSuccess("estudiante")
                }
            }
        ) {
            Text("Ingresar")
        }
    }
}

