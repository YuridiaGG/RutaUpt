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
    var isLoading by remember { mutableStateOf(true) }

    // Sincronizar con el servidor al entrar
    LaunchedEffect(Unit) {
        ChoferRepository.cargarDesdeServidor()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Horarios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver, enabled = !isLoading) {
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = vinoUpt)
            } else if (ChoferRepository.choferes.isEmpty()) {
                Text(
                    "No hay choferes registrados en el sistema", 
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ChoferRepository.choferes) { chofer ->
                        HorarioCard(chofer, onSave = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Horario actualizado para ${chofer.nombre}")
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun HorarioCard(chofer: Chofer, onSave: () -> Unit) {
    val vinoUpt = UPTColors.Vino
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
                    Text(text = "${chofer.nombre} ${chofer.apellidos}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Unidad: ${chofer.numeroUnidad}", fontSize = 14.sp, color = Color.Gray)
                }
                if (!isEditing) {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = vinoUpt)
                    }
                }
            }
            
            if (!isEditing) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = vinoUpt.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, null, tint = vinoUpt, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Horario: ${chofer.horario}", fontWeight = FontWeight.Bold, color = vinoUpt)
                    }
                }
            }

            AnimatedVisibility(visible = isEditing) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                    
                    Text("Hora de salida:", fontSize = 13.sp, color = vinoUpt, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = horaInicio,
                        onValueChange = { horaInicio = it },
                        placeholder = { Text("07:00") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                        TurnoSelector("A.M.", turnoInicio == "A.M.", { turnoInicio = "A.M." }, Modifier.weight(1f))
                        TurnoSelector("P.M.", turnoInicio == "P.M.", { turnoInicio = "P.M." }, Modifier.weight(1f))
                    }

                    Text("Hora de llegada:", fontSize = 13.sp, color = vinoUpt, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = horaLlegada,
                        onValueChange = { horaLlegada = it },
                        placeholder = { Text("08:00") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                        TurnoSelector("A.M.", turnoLlegada == "A.M.", { turnoLlegada = "A.M." }, Modifier.weight(1f))
                        TurnoSelector("P.M.", turnoLlegada == "P.M.", { turnoLlegada = "P.M." }, Modifier.weight(1f))
                    }

                    Button(
                        onClick = {
                            if (horaInicio.isNotBlank() && horaLlegada.isNotBlank()) {
                                chofer.horario = "$horaInicio $turnoInicio - $horaLlegada $turnoLlegada"
                                isEditing = false
                                onSave()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = vinoUpt),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Guardar Horario")
                    }
                }
            }
        }
    }
}

@Composable
fun TurnoSelector(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier.height(40.dp).clickable { onClick() },
        color = if (isSelected) UPTColors.Vino else Color(0xFFF0F0F0),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
