package com.example.rutaupt.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.generated.resources.*
import com.example.rutaupt.LocationBridge
import com.example.rutaupt.LocationUtils
import com.example.rutaupt.model.ReporteUnidad
import com.example.rutaupt.model.ReporteTipo
import com.example.rutaupt.storage.ReporteRepository
import com.example.rutaupt.storage.SessionManager
import com.example.rutaupt.storage.ParadaRepository
import com.example.rutaupt.api.RutaApiService
import com.example.rutaupt.api.UbicacionVehiculo
import com.example.rutaupt.api.Parada
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEstudianteScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToRuta: (Parada?) -> Unit = {}
) {
    val vinoUpt = UPTColors.Vino
    val vinoOscuro = UPTColors.VinoOscuro
    var selectedTab by remember { mutableStateOf("Inicio") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val apiService = remember { RutaApiService() }
    
    var hasLocationPermission by remember { 
        mutableStateOf(LocationBridge.hasPermission?.invoke() ?: false) 
    }

    var currentUserLat by remember { mutableStateOf<Double?>(null) }
    var currentUserLon by remember { mutableStateOf<Double?>(null) }
    var isUserMoving by remember { mutableStateOf(false) }
    var lastLat by remember { mutableStateOf<Double?>(null) }
    var lastLon by remember { mutableStateOf<Double?>(null) }

    var nearbyUnit by remember { mutableStateOf<UbicacionVehiculo?>(null) }
    var lastNotifiedUnitId by remember { mutableStateOf<String?>(null) }
    var showNotifications by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            LocationBridge.onRequestPermission?.invoke { granted ->
                hasLocationPermission = granted
            }
        }
        ParadaRepository.cargarParadas()
        ReporteRepository.cargarReportes()
        
        launch {
            while(true) {
                ReporteRepository.cargarReportes()
                ParadaRepository.cargarParadas()
                delay(15000)
            }
        }

        launch {
            while(true) {
                try {
                    val lat = currentUserLat ?: LocationUtils.DEFAULT_LAT
                    val lon = currentUserLon ?: LocationUtils.DEFAULT_LON
                    val ubicaciones = apiService.obtenerUbicaciones()
                    
                    if (ubicaciones.isNotEmpty()) {
                        val closestPair = ubicaciones.map { unit ->
                            unit to LocationUtils.calcularDistanciaMetros(lat, lon, unit.latitud, unit.longitud)
                        }.minByOrNull { it.second }

                        val closest = closestPair?.first
                        val distance = closestPair?.second ?: Double.MAX_VALUE

                        if (closest != null && distance < 200000) { 
                            if (closest.unidad != lastNotifiedUnitId) {
                                lastNotifiedUnitId = closest.unidad
                                scope.launch {
                                    snackbarHostState.showSnackbar("¡Unidad ${closest.unidad} detectada!")
                                }
                            }
                            nearbyUnit = closest
                        } else {
                            nearbyUnit = null
                            lastNotifiedUnitId = null
                        }
                    } else {
                        nearbyUnit = null
                    }
                } catch (e: Exception) {}
                delay(5000)
            }
        }
    }

    DisposableEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            LocationBridge.getCurrentLocation?.invoke { lat, lon -> 
                currentUserLat = lat
                currentUserLon = lon 
            }
            LocationBridge.onLocationUpdate = { lat, lon -> 
                val pLat = lastLat
                val pLon = lastLon
                if (pLat != null && pLon != null) {
                    val dist = LocationUtils.calcularDistanciaMetros(pLat, pLon, lat, lon)
                    isUserMoving = dist > 1.2 
                }
                currentUserLat = lat
                currentUserLon = lon 
                lastLat = lat
                lastLon = lon
            }
            LocationBridge.startLocationUpdates?.invoke()
        }
        onDispose { LocationBridge.stopLocationUpdates?.invoke() }
    }

    val avisosEstudiante = ReporteRepository.reportes.filter { 
        (it.mensaje?.startsWith("Anónimo:") == true) || it.estado == "Unidad llena" || it.estado == "Disponible" || it.estado?.startsWith("ParadaPasada_") == true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if(selectedTab == "Inicio") "Estudiante" else "Avisos de Ruta", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        IconButton(onClick = { showNotifications = true }) {
                            Icon(Icons.Default.Notifications, null, tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showNotifications,
                            onDismissRequest = { showNotifications = false },
                            modifier = Modifier.width(320.dp).background(Color.White)
                        ) {
                            Text("Avisos Recientes", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                            HorizontalDivider()
                            if (avisosEstudiante.isEmpty()) {
                                Text("No hay avisos nuevos", modifier = Modifier.padding(16.dp), color = Color.Gray)
                            } else {
                                avisosEstudiante.take(10).forEach { reporte ->
                                    DropdownMenuItem(
                                        text = { 
                                            Column {
                                                Text(reporte.mensaje ?: "", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(reporte.tiempo ?: "", fontSize = 10.sp, color = Color.Gray)
                                            }
                                        },
                                        onClick = { showNotifications = false }
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = vinoUpt)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, modifier = Modifier.shadow(8.dp)) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Inicio") },
                    selected = selectedTab == "Inicio",
                    onClick = { selectedTab = "Inicio" },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = vinoUpt, indicatorColor = vinoUpt.copy(alpha = 0.1f))
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Notifications, null) },
                    label = { Text("Avisos") },
                    selected = selectedTab == "Avisos",
                    onClick = { selectedTab = "Avisos" },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = vinoUpt, indicatorColor = vinoUpt.copy(alpha = 0.1f))
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = onNavigateToProfile
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5))) {
            AnimatedContent(targetState = selectedTab) { tab ->
                when (tab) {
                    "Inicio" -> InicioSection(
                        vinoUpt = vinoUpt, 
                        vinoOscuro = vinoOscuro, 
                        nearbyUnit = nearbyUnit,
                        currentUserLat = currentUserLat,
                        currentUserLon = currentUserLon,
                        isUserMoving = isUserMoving,
                        onNavigateToRuta = onNavigateToRuta,
                        onSendReport = { mensaje ->
                            if (SessionManager.puedeEnviarReporte()) {
                                scope.launch {
                                    val nuevoReporte = ReporteUnidad(
                                        unidad = SessionManager.numeroUnidad.ifBlank { "Estudiante" },
                                        mensaje = "Anónimo: $mensaje",
                                        tiempo = "Ahora",
                                        tipo = ReporteTipo.INFORMACION
                                    )
                                    ReporteRepository.agregarReporte(nuevoReporte)
                                    SessionManager.registrarEnvioReporte()
                                    snackbarHostState.showSnackbar("Aviso enviado")
                                }
                            }
                        }
                    )
                    "Avisos" -> AvisosSection(vinoUpt)
                }
            }
        }
    }
}

