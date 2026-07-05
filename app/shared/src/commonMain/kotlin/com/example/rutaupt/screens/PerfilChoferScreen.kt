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
import com.example.rutaupt.api.RutaApiService
import com.example.rutaupt.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilChoferScreen(
    onVolver: () -> Unit,
    onLogout: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val vinoUpt = UPTColors.Vino
    val scope = rememberCoroutineScope()
    val apiService = remember { RutaApiService() }
    
    var nombre by remember { mutableStateOf(SessionManager.nombreUsuario) }
    var apellidos by remember { mutableStateOf(SessionManager.apellidosUsuario) }
    var email by remember { mutableStateOf(SessionManager.emailUsuario) }
    var edad by remember { mutableStateOf(SessionManager.edadUsuario) }
    var telefono by remember { mutableStateOf(SessionManager.telefonoUsuario) }
    var numeroUnidad by remember { mutableStateOf(SessionManager.numeroUnidad) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil Chofer", fontWeight = FontWeight.Bold) },
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
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(vinoUpt.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person, 
                    contentDescription = null, 
                    modifier = Modifier.size(50.dp), 
                    tint = vinoUpt
                )
            }
            
            Text(
                text = "Datos del Chofer",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = vinoUpt
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre(s)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it },
                    label = { Text("Edad") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = numeroUnidad,
                    onValueChange = { numeroUnidad = it },
                    label = { Text("N° Unidad") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )
            }

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Número de Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                shape = RoundedCornerShape(12.dp),
                enabled = false
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Nueva Contraseña (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoading = true
                    scope.launch {
                        val updatedUser = User(
                            nombre = nombre,
                            apellidos = apellidos,
                            email = email,
                            password = if (password.isNotBlank()) password else null,
                            rol = SessionManager.rolUsuario,
                            numeroUnidad = numeroUnidad,
                            edad = edad,
                            telefono = telefono
                        )
                        val success = apiService.actualizarUsuario(updatedUser)
                        isLoading = false
                        if (success) {
                            SessionManager.nombreUsuario = nombre
                            SessionManager.apellidosUsuario = apellidos
                            SessionManager.edadUsuario = edad
                            SessionManager.telefonoUsuario = telefono
                            SessionManager.numeroUnidad = numeroUnidad
                            getPlatform().showNotification("RutaUPT", "¡Perfil actualizado correctamente!")
                            onSaveSuccess()
                        } else {
                            getPlatform().showNotification("RutaUPT", "Error al actualizar perfil")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = vinoUpt),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar Cambios", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedButton(
                onClick = {
                    SessionManager.cerrarSesion()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                enabled = !isLoading
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }
        }
    }
}
