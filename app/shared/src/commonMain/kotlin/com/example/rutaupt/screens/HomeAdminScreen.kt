package com.example.rutaupt.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import com.example.rutaupt.generated.resources.*
import com.example.rutaupt.rememberBitmapFromBase64
import com.example.rutaupt.LocationBridge
import com.example.rutaupt.model.ReporteTipo
import com.example.rutaupt.model.ReporteUnidad
import com.example.rutaupt.storage.ReporteRepository
import com.example.rutaupt.storage.SessionManager
import com.example.rutaupt.storage.ParadaRepository
import com.example.rutaupt.storage.ChoferRepository
import com.example.rutaupt.storage.EstudianteRepository
import com.example.rutaupt.api.RutaApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdminScreen(
    onGestionarChoferes: () -> Unit,
    onGestionarHorarios: () -> Unit,
    onGestionarEstudiantes: () -> Unit,
    onUbicacionMicros: () -> Unit,
    onGestionarParadas: () -> Unit,
    onConfiguracion: () -> Unit,
    onLogout: () -> Unit,
    onVerReportes: () -> Unit,
    mensajeConfirmacion: String? = null,
    onMensajeMostrado: () -> Unit = {}
) {
    val vinoUpt = UPTColors.Vino
    val vinoOscuro = UPTColors.VinoOscuro
    val apiService = remember { RutaApiService() }
    val scope = rememberCoroutineScope()
    
    var stats by remember { mutableStateOf(mapOf("estudiantes" to 0, "choferes" to 0, "rutas" to 0)) }
    var showMenu by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var notificationsRead by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Verificación de permisos para el Admin (Geolocation)
    var hasLocationPermission by remember { 
        mutableStateOf(LocationBridge.hasPermission?.invoke() ?: false) 
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            LocationBridge.onRequestPermission?.invoke { granted ->
                hasLocationPermission = granted
            }
        }
        
        // Cargar datos reales de los repositorios
        scope.launch { ChoferRepository.cargarDesdeServidor() }
        scope.launch { EstudianteRepository.cargarDesdeServidor() }
        scope.launch { ParadaRepository.cargarParadas() }
        
        // Polling para estadísticas y reportes
        while(true) {
            stats = apiService.obtenerEstadisticasAdmin()
            ReporteRepository.cargarReportes()
            delay(10000)
        }
    }

    LaunchedEffect(mensajeConfirmacion) {
        mensajeConfirmacion?.let {
            snackbarHostState.showSnackbar(it)
            onMensajeMostrado()
        }
    }

    // Filtro de Notificaciones para Admin: Estados operativos de los choferes
    val notificacionesAdmin = ReporteRepository.reportes.filter { 
        it.estado != null && it.estado!!.startsWith("EstadoChofer_") 
    }

    // Procesar datos para la gráfica de barras
    val chartData by remember {
        derivedStateOf {
            val counts = IntArray(7) { 0 }
            ReporteRepository.reportes.forEach { reporte ->
                val esRetraso = reporte.estado?.contains("Retrasada", ignoreCase = true) == true || 
                                reporte.mensaje.contains("retraso", ignoreCase = true)
                
                if (esRetraso) {
                    try {
                        val reportDate = kotlinx.datetime.Instant.fromEpochMilliseconds(reporte.id)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                        val dayIndex = reportDate.dayOfWeek.ordinal
                        if (dayIndex in 0..6) {
                            counts[dayIndex]++
                        }
                    } catch (e: Exception) {}
                }
            }
            
            val maxCount = counts.maxOrNull()?.takeIf { it > 0 } ?: 1
            counts.map { it.toFloat() / maxCount }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Administrador", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    Box {
                        IconButton(onClick = { 
                            showNotifications = true 
                            notificationsRead = true
                        }) {
                            BadgedBox(
                                badge = { 
                                    if (notificacionesAdmin.isNotEmpty() && !notificationsRead) {
                                        Badge { Text(notificacionesAdmin.size.toString()) }
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
                                Text("Estados de Unidades", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            }
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                            if (notificacionesAdmin.isEmpty()) {
                                Text("No hay reportes de choferes", modifier = Modifier.padding(16.dp), color = Color.Gray)
                            } else {
                                notificacionesAdmin.take(15).forEach { reporte ->
                                    RecentNotificationItem(
                                        reporte = reporte,
                                        onClick = { showNotifications = false; onVerReportes() },
                                        onDelete = { 
                                            scope.launch {
                                                ReporteRepository.eliminarReporte(reporte.id)
                                            }
                                        },
                                        onValidar = { nuevoEstado ->
                                            scope.launch {
                                                val success = ReporteRepository.actualizarValidacion(reporte.id, nuevoEstado)
                                                if (success) {
                                                    snackbarHostState.showSnackbar("Reporte $nuevoEstado correctamente")
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            TextButton(onClick = { showNotifications = false; onVerReportes() }, modifier = Modifier.fillMaxWidth()) {
                                Text("Ver historial completo", color = vinoUpt)
                            }
                        }
                    }
                    
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mi Perfil", fontWeight = FontWeight.Medium) },
                                onClick = { showMenu = false; onConfiguracion() },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = vinoUpt) }
                            )
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión", color = Color.Red, fontWeight = FontWeight.Medium) },
                                onClick = { showMenu = false; SessionManager.cerrarSesion(); onLogout() },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red) }
                            )
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
                    label = { Text("Inicio", fontSize = 10.sp) },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Map, contentDescription = "Ubicación") },
                    label = { Text("Ubicación", fontSize = 10.sp) },
                    selected = false,
                    onClick = onUbicacionMicros
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DirectionsBus, contentDescription = "Reportes") },
                    label = { Text("Reportes", fontSize = 10.sp) },
                    selected = false,
                    onClick = onVerReportes
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AddLocationAlt, contentDescription = "Paradas") },
                    label = { Text("Paradas", fontSize = 10.sp) },
                    selected = false,
                    onClick = onGestionarParadas
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)).verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        brush = Brush.verticalGradient(colors = listOf(vinoUpt, vinoOscuro)),
                        shape = RoundedCornerShape(bottomStart = 45.dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text("¡Bienvenido,", color = Color.White.copy(alpha = 0.85f), fontSize = 16.sp)
                        Text("${SessionManager.nombreUsuario}!", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Image(
                        painter = painterResource(Res.drawable.admin),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp).offset(y = 20.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text("Resumen General", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Color(0xFF333333))
                Spacer(Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    val numEstudiantes = EstudianteRepository.estudiantes.size.takeIf { it > 0 } ?: stats["estudiantes"] ?: 0
                    StatCard("$numEstudiantes", "Estudiantes registrados", Icons.Default.Group, Modifier.weight(1f))
                    
                    val numChoferes = ChoferRepository.choferes.size.takeIf { it > 0 } ?: stats["choferes"] ?: 0
                    StatCard("$numChoferes", "Choferes registrados", Icons.Default.Badge, Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatCard("${stats["rutas"]}", "Rutas activas", Icons.Default.DirectionsBus, Modifier.weight(1f))
                    StatCard("${notificacionesAdmin.size}", "Reportes de choferes", Icons.Default.Warning, Modifier.weight(1f), Color(0xFFE74C3C))
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("Accesos rápidos", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Color(0xFF333333))
                Spacer(Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    QuickActionCard("Gestionar Horarios", Icons.Default.Schedule, Modifier.weight(1f), onClick = onGestionarHorarios)
                    QuickActionCard("Gestionar Choferes", Icons.Default.Person, Modifier.weight(1f), onClick = onGestionarChoferes)
                }
                Spacer(Modifier.height(14.dp))
                QuickActionCard("Gestionar Estudiantes", Icons.Default.School, Modifier.fillMaxWidth(), onClick = onGestionarEstudiantes)

                Spacer(modifier = Modifier.height(32.dp))
                Text("Actividad semanal", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Color(0xFF333333))
                Text("¿Cómo va mi ruta? (Basado en reportes reales)", fontSize = 14.sp, color = Color.Gray)
                Spacer(Modifier.height(16.dp))
                ActivityChart(chartData)
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, icon: ImageVector, modifier: Modifier, iconColor: Color = UPTColors.Vino) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            Text(label, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
        }
    }
}

@Composable
fun QuickActionCard(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(115.dp).clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = UPTColors.Vino, modifier = Modifier.size(38.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF444444), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ActivityChart(chartData: List<Float>) {
    val vinoUpt = UPTColors.Vino
    val days = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

    Card(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Canvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val width = size.width
                val height = size.height
                val barWidth = width / (chartData.size * 1.5f)
                val spacing = (width - (barWidth * chartData.size)) / (chartData.size + 1)
                
                chartData.forEachIndexed { index, value ->
                    val x = spacing + index * (barWidth + spacing)
                    val barHeight = value * height
                    
                    drawRect(
                        color = if (value > 0.6f) Color.Red else vinoUpt,
                        topLeft = Offset(x, height - barHeight),
                        size = Size(barWidth, barHeight)
                    )
                }
                
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, height),
                    end = Offset(width, height),
                    strokeWidth = 2.dp.toPx()
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                days.forEach { Text(it, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun RecentNotificationItem(
    reporte: ReporteUnidad,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onValidar: (String) -> Unit
) {
    val iconBg = if(reporte.tipo == ReporteTipo.ALERTA) Color.Red else Color(0xFFF39C12)
    val icon = if(reporte.tipo == ReporteTipo.ALERTA) Icons.Default.Warning else Icons.Default.BusAlert
    val bitmap = rememberBitmapFromBase64(reporte.imagen)
    val esRetraso = reporte.estado?.contains("Retrasada", ignoreCase = true) == true || 
                    reporte.mensaje.contains("retraso", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Column(Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(42.dp).background(iconBg.copy(alpha = 0.12f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconBg, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(reporte.mensaje, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black, maxLines = 2)
                    Text(reporte.tiempo, fontSize = 10.sp, color = Color.Gray)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                }
            }
            
            if (bitmap != null) {
                Spacer(Modifier.height(8.dp))
                Image(
                    bitmap = bitmap,
                    contentDescription = "Imagen del reporte",
                    modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            if (esRetraso && reporte.validacionAdmin == null) {
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = { onValidar("Denegado") },
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Denegar", color = Color.Red, fontSize = 12.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onValidar("Aceptado") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Aceptar", color = Color.White, fontSize = 12.sp)
                    }
                }
            } else if (reporte.validacionAdmin != null) {
                Spacer(Modifier.height(6.dp))
                Surface(
                    color = (if (reporte.validacionAdmin == "Aceptado") Color(0xFF27AE60) else Color.Red).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Admin: ${reporte.validacionAdmin}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (reporte.validacionAdmin == "Aceptado") Color(0xFF27AE60) else Color.Red,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
