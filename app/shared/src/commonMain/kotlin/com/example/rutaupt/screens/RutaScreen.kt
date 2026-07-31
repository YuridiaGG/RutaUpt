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
import com.example.rutaupt.LocationUtils
import com.example.rutaupt.storage.ParadaRepository
import com.example.rutaupt.api.Parada

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaScreen(
    initialParada: Parada? = null,
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    
    // Ubicación inicial (UPT) para que el mapa no inicie en 0,0
    var userLat by remember { mutableStateOf(LocationUtils.DEFAULT_LAT) }
    var userLon by remember { mutableStateOf(LocationUtils.DEFAULT_LON) }
    var selectedParada by remember { mutableStateOf<Parada?>(initialParada) }
    var locationFixed by remember { mutableStateOf(false) }
    
    val hasPermission = LocationBridge.hasPermission?.invoke() ?: false

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            LocationBridge.getCurrentLocation?.invoke { lat, lon ->
                userLat = lat
                userLon = lon
                locationFixed = true
            }
            LocationBridge.onLocationUpdate = { lat, lon ->
                userLat = lat
                userLon = lon
                locationFixed = true
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

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        
        // Determinar coordenadas de enfoque (Parada seleccionada o Usuario)
        val focusLat = selectedParada?.let { LocationUtils.extraerCoordenadas(it.ubicacion)?.first } ?: userLat
        val focusLon = selectedParada?.let { LocationUtils.extraerCoordenadas(it.ubicacion)?.second } ?: userLon

        // MAPA A PANTALLA COMPLETA
        MapComponent(
            modifier = Modifier.fillMaxSize(),
            latitude = focusLat,
            longitude = focusLon,
            title = "Mi Ubicación",
            paradas = ParadaRepository.paradas,
            onParadaSelected = { selectedParada = it }
        )

        // Buscador superior flotante
        Box(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp)) {
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp).shadow(12.dp, RoundedCornerShape(28.dp)),
                color = Color.White,
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = vinoUpt)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("Explorando paradas Ruta UPT", color = Color.Gray, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Search, null, tint = Color.Gray, modifier = Modifier.padding(end = 12.dp))
                }
            }
        }

        // Botón Centrar Mi Ubicación
        FloatingActionButton(
            onClick = { 
                LocationBridge.getCurrentLocation?.invoke { lat, lon -> userLat = lat; userLon = lon }
                selectedParada = null
            },
            containerColor = Color.White,
            contentColor = vinoUpt,
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = if (selectedParada != null) 200.dp else 32.dp, end = 16.dp).size(56.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Default.MyLocation, "Centrar")
        }

        // Tarjeta de parada seleccionada
        if (selectedParada != null) {
            val distTexto = LocationUtils.extraerCoordenadas(selectedParada!!.ubicacion)?.let { coords ->
                val m = LocationUtils.calcularDistanciaMetros(userLat, userLon, coords.first, coords.second)
                val t = LocationUtils.calcularTiempoMinutos(m)
                "${LocationUtils.formatoDistancia(m)} ($t min)"
            } ?: "Link no disponible"

            Card(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp).shadow(20.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(50.dp).background(vinoUpt.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.DirectionsBus, null, tint = vinoUpt)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(selectedParada!!.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        Text(distTexto, color = vinoUpt, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                    IconButton(onClick = { selectedParada = null }) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                }
            }
        }
    }
}
