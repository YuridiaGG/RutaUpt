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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.model.Chofer
import com.example.rutaupt.storage.ChoferRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioChoferScreen(
    choferElegido: Chofer? = null,
    onVolver: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var nombre by remember { mutableStateOf(choferElegido?.nombre ?: "") }
    var apellidos by remember { mutableStateOf(choferElegido?.apellidos ?: "") }
    var edad by remember { mutableStateOf(choferElegido?.edad ?: "") }
    var telefono by remember { mutableStateOf(choferElegido?.telefono ?: "") }
    var numeroUnidad by remember { mutableStateOf(choferElegido?.numeroUnidad ?: "") }
    var email by remember { mutableStateOf(choferElegido?.email ?: "") }
    var password by remember { mutableStateOf("") } // Password empty by default for edit
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val vinoUpt = UPTColors.Vino

    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return email.matches(Regex(emailRegex))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (choferElegido == null) "Agregar Chofer" else "Editar Chofer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver, enabled = !isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.White,
                    titleContentColor = vinoUpt
                )
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
                    onValueChange = { if(it.length <= 3 && it.all { c -> c.isDigit() }) edad = it },
                    label = { Text("Edad") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                onValueChange = { if(it.length <= 10 && it.all { c -> c.isDigit() }) telefono = it },
                label = { Text("Número de Teléfono (10 dígitos)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(if(choferElegido == null) "Contraseña (mín. 8 caracteres)" else "Nueva Contraseña (opcional)") },
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

            if (password.isNotBlank()) {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombre.isBlank() || email.isBlank() || (choferElegido == null && password.isBlank()) || telefono.isBlank() || numeroUnidad.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Complete todos los campos obligatorios") }
                        return@Button
                    }
                    if (!isEmailValid(email.trim())) {
                        scope.launch { snackbarHostState.showSnackbar("Ingrese un correo válido") }
                        return@Button
                    }
                    if (telefono.length != 10) {
                        scope.launch { snackbarHostState.showSnackbar("El teléfono debe tener 10 dígitos") }
                        return@Button
                    }
                    if (password.isNotBlank()) {
                        if (password.length < 8) {
                            scope.launch { snackbarHostState.showSnackbar("La contraseña debe tener al menos 8 caracteres") }
                            return@Button
                        }
                        if (password != confirmPassword) {
                            scope.launch { snackbarHostState.showSnackbar("Las contraseñas no coinciden") }
                            return@Button
                        }
                    }

                    isLoading = true
                    scope.launch {
                        val chofer = Chofer(
                            id = choferElegido?.id ?: 0,
                            nombre = nombre.trim(),
                            apellidos = apellidos.trim(),
                            edad = edad.trim(),
                            telefono = telefono.trim(),
                            numeroUnidad = numeroUnidad.trim().uppercase(),
                            email = email.lowercase().trim(),
                            contrasena = password
                        )
                        
                        val exito = if (choferElegido == null) {
                            ChoferRepository.registrarChofer(chofer)
                        } else {
                            ChoferRepository.actualizarChofer(chofer)
                        }
                        
                        isLoading = false
                        if (exito) {
                            onVolver()
                        } else {
                            snackbarHostState.showSnackbar("Error al procesar los datos")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = vinoUpt),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (choferElegido == null) "Guardar Chofer" else "Actualizar Datos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
