package com.example.rutaupt.screens

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
fun ListaChoferesScreen(
    onVolver: () -> Unit,
    onAgregarChofer: () -> Unit,
    onEditarChofer: (Chofer) -> Unit
) {
    val vinoUpt = UPTColors.Vino
    val scope = rememberCoroutineScope()
    var choferAEliminar by remember { mutableStateOf<Chofer?>(null) }

    // Cargar datos reales desde el servidor al entrar
    LaunchedEffect(Unit) {
        ChoferRepository.cargarDesdeServidor()
    }

    if (choferAEliminar != null) {
        AlertDialog(
            onDismissRequest = { choferAEliminar = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro de que desea eliminar al chofer ${choferAEliminar?.nombre}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = choferAEliminar?.id
                        if (id != null) {
                            scope.launch {
                                ChoferRepository.eliminarChofer(id)
                            }
                        }
                        choferAEliminar = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { choferAEliminar = null }) {
                    Text("Cancelar")
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Choferes", fontWeight = FontWeight.Bold) },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarChofer,
                containerColor = vinoUpt,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Chofer")
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (ChoferRepository.choferes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("No hay choferes registrados", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(ChoferRepository.choferes) { chofer ->
                    ChoferPerfilCard(
                        chofer = chofer,
                        onEditar = { onEditarChofer(chofer) },
                        onEliminar = { choferAEliminar = chofer }
                    )
                }
            }
        }
    }
}

@Composable
fun ChoferPerfilCard(
    chofer: Chofer,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp).background(UPTColors.Vino.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = UPTColors.Vino)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "${chofer.nombre} ${chofer.apellidos}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsBus, null, modifier = Modifier.size(14.dp), tint = UPTColors.Vino)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Unidad: ${chofer.numeroUnidad}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Sección de Horario Asignado
            Surface(
                color = if (chofer.horario == "Sin asignar") Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = if (chofer.horario == "Sin asignar") Color(0xFFD32F2F) else Color(0xFF2E7D32),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "Horario de Ruta", 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold,
                            color = if (chofer.horario == "Sin asignar") Color(0xFFD32F2F) else Color(0xFF2E7D32)
                        )
                        Text(
                            text = chofer.horario,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.7f))
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onEditar,
                    colors = ButtonDefaults.buttonColors(containerColor = UPTColors.Vino),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Editar", fontSize = 14.sp)
                }
            }
        }
    }
}
