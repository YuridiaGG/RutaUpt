package com.example.rutaupt.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.storage.ParadaRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarParadasScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    val paradas = ParadaRepository.paradas
    var nombreParada by remember { mutableStateOf("") }
    var linkUbicacion by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        ParadaRepository.cargarParadas()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Paradas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver, enabled = !isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = vinoUpt
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(
                "Establece las paradas y sus ubicaciones (links de Google Maps):",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = nombreParada,
                onValueChange = { nombreParada = it },
                label = { Text("Nombre de la parada") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = linkUbicacion,
                onValueChange = { linkUbicacion = it },
                label = { Text("Link de Google Maps") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        if (nombreParada.isNotBlank()) {
                            scope.launch {
                                val success = ParadaRepository.agregarParada(nombreParada, linkUbicacion.ifBlank { null })
                                if (success) {
                                    nombreParada = ""
                                    linkUbicacion = ""
                                    snackbarHostState.showSnackbar("Parada agregada correctamente")
                                } else {
                                    snackbarHostState.showSnackbar("Error al guardar en el servidor")
                                }
                            }
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar", tint = vinoUpt)
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = vinoUpt)
                }
            } else if (paradas.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No hay paradas registradas", color = Color.LightGray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(paradas) { parada ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = vinoUpt)
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(parada.nombre, fontWeight = FontWeight.Bold)
                                    if (!parada.ubicacion.isNullOrBlank()) {
                                        Text(parada.ubicacion!!, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                                    }
                                }
                                IconButton(onClick = { 
                                    scope.launch {
                                        val success = ParadaRepository.eliminarParada(parada.nombre)
                                        if (success) {
                                            snackbarHostState.showSnackbar("Parada eliminada")
                                        } else {
                                            snackbarHostState.showSnackbar("Error al eliminar")
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
