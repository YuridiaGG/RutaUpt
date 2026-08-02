package com.example.rutaupt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.storage.ReporteRepository
import com.example.rutaupt.model.ReporteTipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesEstudiantesChoferScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino

    // Filtramos para mostrar avisos que vienen de estudiantes (los que no son marcadores de estado del chofer)
    val reportesReales = ReporteRepository.reportes.filter { 
        it.estado == null || !it.estado!!.startsWith("EstadoChofer_")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Avisos de Estudiantes", fontWeight = FontWeight.Bold) },
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
        if (reportesReales.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay avisos reales de estudiantes", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reportesReales) { reporte ->
                    val mensaje = reporte.mensaje ?: ""
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp).background(
                                    if(reporte.tipo == ReporteTipo.ALERTA) Color(0xFFFFEBEE) else Color(0xFFF1F3F4), 
                                    CircleShape
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    when {
                                        mensaje.contains("llena", ignoreCase = true) -> Icons.Default.Groups
                                        reporte.tipo == ReporteTipo.ALERTA -> Icons.Default.Warning
                                        else -> Icons.Default.Info
                                    }, 
                                    null, 
                                    tint = if(reporte.tipo == ReporteTipo.ALERTA) Color.Red else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(text = mensaje, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = reporte.tiempo ?: "", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
