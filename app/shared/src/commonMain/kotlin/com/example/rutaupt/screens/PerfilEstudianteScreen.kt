package com.example.rutaupt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.getPlatform
import com.example.rutaupt.storage.SessionManager
import com.example.rutaupt.LocationBridge
import com.example.rutaupt.LocationUtils
import com.example.rutaupt.api.RutaApiService
import com.example.rutaupt.api.UbicacionVehiculo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilEstudianteScreen(
    onVolver: () -> Unit,
    onLogout: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    val apiService = remember { RutaApiService() }
    
    var nombre by remember { mutableStateOf(SessionManager.nombreUsuario) }
    var email by remember { mutableStateOf(SessionManager.emailUsuario) }
    var password by remember { mutableStateOf(SessionManager.passwordUsuario) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Estados para detección de micro cercana
    var currentUserLat by remember { mutableStateOf<Double?>(null) }
    var currentUserLon by remember { mutableStateOf<Double?>(null) }
    var nearbyUnit by remember { mutableStateOf<UbicacionVehiculo?>(null) }

    // Iniciar rastreo de ubicación
    DisposableEffect(Unit) {
        val hasPermission = LocationBridge.hasPermission?.invoke() ?: false
        if (hasPermission) {
            LocationBridge.getCurrentLocation?.invoke { lat, lon -> 
                currentUserLat = lat
                currentUserLon = lon 
            }
            LocationBridge.onLocationUpdate = { lat, lon -> 
                currentUserLat = lat
                currentUserLon = lon 
            }
            LocationBridge.startLocationUpdates?.invoke()
        }
        onDispose { LocationBridge.stopLocationUpdates?.invoke() }
    }

    // Lógica de detección de micro cercana
    LaunchedEffect(currentUserLat, currentUserLon) {
        if (currentUserLat != null && currentUserLon != null) {
            while(true) {
                try {
                    val ubicaciones = apiService.obtenerUbicaciones()
                    nearbyUnit = ubicaciones.find { unit ->
                        val dist = LocationUtils.calcularDistanciaMetros(
                            currentUserLat!!, currentUserLon!!,
                            unit.latitud, unit.longitud
                        )
                        dist < 1000 
                    }
                } catch (e: Exception) {}
                delay(12000)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // --- Card de Micro Cercana (Usa el componente centralizado en CommonComponents.kt) ---
            if (nearbyUnit != null && currentUserLat != null && currentUserLon != null) {
                val dist = LocationUtils.calcularDistanciaMetros(
                    currentUserLat!!, currentUserLon!!,
                    nearbyUnit!!.latitud, nearbyUnit!!.longitud
                )
                NearbyUnitCard(nearbyUnit!!.unidad, dist, vinoUpt)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(vinoUpt.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.School, null, modifier = Modifier.size(50.dp), tint = vinoUpt)
            }
            
            Text("Información Personal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = vinoUpt)

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, null) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Institucional") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                shape = RoundedCornerShape(12.dp),
                enabled = false
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    SessionManager.nombreUsuario = nombre
                    SessionManager.passwordUsuario = password
                    getPlatform().showNotification("RutaUPT", "¡Perfil actualizado!")
                    onSaveSuccess()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = vinoUpt)
            ) {
                Text("Guardar Cambios", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    SessionManager.cerrarSesion()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}
