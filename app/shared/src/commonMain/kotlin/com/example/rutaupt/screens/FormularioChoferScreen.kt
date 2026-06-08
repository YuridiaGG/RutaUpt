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
<<<<<<< HEAD
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
=======
    choferEditar: Chofer? = null,
    onVolver: () -> Unit
) {
    val modoEdicion = choferEditar != null

    var nombre by remember { mutableStateOf(choferEditar?.nombre ?: "") }
    var apellido by remember { mutableStateOf(choferEditar?.apellido ?: "") }
    var unidad by remember { mutableStateOf(choferEditar?.numeroUnidad ?: "") }
    var telefono by remember { mutableStateOf(choferEditar?.telefono ?: "") }
    var horaSalida by remember { mutableStateOf(choferEditar?.horaSalida ?: "") }
    var horaLlegada by remember { mutableStateOf(choferEditar?.horaLlegada ?: "") }
>>>>>>> 02d9e061b74cb7907381fed3dc65f49648b87484

    Scaffold(
        topBar = {
            TopAppBar(
<<<<<<< HEAD
                title = { Text(if (choferElegido == null) "Agregar Chofer" else "Editar Chofer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
=======
                title = { Text(if (modoEdicion) "Editar Chofer" else "Nuevo Chofer") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
>>>>>>> 02d9e061b74cb7907381fed3dc65f49648b87484
                    }
                }
            )
        }
<<<<<<< HEAD
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
=======
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
>>>>>>> 02d9e061b74cb7907381fed3dc65f49648b87484
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
<<<<<<< HEAD

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

=======
            OutlinedTextField(
                value = unidad,
                onValueChange = { unidad = it },
                label = { Text("Número de Unidad") },
                modifier = Modifier.fillMaxWidth()
            )
>>>>>>> 02d9e061b74cb7907381fed3dc65f49648b87484
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Número de Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = horaSalida,
                    onValueChange = { horaSalida = it },
                    label = { Text("Salida") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = horaLlegada,
                    onValueChange = { horaLlegada = it },
                    label = { Text("Llegada") },
                    modifier = Modifier.weight(1f)
                )
            }

<<<<<<< HEAD
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

=======
>>>>>>> 02d9e061b74cb7907381fed3dc65f49648b87484
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
<<<<<<< HEAD
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
                            val index = ChoferRepository.choferes.indexOf(choferElegido)
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
=======
                    if (modoEdicion) {
                        val index = ChoferRepository.choferes.indexOfFirst { it.id == choferEditar.id }
                        if (index != -1) {
                            ChoferRepository.choferes[index] = choferEditar.copy(
                                nombre = nombre,
                                apellido = apellido,
                                numeroUnidad = unidad,
                                telefono = telefono,
                                horaSalida = horaSalida,
                                horaLlegada = horaLlegada
                            )
                        }
                    } else {
                        val nuevoId = if (ChoferRepository.choferes.isEmpty()) 1 else ChoferRepository.choferes.maxOf { it.id } + 1
                        ChoferRepository.choferes.add(
                            Chofer(
                                id = nuevoId,
                                nombre = nombre,
                                apellido = apellido,
                                numeroUnidad = unidad,
                                telefono = telefono,
                                horaSalida = horaSalida,
                                horaLlegada = horaLlegada
                            )
                        )
                    }
                    onVolver()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (modoEdicion) "Actualizar Datos" else "Guardar Chofer")
>>>>>>> 02d9e061b74cb7907381fed3dc65f49648b87484
            }
        }
    }
}
