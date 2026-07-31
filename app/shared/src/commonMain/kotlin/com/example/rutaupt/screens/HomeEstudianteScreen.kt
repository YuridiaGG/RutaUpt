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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.TrackingService
import com.example.rutaupt.generated.resources.*
import com.example.rutaupt.getPlatform
import com.example.rutaupt.LocationBridge
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
import kotlin.math.abs

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
    
    var hasLocationPermission by remember { 
        mutableStateOf(LocationBridge.hasPermission?.invoke() ?: false) 
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            LocationBridge.onRequestPermission?.invoke { granted ->
                hasLocationPermission = granted
            }
        }
        ParadaRepository.cargarParadas()
        ReporteRepository.cargarReportes()
    }

    var showNotifications by remember { mutableStateOf(false) }
    var lastSeenNotificationCount by remember { mutableStateOf(0) }
    
    // FILTRO: Solo avisos que interesan al estudiante (otros estudiantes o estados de unidad)
    val avisosEstudiante = ReporteRepository.reportes.filter { 
        it.mensaje.startsWith("Anónimo:") || it.estado == "Unidad llena" || it.estado == "Disponible"
    }
    
    val currentNotificationCount = avisosEstudiante.size
    val hasNewNotifications = currentNotificationCount > lastSeenNotificationCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when(selectedTab) {
                            "Inicio" -> "Estudiante"
                            "Avisos" -> "Avisos de Ruta"
                            else -> "Estudiante"
                        }, 
                        color = Color.White, 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    Box {
                        IconButton(onClick = { 
                            showNotifications = true 
                            lastSeenNotificationCount = currentNotificationCount
                        }) {
                            BadgedBox(
                                badge = { 
                                    if (hasNewNotifications && currentNotificationCount > 0) {
                                        Badge { Text(currentNotificationCount.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                            }
                        }
                        DropdownMenu(
                            expanded = showNotifications,
                            onDismissRequest = { showNotifications = false },
                            modifier = Modifier.width(280.dp).background(Color.White)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Avisos Recientes", fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider()
                            if (avisosEstudiante.isEmpty()) {
                                Text("No hay avisos nuevos", modifier = Modifier.padding(16.dp), color = Color.Gray)
                            } else {
                                avisosEstudiante.take(5).forEach { reporte ->
                                    DropdownMenuItem(
                                        text = { 
                                            Column {
                                                Text(reporte.mensaje, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(reporte.tiempo, fontSize = 10.sp, color = Color.Gray)
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
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = selectedTab == "Inicio",
                    onClick = { selectedTab = "Inicio" },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = vinoUpt, indicatorColor = vinoUpt.copy(alpha = 0.1f))
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Avisos") },
                    label = { Text("Avisos") },
                    selected = selectedTab == "Avisos",
                    onClick = { selectedTab = "Avisos" },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = vinoUpt, indicatorColor = vinoUpt.copy(alpha = 0.1f))
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = onNavigateToProfile
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            AnimatedContent(targetState = selectedTab) { tab ->
                when (tab) {
                    "Inicio" -> InicioSection(vinoUpt, vinoOscuro, hasLocationPermission, onNavigateToRuta) { mensaje ->
                        if (SessionManager.puedeEnviarReporte()) {
                            scope.launch {
                                val nuevoReporte = ReporteUnidad(
                                    unidad = "UPT-05",
                                    mensaje = "Anónimo: $mensaje",
                                    tiempo = "Ahora",
                                    tipo = ReporteTipo.INFORMACION
                                )
                                ReporteRepository.agregarReporte(nuevoReporte)
                                getPlatform().showNotification("Reporte Recibido", "Un estudiante avisó: $mensaje")
                                SessionManager.registrarEnvioReporte()
                                snackbarHostState.showSnackbar("Aviso enviado de forma anónima")
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Límite diario de 2 avisos alcanzado")
                            }
                        }
                    }
                    "Avisos" -> AvisosSection(vinoUpt)
                }
            }
        }
    }
}

@Composable
fun InicioSection(vinoUpt: Color, vinoOscuro: Color, hasPermission: Boolean, onNavigateToRuta: (Parada?) -> Unit, onSendReport: (String) -> Unit) {
    val trackingService = remember { TrackingService() }
    val apiService = remember { RutaApiService() }
    
    var currentUserLat by remember { mutableStateOf<Double?>(null) }
    var currentUserLon by remember { mutableStateOf<Double?>(null) }
    val unidadesReales = remember { mutableStateListOf<UbicacionVehiculo>() }

    DisposableEffect(hasPermission) {
        if (hasPermission) {
            LocationBridge.getCurrentLocation?.invoke { lat, lon ->
                currentUserLat = lat
                currentUserLon = lon
            }
            LocationBridge.onLocationUpdate = { lat, lon ->
                currentUserLat = lat
                currentUserLon = lon
            }
            LocationBridge.startLocationUpdates?.invoke()
        }
        onDispose {
            LocationBridge.stopLocationUpdates?.invoke()
        }
    }

    var unidadMasCercana by remember { mutableStateOf<UbicacionVehiculo?>(null) }
    var tiempoEstimado by remember { mutableStateOf(0) }
    var lastNotifiedUnidad by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while(true) {
            val lista = apiService.obtenerUbicaciones()
            unidadesReales.clear()
            unidadesReales.addAll(lista)
            delay(10000)
        }
    }

    LaunchedEffect(currentUserLat, currentUserLon, unidadesReales.size) {
        val lat = currentUserLat ?: return@LaunchedEffect
        val lon = currentUserLon ?: return@LaunchedEffect
        
        if (unidadesReales.isNotEmpty()) {
            val masCercana = unidadesReales.minBy { 
                abs(it.latitud - lat) + abs(it.longitud - lon)
            }
            unidadMasCercana = masCercana
            tiempoEstimado = trackingService.calcularTiempoEstimado(
                lat, lon, masCercana.latitud, masCercana.longitud
            )

            if (tiempoEstimado <= 5 && lastNotifiedUnidad != masCercana.unidad) {
                getPlatform().showNotification("Unidad Cerca", "La Unidad ${masCercana.unidad}, esta cerca de ti")
                lastNotifiedUnidad = masCercana.unidad
            } else if (tiempoEstimado > 10) {
                lastNotifiedUnidad = ""
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(colors = listOf(vinoUpt, vinoOscuro)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(horizontal = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = buildAnnotatedString {
                            append("¡Hola, ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(SessionManager.nombreUsuario)
                            }
                            append("!")
                        },
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                Image(
                    painter = painterResource(Res.drawable.estudiante),
                    contentDescription = "Mascota Estudiante",
                    modifier = Modifier.size(140.dp).offset(y = 10.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            unidadMasCercana?.let { unidad ->
                if (tiempoEstimado <= 15) {
                    SectionTitle("Micro cerca de ti")
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = vinoUpt)
                                Spacer(Modifier.width(8.dp))
                                Text("¡Unidad aproximándose!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = vinoUpt)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Unidad ${unidad.unidad}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                    Text("Ruta en curso", color = Color.Gray, fontSize = 12.sp)
                                }
                                Surface(color = vinoUpt, shape = RoundedCornerShape(12.dp)) {
                                    Text("$tiempoEstimado min", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }
            }

            SectionTitle("Mi ubicación")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(vertical = 8.dp)
                    .clickable { onNavigateToRuta(null) },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (hasPermission && currentUserLat != null) {
                        MapComponent(
                            modifier = Modifier.fillMaxSize(),
                            latitude = currentUserLat!!,
                            longitude = currentUserLon!!,
                            title = "Mi Ubicación Actual"
                        )
                        
                        Surface(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(12.dp).align(Alignment.TopStart).shadow(2.dp, RoundedCornerShape(20.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF2196F3), CircleShape))
                                Spacer(Modifier.width(8.dp))
                                Text("Ubicación en tiempo real", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = vinoUpt)
                        }
                    }
                }
            }

            SectionTitle("Paradas disponibles")
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    val paradas = ParadaRepository.paradas
                    if (paradas.isEmpty()) {
                        Text("No hay paradas registradas", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(16.dp))
                    } else {
                        paradas.forEachIndexed { index, parada ->
                            StopItem(
                                name = parada.nombre,
                                time = "--:--",
                                isFirst = index == 0,
                                isLast = index == paradas.size - 1,
                                color = if (index == 0) Color.Red else if (index == paradas.size - 1) Color.Black else Color.Gray,
                                onClick = {
                                    onNavigateToRuta(parada)
                                }
                            )
                        }
                    }
                }
            }

            SectionTitle("¿Cómo va mi ruta?")
            val puedeReportar = SessionManager.puedeEnviarReporte()
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusButton("Ya pasó", Icons.Default.CheckCircle, Color(0xFFE8F5E9), Color(0xFF4CAF50), Modifier.weight(1f), enabled = puedeReportar) {
                    onSendReport("La unidad ya pasó")
                }
                StatusButton("Va llena", Icons.Default.Groups, Color(0xFFFFF3E0), Color(0xFFFF9800), Modifier.weight(1f), enabled = puedeReportar) {
                    onSendReport("La unidad va llena")
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusButton("Retrasada", Icons.Default.AccessTime, Color(0xFFFFEBEE), Color(0xFFF44336), Modifier.weight(1f), enabled = puedeReportar) {
                    onSendReport("La unidad viene retrasada")
                }
                StatusButton("Todo normal", Icons.Default.SentimentSatisfied, Color(0xFFE3F2FD), Color(0xFF2196F3), Modifier.weight(1f), enabled = puedeReportar) {
                    onSendReport("Ruta fluyendo normal")
                }
            }

            SectionTitle("Avisos de otros estudiantes")
            val avisosOtros = ReporteRepository.reportes.filter { it.mensaje.startsWith("Anónimo:") }
            if (avisosOtros.isEmpty()) {
                Text("No hay avisos recientes", modifier = Modifier.padding(8.dp), color = Color.Gray)
            } else {
                avisosOtros.take(3).forEach { reporte ->
                    RecentReportItem(reporte.mensaje, reporte.tiempo, Icons.Default.Info, if(reporte.tipo == ReporteTipo.ALERTA) Color.Red else vinoUpt)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AvisosSection(vinoUpt: Color) {
    val avisosEstudiantes = ReporteRepository.reportes.filter { it.mensaje.startsWith("Anónimo:") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Avisos de Estudiantes", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = vinoUpt)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (avisosEstudiantes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.NotificationsNone, null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                    Text("No hay avisos de otros estudiantes", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(avisosEstudiantes) { reporte ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if(reporte.tipo == ReporteTipo.ALERTA) Icons.Default.Warning else Icons.Default.Info, 
                                contentDescription = null, 
                                tint = if(reporte.tipo == ReporteTipo.ALERTA) Color.Red else vinoUpt,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(reporte.mensaje, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(reporte.tiempo, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        color = Color(0xFF333333)
    )
}

@Composable
fun StopItem(name: String, time: String, isFirst: Boolean = false, isLast: Boolean = false, color: Color = Color.Gray, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp), 
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(32.dp).heightIn(min = 64.dp), contentAlignment = Alignment.Center) {
            if (!isFirst) Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.5f).align(Alignment.TopCenter).background(Color.LightGray))
            if (!isLast) Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.5f).align(Alignment.BottomCenter).background(Color.LightGray))
            
            Box(modifier = Modifier.size(14.dp).background(Color.White, CircleShape).border(1.dp, Color.LightGray, CircleShape).padding(2.dp)) {
                Box(modifier = Modifier.fillMaxSize().background(color, CircleShape))
            }
        }
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp, end = 8.dp, top = 12.dp, bottom = 12.dp)) {
            Text(
                text = name, 
                fontSize = 16.sp, 
                fontWeight = if(isFirst || isLast) FontWeight.Bold else FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "Ver en mapa", 
                fontSize = 12.sp, 
                color = Color(0xFF2196F3),
                fontWeight = FontWeight.Bold
            )
        }
        Text(time, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun StatusButton(text: String, icon: ImageVector, bgColor: Color, iconColor: Color, modifier: Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(60.dp).clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if(enabled) bgColor else Color.LightGray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = if(enabled) iconColor else Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = if(enabled) iconColor else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun RecentReportItem(title: String, time: String, icon: ImageVector, iconColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(time, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        }
    }
}
