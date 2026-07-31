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
import com.example.rutaupt.api.RutaApiService
import com.example.rutaupt.api.UbicacionVehiculo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaScreen(
    initialParada: Parada? = null,
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    val apiService = remember { RutaApiService() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var userLat by remember { mutableStateOf(LocationUtils.DEFAULT_LAT) }
    var userLon by remember { mutableStateOf(LocationUtils.DEFAULT_LON) }
    var selectedParada by remember { mutableStateOf<Parada?>(initialParada) }
    
    // Estados para detección de micro cercana
    var nearbyUnit by remember { mutableStateOf<UbicacionVehiculo?>(null) }
    var lastNotifiedUnitId by remember { mutableStateOf<String?>(null) }
    var dismissedUnitId by remember { mutableStateOf<String?>(null) }

    val hasPermission = LocationBridge.hasPermission?.invoke() ?: false

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

    // Lógica de detección de micro cercana y notificación en tiempo real
    LaunchedEffect(userLat, userLon) {
        if (userLat != 0.0 && userLon != 0.0) {
            while(true) {
                try {
                    val ubicaciones = apiService.obtenerUbicaciones()
                    val detected = ubicaciones.find { unit ->
                        val dist = LocationUtils.calcularDistanciaMetros(
                            userLat, userLon,
                            unit.latitud, unit.longitud
                        )
                        dist < 1000 
                    }

                    if (detected != null && detected.unidad != lastNotifiedUnitId && detected.unidad != dismissedUnitId) {
                        scope.launch {
                            snackbarHostState.showSnackbar("¡La Unidad ${detected.unidad} está cerca de ti!")
                        }
                        lastNotifiedUnitId = detected.unidad
                    } else if (detected == null) {
                        lastNotifiedUnitId = null
                        dismissedUnitId = null
                    }
                    
                    nearbyUnit = if (detected?.unidad == dismissedUnitId) null else detected
                    
                } catch (e: Exception) {}
                delay(12000)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            LocationBridge.stopLocationUpdates?.invoke()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Color.White).padding(padding)) {
            
            val focusLat = selectedParada?.let { LocationUtils.extraerCoordenadas(it.ubicacion)?.first } ?: userLat
            val focusLon = selectedParada?.let { LocationUtils.extraerCoordenadas(it.ubicacion)?.second } ?: userLon

            MapComponent(
                modifier = Modifier.fillMaxSize(),
                latitude = focusLat,
                longitude = focusLon,
                title = "Mi Ubicación",
                paradas = ParadaRepository.paradas,
                onParadaSelected = { selectedParada = it }
            )

            // UI Superior: Buscador + Card de Micro Cercana
            Column(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp)) {
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

                // --- Card de Micro Cercana (Shared Component) ---
                if (nearbyUnit != null) {
                    val dist = LocationUtils.calcularDistanciaMetros(userLat, userLon, nearbyUnit!!.latitud, nearbyUnit!!.longitud)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box {
                        NearbyUnitCard(nearbyUnit!!.unidad, dist, vinoUpt)
                        // Botón para cerrar la card y "quitar el marcador"
                        IconButton(
                            onClick = { 
                                dismissedUnitId = nearbyUnit?.unidad
                                nearbyUnit = null 
                            },
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
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
                } ?: "Ubicación no disponible"

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
}
