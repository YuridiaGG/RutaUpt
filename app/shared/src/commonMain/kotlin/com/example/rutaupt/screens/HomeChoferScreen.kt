package com.example.rutaupt.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.jetbrains.compose.resources.painterResource
import com.example.rutaupt.generated.resources.*
import com.example.rutaupt.getPlatform
import com.example.rutaupt.LocationBridge
import com.example.rutaupt.CameraBridge
import com.example.rutaupt.model.ReporteUnidad
import com.example.rutaupt.model.ReporteTipo
import com.example.rutaupt.api.Parada
import com.example.rutaupt.storage.ReporteRepository
import com.example.rutaupt.storage.SessionManager
import com.example.rutaupt.storage.ParadaRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeChoferScreen(
    onLogout: () -> Unit,
    onConfiguracion: () -> Unit,
    onVerReportes: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    val vinoOscuro = UPTColors.VinoOscuro
    val fondoGris = Color(0xFFF8F9FA)
    val scope = rememberCoroutineScope()
    
    var selectedTab by remember { mutableStateOf("Inicio") }
    var showNotifications by remember { mutableStateOf(false) }
    
    // Lógica de notificaciones leídas
    var lastSeenCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        LocationBridge.onRequestPermission?.invoke { _ ->
            CameraBridge.onRequestPermission?.invoke { _ -> }
        }
        ParadaRepository.cargarParadas()
        ReporteRepository.cargarReportes()
    }

    val notificacionesChofer = ReporteRepository.reportes.filter { reporte ->
        reporte.unidad == SessionManager.numeroUnidad && (
            reporte.estado == "HorarioAsignado" || reporte.validacionAdmin != null
        )
    }
    val unreadCount = (notificacionesChofer.size - lastSeenCount).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chofer", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        IconButton(onClick = { 
                            showNotifications = true 
                            lastSeenCount = notificacionesChofer.size // Marcar como leídas
                        }) {
                            BadgedBox(
                                badge = { if (unreadCount > 0) Badge { Text(unreadCount.toString()) } }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                            }
                        }
                        DropdownMenu(
                            expanded = showNotifications,
                            onDismissRequest = { showNotifications = false },
                            modifier = Modifier.width(320.dp).background(Color.White)
                        ) {
                            Text("Avisos Recientes", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                            HorizontalDivider()
                            if (notificacionesChofer.isEmpty()) {
                                Text("No hay avisos nuevos", modifier = Modifier.padding(16.dp), color = Color.Gray)
                            } else {
                                notificacionesChofer.take(10).forEach { reporte ->
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
            ChoferBottomNavigation(vinoUpt, selectedTab, { selectedTab = it }, onVerReportes, onConfiguracion) 
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(fondoGris)) {
            AnimatedContent(targetState = selectedTab) { tab ->
                when (tab) {
                    "Inicio" -> ChoferInicioSection(vinoUpt, vinoOscuro)
                    "Mapa" -> ChoferMapaSection(vinoUpt)
                }
            }
        }
    }
}

@Composable
fun ChoferInicioSection(vinoUpt: Color, vinoOscuro: Color) {
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Brush.verticalGradient(listOf(vinoUpt, vinoOscuro)), RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1.2f)) {
                    Text("¡Buen día,", color = Color.White.copy(alpha = 0.85f), fontSize = 20.sp)
                    Text("${SessionManager.nombreUsuario}!", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                Image(painter = painterResource(Res.drawable.chofer), contentDescription = null, modifier = Modifier.size(130.dp).offset(y = 35.dp), contentScale = ContentScale.Fit)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text("Ruta asignada", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(color = Color(0xFFD32F2F), shape = RoundedCornerShape(8.dp)) {
                        Text("Unidad ${SessionManager.numeroUnidad}", color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) { append("Centro – UPT") }
                        append("   ")
                        withStyle(SpanStyle(color = vinoUpt, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)) { append("🕒 ${SessionManager.horarioUsuario}") }
                    }, color = Color.Black)
                }
            }

            Text("Marcar paso por parada", fontWeight = FontWeight.Bold, fontSize = 19.sp, modifier = Modifier.padding(bottom = 12.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val paradas = ParadaRepository.paradas
                    if (paradas.isEmpty()) {
                        Text("No hay paradas asignadas", color = Color.Gray)
                    } else {
                        paradas.forEachIndexed { index, parada ->
                            var isPassed by remember { mutableStateOf(false) }
                            ChoferStopItem(
                                name = parada.nombre, 
                                time = if (isPassed) "Completada" else "--:--", 
                                active = isPassed, 
                                isFirst = index == 0,
                                isLast = index == paradas.size - 1,
                                onPassed = {
                                    isPassed = true
                                    scope.launch {
                                        ReporteRepository.agregarReporte(
                                            ReporteUnidad(
                                                unidad = SessionManager.numeroUnidad,
                                                mensaje = "Unidad ${SessionManager.numeroUnidad} ya pasó por: ${parada.nombre}",
                                                tiempo = "Justo ahora",
                                                tipo = ReporteTipo.INFORMACION,
                                                estado = "ParadaPasada_${parada.nombre}"
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun ChoferMapaSection(vinoUpt: Color) {
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    LaunchedEffect(Unit) {
        LocationBridge.getCurrentLocation?.invoke { lat, lon -> userLocation = Pair(lat, lon) }
        LocationBridge.onLocationUpdate = { lat, lon -> userLocation = Pair(lat, lon) }
        LocationBridge.startLocationUpdates?.invoke()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        MapComponent(
            modifier = Modifier.fillMaxSize(),
            latitude = userLocation?.first ?: com.example.rutaupt.LocationUtils.DEFAULT_LAT,
            longitude = userLocation?.second ?: com.example.rutaupt.LocationUtils.DEFAULT_LON,
            title = "Mi Ubicación",
            paradas = ParadaRepository.paradas
        )
    }
}

@Composable
fun ChoferStopItem(name: String, time: String, active: Boolean, isFirst: Boolean, isLast: Boolean, onPassed: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(30.dp).heightIn(min = 55.dp), contentAlignment = Alignment.Center) {
            if (!isFirst) Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.5f).align(Alignment.TopCenter).background(Color.LightGray))
            if (!isLast) Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.5f).align(Alignment.BottomCenter).background(Color.LightGray))
            Box(modifier = Modifier.size(12.dp).background(if(active) Color(0xFF2E7D32) else Color.Gray, CircleShape))
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontSize = 16.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium)
            Text(time, color = Color.Gray, fontSize = 13.sp)
        }
        IconButton(onClick = onPassed, enabled = !active) {
            Icon(if (active) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, null, tint = if (active) Color(0xFF2E7D32) else Color.LightGray)
        }
    }
}

@Composable
fun ChoferBottomNavigation(vinoUpt: Color, selectedTab: String, onTabSelected: (String) -> Unit, onVerReportes: () -> Unit, onPerfil: () -> Unit) {
    NavigationBar(containerColor = Color.White, modifier = Modifier.shadow(16.dp).height(80.dp)) {
        NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") }, selected = selectedTab == "Inicio", onClick = { onTabSelected("Inicio") })
        NavigationBarItem(icon = { Icon(Icons.Default.Map, null) }, label = { Text("Mapa") }, selected = selectedTab == "Mapa", onClick = { onTabSelected("Mapa") })
        NavigationBarItem(icon = { Icon(Icons.Default.Notifications, null) }, label = { Text("Avisos") }, selected = false, onClick = onVerReportes)
        NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Perfil") }, selected = false, onClick = onPerfil)
    }
}
