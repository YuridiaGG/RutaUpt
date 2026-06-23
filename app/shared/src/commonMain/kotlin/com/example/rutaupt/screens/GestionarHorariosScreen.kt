package com.example.rutaupt.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Edit
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarHorariosScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    HorarioCard(chofer, onSave = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Horario asignado")
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun HorarioCard(chofer: Chofer, onSave: () -> Unit) {
    // Determinar si ya tiene un horario asignado (asumimos que un horario real tiene un guion "-" o es distinto de los defaults)
    val tieneHorario = chofer.horario.contains("-")
    var isEditing by remember { mutableStateOf(!tieneHorario) }
    
    var horaInicio by remember { mutableStateOf("") }
    var turnoInicio by remember { mutableStateOf("A.M.") }
    
    var horaLlegada by remember { mutableStateOf("") }
    var turnoLlegada by remember { mutableStateOf("A.M.") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Chofer: ${chofer.nombre} ${chofer.apellidos}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Unidad: ${chofer.numeroUnidad}", fontSize = 14.sp, color = Color.Gray)
                }
                if (!isEditing) {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = UPTColors.Vino)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mostrar horario si ya está asignado y no estamos editando
            if (!isEditing) {
                Surface(
                    color = UPTColors.Vino.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = UPTColors.Vino, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Horario: ${chofer.horario}",
                            fontWeight = FontWeight.Bold,
                            color = UPTColors.Vino,
                            fontSize = 15.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = UPTColors.Vino)
                ) {
                    Text("Modificar horario")
                }
            }

            AnimatedVisibility(visible = isEditing) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Sección Inicio
                    Text(text = "Hora de inicio de ruta:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = UPTColors.Vino)
                    OutlinedTextField(
                        value = horaInicio,
                        onValueChange = { horaInicio = it },
                        placeholder = { Text("Ej: 07:30") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TurnoSelector(
                            text = "A.M.",
                            isSelected = turnoInicio == "A.M.",
                            onClick = { turnoInicio = "A.M." },
                            modifier = Modifier.weight(1f)
                        )
                        TurnoSelector(
                            text = "P.M.",
                            isSelected = turnoInicio == "P.M.",
                            onClick = { turnoInicio = "P.M." },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sección Llegada
                    Text(text = "Hora de llegada:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = UPTColors.Vino)
                    OutlinedTextField(
                        value = horaLlegada,
                        onValueChange = { horaLlegada = it },
                        placeholder = { Text("Ej: 08:30") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TurnoSelector(
                            text = "A.M.",
                            isSelected = turnoLlegada == "A.M.",
                            onClick = { turnoLlegada = "A.M." },
                            modifier = Modifier.weight(1f)
                        )
                        TurnoSelector(
                            text = "P.M.",
                            isSelected = turnoLlegada == "P.M.",
                            onClick = { turnoLlegada = "P.M." },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (horaInicio.isNotBlank() && horaLlegada.isNotBlank()) {
                                chofer.horario = "$horaInicio $turnoInicio - $horaLlegada $turnoLlegada"
                                isEditing = false
                                onSave()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UPTColors.Vino),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Guardar horario", fontWeight = FontWeight.Bold)
                    }
                    
                    if (tieneHorario) {
                        TextButton(
                            onClick = { isEditing = false },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Text("Cancelar", color = Color.Gray)
                        }
                    }
                }
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
