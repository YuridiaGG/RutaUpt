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

    // Obtener ubicación al iniciar
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

    // Usamos Box para que el mapa ocupe TODA la pantalla y los elementos floten encima
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        
        // 1. EL MAPA (Completo)
        if (userLat != null && userLon != null) {
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

        // 2. BUSCADOR Y BOTÓN VOLVER (Overlay superior)
        Column(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(28.dp))
                    .background(Color.White, RoundedCornerShape(28.dp))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = vinoUpt)
                }
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                Spacer(Modifier.width(12.dp))
                Text("Explorar ruta...", color = Color.Gray, modifier = Modifier.weight(1f))
            }
        }

        // 3. BOTÓN CENTRAR
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
                .padding(bottom = if (selectedParada != null) 220.dp else 32.dp, end = 16.dp)
                .size(56.dp)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Centrar")
        }

        // 4. TARJETA DE DETALLES (Overlay inferior)
        if (selectedParada != null) {
            val distStr = if (userLat != null && userLon != null) {
                calcularDistanciaKM(userLat!!, userLon!!, selectedParada!!.ubicacion)
            } else "Calculando..."

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
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
                            Text(selectedParada!!.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                            Text("A $distStr de ti", color = vinoUpt, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                        IconButton(onClick = { selectedParada = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }
                }
            }
        }
    }
}

private fun calcularDistanciaKM(lat1: Double, lon1: Double, link: String?): String {
    if (link.isNullOrBlank()) return "Sin ubicación"
    try {
        val regex = Regex("([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)")
        val match = regex.find(link)
        if (match != null) {
            val lat2 = match.groupValues[1].toDouble()
            val lon2 = match.groupValues[2].toDouble()
            
            val r = 6371.0 // km
            val dLat = (lat2 - lat1) * PI / 180.0
            val dLon = (lon2 - lon1) * PI / 180.0
            val a = sin(dLat / 2).pow(2.0) + cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2.0)
            val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
            val distancia = r * c

            return if (distancia < 1.0) {
                "${(distancia * 1000).toInt()} metros"
            } else {
                "${distancia.format(1)} km"
            }
        }
    } catch (e: Exception) {}
    return "Cerca"
}

private fun Double.format(digits: Int): String {
    val factor = 10.0.pow(digits)
    return ((this * factor).toInt() / factor).toString()
}
