package com.example.rutaupt.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AgregarChoferScreen(
    onVolver: () -> Unit
) {

    var pantalla by remember {
        mutableStateOf("menu")
    }

    when (pantalla) {

        "formulario" -> {
            FormularioChoferScreen {
                pantalla = "menu"
            }
        }

        "lista" -> {
            ListaChoferesScreen {
                pantalla = "menu"
            }
        }

        else -> {

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text("Gestión de Choferes")
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
                            pantalla = "formulario"
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {

                            Icon(
                                Icons.Default.DirectionsBus,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Text(
                                "Agregar Chofer Nuevo"
                            )
                        }
                    }

                    Card(
                        onClick = {
                            pantalla = "lista"
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {

                            Icon(
                                Icons.Default.List,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Text(
                                "Ver Choferes"
                            )
                        }
                    }

                    Button(
                        onClick = onVolver,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Regresar")
                    }
                }
            }
        }
    }
}