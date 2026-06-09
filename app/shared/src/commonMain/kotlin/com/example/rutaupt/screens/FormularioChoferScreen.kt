package com.example.rutaupt.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
    choferEditar: Chofer? = null,
    onVolver: () -> Unit,
) {
    val modoEdicion = choferEditar != null

    var nombre by remember { mutableStateOf(choferEditar?.nombre ?: "") }
    var apellido by remember { mutableStateOf(choferEditar?.apellido ?: "") }
    var unidad by remember { mutableStateOf(choferEditar?.numeroUnidad ?: "") }
    var telefono by remember { mutableStateOf(choferEditar?.telefono ?: "") }
    var horaSalida by remember { mutableStateOf(choferEditar?.horaSalida ?: "") }
    var horaLlegada by remember { mutableStateOf(choferEditar?.horaLlegada ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (modoEdicion) "Editar Chofer" else "Nuevo Chofer") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
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
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = horaSalida,
                    onValueChange = { horaSalida = it },
                    label = { Text("Salida") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = horaLlegada,
                    onValueChange = { horaLlegada = it },
                    label = { Text("Llegada") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (choferEditar != null) {
                        val index = ChoferRepository.choferes.indexOfFirst { it.id == choferEditar.id }
                        if (index != -1) {
                            ChoferRepository.choferes[index] = choferEditar.copy(
                                nombre = nombre,
                                apellido = apellido,
                                numeroUnidad = unidad,
                                telefono = telefono,
                                horaSalida = horaSalida,
                                horaLlegada = horaLlegada
                            )
                        }
                    } else {
                        val nuevoId = if (ChoferRepository.choferes.isEmpty()) 1 else ChoferRepository.choferes.maxOf { it.id } + 1
                        ChoferRepository.choferes.add(
                            Chofer(
                                id = nuevoId,
                                nombre = nombre,
                                apellido = apellido,
                                numeroUnidad = unidad,
                                telefono = telefono,
                                horaSalida = horaSalida,
                                horaLlegada = horaLlegada
                            )
                        )
                    }
                    onVolver()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (modoEdicion) "Actualizar Datos" else "Guardar Chofer")
            }
        }
    }
}
