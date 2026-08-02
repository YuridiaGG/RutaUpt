package com.example.rutaupt.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.rutaupt.storage.ReporteRepository
import com.example.rutaupt.model.ReporteUnidad
import com.example.rutaupt.model.ReporteTipo
import com.example.rutaupt.rememberBitmapFromBase64
import kotlinx.coroutines.launch
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesUnidadesScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    val scope = rememberCoroutineScope()
    var searchDate by remember { mutableStateOf("") }
    val selectedIds = remember { mutableStateListOf<Long>() }
    var fullImage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val filteredReportes = ReporteRepository.reportes.filter {
        val dateStr = formatTime(it.id)
        val unidad = it.unidad ?: ""
        val mensaje = it.mensaje ?: ""
        dateStr.contains(searchDate) || unidad.contains(searchDate) || mensaje.contains(searchDate, ignoreCase = true)
    }.sortedByDescending { it.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes y Evidencias", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (selectedIds.isNotEmpty()) {
                        IconButton(onClick = {
                            scope.launch {
                                var count = 0
                                selectedIds.toList().forEach { id ->
                                    if (ReporteRepository.eliminarReporte(id)) count++
                                }
                                selectedIds.clear()
                                snackbarHostState.showSnackbar("Eliminados $count reportes")
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = vinoUpt)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF8F9FA))) {
            OutlinedTextField(
                value = searchDate,
                onValueChange = { searchDate = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Buscar unidad, mensaje o fecha...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )

            if (filteredReportes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay reportes disponibles", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredReportes) { reporte ->
                        ReporteAdminCard(
                            reporte = reporte,
                            isSelected = selectedIds.contains(reporte.id),
                            onToggleSelection = {
                                if (selectedIds.contains(reporte.id)) selectedIds.remove(reporte.id)
                                else selectedIds.add(reporte.id)
                            },
                            onValidar = { estado ->
                                scope.launch {
                                    if (ReporteRepository.actualizarValidacion(reporte.id, estado)) {
                                        snackbarHostState.showSnackbar("Reporte $estado")
                                    } else {
                                        snackbarHostState.showSnackbar("Error al actualizar estado")
                                    }
                                }
                            },
                            onZoom = { fullImage = it }
                        )
                    }
                }
            }
        }

        fullImage?.let { base64 ->
            FullScreenImageDialog(base64) { fullImage = null }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReporteAdminCard(
    reporte: ReporteUnidad,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onValidar: (String) -> Unit,
    onZoom: (String) -> Unit
) {
    val bitmap = rememberBitmapFromBase64(reporte.imagen)
    val necesitaValidacion = (reporte.imagen != null || reporte.estado == "Retrasada") && reporte.validacionAdmin == null
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { if (isSelected) onToggleSelection() else Unit },
                onLongClick = onToggleSelection
            ),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) BorderStroke(2.dp, Color.Blue) else null,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (reporte.tipo == ReporteTipo.ALERTA) Icons.Default.Warning else Icons.Default.Info, 
                    null, 
                    tint = if (reporte.tipo == ReporteTipo.ALERTA) Color.Red else Color(0xFFF39C12)
                )
                Spacer(Modifier.width(8.dp))
                Text("Unidad ${reporte.unidad ?: "S/N"}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text(formatTime(reporte.id), fontSize = 11.sp, color = Color.Gray)
            }
            
            Text(reporte.mensaje ?: "", modifier = Modifier.padding(vertical = 8.dp), fontSize = 14.sp)
            
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { reporte.imagen?.let { onZoom(it) } },
                    contentScale = ContentScale.Crop
                )
            }

            if (necesitaValidacion) {
                Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onValidar("Denegado") }) { Text("Denegar", color = Color.Red) }
                    Button(
                        onClick = { onValidar("Aceptado") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) { Text("Aceptar Evidencia") }
                }
            } else if (reporte.validacionAdmin != null) {
                Surface(
                    color = if (reporte.validacionAdmin == "Aceptado") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.End)
                ) {
                    Text(
                        text = "VISTO: ${reporte.validacionAdmin}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (reporte.validacionAdmin == "Aceptado") Color(0xFF2E7D32) else Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun FullScreenImageDialog(base64: String, onDismiss: () -> Unit) {
    val bitmap = rememberBitmapFromBase64(base64)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black).clickable { onDismiss() }, contentAlignment = Alignment.Center) {
            bitmap?.let {
                Image(bitmap = it, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
            }
            IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                Icon(Icons.Default.Close, null, tint = Color.White)
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    // Si el ID es muy pequeño (como 0), no es una fecha válida (evita 1969/1970)
    if (ms < 1000000L) return "Reciente"
    
    return try {
        // Detectar si son segundos (10 dígitos) o milisegundos (13 dígitos)
        val timestamp = if (ms < 10000000000L) ms * 1000 else ms
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        
        // Tulancingo es UTC-6. Si por error sale una fecha muy vieja, mostramos "Reciente"
        if (dt.year < 2024) return "Reciente"

        val day = dt.dayOfMonth.toString().padStart(2, '0')
        val month = dt.monthNumber.toString().padStart(2, '0')
        val hour = dt.hour.toString().padStart(2, '0')
        val min = dt.minute.toString().padStart(2, '0')
        
        "$day/$month/${dt.year} $hour:$min"
    } catch (e: Exception) { "Reciente" }
}
