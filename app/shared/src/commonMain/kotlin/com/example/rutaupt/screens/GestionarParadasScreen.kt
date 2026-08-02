package com.example.rutaupt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.LocationBridge
import com.example.rutaupt.LocationUtils
import com.example.rutaupt.storage.ParadaRepository
import com.example.rutaupt.api.Parada
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarParadasScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    val paradas = ParadaRepository.paradas
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var nombreParada by remember { mutableStateOf("") }
    var pickedLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var editingParada by remember { mutableStateOf<Parada?>(null) }
    var isActionLoading by remember { mutableStateOf(false) }

    // Ubicación inicial del mapa (Tulancingo por defecto)
    var mapCenterLat by remember { mutableStateOf(LocationUtils.DEFAULT_LAT) }
    var mapCenterLon by remember { mutableStateOf(LocationUtils.DEFAULT_LON) }

    LaunchedEffect(Unit) {
        ParadaRepository.cargarParadas()
        LocationBridge.getCurrentLocation?.invoke { lat, lon ->
            mapCenterLat = lat
            mapCenterLon = lon
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editingParada == null) "Gestionar Paradas" else "Editando Parada", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (editingParada != null) {
                            editingParada = null
                            nombreParada = ""
                            pickedLocation = null
                        } else {
                            onVolver()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = vinoUpt)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                    MapComponent(
                        modifier = Modifier.fillMaxSize(),
                        latitude = mapCenterLat,
                        longitude = mapCenterLon,
                        paradas = paradas,
                        pickedLocation = pickedLocation,
                        onMapClick = { lat, lon ->
                            pickedLocation = Pair(lat, lon)
                        }
                    )
                    
                    Surface(
                        modifier = Modifier.align(Alignment.TopCenter).padding(16.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            "Toca el mapa para ubicar la parada",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    SmallFloatingActionButton(
                        onClick = {
                            LocationBridge.getCurrentLocation?.invoke { lat, lon ->
                                mapCenterLat = lat
                                mapCenterLon = lon
                            }
                        },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                        containerColor = Color.White,
                        contentColor = vinoUpt
                    ) {
                        Icon(Icons.Default.MyLocation, null)
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = nombreParada,
                                onValueChange = { nombreParada = it },
                                label = { Text("Nombre de la parada") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            Spacer(Modifier.height(8.dp))
                            
                            val locationStatus = if (pickedLocation != null) "Ubicación seleccionada ✓" else "Selecciona en el mapa ⚠"
                            Text(locationStatus, fontSize = 12.sp, color = if(pickedLocation != null) Color(0xFF2E7D32) else Color.Red, fontWeight = FontWeight.Bold)

                            Spacer(Modifier.height(12.dp))
                            
                            Button(
                                onClick = {
                                    if (nombreParada.isNotBlank() && pickedLocation != null) {
                                        isActionLoading = true
                                        scope.launch {
                                            val ubiString = "${pickedLocation!!.first},${pickedLocation!!.second}"
                                            val success = if (editingParada == null) {
                                                ParadaRepository.agregarParada(nombreParada, ubiString)
                                            } else {
                                                val actualizada = editingParada!!.copy(nombre = nombreParada, ubicacion = ubiString)
                                                ParadaRepository.actualizarParada(actualizada)
                                            }

                                            if (success) {
                                                snackbarHostState.showSnackbar(if (editingParada == null) "Parada guardada" else "Actualizada")
                                                editingParada = null
                                                nombreParada = ""
                                                pickedLocation = null
                                            } else {
                                                snackbarHostState.showSnackbar("Error al guardar")
                                            }
                                            isActionLoading = false
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = vinoUpt),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isActionLoading && nombreParada.isNotBlank() && pickedLocation != null
                            ) {
                                Icon(if (editingParada == null) Icons.Default.Add else Icons.Default.Save, null)
                                Spacer(Modifier.width(8.dp))
                                Text(if (editingParada == null) "Guardar Parada" else "Actualizar Parada")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "Paradas Registradas (${paradas.size}):", 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
                )
            }

            items(paradas) { parada ->
                Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)) {
                    ListItem(
                        headlineContent = { Text(parada.nombre, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(parada.ubicacion ?: "", fontSize = 10.sp, maxLines = 1) },
                        leadingContent = { Icon(Icons.Default.LocationOn, null, tint = vinoUpt) },
                        trailingContent = {
                            IconButton(onClick = {
                                scope.launch {
                                    parada.id?.let { ParadaRepository.eliminarParada(it) }
                                }
                            }) {
                                Icon(Icons.Default.Delete, null, tint = Color.LightGray)
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable {
                                editingParada = parada
                                nombreParada = parada.nombre
                                pickedLocation = LocationUtils.extraerCoordenadas(parada.ubicacion)
                                pickedLocation?.let { 
                                    mapCenterLat = it.first
                                    mapCenterLon = it.second
                                }
                            }
                    )
                }
            }
        }
    }
}
