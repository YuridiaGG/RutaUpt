package com.example.rutaupt.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeEstudianteScreen(onVerRuta: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Bienvenido Estudiante a RutaUPT")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onVerRuta) {
                Text("Ver Ruta Actual")
            }
        }
    }
}
