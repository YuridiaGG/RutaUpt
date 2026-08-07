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
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeChoferScreen(
    onLogout: () -> Unit,
    onConfiguracion: () -> Unit,
    onVerReportes: () -> Unit,
    onNavigateToRuta: (Parada) -> Unit 
) {
    val vinoUpt = UPTColors.Vino
    val vinoOscuro = UPTColors.VinoOscuro
    val fondoGris = Color(0xFFF8F9FA)
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf("Inicio") }
    var showNotifications by remember { mutableStateOf(false) }
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
                            lastSeenCount = notificacionesChofer.size 
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
            ChoferBottomNavigation(vinoUpt, selectedTab, { selectedTab = it }, onVerReportes, onConfiguracion)
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(fondoGris)) {
            AnimatedContent(targetState = selectedTab) { tab ->
                when (tab) {
                    "Inicio" -> ChoferInicioSection(vinoUpt, vinoOscuro, onNavigateToRuta)
                    "Mapa" -> ChoferMapaSection(vinoUpt)
                }
            }
        }
    }
}

@Composable
fun ChoferInicioSection(vinoUpt: Color, vinoOscuro: Color, onNavigateToRuta: (Parada) -> Unit) {
    val scope = rememberCoroutineScope()
    val paradas = ParadaRepository.paradas
    // Estado de paradas completadas (se resetea al finalizar ruta)
    val paradasPasadas = remember { mutableStateMapOf<Long, Boolean>() }
    val todasCompletadas = paradas.isNotEmpty() && paradasPasadas.size >= paradas.size

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

            // --- SECCIÓN: ESTADO ACTUAL (Con lógica de reset) ---
            ChoferStatusSection(
                vinoUpt = vinoUpt, 
                scope = scope, 
                canReset = todasCompletadas,
                onResetParadas = { paradasPasadas.clear() }
            )

            Text("Marcar paso por parada (${paradasPasadas.size}/${paradas.size})", fontWeight = FontWeight.Bold, fontSize = 19.sp, modifier = Modifier.padding(bottom = 12.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (paradas.isEmpty()) {
                        Text("No hay paradas asignadas", color = Color.Gray)
                    } else {
                        paradas.forEachIndexed { index, parada ->
                            val isPassed = paradasPasadas[parada.id ?: -1L] ?: false
                            ChoferStopItem(
                                name = parada.nombre,
                                time = if (isPassed) "Completada" else "--:--",
                                active = isPassed,
                                isFirst = index == 0,
                                isLast = index == paradas.size - 1,
                                onPassed = {
                                    parada.id?.let { id -> 
                                        paradasPasadas[id] = true 
                                    }
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
                                },
                                onClick = { onNavigateToRuta(parada) }
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
fun ChoferStatusSection(
    vinoUpt: Color, 
    scope: CoroutineScope, 
    canReset: Boolean,
    onResetParadas: () -> Unit
) {
    var currentStatus by remember { mutableStateOf("En recorrido") }

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text("Estado actual", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(70.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (currentStatus == "En recorrido") Color(0xFFD32F2F) else Color(0xFFF57C00))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DirectionsBus, null, tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Text(currentStatus, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusGridButton("Retrasada", Icons.Default.PhotoCamera, Color(0xFFF57C00), Modifier.weight(1f)) {
                CameraBridge.onLaunchCamera?.invoke { base64 ->
                    scope.launch {
                        ReporteRepository.agregarReporte(
                            ReporteUnidad(
                                unidad = SessionManager.numeroUnidad,
                                mensaje = "Unidad con retraso reportado por chofer",
                                tiempo = "Justo ahora",
                                tipo = ReporteTipo.ALERTA,
                                imagen = base64,
                                estado = "Retrasada"
                            )
                        )
                        currentStatus = "Retrasada"
                    }
                }
            }
            StatusGridButton("Unidad llena", Icons.Default.Groups, Color(0xFFF57C00), Modifier.weight(1f)) {
                scope.launch {
                    ReporteRepository.agregarReporte(
                        ReporteUnidad(
                            unidad = SessionManager.numeroUnidad,
                            mensaje = "Unidad llena reportada por chofer",
                            tiempo = "Justo ahora",
                            tipo = ReporteTipo.INFORMACION,
                            estado = "Unidad llena"
                        )
                    )
                    currentStatus = "Unidad llena"
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusGridButton("Disponible", Icons.Default.CheckCircle, Color(0xFF2E7D32), Modifier.weight(1f)) {
                scope.launch {
                    ReporteRepository.agregarReporte(
                        ReporteUnidad(
                            unidad = SessionManager.numeroUnidad,
                            mensaje = "Unidad disponible reportada por chofer",
                            tiempo = "Justo ahora",
                            tipo = ReporteTipo.INFORMACION,
                            estado = "Disponible"
                        )
                    )
                    currentStatus = "En recorrido"
                }
            }
            StatusGridButton("Fin de ruta", Icons.Default.Flag, Color(0xFF757575), Modifier.weight(1f)) {
                scope.launch {
                    ReporteRepository.agregarReporte(
                        ReporteUnidad(
                            unidad = SessionManager.numeroUnidad,
                            mensaje = "Ruta finalizada por chofer",
                            tiempo = "Justo ahora",
                            tipo = ReporteTipo.INFORMACION,
                            estado = "Finalizado"
                        )
                    )
                    currentStatus = "Finalizado"
                    // REINICIO DE PARADAS: Solo si ya pasó por todas
                    if (canReset) {
                        onResetParadas()
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        StatusGridButton("En recorrido", Icons.Default.DirectionsBus, Color(0xFFD32F2F), Modifier.fillMaxWidth()) {
            scope.launch {
                ReporteRepository.agregarReporte(
                    ReporteUnidad(
                        unidad = SessionManager.numeroUnidad,
                        mensaje = "Unidad en recorrido reportada por chofer",
                        tiempo = "Justo ahora",
                        tipo = ReporteTipo.INFORMACION,
                        estado = "En recorrido"
                    )
                )
                currentStatus = "En recorrido"
            }
        }
    }
}

@Composable
fun StatusGridButton(text: String, icon: ImageVector, iconColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(90.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(30.dp))
            Spacer(Modifier.height(8.dp))
            Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
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
fun ChoferStopItem(name: String, time: String, active: Boolean, isFirst: Boolean, isLast: Boolean, onPassed: () -> Unit, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
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
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.shadow(16.dp),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Inicio") },
            selected = selectedTab == "Inicio",
            onClick = { onTabSelected("Inicio") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = vinoUpt, indicatorColor = vinoUpt.copy(alpha = 0.1f))
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Map, null) },
            label = { Text("Mapa") },
            selected = selectedTab == "Mapa",
            onClick = { onTabSelected("Mapa") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = vinoUpt, indicatorColor = vinoUpt.copy(alpha = 0.1f))
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, null) },
            label = { Text("Avisos") },
            selected = false,
            onClick = onVerReportes,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = vinoUpt, indicatorColor = vinoUpt.copy(alpha = 0.1f))
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Perfil") },
            selected = false,
            onClick = onPerfil,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = vinoUpt, indicatorColor = vinoUpt.copy(alpha = 0.1f))
        )
    }
}
