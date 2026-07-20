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
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.LocationBridge
import com.example.rutaupt.api.RutaApiService
import com.example.rutaupt.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionMicrosScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    var unidadSeleccionada by remember { mutableStateOf<String?>(null) }
    val apiService = remember { RutaApiService() }
    var choferes by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    var hasLocationPermission by remember { 
        mutableStateOf(LocationBridge.hasPermission?.invoke() ?: false) 
    }

    LaunchedEffect(Unit) {
        choferes = apiService.obtenerUsuariosPorRol("chofer")
        isLoading = false
    }

    // Efecto para solicitar permiso si se selecciona una unidad y no lo tiene
    LaunchedEffect(unidadSeleccionada) {
        if (unidadSeleccionada != null && !hasLocationPermission) {
            LocationBridge.onRequestPermission?.invoke { granted ->
                hasLocationPermission = granted
                if (!granted) {
                    unidadSeleccionada = null // Regresar a la lista si rechaza
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (unidadSeleccionada == null) "Ubicación de Micros" else "Ubicación: $unidadSeleccionada", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (unidadSeleccionada != null) unidadSeleccionada = null
                        else onVolver()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = vinoUpt
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (unidadSeleccionada == null) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = vinoUpt)
                }
            } else {
                ListaUnidadesActivas(padding, choferes) { unidad -> 
                    unidadSeleccionada = unidad 
                }
            }
        } else if (hasLocationPermission) {
            MapaTiempoReal(padding, unidadSeleccionada!!)
        } else {
            // Mientras pide el permiso o si se está procesando
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = vinoUpt)
            }
        }
    }
}

@Composable
fun ListaUnidadesActivas(padding: PaddingValues, choferes: List<User>, onSelect: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                if (choferes.isEmpty()) "No hay unidades activas registradas." 
                else "Seleccione una unidad para ver su ubicación en tiempo real:", 
                color = Color.Gray, 
                fontSize = 14.sp, 
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(choferes) { chofer ->
            val unidad = chofer.numeroUnidad ?: "Sin número"
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(unidad) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).background(UPTColors.Vino.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DirectionsBus, contentDescription = "Unidad", tint = UPTColors.Vino)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Unidad $unidad", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${chofer.nombre} ${chofer.apellidos}", color = Color.Gray, fontSize = 14.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun MapaTiempoReal(padding: PaddingValues, unidad: String) {
    val vinoUpt = UPTColors.Vino
    
    // 1. Estado para la ubicación de la micro (empezamos en null para evitar el salto a Perú/Tulancingo)
    var microLat by remember { mutableStateOf<Double?>(null) }
    var microLon by remember { mutableStateOf<Double?>(null) }

    // 2. Al abrir el mapa de la micro, obtenemos la ubicación real inmediatamente
    LaunchedEffect(unidad) {
        // Pedir ubicación actual para centrar el mapa rápido
        LocationBridge.getCurrentLocation?.invoke { lat, lon ->
            microLat = lat
            microLon = lon
        }
        
        // Suscribirse a actualizaciones (por si la micro se mueve)
        LocationBridge.onLocationUpdate = { lat, lon ->
            microLat = lat
            microLon = lon
        }
        LocationBridge.startLocationUpdates?.invoke()
    }

    // Detener el rastreo al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            LocationBridge.stopLocationUpdates?.invoke()
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
        if (microLat != null && microLon != null) {
            // 3. PASAMOS LA UBICACIÓN REAL AL MAPA (Adiós a las coordenadas de Perú/Tulancingo)
            MapComponent(
                modifier = Modifier.fillMaxSize(),
                latitude = microLat!!,
                longitude = microLon!!,
                title = "Unidad $unidad"
            )
            
            // Tarjeta informativa flotante
            Card(
                modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFF4CAF50), CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Text("Unidad $unidad - En Vivo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text("Transmitiendo ubicación exacta", fontSize = 13.sp, color = Color.Gray)
                    
                    Button(
                        onClick = { 
                            LocationBridge.getCurrentLocation?.invoke { lat, lon ->
                                microLat = lat
                                microLon = lon
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = vinoUpt)
                    ) {
                        Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Actualizar Posición")
                    }
                }
            }
        } else {
            // 4. Pantalla de carga mientras el GPS te encuentra
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = vinoUpt)
                    Spacer(Modifier.height(16.dp))
                    Text("Conectando con el GPS de la unidad...", color = Color.Gray)
                }
            }
        }
    }
}
