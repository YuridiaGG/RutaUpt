package com.example.rutaupt.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.rutaupt.storage.ChoferRepository
import com.example.rutaupt.storage.ReporteRepository
import com.example.rutaupt.storage.SessionManager
import com.example.rutaupt.storage.ParadaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEstudianteScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToRuta: () -> Unit = {}
) {
    val vinoUpt = UPTColors.Vino
    val vinoOscuro = UPTColors.VinoOscuro
    var selectedTab by remember { mutableStateOf("Inicio") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Verificación de permisos para el Estudiante
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
    }

    var showNotifications by remember { mutableStateOf(false) }
    var lastSeenNotificationCount by remember { mutableStateOf(0) }
    val currentNotificationCount = ReporteRepository.reportes.size
    val hasNewNotifications = currentNotificationCount > lastSeenNotificationCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (selectedTab == "Inicio") "Estudiante" else "Horarios de Ruta", 
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
                                Text("Avisos de Ruta", fontWeight = FontWeight.Bold)
                                if (ReporteRepository.reportes.isNotEmpty()) {
                                    IconButton(
                                        onClick = { ReporteRepository.limpiarReportes() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.DeleteSweep, contentDescription = "Limpiar todo", tint = Color.Gray)
                                    }
                                }
                            }
                            HorizontalDivider()
                            if (ReporteRepository.reportes.isEmpty()) {
                                Text("No hay avisos nuevos", modifier = Modifier.padding(16.dp), color = Color.Gray)
                            } else {
                                ReporteRepository.reportes.filter { 
                                    it.estado == "Unidad llena" || it.estado == "Disponible" 
                                }.take(5).forEach { reporte ->
                                    DropdownMenuItem(
                                        text = { 
                                            Column {
                                                Text(reporte.mensaje, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(reporte.tiempo, fontSize = 10.sp, color = Color.Gray)
                                            }
                                        },
                                        onClick = { showNotifications = false },
                                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = vinoUpt) },
                                        trailingIcon = {
                                            IconButton(onClick = { ReporteRepository.eliminarReporte(reporte.id) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                                            }
                                        }
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
                    icon = { Icon(Icons.Default.Schedule, contentDescription = "Horarios") },
                    label = { Text("Horarios") },
                    selected = selectedTab == "Horarios",
                    onClick = { selectedTab = "Horarios" },
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
                    "Horarios" -> HorariosSection(vinoUpt)
                }
            }
        }
    }
}

@Composable
fun InicioSection(vinoUpt: Color, vinoOscuro: Color, hasPermission: Boolean, onNavigateToRuta: () -> Unit, onSendReport: (String) -> Unit) {
    val trackingService = remember { TrackingService() }
    
    // 1. ESTADO PARA TU UBICACIÓN REAL
    var currentUserLat by remember { mutableStateOf<Double?>(null) }
    var currentUserLon by remember { mutableStateOf<Double?>(null) }

    // 2. ACTIVAR RASTREO GPS EN TIEMPO REAL
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

    val unidadesMock = remember {
        mutableStateListOf(
            Triple("UPT-01", 20.0810, -98.3660),
            Triple("UPT-05", 20.0845, -98.3695),
            Triple("UPT-12", 20.0880, -98.3720),
            Triple("UPT-24", 20.0780, -98.3640)
        )
    }

    var unidadMasCercana by remember { mutableStateOf(unidadesMock[0]) }
    var tiempoEstimado by remember { mutableStateOf(5) }

    LaunchedEffect(currentUserLat, currentUserLon) {
        val lat = currentUserLat ?: 20.0820
        val lon = currentUserLon ?: -98.3680
        
        while(true) {
            for (i in unidadesMock.indices) {
                val u = unidadesMock[i]
                unidadesMock[i] = u.copy(
                    second = u.second + (Random.nextDouble() - 0.5) * 0.0005,
                    third = u.third + (Random.nextDouble() - 0.5) * 0.0005
                )
            }
            
            unidadMasCercana = unidadesMock.minBy { (_, bLat, bLon) ->
                abs(bLat - lat) + abs(bLon - lon)
            }
            
            tiempoEstimado = trackingService.calcularTiempoEstimado(
                lat, lon, unidadMasCercana.second, unidadMasCercana.third
            )
            delay(5000)
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
            SectionTitle("Mi ruta")
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = vinoUpt,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                unidadMasCercana.first,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text("Micro detectada cerca de ti", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Estado", color = Color.Gray, fontSize = 12.sp)
                            Text("En movimiento", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                        Column {
                            Text("Llegada estimada", color = Color.Gray, fontSize = 12.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("$tiempoEstimado min", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = vinoUpt, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            SectionTitle("Ubicación en tiempo real")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(vertical = 8.dp)
                    .clickable { onNavigateToRuta() },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (hasPermission && currentUserLat != null) {
                        MapComponent(
                            modifier = Modifier.fillMaxSize(),
                            latitude = currentUserLat!!,
                            longitude = currentUserLon!!,
                            title = "Mi Ubicación Exacta"
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
                                Text("Tu ubicación actual", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    } else if (!hasPermission) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) {
                            Text("Se requiere permiso de ubicación", color = Color.DarkGray)
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = vinoUpt)
                                Spacer(Modifier.height(16.dp))
                                Text("Obteniendo ubicación exacta...", color = Color.Gray)
                            }
                        }
                    }
                }
            }

            SectionTitle("Paradas disponibles")
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val paradas = ParadaRepository.paradas
                    if (paradas.isEmpty()) {
                        Text("No hay paradas registradas", color = Color.Gray, fontSize = 14.sp)
                    } else {
                        paradas.forEachIndexed { index, parada ->
                            StopItem(
                                name = parada,
                                time = "--:--",
                                isFirst = index == 0,
                                isLast = index == paradas.size - 1,
                                color = if (index == 0) Color.Red else if (index == paradas.size - 1) Color.Black else Color.Gray
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
            if (ReporteRepository.reportes.isEmpty()) {
                Text("No hay avisos recientes", modifier = Modifier.padding(8.dp), color = Color.Gray)
            } else {
                ReporteRepository.reportes.forEach { reporte ->
                    RecentReportItem(reporte.mensaje, reporte.tiempo, Icons.Default.Info, if(reporte.tipo == ReporteTipo.ALERTA) Color.Red else vinoUpt)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun HorariosSection(vinoUpt: Color) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Horarios de Unidades", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = vinoUpt)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (ChoferRepository.choferes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("No hay horarios asignados por el administrador", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(ChoferRepository.choferes) { chofer ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(50.dp).background(vinoUpt.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = vinoUpt, modifier = Modifier.size(24.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("Unidad ${chofer.numeroUnidad}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Chofer: ${chofer.nombre}", fontSize = 14.sp, color = Color.Gray)
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = vinoUpt, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(chofer.horario, fontWeight = FontWeight.SemiBold, color = vinoUpt)
                                }
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
fun StopItem(name: String, time: String, isFirst: Boolean = false, isLast: Boolean = false, color: Color = Color.Gray) {
    Row(modifier = Modifier.fillMaxWidth().height(40.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(24.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
            if (!isFirst) Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.5f).align(Alignment.TopCenter).background(Color.LightGray))
            if (!isLast) Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.5f).align(Alignment.BottomCenter).background(Color.LightGray))
            
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color.White).padding(2.dp)) {
                Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(color))
            }
        }
        Text(name, modifier = Modifier.weight(1f).padding(start = 8.dp), fontSize = 14.sp, fontWeight = if(isFirst || isLast) FontWeight.Bold else FontWeight.Normal)
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
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(time, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        }
    }
}
