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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import com.example.rutaupt.generated.resources.*
import com.example.rutaupt.rememberBitmapFromBase64
import com.example.rutaupt.model.ReporteTipo
import com.example.rutaupt.storage.ReporteRepository
import com.example.rutaupt.storage.SessionManager
import com.example.rutaupt.api.RutaApiService
import kotlinx.coroutines.launch

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
    
    var stats by remember { mutableStateOf(mapOf("estudiantes" to 0, "choferes" to 0, "rutas" to 0)) }
    var showMenu by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var notificationsRead by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar estadísticas reales desde el servidor al iniciar
    LaunchedEffect(Unit) {
        stats = apiService.obtenerEstadisticasAdmin()
    }

    LaunchedEffect(mensajeConfirmacion) {
        mensajeConfirmacion?.let {
            snackbarHostState.showSnackbar(it)
            onMensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Administrador", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    // Campana de Notificaciones
                    Box {
                        IconButton(onClick = { 
                            showNotifications = true 
                            notificationsRead = true
                        }) {
                            BadgedBox(
                                badge = { 
                                    if (ReporteRepository.reportes.isNotEmpty() && !notificationsRead) {
                                        Badge { Text(ReporteRepository.reportes.size.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                            }
                        }
                        DropdownMenu(
                            expanded = showNotifications,
                            onDismissRequest = { showNotifications = false },
                            modifier = Modifier.width(300.dp).background(Color.White)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Avisos Recientes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                if (ReporteRepository.reportes.isNotEmpty()) {
                                    IconButton(onClick = { ReporteRepository.limpiarReportes() }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.Gray)
                                    }
                                }
                            }
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                            if (ReporteRepository.reportes.isEmpty()) {
                                Text("No hay avisos nuevos", modifier = Modifier.padding(16.dp), color = Color.Gray)
                            } else {
                                ReporteRepository.reportes.take(5).forEach { reporte ->
                                    RecentNotificationItem(
                                        mensaje = reporte.mensaje, 
                                        tiempo = reporte.tiempo, 
                                        iconBg = if(reporte.tipo == ReporteTipo.ALERTA) Color.Red else Color(0xFFF39C12), 
                                        icon = if(reporte.tipo == ReporteTipo.ALERTA) Icons.Default.Warning else Icons.Default.BusAlert,
                                        imagenBase64 = reporte.imagen,
                                        onClick = { showNotifications = false; onVerReportes() },
                                        onDelete = { ReporteRepository.eliminarReporte(reporte.id) }
                                    )
                                }
                            }
                            TextButton(onClick = { showNotifications = false; onVerReportes() }, modifier = Modifier.fillMaxWidth()) {
                                Text("Ver todos los reportes", color = vinoUpt)
                            }
                        }
                    }
                    
                    // Engrane de Configuración
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
            // Banner de bienvenida
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
                    StatCard("${stats["estudiantes"]}", "Estudiantes registrados", Icons.Default.Group, Modifier.weight(1f))
                    StatCard("${stats["choferes"]}", "Choferes registrados", Icons.Default.Badge, Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatCard("${stats["rutas"]}", "Rutas activas", Icons.Default.DirectionsBus, Modifier.weight(1f))
                    StatCard("${ReporteRepository.reportes.size}", "Reportes totales", Icons.Default.Warning, Modifier.weight(1f), Color(0xFFE74C3C))
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("Accesos rápidos", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    QuickActionCard("Gestionar Horarios", Icons.Default.Schedule, Modifier.weight(1f), onClick = onGestionarHorarios)
                    QuickActionCard("Gestionar Choferes", Icons.Default.Person, Modifier.weight(1f), onClick = onGestionarChoferes)
                }
                Spacer(modifier = Modifier.height(14.dp))
                QuickActionCard("Gestionar Estudiantes", Icons.Default.School, Modifier.fillMaxWidth(), onClick = onGestionarEstudiantes)

                Spacer(modifier = Modifier.height(32.dp))
                Text("Actividad semanal", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(16.dp))
                ActivityChart()
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
fun ActivityChart() {
    val vinoUpt = UPTColors.Vino
    val chartData = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f)
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
                val spacing = width / (chartData.size - 1)
                val path = Path()
                chartData.forEachIndexed { index, value ->
                    val x = index * spacing
                    val y = height - (value * height)
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path = path, color = vinoUpt, style = Stroke(width = 4.dp.toPx()))
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
    mensaje: String, tiempo: String, iconBg: Color, icon: ImageVector,
    imagenBase64: String? = null, onClick: () -> Unit, onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).background(iconBg.copy(alpha = 0.12f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconBg, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(mensaje, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black, maxLines = 1)
                Text(tiempo, fontSize = 10.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
            }
        }
    }
}
