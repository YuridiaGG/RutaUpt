package com.example.rutaupt.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.storage.ReporteRepository
import org.jetbrains.compose.resources.painterResource
import com.example.rutaupt.generated.resources.*

data class ReporteUnidad(
    val unidad: String,
    val mensaje: String,
    val tiempo: String,
    val tipo: ReporteTipo,
    val imagen: String? = null,
    val estado: String? = null
)

enum class ReporteTipo {
    ALERTA, INFORMACION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesUnidadesScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes de Unidades", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = vinoUpt
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (ReporteRepository.reportes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay reportes recientes", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ReporteRepository.reportes) { reporte ->
                    ReporteCard(reporte)
                }
            }
        }
    }
}

@Composable
fun ReporteCard(reporte: ReporteUnidad) {
    val colorIcono = when(reporte.estado) {
        "Retrasada (Validando)" -> Color(0xFFE67E22)
        "Unidad llena" -> Color.Red
        "Disponible" -> Color(0xFF27AE60)
        "Fin de ruta" -> Color.Gray
        else -> if (reporte.tipo == ReporteTipo.ALERTA) Color.Red else Color(0xFFF39C12)
    }
    
    val icono = when(reporte.estado) {
        "Retrasada (Validando)" -> Icons.Default.AccessTime
        "Unidad llena" -> Icons.Default.Groups
        "Disponible" -> Icons.Default.CheckCircle
        "Fin de ruta" -> Icons.Default.Flag
        "En recorrido" -> Icons.Default.DirectionsBus
        else -> if (reporte.tipo == ReporteTipo.ALERTA) Icons.Default.Warning else Icons.Default.BusAlert
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).background(colorIcono.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icono, contentDescription = null, tint = colorIcono)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Unidad: ${reporte.unidad}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = reporte.mensaje,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = reporte.tiempo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            if (reporte.imagen != null) {
                Spacer(Modifier.height(12.dp))
                Text("Evidencia de retraso:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.compose_multiplatform), // Simulación de imagen capturada
                        contentDescription = "Evidencia",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
