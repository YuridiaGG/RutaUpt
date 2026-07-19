package com.example.rutaupt.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Save
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

    LaunchedEffect(Unit) {
        ChoferRepository.cargarDesdeServidor()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar Horarios", fontWeight = FontWeight.Bold) },
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = vinoUpt)
                }
            } else if (ChoferRepository.choferes.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Text("No hay choferes para asignar horarios", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ChoferRepository.choferes) { chofer ->
                        HorarioCard(chofer, onSave = { nuevoHorario ->
                            scope.launch {
                                val success = ChoferRepository.actualizarHorario(chofer.id ?: 0, nuevoHorario)
                                if (success) {
                                    snackbarHostState.showSnackbar("¡Horario guardado para ${chofer.nombre}!")
                                } else {
                                    snackbarHostState.showSnackbar("Error: Verifica que el backend acepte horarios")
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun HorarioCard(chofer: Chofer, onSave: (String) -> Unit) {
    val vinoUpt = UPTColors.Vino
    var isEditing by remember { mutableStateOf(chofer.horario == "Sin asignar" || chofer.horario.isBlank()) }
    
    var horaInicio by remember { mutableStateOf("") }
    var turnoInicio by remember { mutableStateOf("A.M.") }
    var horaLlegada by remember { mutableStateOf("") }
    var turnoLlegada by remember { mutableStateOf("A.M.") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "${chofer.nombre} ${chofer.apellidos}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(text = "Unidad: ${chofer.numeroUnidad}", fontSize = 13.sp, color = Color.Gray)
                }
                
                if (!isEditing) {
                    Button(
                        onClick = { isEditing = true },
                        colors = ButtonDefaults.buttonColors(containerColor = vinoUpt.copy(alpha = 0.1f), contentColor = vinoUpt),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("MODIFICAR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            if (!isEditing) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFF1F8E9), // Verde suave para indicar que ya tiene horario
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("HORARIO ACTUAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Text(text = chofer.horario, fontWeight = FontWeight.ExtraBold, color = Color.Black, fontSize = 15.sp)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isEditing) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFEEEEEE))
                    
                    Text("SALIDA DE RUTA", fontSize = 11.sp, color = vinoUpt, fontWeight = FontWeight.ExtraBold)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        OutlinedTextField(
                            value = horaInicio,
                            onValueChange = { if(it.length <= 5) horaInicio = it },
                            placeholder = { Text("00:00") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(Modifier.width(8.dp))
                        TurnoSelector("AM", turnoInicio == "A.M.", { turnoInicio = "A.M." }, Modifier.weight(0.6f))
                        Spacer(Modifier.width(4.dp))
                        TurnoSelector("PM", turnoInicio == "P.M.", { turnoInicio = "P.M." }, Modifier.weight(0.6f))
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("LLEGADA A DESTINO", fontSize = 11.sp, color = vinoUpt, fontWeight = FontWeight.ExtraBold)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        OutlinedTextField(
                            value = horaLlegada,
                            onValueChange = { if(it.length <= 5) horaLlegada = it },
                            placeholder = { Text("00:00") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(Modifier.width(8.dp))
                        TurnoSelector("AM", turnoLlegada == "A.M.", { turnoLlegada = "A.M." }, Modifier.weight(0.6f))
                        Spacer(Modifier.width(4.dp))
                        TurnoSelector("PM", turnoLlegada == "P.M.", { turnoLlegada = "P.M." }, Modifier.weight(0.6f))
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (horaInicio.isNotBlank() && horaLlegada.isNotBlank()) {
                                val nuevoHorario = "$horaInicio $turnoInicio - $horaLlegada $turnoLlegada"
                                onSave(nuevoHorario)
                                isEditing = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = vinoUpt),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(8.dp))
                        Text("GUARDAR NUEVO HORARIO", fontWeight = FontWeight.Bold)
                    }
                    
                    if (chofer.horario != "Sin asignar" && chofer.horario.isNotBlank()) {
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
fun TurnoSelector(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier.height(45.dp).clickable { onClick() },
        color = if (isSelected) UPTColors.Vino else Color(0xFFF5F5F5),
        shape = RoundedCornerShape(10.dp),
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
