package com.example.rutaupt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaScreen(onVolver: () -> Unit) {
    val vinoUpt = UPTColors.Vino
    
    // Estado para la ubicación real del usuario
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLon by remember { mutableStateOf<Double?>(null) }
    
    val hasPermission = LocationBridge.hasPermission?.invoke() ?: false

    // Obtener ubicación al iniciar para centrar el mapa
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            LocationBridge.getCurrentLocation?.invoke { lat, lon ->
                userLat = lat
                userLon = lon
            }
            
            LocationBridge.onLocationUpdate = { lat, lon ->
                if (userLat == null) {
                    userLat = lat
                    userLon = lon
                }
            }
            LocationBridge.startLocationUpdates?.invoke()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            LocationBridge.stopLocationUpdates?.invoke()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seguimiento en Tiempo Real", fontWeight = FontWeight.Bold) },
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
                // --- MAPA CON UBICACIÓN REAL ---
                MapComponent(
                    modifier = Modifier.fillMaxSize(),
                    latitude = userLat!!,
                    longitude = userLon!!,
                    title = "Mi Ubicación Exacta"
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = vinoUpt)
                }
            }

            // --- BUSCADOR TIPO GOOGLE MAPS ---
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
                    Text("Buscar parada o micro...", color = Color.Gray, modifier = Modifier.weight(1f))
                }
            }

            // --- BOTONES FLOTANTES DE MAPA ---
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 120.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Centrar")
                }
            }

            // --- TARJETA DE DETALLES ---
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
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = vinoUpt),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Detalles")
                    }
                }
            }
        }
    }
}
