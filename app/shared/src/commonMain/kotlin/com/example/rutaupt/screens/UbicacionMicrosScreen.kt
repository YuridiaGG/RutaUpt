package com.example.rutaupt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionMicrosScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    var unidadSeleccionada by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (unidadSeleccionada == null) "Ubicación de Micros" else "Ubicación: $unidadSeleccionada", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (unidadSeleccionada != null) unidadSeleccionada = null
                        else onVolver()
                    }) {
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
        if (unidadSeleccionada == null) {
            ListaUnidadesActivas(padding) { unidadSeleccionada = it }
        } else {
            MapaTiempoReal(padding, unidadSeleccionada!!)
        }
    }
}

@Composable
fun ListaUnidadesActivas(padding: PaddingValues, onSelect: (String) -> Unit) {
    val unidades = listOf("UPT-01", "UPT-05", "UPT-12", "UPT-24")
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Seleccione una unidad para ver su ubicación en tiempo real:", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        }
        items(unidades) { unidad ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(unidad) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).background(UPTColors.Vino.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DirectionsBus, contentDescription = "Unidad", tint = UPTColors.Vino)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(unidad, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("En recorrido - Ruta 05", color = Color.Gray, fontSize = 14.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun MapaTiempoReal(padding: PaddingValues, unidad: String) {
    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE0E0E0)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Map, modifier = Modifier.size(100.dp), tint = Color.Gray, contentDescription = "Mapa")
                Text("Simulación de Mapa en Tiempo Real", color = Color.Gray)
                Text("Unidad $unidad moviéndose...", fontWeight = FontWeight.Bold)
            }
        }
        
        Icon(
            Icons.Default.DirectionsBus,
            contentDescription = "Bus",
            tint = UPTColors.Vino,
            modifier = Modifier.size(40.dp).align(Alignment.Center).offset(y = (-20).dp)
        )
        
        Card(
            modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Información de la Unidad", fontWeight = FontWeight.Bold)
                Text("Velocidad: 35 km/h")
                Text("Próxima parada: Parada La Joya")
            }
        }
    }
}
