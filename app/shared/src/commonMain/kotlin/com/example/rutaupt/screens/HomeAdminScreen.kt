package com.example.rutaupt.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.rutaupt.model.ReporteUnidad
import com.example.rutaupt.storage.ReporteRepository
import com.example.rutaupt.storage.SessionManager
import com.example.rutaupt.storage.ParadaRepository
import com.example.rutaupt.storage.ChoferRepository
import com.example.rutaupt.storage.EstudianteRepository
import com.example.rutaupt.api.RutaApiService
import com.example.rutaupt.getPlatform
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.*

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
    var unidadesActivas by remember { mutableStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Identificador para evitar duplicados en notificaciones del sistema
    var ultimoIdNotificado by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        scope.launch { ChoferRepository.cargarDesdeServidor() }
        scope.launch { EstudianteRepository.cargarDesdeServidor() }
        scope.launch { ParadaRepository.cargarParadas() }

        // Inicializar el ID base con el reporte más reciente existente
        ReporteRepository.cargarReportes()
        if (ReporteRepository.reportes.isNotEmpty()) {
            ultimoIdNotificado = ReporteRepository.reportes.maxOf { it.id }
        }

        while(true) {
            try {
                stats = apiService.obtenerEstadisticasAdmin()
                unidadesActivas = apiService.obtenerUbicaciones().size
                
                ReporteRepository.cargarReportes()
                
                // Lógica de notificaciones nativas del celular
                val nuevos = ReporteRepository.reportes.filter { it.id > ultimoIdNotificado }
                if (nuevos.isNotEmpty()) {
                    nuevos.forEach { reporte ->
                        val titulo = if (reporte.mensaje?.startsWith("Anónimo:") == true) "Aviso de Alumno" else "Unidad ${reporte.unidad ?: "S/N"}"
                        getPlatform().showNotification(titulo, reporte.mensaje ?: "Nuevo aviso recibido")
                    }
                    ultimoIdNotificado = ReporteRepository.reportes.maxOf { it.id }
                }
            } catch (e: Exception) {
                // Silencioso para evitar crashes
            }
            delay(12000) // Cada 12 segundos para no saturar
        }
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
                title = { Text("Administrador", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
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
                                text = { Text("Mi Perfil") },
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
                                    SessionManager.cerrarSesion()
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
            NavigationBar(
                containerColor = Color.White, 
                modifier = Modifier.shadow(16.dp).navigationBarsPadding()
            ) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.Map, null) }, label = { Text("Mapa") }, selected = false, onClick = onUbicacionMicros)
                NavigationBarItem(icon = { Icon(Icons.Default.DirectionsBus, null) }, label = { Text("Reportes") }, selected = false, onClick = onVerReportes)
                NavigationBarItem(icon = { Icon(Icons.Default.LocationOn, null) }, label = { Text("Paradas") }, selected = false, onClick = onGestionarParadas)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(Brush.verticalGradient(listOf(vinoUpt, vinoOscuro)), RoundedCornerShape(bottomStart = 45.dp))) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text("¡Bienvenido,", color = Color.White.copy(alpha = 0.85f), fontSize = 16.sp)
                        Text("${SessionManager.nombreUsuario}!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Image(painter = painterResource(Res.drawable.admin), contentDescription = null, modifier = Modifier.size(120.dp).offset(y = 10.dp), contentScale = ContentScale.Fit)
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
                    StatCard("$unidadesActivas", "Unidades en línea", Icons.Default.DirectionsBus, Modifier.weight(1f))
                    StatCard("${ReporteRepository.reportes.size}", "Reportes hoy", Icons.Default.Warning, Modifier.weight(1f))
                }
                
                Spacer(Modifier.height(32.dp))
                Text("Accesos rápidos", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                Spacer(Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    QuickActionCard("Horarios", Icons.Default.Schedule, Modifier.weight(1f), onClick = onGestionarHorarios)
                    QuickActionCard("Choferes", Icons.Default.Badge, Modifier.weight(1f), onClick = onGestionarChoferes)
                }
                
                Spacer(Modifier.height(14.dp))

                QuickActionCard("Gestionar Estudiantes / Alumnos", Icons.Default.Group, Modifier.fillMaxWidth(), onClick = onGestionarEstudiantes)
                
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, icon: ImageVector, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Icon(icon, null, tint = UPTColors.Vino, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(10.dp))
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun QuickActionCard(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.height(100.dp).clickable { onClick() }, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = UPTColors.Vino, modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}
