package com.example.rutaupt.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NearbyUnitCard(
    unidad: String, 
    distMetros: Double, 
    vinoUpt: Color, 
    estaEnMovimiento: Boolean = false,
    onClick: () -> Unit = {}
) {
    // Estimación: 400 metros por minuto
    val tiempoMinutos = (distMetros / 400.0).toInt().coerceAtLeast(1)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFF7B161E), 
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "UPT-$unidad",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Micro detectada cerca\nde ti",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF212121)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Estado", color = Color.Gray, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (estaEnMovimiento) "En movimiento" else "Estático", 
                        color = if (estaEnMovimiento) Color(0xFF669966) else Color.Gray, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) { // Alineado a la derecha como en la imagen
                    Text("Llegada estimada", color = Color.Gray, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$tiempoMinutos min", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Default.AccessTime, 
                            null, 
                            tint = Color(0xFF7B161E), 
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
