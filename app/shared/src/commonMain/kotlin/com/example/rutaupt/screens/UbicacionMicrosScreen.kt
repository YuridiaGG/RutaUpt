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
import com.example.rutaupt.api.RutaApiService
import com.example.rutaupt.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionMicrosScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    var unidadSeleccionada by remember { mutableStateOf<String?>(null) }
    val apiService = remember { RutaApiService() }
    var choferes by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        choferes = apiService.obtenerUsuariosPorRol("chofer")
        isLoading = false
    }

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
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = vinoUpt)
                }
            } else {
                ListaUnidadesActivas(padding, choferes) { unidadSeleccionada = it }
            }
        } else {
            MapaTiempoReal(padding, unidadSeleccionada!!)
        }
    }
}

@Composable
fun ListaUnidadesActivas(padding: PaddingValues, choferes: List<User>, onSelect: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                if (choferes.isEmpty()) "No hay unidades activas registradas." 
                else "Seleccione una unidad para ver su ubicación en tiempo real:", 
                color = Color.Gray, 
                fontSize = 14.sp, 
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(choferes) { chofer ->
            val unidad = chofer.numeroUnidad ?: "Sin número"
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
                        Text("Unidad $unidad", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${chofer.nombre} ${chofer.apellidos}", color = Color.Gray, fontSize = 14.sp)
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
        // Mapa real integrado aquí
        MapComponent(
            modifier = Modifier.fillMaxSize(),
            latitude = 20.0845, // Ubicación simulada para demo
            longitude = -98.3695,
            title = "Unidad $unidad"
        )
        
        Card(
            modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF4CAF50), CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text("Unidad $unidad", fontWeight = FontWeight.Bold)
                }
                Text("Estado: En movimiento", fontSize = 14.sp, color = Color.Gray)
                Text("Velocidad: 35 km/h", fontSize = 14.sp, color = Color.Gray)
                Text("Próxima parada: Parada La Joya", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
