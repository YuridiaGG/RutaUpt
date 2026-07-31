package com.example.rutaupt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.LocationBridge
import com.example.rutaupt.storage.ParadaRepository
import com.example.rutaupt.api.Parada
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaScreen(
    initialParada: Parada? = null,
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    
    // Estado para la ubicación real del usuario
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLon by remember { mutableStateOf<Double?>(null) }
    var selectedParada by remember { mutableStateOf<Parada?>(initialParada) }
    
    val hasPermission = LocationBridge.hasPermission?.invoke() ?: false

    // Obtener ubicación al iniciar para centrar el mapa
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            LocationBridge.getCurrentLocation?.invoke { lat, lon ->
                userLat = lat
                userLon = lon
            }
            
            LocationBridge.onLocationUpdate = { lat, lon ->
                userLat = lat
                userLon = lon
            }
            LocationBridge.startLocationUpdates?.invoke()
        }
        ParadaRepository.cargarParadas()
    }

    DisposableEffect(Unit) {
        onDispose {
            LocationBridge.stopLocationUpdates?.invoke()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seguimiento de Ruta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = vinoUpt,
                    navigationIconContentColor = vinoUpt
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (userLat != null && userLon != null) {
                // --- MAPA CON UBICACIÓN REAL Y PARADAS ---
                MapComponent(
                    modifier = Modifier.fillMaxSize(),
                    latitude = userLat!!,
                    longitude = userLon!!,
                    title = "Mi Ubicación",
                    paradas = ParadaRepository.paradas,
                    onParadaSelected = { selectedParada = it }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = vinoUpt)
                        Spacer(Modifier.height(16.dp))
                        Text("Sincronizando GPS...", color = Color.Gray)
                    }
                }
            }

            // --- BUSCADOR ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(Modifier.width(12.dp))
                    Text("Buscar parada...", color = Color.Gray, modifier = Modifier.weight(1f))
                }
            }

            // --- BOTÓN CENTRAR ---
            FloatingActionButton(
                onClick = { 
                    LocationBridge.getCurrentLocation?.invoke { lat, lon ->
                        userLat = lat
                        userLon = lon
                    }
                },
                containerColor = Color.White,
                contentColor = vinoUpt,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = if (selectedParada != null) 200.dp else 32.dp, end = 16.dp)
                    .size(56.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Centrar")
            }

            // --- TARJETA DE DETALLES ---
            if (selectedParada != null) {
                val distStr = if (userLat != null && userLon != null) {
                    calcularDistancia(userLat!!, userLon!!, selectedParada!!.ubicacion)
                } else "Calculando..."

                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(16.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(vinoUpt.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = vinoUpt)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(selectedParada!!.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("A $distStr de ti", color = vinoUpt, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            }
                            IconButton(onClick = { selectedParada = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }
                    }
                }
            } else {
                // Tarjeta por defecto
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(16.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(vinoUpt.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = vinoUpt)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Unidad UPT-05", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("En recorrido - Ubicación real", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun calcularDistancia(lat1: Double, lon1: Double, link: String?): String {
    if (link.isNullOrBlank()) return "Sin ubicación"
    try {
        val regex = Regex("([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)")
        val match = regex.find(link)
        if (match != null) {
            val lat2 = match.groupValues[1].toDouble()
            val lon2 = match.groupValues[2].toDouble()
            
            val r = 6371e3 // Radio de la tierra en metros
            val phi1 = lat1 * PI / 180
            val phi2 = lat2 * PI / 180
            val deltaPhi = (lat2 - lat1) * PI / 180
            val deltaLambda = (lon2 - lon1) * PI / 180

            val a = sin(deltaPhi / 2).pow(2.0) + cos(phi1) * cos(phi2) * sin(deltaLambda / 2).pow(2.0)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val metros = r * c

            return if (metros < 1000) "${metros.toInt()} metros" 
                   else "${(metros/1000).toInt()}.${((metros/100)%10).toInt()} km"
        }
    } catch (e: Exception) {}
    return "Calculando..."
}
