package com.example.rutaupt.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import org.jetbrains.compose.resources.painterResource
import com.example.rutaupt.app.shared.generated.resources.*

object DriverColors {
    val Vino          = Color(0xFF6B1124)
    val FondoGris     = Color(0xFFF4F4F4)
    val Blanco        = Color(0xFFFFFFFF)
    val TextoGris     = Color(0xFF7A7A7A)
    val VerdeOk       = Color(0xFF2E7D32)
    val NaranjaAlerta = Color(0xFFE65100)
    val BordeInput    = Color(0xFFD6C5C7)
}

@Composable
fun HomeChoferScreen() {
    Scaffold(
        // Faldón inferior fijo con botones de navegación estilizados
        bottomBar = { BarraNavegacionSimple() },
    ) { paddingValues ->
        
        // Contenedor principal con scroll vertical para ver todo el historial abajo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DriverColors.FondoGris)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            
            // 1. CABECERA VINO CURVA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(DriverColors.Vino)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¡Buen día,",
                            color = DriverColors.Blanco.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Juan!",
                            color = DriverColors.Blanco,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Unidad: UPT-05",
                            color = DriverColors.Blanco.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    // Imagen del torito chofer (sin fondo) montada en la cabecera
                    Image(
                        painter = painterResource(Res.drawable.toritos),
                        contentDescription = "Torito Chofer",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            // CUERPO DE LA INTERFAZ (Deslizable)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                
                // 2. TARJETA DE RUTA ASIGNADA
                Text(
                    text = "Ruta asignada",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DriverColors.Blanco),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .background(DriverColors.Vino, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Ruta 05", color = DriverColors.Blanco, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Centro - UPT", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Unidad: UPT-05", color = DriverColors.TextoGris, fontSize = 13.sp)
                        }
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            modifier = Modifier.size(75.dp),
                            tint = DriverColors.Vino.copy(alpha = 0.1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. ESTADO ACTUAL (Botones táctiles)
                Text(
                    text = "Estado actual",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DriverColors.Vino),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("En recorrido", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BotonEstadoChofer(texto = "Retrasada", colorFondo = DriverColors.Blanco, colorTexto = DriverColors.NaranjaAlerta, esDelineado = true, modifier = Modifier.weight(1f))
                    BotonEstadoChofer(texto = "Unidad llena", colorFondo = DriverColors.Blanco, colorTexto = DriverColors.TextoGris, esDelineado = true, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BotonEstadoChofer(texto = "Disponible", colorFondo = DriverColors.Blanco, colorTexto = DriverColors.VerdeOk, esDelineado = true, modifier = Modifier.weight(1f))
                    BotonEstadoChofer(texto = "Fin de ruta", colorFondo = DriverColors.Blanco, colorTexto = Color.Black, esDelineado = true, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. PARADAS DE LA RUTA
                Text(
                    text = "Paradas de la ruta",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                val paradasRuta = listOf(
                    Triple("Centro", "7:00 AM", true),
                    Triple("Parada La Joya", "7:10 AM", false),
                    Triple("Parada Las Flores", "7:18 AM", false),
                    Triple("UPT", "7:30 AM", false)
                )

                paradasRuta.forEachIndexed { index, parada ->
                    Row(
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (parada.third) Color.Black else DriverColors.BordeInput)
                            )
                            if (index < paradasRuta.size - 1) {
                                Box(modifier = Modifier.width(2.dp).weight(1f).background(DriverColors.BordeInput))
                            }
                        }
                        Text(text = parada.first, color = Color.Black, fontSize = 15.sp, modifier = Modifier.weight(1f))
                        Text(text = parada.second, color = DriverColors.TextoGris, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. NUEVA SECCIÓN: REPORTES DE ESTUDIANTES
                Text(
                    text = "Reportes de estudiantes",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DriverColors.Blanco),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(DriverColors.TextoGris))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Unidad llena en Parada La Joya", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                                Text("Hace 5 min", fontSize = 12.sp, color = DriverColors.TextoGris)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DriverColors.FondoGris)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Retraso aproximado de 10 min", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                                Text("Hace 15 min", fontSize = 12.sp, color = DriverColors.TextoGris)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 6. NUEVA SECCIÓN: HISTORIAL RECIENTE
                Text(
                    text = "Historial reciente",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DriverColors.Blanco),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Fila Historial 1
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Ruta 04", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black, modifier = Modifier.weight(1f))
                            Text("✓ Completada", color = DriverColors.VerdeOk, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            Text("Ayer 6:45 PM", color = DriverColors.TextoGris, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = DriverColors.FondoGris)
                        // Fila Historial 2
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Ruta 03", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black, modifier = Modifier.weight(1f))
                            Text("✓ Completada", color = DriverColors.VerdeOk, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            Text("Ayer 5:10 PM", color = DriverColors.TextoGris, fontSize = 12.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun BotonEstadoChofer(
    texto: String,
    colorFondo: Color,
    colorTexto: Color,
    esDelineado: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if(esDelineado) DriverColors.BordeInput else Color.Transparent),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = colorFondo, contentColor = colorTexto)
    ) {
        Text(texto, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun BarraNavegacionSimple() {
    Surface(
        color = DriverColors.Blanco,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("• Inicio", color = DriverColors.Vino, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Pasajeros", color = DriverColors.TextoGris, fontSize = 14.sp)
            Text("Reportes", color = DriverColors.TextoGris, fontSize = 14.sp)
        }
    }
}
