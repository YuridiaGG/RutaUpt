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

data class ChoferHistorialAccion(
    val ruta: String,
    val estado: String,
    val fecha: String,
    val icono: ImageVector,
    val color: Color
)

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
    val historial = remember { mutableStateListOf<ChoferHistorialAccion>() }
    var showNotifications by remember { mutableStateOf(false) }
    var notificationsRead by remember { mutableStateOf(false) }

    // Solicitar permisos de Ubicación y Cámara EN CADENA al iniciar (específico para Chofer)
    LaunchedEffect(Unit) {
        LocationBridge.onRequestPermission?.invoke { _ ->
            // Una vez que responde a ubicacion, pedimos camara inmediatamente
            CameraBridge.onRequestPermission?.invoke { _ -> }
        }
        ParadaRepository.cargarParadas()
        ReporteRepository.cargarReportes() // Cargamos reportes reales de la BD
    }

    fun registrarEstado(estado: String, icono: ImageVector, color: Color, imagen: String? = null) {
        val now = kotlinx.datetime.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val horaStr = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
        val fechaStr = "${now.dayOfMonth}/${now.monthNumber} $horaStr"
        
        val unidad = SessionManager.numeroUnidad.ifBlank { "Sin asignar" }
        val mensaje = if (imagen != null) "Unidad $unidad reporta retraso con evidencia" else "Unidad $unidad: $estado"
        
        scope.launch {
            ReporteRepository.agregarReporte(
                ReporteUnidad(
                    unidad = unidad, 
                    mensaje = mensaje, 
                    tiempo = fechaStr, 
                    tipo = if (imagen != null) ReporteTipo.ALERTA else ReporteTipo.INFORMACION,
                    imagen = imagen,
                    estado = if (imagen != null) "Retrasada (Validando)" else "EstadoChofer_$estado" // Marcador
                )
            )
        }
        
        historial.add(0, ChoferHistorialAccion("Ruta 05", estado, fechaStr, icono, color))
    }

    // Filtrado de notificaciones para el chofer: incluye avisos de horario y respuestas del admin
    val notificacionesChofer = ReporteRepository.reportes.filter { reporte ->
        reporte.unidad == SessionManager.numeroUnidad && (
            reporte.estado == "HorarioAsignado" || reporte.validacionAdmin != null
        )
    }

    // Sincronizar el horario del SessionManager si llega una notificación de horario
    LaunchedEffect(ReporteRepository.reportes.size) {
        val ultimoHorario = ReporteRepository.reportes.find { 
            it.unidad == SessionManager.numeroUnidad && it.estado == "HorarioAsignado" 
        }
        ultimoHorario?.let {
            val nuevoHorario = it.mensaje.substringAfter(": ").trim()
            SessionManager.horarioUsuario = nuevoHorario
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chofer", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        IconButton(onClick = { 
                            showNotifications = true 
                            notificationsRead = true
                        }) {
                            BadgedBox(
                                badge = { 
                                    if (notificacionesChofer.isNotEmpty() && !notificationsRead) {
                                        Badge { Text(notificacionesChofer.size.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                            }
                        }
                        DropdownMenu(
                            expanded = showNotifications,
                            onDismissRequest = { showNotifications = false },
                            modifier = Modifier.width(320.dp).background(Color.White)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Avisos Recientes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            }
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                            if (notificacionesChofer.isEmpty()) {
                                Text("No hay avisos nuevos", modifier = Modifier.padding(16.dp), color = Color.Gray)
                            } else {
                                notificacionesChofer.take(10).forEach { reporte ->
                                    val esRespuestaAdmin = reporte.validacionAdmin != null
                                    
                                    DropdownMenuItem(
                                        text = { 
                                            Column {
                                                if (esRespuestaAdmin) {
                                                    Text(
                                                        text = "Respuesta a retraso: ${reporte.validacionAdmin}",
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = if (reporte.validacionAdmin == "Aceptado") Color(0xFF27AE60) else Color.Red,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(reporte.mensaje, fontSize = 11.sp, maxLines = 1)
                                                } else {
                                                    Text(reporte.mensaje, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                                Text(reporte.tiempo, fontSize = 10.sp, color = Color.Gray)
                                            }
                                        },
                                        onClick = { showNotifications = false },
                                        leadingIcon = { 
                                            Icon(
                                                when {
                                                    reporte.validacionAdmin == "Aceptado" -> Icons.Default.CheckCircle
                                                    reporte.validacionAdmin == "Denegado" -> Icons.Default.Cancel
                                                    reporte.estado == "HorarioAsignado" -> Icons.Default.Schedule
                                                    else -> Icons.Default.School
                                                },
                                                contentDescription = null, 
                                                tint = when {
                                                    reporte.validacionAdmin == "Aceptado" -> Color(0xFF27AE60)
                                                    reporte.validacionAdmin == "Denegado" -> Color.Red
                                                    else -> vinoUpt
                                                }
                                            ) 
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
            ChoferBottomNavigation(
                vinoUpt = vinoUpt,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onVerReportes = onVerReportes,
                onPerfil = onConfiguracion
            ) 
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(fondoGris)
        ) {
            AnimatedContent(targetState = selectedTab) { tab ->
                when (tab) {
                    "Inicio" -> ChoferInicioSection(
                        vinoUpt, 
                        vinoOscuro, 
                        historial, 
                        onEstadoSelected = { e, i, c, img -> registrarEstado(e, i, c, img) }
                    )
                    "Mapa" -> ChoferMapaSection(vinoUpt)
                }
            }
        }
    }
}

@Composable
fun ChoferInicioSection(
    vinoUpt: Color,
    vinoOscuro: Color,
    historial: List<ChoferHistorialAccion>,
    onEstadoSelected: (String, ImageVector, Color, String?) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp) 
                .background(
                    brush = Brush.verticalGradient(colors = listOf(vinoUpt, vinoOscuro)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.2f)) {
                    Text("¡Buen día,", color = Color.White.copy(alpha = 0.85f), fontSize = 20.sp)
                    Text(
                        "${SessionManager.nombreUsuario}!", 
                        color = Color.White, 
                        fontSize = 32.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(contentAlignment = Alignment.TopEnd) {
                    Image(
                        painter = painterResource(Res.drawable.chofer),
                        contentDescription = "Chofer",
                        modifier = Modifier.size(130.dp).offset(y = 35.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Ruta asignada",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        color = Color(0xFFD32F2F), 
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Unidad X (${SessionManager.numeroUnidad.ifBlank { "S/N" }})", 
                            color = Color.White, 
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    
                    // Horario colocado en el espacio en blanco al lado de Centro - UPT
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                                    append("Centro – UPT")
                                }
                                append("   ") // Espacio solicitado
                                withStyle(style = SpanStyle(color = vinoUpt, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)) {
                                    append("🕒 ${SessionManager.horarioUsuario}")
                                }
                            },
                            color = Color.Black
                        )
                    }
                    
                    Spacer(Modifier.height(4.dp))
                    Text("Unidad: ${SessionManager.numeroUnidad.ifBlank { "N/A" }}", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Text(
                text = "Estado actual",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Button(
                onClick = { onEstadoSelected("En recorrido", Icons.Default.DirectionsBus, Color(0xFFC62828), null) },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(Icons.Default.DirectionsBus, null, modifier = Modifier.size(32.dp)) 
                Spacer(Modifier.width(12.dp))
                Text("En recorrido", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusGridButton("Retrasada", Icons.Default.CameraAlt, Color(0xFFE67E22), Modifier.weight(1f)) {
                    getPlatform().openCamera { img ->
                        onEstadoSelected("Retrasada", Icons.Default.AccessTime, Color(0xFFE67E22), img)
                    }
                }
                StatusGridButton("Unidad llena", Icons.Default.Groups, Color(0xFFE67E22), Modifier.weight(1f)) {
                    onEstadoSelected("Unidad llena", Icons.Default.Groups, Color(0xFFE67E22), null)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusGridButton("Disponible", Icons.Default.CheckCircle, Color(0xFF27AE60), Modifier.weight(1f)) {
                    onEstadoSelected("Disponible", Icons.Default.CheckCircle, Color(0xFF27AE60), null)
                }
                StatusGridButton("Fin de ruta", Icons.Default.Flag, Color.Gray, Modifier.weight(1f)) {
                    onEstadoSelected("Fin de ruta", Icons.Default.Flag, Color.Gray, null)
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Paradas de la ruta",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
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
                                    getPlatform().showNotification("Ruta UPT", "Has notificado el paso por: ${parada.nombre}")
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
    val hasLocationPermission = LocationBridge.hasPermission?.invoke() ?: false
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            LocationBridge.getCurrentLocation?.invoke { lat, lon ->
                if (userLocation == null) userLocation = Pair(lat, lon)
            }
            LocationBridge.onLocationUpdate = { lat, lon ->
                userLocation = Pair(lat, lon)
            }
            LocationBridge.startLocationUpdates?.invoke()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            LocationBridge.stopLocationUpdates?.invoke()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasLocationPermission) {
            val location = userLocation
            if (location != null) {
                MapComponent(
                    modifier = Modifier.fillMaxSize(),
                    latitude = location.first,
                    longitude = location.second,
                    title = "Mi Ubicación"
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = vinoUpt)
                        Spacer(Modifier.height(16.dp))
                        Text("Obteniendo tu ubicación...", color = Color.Gray)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Permiso de ubicación requerido para ver el mapa", color = Color.Gray)
                    Button(onClick = { LocationBridge.onRequestPermission?.invoke {} }) {
                        Text("Solicitar Permiso")
                    }
                }
            }
        }
        
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.fillMaxWidth().height(60.dp).shadow(8.dp, RoundedCornerShape(30.dp))
            ) {
                Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MyLocation, null, tint = Color(0xFF2196F3))
                    Spacer(Modifier.width(12.dp))
                    Text(if (userLocation != null) "GPS en tiempo real activo" else "Buscando señal GPS...", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun StatusGridButton(label: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(110.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }
    }
}

@Composable
fun ChoferHistorialItem(accion: ChoferHistorialAccion) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(45.dp).background(accion.color.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(accion.icono, null, tint = accion.color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(accion.estado, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.Black)
                Text(accion.ruta, fontSize = 13.sp, color = Color.Gray)
            }
            Text(accion.fecha, color = Color.DarkGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ChoferStopItem(
    name: String, 
    time: String, 
    active: Boolean, 
    isFirst: Boolean = false, 
    isLast: Boolean = false, 
    color: Color = Color.Black,
    onPassed: () -> Unit = {}
) {
    Row(modifier = Modifier.height(55.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(30.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
            if (!isFirst) Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.5f).align(Alignment.TopCenter).background(Color.LightGray))
            if (!isLast) Box(modifier = Modifier.width(2.dp).fillMaxHeight(0.5f).align(Alignment.BottomCenter).background(Color.LightGray))
            Box(modifier = Modifier.size(12.dp).background(if(active) Color(0xFF2E7D32) else color, CircleShape))
        }
        Spacer(Modifier.width(16.dp))
        Text(name, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium)
        
        IconButton(onClick = onPassed) {
            Icon(
                if (active) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, 
                contentDescription = "Marcar como pasada", 
                tint = if (active) Color(0xFF2E7D32) else Color.LightGray
            )
        }

        Text(time, color = Color.Gray, fontSize = 13.sp)
    }
}

@Composable
fun ChoferBottomNavigation(
    vinoUpt: Color,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onVerReportes: () -> Unit,
    onPerfil: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.shadow(32.dp).height(100.dp),
        tonalElevation = 15.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio", modifier = Modifier.size(28.dp)) },
            label = { Text("Inicio") },
            selected = selectedTab == "Inicio",
            onClick = { onTabSelected("Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = vinoUpt,
                unselectedIconColor = Color.Gray,
                selectedTextColor = vinoUpt,
                indicatorColor = vinoUpt.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Map, contentDescription = "Mapa", modifier = Modifier.size(28.dp)) },
            label = { Text("Mapa") },
            selected = selectedTab == "Mapa",
            onClick = { onTabSelected("Mapa") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = vinoUpt,
                unselectedIconColor = Color.Gray,
                selectedTextColor = vinoUpt,
                indicatorColor = vinoUpt.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Reportes", modifier = Modifier.size(28.dp)) },
            label = { Text("Avisos") },
            selected = false,
            onClick = onVerReportes,
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil", modifier = Modifier.size(28.dp)) },
            label = { Text("Perfil") },
            selected = false,
            onClick = onPerfil,
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
    }
}