@Composable
fun InicioSection(
    vinoUpt: Color, 
    vinoOscuro: Color, 
    nearbyUnit: UbicacionVehiculo?,
    currentUserLat: Double?,
    currentUserLon: Double?,
    isUserMoving: Boolean,
    onNavigateToRuta: (Parada?) -> Unit,
    onSendReport: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(Brush.verticalGradient(listOf(vinoUpt, vinoOscuro)), RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).padding(horizontal = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("¡Hola,", color = Color.White, fontSize = 20.sp)
                    Text("${SessionManager.nombreUsuario}!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                Image(painter = painterResource(Res.drawable.estudiante), contentDescription = null, modifier = Modifier.size(130.dp).offset(y = 10.dp), contentScale = ContentScale.Fit)
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Mi ruta")
            
            val microParaMostrar = nearbyUnit ?: UbicacionVehiculo("12", 20.14, -98.32, 0L)
            val curLat = currentUserLat ?: LocationUtils.DEFAULT_LAT
            val curLon = currentUserLon ?: LocationUtils.DEFAULT_LON
            
            val dist = if (nearbyUnit != null) {
                LocationUtils.calcularDistanciaMetros(curLat, curLon, nearbyUnit.latitud, nearbyUnit.longitud)
            } else 97600.0

            NearbyUnitCard(
                unidad = microParaMostrar.unidad,
                distMetros = dist,
                vinoUpt = vinoUpt,
                estaEnMovimiento = isUserMoving,
                onClick = { onNavigateToRuta(null) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Mi ubicación")
            Card(
                modifier = Modifier.fillMaxWidth().height(220.dp).padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp), 
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    MapComponent(
                        modifier = Modifier.fillMaxSize(),
                        latitude = curLat,
                        longitude = curLon,
                        title = "Mi Ubicación",
                        paradas = ParadaRepository.paradas,
                        onMapClick = { _, _ -> onNavigateToRuta(null) }
                    )
                    
                    // Botón para ampliar (ayuda al usuario a saber que puede ir al mapa completo)
                    IconButton(
                        onClick = { onNavigateToRuta(null) },
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.White.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(Icons.Default.OpenInFull, null, tint = vinoUpt, modifier = Modifier.size(20.dp))
                    }
                }
            }

            SectionTitle("Paradas de la ruta")
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    val paradas = ParadaRepository.paradas
                    if (paradas.isEmpty()) {
                        Text("Cargando paradas...", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    } else {
                        paradas.forEachIndexed { index, parada ->
                            val reportePaso = ReporteRepository.reportes.find { it.estado == "ParadaPasada_${parada.nombre}" }
                            
                            val coordsParada = LocationUtils.extraerCoordenadas(parada.ubicacion)
                            val studentLat = currentUserLat
                            val studentLon = currentUserLon
                            val infoUbicacion = if (coordsParada != null) {
                                val distPart = if (studentLat != null && studentLon != null) {
                                    val m = LocationUtils.calcularDistanciaMetros(studentLat, studentLon, coordsParada.first, coordsParada.second)
                                    val km = m / 1000.0
                                    val formattedKm = (km * 10).toInt() / 10.0
                                    " a $formattedKm km"
                                } else ""
                                " • Ubicación$distPart"
                            } else ""

                            StopItem(
                                name = parada.nombre,
                                status = (if (reportePaso != null) "Ya pasó Unidad ${reportePaso.unidad ?: ""}" else "En espera") + infoUbicacion,
                                isFirst = index == 0,
                                isLast = index == paradas.size - 1,
                                color = if (reportePaso != null) Color(0xFF2E7D32) else Color.Gray,
                                onClick = { onNavigateToRuta(parada) }
                            )
                        }
                    }
                }
            }

            SectionTitle("¿Cómo va mi ruta?")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusButton("Ya pasó", Icons.Default.CheckCircle, Color(0xFFE8F5E9), Color(0xFF4CAF50), Modifier.weight(1f)) { onSendReport("La unidad ya pasó") }
                    StatusButton("Va llena", Icons.Default.Groups, Color(0xFFFFF3E0), Color(0xFFFF9800), Modifier.weight(1f)) { onSendReport("La unidad va llena") }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusButton("Retrasada", Icons.Default.History, Color(0xFFFFEBEE), Color(0xFFD32F2F), Modifier.weight(1f)) { onSendReport("La unidad viene retrasada") }
                    StatusButton("Todo normal", Icons.Default.ThumbUp, Color(0xFFE3F2FD), Color(0xFF1976D2), Modifier.weight(1f)) { onSendReport("Todo normal por aquí") }
                }
            }
        }
    }
}

