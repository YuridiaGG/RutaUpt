package com.example.rutaupt.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.model.Chofer
import com.example.rutaupt.storage.ChoferRepository
import com.example.rutaupt.storage.ChoferLogger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioChoferScreen(
    choferElegido: Chofer? = null,
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf(choferElegido?.nombre ?: "") }
    var apellidos by remember { mutableStateOf(choferElegido?.apellidos ?: "") }
    var edad by remember { mutableStateOf(choferElegido?.edad ?: "") }
    var telefono by remember { mutableStateOf(choferElegido?.telefono ?: "") }
    var numeroUnidad by remember { mutableStateOf(choferElegido?.numeroUnidad ?: "") }
    var email by remember { mutableStateOf(choferElegido?.email ?: "") }
    var password by remember { mutableStateOf(choferElegido?.contrasena ?: "") }
    var confirmPassword by remember { mutableStateOf(choferElegido?.contrasena ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }

    val vinoUpt = UPTColors.Vino

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (choferElegido == null) "Agregar Chofer" else "Editar Chofer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.White,
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
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre(s)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it },
                    label = { Text("Edad") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = numeroUnidad,
                    onValueChange = { numeroUnidad = it },
                    label = { Text("N° Unidad") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Número de Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
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
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombre.isNotBlank() && numeroUnidad.isNotBlank()) {
                        if (choferElegido == null) {
                            ChoferLogger.agregarChofer(
                                Chofer(
                                    id = ChoferRepository.choferes.size + 1,
                                    nombre = nombre,
                                    apellidos = apellidos,
                                    edad = edad,
                                    telefono = telefono,
                                    numeroUnidad = numeroUnidad,
                                    email = email,
                                    contrasena = password
                                )
                            )
                        } else {
                            val index = ChoferRepository.choferes.indexOfFirst { it.id == choferElegido.id }
                            if (index != -1) {
                                ChoferRepository.choferes[index] = choferElegido.copy(
                                    nombre = nombre,
                                    apellidos = apellidos,
                                    edad = edad,
                                    telefono = telefono,
                                    numeroUnidad = numeroUnidad,
                                    email = email,
                                    contrasena = password
                                )
                            }
                        }
                        onVolver()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = vinoUpt)
            ) {
                Text(if (choferElegido == null) "Guardar Chofer" else "Actualizar Datos", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
