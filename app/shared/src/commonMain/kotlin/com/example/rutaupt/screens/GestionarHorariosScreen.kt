package com.example.rutaupt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.model.Chofer
import com.example.rutaupt.storage.ChoferRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarHorariosScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Horarios", fontWeight = FontWeight.Bold) },
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
        if (ChoferRepository.choferes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay choferes registrados", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(ChoferRepository.choferes) { chofer ->
                    HorarioCard(chofer)
                }
            }
        }
    }
}

@Composable
fun HorarioCard(chofer: Chofer) {
    var selectedTurno by remember { mutableStateOf("a.m.") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Chofer: ${chofer.nombre} ${chofer.apellidos}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "Número de unidad: ${chofer.numeroUnidad}", fontSize = 14.sp, color = Color.Gray)
            Text(text = "Teléfono: ${chofer.telefono}", fontSize = 14.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = "Agregar horario:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = UPTColors.Vino)
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TurnoSelector(
                    text = "A.M.",
                    isSelected = selectedTurno == "a.m.",
                    onClick = { selectedTurno = "a.m." },
                    modifier = Modifier.weight(1f)
                )
                TurnoSelector(
                    text = "P.M.",
                    isSelected = selectedTurno == "p.m.",
                    onClick = { selectedTurno = "p.m." },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TurnoSelector(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) UPTColors.Vino else Color(0xFFF0F0F0)
    val textColor = if (isSelected) Color.White else Color.Gray

    Box(
        modifier = modifier
            .height(44.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontWeight = FontWeight.Bold)
    }
}
