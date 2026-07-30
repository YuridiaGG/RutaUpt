package com.example.rutaupt.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.getPlatform
import com.example.rutaupt.storage.SessionManager
import com.example.rutaupt.api.AuthApiService
import com.example.rutaupt.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterChoferScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var numeroUnidad by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val vinoUpt = UPTColors.Vino
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val authService = remember { AuthApiService() }

    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return email.matches(Regex(emailRegex))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro Chofer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
            Text(
                text = "Datos del Chofer",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = vinoUpt
            )

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
                    onValueChange = { if(it.length <= 3 && it.all { char -> char.isDigit() }) edad = it },
                    label = { Text("Edad") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = numeroUnidad,
                    onValueChange = { numeroUnidad = it },
                    label = { Text("N° Unidad") },
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )
            }

            OutlinedTextField(
                value = telefono,
                onValueChange = { if(it.length <= 10 && it.all { char -> char.isDigit() }) telefono = it },
                label = { Text("Número de Teléfono (10 dígitos)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (mín. 8 caracteres)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombre.isBlank() || email.isBlank() || password.isBlank() || numeroUnidad.isBlank() || telefono.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Complete todos los campos obligatorios") }
                        return@Button
                    }
                    if (!isEmailValid(email.trim())) {
                        scope.launch { snackbarHostState.showSnackbar("Ingrese un correo electrónico válido") }
                        return@Button
                    }
                    if (telefono.length != 10) {
                        scope.launch { snackbarHostState.showSnackbar("El teléfono debe tener 10 dígitos") }
                        return@Button
                    }
                    if (password.length < 8) {
                        scope.launch { snackbarHostState.showSnackbar("La contraseña debe tener al menos 8 caracteres") }
                        return@Button
                    }
                    if (password != confirmPassword) {
                        scope.launch { snackbarHostState.showSnackbar("Las contraseñas no coinciden") }
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        val user = User(
                            nombre = nombre.trim(),
                            apellidos = apellidos.trim(),
                            email = email.lowercase().trim(),
                            password = password,
                            rol = "chofer",
                            numeroUnidad = numeroUnidad.trim().uppercase(),
                            edad = edad.trim(),
                            telefono = telefono.trim()
                        )
                        val response = authService.register(user)
                        isLoading = false
                        if (response.success) {
                            SessionManager.iniciarSesion(
                                nombre = nombre,
                                apellidos = apellidos,
                                email = email,
                                password = password,
                                rol = "chofer",
                                unidad = numeroUnidad,
                                telefono = telefono,
                                edad = edad
                            )
                            getPlatform().showNotification("RutaUPT", "¡Bienvenido Chofer $nombre!")
                            onRegisterSuccess()
                        } else {
                            snackbarHostState.showSnackbar(response.message)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = vinoUpt)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Registrar Chofer", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