@Composable
fun AvisosSection(vinoUpt: Color) {
    val avisosEstudiantes = ReporteRepository.reportes.filter { it.mensaje?.startsWith("Anónimo:") == true }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Avisos de Estudiantes", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = vinoUpt)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(avisosEstudiantes) { reporte ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = vinoUpt, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(reporte.mensaje ?: "", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(reporte.tiempo ?: "", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp), color = Color(0xFF333333))
}

@Composable
fun StopItem(name: String, status: String, isFirst: Boolean, isLast: Boolean, color: Color, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(32.dp).heightIn(min = 72.dp), contentAlignment = Alignment.Center) {
            if (!isFirst) Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.5f).align(Alignment.TopCenter).background(Color.LightGray))
            if (!isLast) Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.5f).align(Alignment.BottomCenter).background(Color.LightGray))
            Box(modifier = Modifier.size(14.dp).background(Color.White, CircleShape).border(1.dp, Color.LightGray, CircleShape).padding(2.dp)) {
                Box(modifier = Modifier.fillMaxSize().background(color, CircleShape))
            }
        }
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp, top = 12.dp, bottom = 12.dp)) {
            Text(text = name, fontSize = 16.sp, fontWeight = if(isFirst || isLast) FontWeight.Bold else FontWeight.Medium, color = Color.Black)
            Text(text = status, fontSize = 12.sp, color = if(status.contains("pasó")) Color(0xFF2E7D32) else Color(0xFF2196F3), fontWeight = FontWeight.Bold)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
    }
}

@Composable
fun StatusButton(text: String, icon: ImageVector, bgColor: Color, iconColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.height(60.dp).clickable { onClick() }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = iconColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
