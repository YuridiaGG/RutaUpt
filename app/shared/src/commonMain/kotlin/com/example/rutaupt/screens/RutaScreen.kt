package com.example.rutaupt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaScreen(onVolver: () -> Unit) {
    val vinoUpt = UPTColors.Vino

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seguimiento en Tiempo Real", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = vinoUpt,
                    navigationIconContentColor = vinoUpt
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFEEEEEE)) // Color fondo mapa
        ) {
            // --- SIMULACIÓN DE MAPA ---
            // Aquí iría el componente real de Google Maps SDK
            // Simulamos calles con cajas
            Box(modifier = Modifier.fillMaxSize()) {
                // Calles horizontales
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                    repeat(4) {
                        Box(modifier = Modifier.fillMaxWidth().height(20.dp).background(Color.White.copy(alpha = 0.5f)))
                    }
                }
                // Calles verticales
                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    repeat(3) {
                        Box(modifier = Modifier.fillMaxHeight().width(20.dp).background(Color.White.copy(alpha = 0.5f)))
                    }
                }
            }

            // --- BUSCADOR TIPO GOOGLE MAPS ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(Modifier.width(12.dp))
                    Text("Buscar parada o micro...", color = Color.Gray, modifier = Modifier.weight(1f))
                }
            }

            // --- MARCADOR DE MI UBICACIÓN ---
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (-40).dp, y = 50.dp)
            ) {
                Icon(
                    Icons.Default.MyLocation,
                    contentDescription = "Mi ubicación",
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(32.dp)
                )
            }

            // --- MARCADOR DE LA MICRO (Google Maps Style) ---
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = 60.dp, y = (-80).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = vinoUpt,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.shadow(4.dp, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        "Micro UPT-05",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Icon(
                    Icons.Default.DirectionsBus,
                    contentDescription = "Transporte",
                    tint = vinoUpt,
                    modifier = Modifier.size(40.dp)
                )
            }

            // --- BOTONES FLOTANTES DE MAPA ---
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 100.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButton(
                    onClick = { },
                    containerColor = Color.White,
                    contentColor = vinoUpt,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Centrar")
                }
            }

            // --- TARJETA DE DETALLES DE LA MICRO ---
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(16.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(vinoUpt.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = vinoUpt)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unidad UPT-05", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("En recorrido - LLegada en 5 min", color = Color.Gray, fontSize = 14.sp)
                    }
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = vinoUpt),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ver más")
                    }
                }
            }
        }
    }
}
