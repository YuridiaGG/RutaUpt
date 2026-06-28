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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesEstudiantesChoferScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino

    val reportes = listOf(
        Pair("Unidad llena en Parada La Joya", "Hace 5 min"),
        Pair("Retraso aproximado de 10 min", "Hace 15 min"),
        Pair("Mucho tráfico en Av. Juárez", "Hace 30 min"),
        Pair("Unidad UPT-05 no pasó a tiempo", "Hace 1 hora")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes de Estudiantes", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reportes) { reporte ->
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
                            modifier = Modifier.size(40.dp).background(Color(0xFFF1F3F4), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (reporte.first.contains("llena")) Icons.Default.Groups else Icons.Default.AccessTime, 
                                null, 
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(text = reporte.first, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = reporte.second, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
