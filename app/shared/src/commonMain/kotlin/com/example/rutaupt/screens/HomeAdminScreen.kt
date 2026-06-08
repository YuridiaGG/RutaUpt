package com.example.rutaupt.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import com.example.rutaupt.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdminScreen(
    onGestionarChoferes: () -> Unit,
    onGestionarHorarios: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    val vinoOscuro = UPTColors.VinoOscuro

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Box {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color.Red, CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 2.dp)
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
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    selected = true,
                    onClick = {},
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = vinoUpt, indicatorColor = vinoUpt.copy(alpha = 0.1f))
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DirectionsBus, contentDescription = null) },
                    selected = false,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    selected = false,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    selected = false,
                    onClick = {}
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER VINO CON MASCOTA ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        brush = Brush.verticalGradient(colors = listOf(vinoUpt, vinoOscuro)),
                        shape = RoundedCornerShape(bottomStart = 45.dp, bottomEnd = 0.dp)
                    )
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text(
                            "¡Bienvenido,",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Administrador!",
                            color = Color.White,
                            fontSize = 19.sp, // Tamaño reducido para que no se corte
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Image(
                        painter = painterResource(Res.drawable.admin),
                        contentDescription = "Admin Mascot",
                        modifier = Modifier
                            .size(150.dp)
                            .offset(y = 20.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // --- SECCIÓN 1: Resumen General ---
                SectionHeader("Resumen General")
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatCard("1,248", "Estudiantes registrados", Icons.Default.Group, Modifier.weight(1f))
                    StatCard("56", "Choferes activos", Icons.Default.Badge, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatCard("18", "Rutas activas", Icons.Default.DirectionsBus, Modifier.weight(1f))
                    StatCard("23", "Reportes pendientes", Icons.Default.Warning, Modifier.weight(1f), iconColor = Color(0xFFE74C3C))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- SECCIÓN 2: Accesos Rápidos ---
                SectionHeader("Accesos rápidos")
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    // "Gestionar Rutas" ELIMINADO según solicitud
                    QuickActionCard("Gestionar Horarios", Icons.Default.Schedule, Modifier.weight(1f), onClick = onGestionarHorarios)
                    QuickActionCard("Gestionar Choferes", Icons.Default.Person, Modifier.weight(1f), onClick = onGestionarChoferes)
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    QuickActionCard("Gestionar Estudiantes", Icons.Default.School, Modifier.weight(1f))
                    // Espacio vacío para mantener el grid
                    Box(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(20.dp))
                
                // Botón Ver Reportes
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = vinoUpt)
                ) {
                    Icon(Icons.Default.Assessment, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(12.dp))
                    Text("Ver Reportes", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- SECCIÓN 3: Actividad Semanal ---
                SectionHeader("Actividad semanal")
                Spacer(modifier = Modifier.height(16.dp))
                ActivityChart()

                Spacer(modifier = Modifier.height(32.dp))

                // --- SECCIÓN 4: Notificaciones ---
                SectionHeader("Notificaciones recientes")
                Spacer(modifier = Modifier.height(16.dp))
                
                RecentNotificationItem("Reporte de ruta 05: Unidad llena", "Hace 10 minutos", Color(0xFFF39C12), Icons.Default.BusAlert)
                RecentNotificationItem("Nueva ruta 18 creada", "Hace 1 hora", Color(0xFF27AE60), Icons.Default.AddLocation)
                RecentNotificationItem("Chofer Luis ha completado su ruta", "Hace 2 horas", Color(0xFF2980B9), Icons.Default.CheckCircle)
                
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Ver todas las notificaciones",
                    color = vinoUpt,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.fillMaxWidth().clickable { }.padding(8.dp)
                )
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        color = Color(0xFF333333)
    )
}

@Composable
private fun StatCard(value: String, label: String, icon: ImageVector, modifier: Modifier, iconColor: Color = UPTColors.Vino) {
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
private fun QuickActionCard(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.height(115.dp).clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = UPTColors.Vino, modifier = Modifier.size(38.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF444444), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun RecentNotificationItem(title: String, time: String, iconBg: Color, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(46.dp).background(iconBg.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconBg, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                Text(time, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.LightGray
            )
        }
    }
}

@Composable
private fun ActivityChart() {
    val vinoUpt = UPTColors.Vino
    val chartData = listOf(0.3f, 0.6f, 0.2f, 0.4f, 0.9f, 0.5f, 0.7f)
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

                // Línea del gráfico
                drawPath(
                    path = path,
                    color = vinoUpt,
                    style = Stroke(width = 4.dp.toPx())
                )

                // Puntos decorativos
                chartData.forEachIndexed { index, value ->
                    val x = index * spacing
                    val y = height - (value * height)
                    drawCircle(color = vinoUpt, radius = 6.dp.toPx(), center = Offset(x, y))
                    drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(x, y))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                days.forEach { day ->
                    Text(day, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
