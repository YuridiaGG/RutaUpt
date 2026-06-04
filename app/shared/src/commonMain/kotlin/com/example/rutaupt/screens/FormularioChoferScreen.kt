package com.example.rutaupt.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rutaupt.model.Chofer
import com.example.rutaupt.storage.ChoferRepository

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

    Scaffold {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") }
            )

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") }
            )

            OutlinedTextField(
                value = unidad,
                onValueChange = { unidad = it },
                label = { Text("Número de Unidad") }
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") }
            )

            OutlinedTextField(
                value = salida,
                onValueChange = { salida = it },
                label = { Text("Hora Salida") }
            )

            OutlinedTextField(
                value = llegada,
                onValueChange = { llegada = it },
                label = { Text("Hora Llegada") }
            )

            Button(
                onClick = {

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
            ) {
                Text("Guardar")
            }
        }
    }
}