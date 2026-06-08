package com.example.rutaupt.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rutaupt.model.Chofer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarChoferScreen(
    onVolver: () -> Unit
) {
    var pantalla by remember { mutableStateOf("menu") }
    var choferAEditar by remember { mutableStateOf<Chofer?>(null) }

    when (pantalla) {
        "formulario" -> {
            FormularioChoferScreen(
                choferElegido = choferAEditar,
                onVolver = {
                    pantalla = "menu"
                }
            )
        }

        "lista" -> {
            ListaChoferesScreen(
                onVolver = {
                    pantalla = "menu"
                },
                onAgregarChofer = {
                    choferAEditar = null
                    pantalla = "formulario"
                },
                onEditarChofer = { chofer ->
                    choferAEditar = chofer
                    pantalla = "formulario"
                }
            )
        }

        else -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Gestión de Choferes") },
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
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Card(
                        onClick = {
                            choferAEditar = null
                            pantalla = "formulario"
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Icon(Icons.Default.DirectionsBus, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Agregar Chofer Nuevo")
                        }
                    }

                    Card(
                        onClick = {
                            pantalla = "lista"
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Icon(Icons.Default.List, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Ver Choferes")
                        }
                    }
                }
            }
        }
    }
}
