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
import androidx.compose.ui.graphics.vector.ImageVector
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
    val snackbarHostState = remember { SnackbarHostState() }

    // Lógica para marcar notificaciones como leídas
    var lastSeenCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        scope.launch { ChoferRepository.cargarDesdeServidor() }
        scope.launch { EstudianteRepository.cargarDesdeServidor() }
        scope.launch { ParadaRepository.cargarParadas() }

        while(true) {
            stats = apiService.obtenerEstadisticasAdmin()
            ReporteRepository.cargarReportes()
            delay(10000)
        }
    }

    val notificacionesAdmin = ReporteRepository.reportes.filter {
        it.estado != null && it.estado!!.startsWith("EstadoChofer_")
    }
    val unreadCount = (notificacionesAdmin.size - lastSeenCount).coerceAtLeast(0)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Administrador", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        IconButton(onClick = {
                            showNotifications = true
                            lastSeenCount = notificacionesAdmin.size // Marcar como leídas
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
                            Text("Estados de Unidades", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                            HorizontalDivider()
                            if (notificacionesAdmin.isEmpty()) {
                                Text("Sin reportes operativos", modifier = Modifier.padding(16.dp), color = Color.Gray)
                            } else {
                                notificacionesAdmin.take(15).forEach { reporte ->
                                    RecentNotificationItem(
                                        reporte = reporte,
                                        onClick = { showNotifications = false; onVerReportes() },
                                        onDelete = { scope.launch { ReporteRepository.eliminarReporte(reporte.id) } },
                                        onValidar = { nuevo ->
                                            scope.launch {
                                                if (ReporteRepository.actualizarValidacion(reporte.id, nuevo)) {
                                                    snackbarHostState.showSnackbar("Reporte $nuevo")
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Settings, null, tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Perfil") },
                                leadingIcon = { Icon(Icons.Default.Person, null, tint = vinoUpt) },
                                onClick = {
                                    showMenu = false
                                    onConfiguracion()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    onLogout()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = vinoUpt)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.Map, null) }, label = { Text("Ubicación") }, selected = false, onClick = onUbicacionMicros)
                NavigationBarItem(icon = { Icon(Icons.Default.DirectionsBus, null) }, label = { Text("Reportes") }, selected = false, onClick = onVerReportes)
                NavigationBarItem(icon = { Icon(Icons.Default.AddLocationAlt, null) }, label = { Text("Paradas") }, selected = false, onClick = onGestionarParadas)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Brush.verticalGradient(listOf(vinoUpt, vinoOscuro)), RoundedCornerShape(bottomStart = 45.dp))) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text("¡Bienvenido,", color = Color.White.copy(alpha = 0.85f), fontSize = 16.sp)
                        Text("${SessionManager.nombreUsuario}!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Image(painter = painterResource(Res.drawable.admin), contentDescription = null, modifier = Modifier.size(150.dp).offset(y = 20.dp), contentScale = ContentScale.Fit)
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text("Resumen General", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatCard("${maxOf(EstudianteRepository.estudiantes.size, stats["estudiantes"] ?: 0)}", "Estudiantes", Icons.Default.Group, Modifier.weight(1f))
                    StatCard("${maxOf(ChoferRepository.choferes.size, stats["choferes"] ?: 0)}", "Choferes", Icons.Default.Badge, Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatCard("${stats["rutas"]}", "Rutas activas", Icons.Default.DirectionsBus, Modifier.weight(1f))
                    StatCard("${notificacionesAdmin.size}", "Reportes Chofer", Icons.Default.Warning, Modifier.weight(1f), Color(0xFFE74C3C))
                }
                Spacer(Modifier.height(32.dp))
                Text("Accesos rápidos", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    QuickActionCard("Gestionar Horarios", Icons.Default.Schedule, Modifier.weight(1f), onClick = onGestionarHorarios)
                    QuickActionCard("Gestionar Choferes", Icons.Default.Person, Modifier.weight(1f), onClick = onGestionarChoferes)
                }
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, icon: ImageVector, modifier: Modifier, iconColor: Color = UPTColors.Vino) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(10.dp))
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun QuickActionCard(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.height(115.dp).clickable { onClick() }, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = UPTColors.Vino, modifier = Modifier.size(38.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ActivityChart(chartData: List<Float>) {
    val vinoUpt = UPTColors.Vino
    Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        val width = size.width
        val height = size.height
        val barWidth = width / (chartData.size * 1.5f)
        val spacing = (width - (barWidth * chartData.size)) / (chartData.size + 1)
        chartData.forEachIndexed { index, value ->
            val x = spacing + index * (barWidth + spacing)
            val barHeight = value * height
            drawRect(color = if (value > 0.6f) Color.Red else vinoUpt, topLeft = Offset(x, height - barHeight), size = Size(barWidth, barHeight))
        }
    }
}

@Composable
fun RecentNotificationItem(reporte: ReporteUnidad, onClick: () -> Unit, onDelete: () -> Unit, onValidar: (String) -> Unit) {
    val iconBg = if(reporte.tipo == ReporteTipo.ALERTA) Color.Red else Color(0xFFF39C12)
    val esRetraso = reporte.estado?.contains("Retrasada") == true
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).background(iconBg.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Warning, null, tint = iconBg, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(reporte.mensaje, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(reporte.tiempo, fontSize = 10.sp, color = Color.Gray)
                }
            }
            if (esRetraso && reporte.validacionAdmin == null) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onValidar("Denegado") }) { Text("Denegar", color = Color.Red) }
                    Button(onClick = { onValidar("Aceptado") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60))) { Text("Aceptar") }
                }
            }
        }
    }
}