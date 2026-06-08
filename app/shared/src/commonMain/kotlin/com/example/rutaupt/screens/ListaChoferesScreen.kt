package com.example.rutaupt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun ListaChoferesScreen(
    onVolver: () -> Unit,
<<<<<<< HEAD
    onAgregarChofer: () -> Unit,
=======
>>>>>>> 02d9e061b74cb7907381fed3dc65f49648b87484
    onEditarChofer: (Chofer) -> Unit
) {
    val vinoUpt = UPTColors.Vino

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
<<<<<<< HEAD
                items(ChoferRepository.choferes) { chofer ->
                    ChoferPerfilCard(
                        chofer = chofer,
                        onVer = { /* Implementar ver detalles si es necesario */ },
                        onEditar = { onEditarChofer(chofer) }
=======
                items(
                    ChoferRepository.choferes,
                    key = { it.id }
                ) { chofer ->
                    ChoferCard(
                        chofer = chofer,
                        onEdit = { onEditarChofer(chofer) }
>>>>>>> 02d9e061b74cb7907381fed3dc65f49648b87484
                    )
                }
            }
        }
    }
}

@Composable
<<<<<<< HEAD
fun ChoferPerfilCard(
    chofer: Chofer,
    onVer: () -> Unit,
    onEditar: () -> Unit
=======
fun ChoferCard(
    chofer: Chofer,
    onEdit: () -> Unit
>>>>>>> 02d9e061b74cb7907381fed3dc65f49648b87484
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
<<<<<<< HEAD
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp).background(UPTColors.Vino.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
=======
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${chofer.nombre} ${chofer.apellido}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Unidad: ${chofer.numeroUnidad}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Tel: ${chofer.telefono}", style = MaterialTheme.typography.bodyMedium)
            Text("Horario: ${chofer.horaSalida} - ${chofer.horaLlegada}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onEdit
>>>>>>> 02d9e061b74cb7907381fed3dc65f49648b87484
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
                    Text(
                        text = "Unidad: ${chofer.numeroUnidad}",
                        fontSize = 14.sp,
                        color = UPTColors.Vino,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onVer) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Ver", fontWeight = FontWeight.SemiBold)
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
