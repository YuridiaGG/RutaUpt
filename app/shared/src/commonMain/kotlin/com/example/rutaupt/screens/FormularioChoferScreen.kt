package com.example.rutaupt.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rutaupt.model.Chofer
import com.example.rutaupt.storage.ChoferRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioChoferScreen(
    onVolver: () -> Unit
) {

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var salida by remember { mutableStateOf("") }
    var llegada by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Chofer") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = unidad,
                onValueChange = { unidad = it },
                label = { Text("Número de Unidad") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = salida,
                onValueChange = { salida = it },
                label = { Text("Hora Salida") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = llegada,
                onValueChange = { llegada = it },
                label = { Text("Hora Llegada") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (nombre.isNotBlank() && unidad.isNotBlank()) {
                        ChoferRepository.choferes.add(
                            Chofer(
                                id = ChoferRepository.choferes.size + 1,
                                nombre = nombre,
                                apellido = apellido,
                                numeroUnidad = unidad,
                                telefono = telefono,
                                horaSalida = salida,
                                horaLlegada = llegada
                            )
                        )
                        onVolver()
                    }
                }
            ) {
                Text("Guardar Chofer")
            }
        }
    }
}
