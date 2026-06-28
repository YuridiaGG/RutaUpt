package com.example.rutaupt.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.rutaupt.generated.resources.*
import com.example.rutaupt.rememberBitmapFromBase64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesUnidadesScreen(
    onVolver: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    var imagenSeleccionada by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Reportes", fontWeight = FontWeight.Bold) },
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
        val listaReportes = ReporteRepository.reportes
        
        if (listaReportes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inbox, null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Text("No hay reportes para revisar", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listaReportes, key = { it.id }) { reporte ->
                    ReporteCard(
                        reporte = reporte,
                        onValidar = { ReporteRepository.actualizarValidacion(reporte.id, "VALIDADO") },
                        onRechazar = { ReporteRepository.actualizarValidacion(reporte.id, "RECHAZADO") },
                        onVerImagen = { imagenSeleccionada = it }
                    )
                }
            }
        }

        // Diálogo para ver la foto en pantalla completa
        imagenSeleccionada?.let { imgBase64 ->
            FullScreenImageDialog(
                base64 = imgBase64,
                onDismiss = { imagenSeleccionada = null }
            )
        }
    }
}

@Composable
fun ReporteCard(
    reporte: ReporteUnidad,
    onValidar: () -> Unit,
    onRechazar: () -> Unit,
    onVerImagen: (String) -> Unit
) {
    val colorIcono = when(reporte.estado) {
        "Retrasada (Validando)" -> Color(0xFFE67E22)
        "Unidad llena" -> Color.Red
        "Disponible" -> Color(0xFF27AE60)
        else -> if (reporte.tipo == ReporteTipo.ALERTA) Color.Red else Color(0xFFF39C12)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(colorIcono.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (!reporte.imagen.isNullOrEmpty()) Icons.Default.CameraAlt else Icons.Default.Info, 
                        null, tint = colorIcono, modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Unidad ${reporte.unidad}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(reporte.tiempo, fontSize = 11.sp, color = Color.Gray)
                }
                if (!reporte.imagen.isNullOrEmpty()) {
                    Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(4.dp)) {
                        Text("EVIDENCIA", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            Text(text = reporte.mensaje, fontSize = 14.sp, color = Color.Black)
            
            if (!reporte.imagen.isNullOrEmpty()) {
                val bitmap = rememberBitmapFromBase64(reporte.imagen)
                Spacer(Modifier.height(12.dp))
                Text("Toca la imagen para ampliar:", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F0F0))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .clickable { onVerImagen(reporte.imagen!!) },
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(bitmap = bitmap, contentDescription = "Evidencia", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ZoomIn, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    } else {
                        Icon(Icons.Default.BrokenImage, null, tint = Color.Gray)
                    }
                }

                if (reporte.estado == "Retrasada (Validando)" && reporte.validacionAdmin == null) {
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onValidar, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60)), shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Validar")
                        }
                        Button(onClick = onRechazar, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Red), shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Rechazar")
                        }
                    }
                } else if (reporte.validacionAdmin != null) {
                    Spacer(Modifier.height(12.dp))
                    StatusTag(reporte.validacionAdmin!!)
                }
            }
        }
    }
}

@Composable
fun FullScreenImageDialog(base64: String, onDismiss: () -> Unit) {
    val bitmap = rememberBitmapFromBase64(base64)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Zoom",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, null, tint = Color.White)
            }
        }
    }
}

@Composable
fun StatusTag(status: String) {
    val isValid = status == "VALIDADO"
    Surface(
        color = if (isValid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                if (isValid) Icons.Default.CheckCircle else Icons.Default.Cancel,
                null, tint = if (isValid) Color(0xFF2E7D32) else Color.Red, modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isValid) "RETRASO VALIDADO" else "RETRASO RECHAZADO",
                color = if (isValid) Color(0xFF2E7D32) else Color.Red,
                fontWeight = FontWeight.ExtraBold, fontSize = 12.sp
            )
        }
    }
}
