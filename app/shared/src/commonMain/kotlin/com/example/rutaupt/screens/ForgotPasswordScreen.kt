package com.example.rutaupt.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.generated.resources.Res
import com.example.rutaupt.generated.resources.imagentoro
import com.example.rutaupt.api.AuthApiService
import com.example.rutaupt.storage.SessionManager
import com.example.rutaupt.model.User
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1: Email, 2: Code
    var isLoading by remember { mutableStateOf(false) }
    
    val vinoUpt = UPTColors.Vino
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val authService = remember { AuthApiService() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (step == 1) "Recuperación" else "Verificar Código", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { if (step == 2) step = 1 else onBack() }, enabled = !isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = vinoUpt,
                    navigationIconContentColor = vinoUpt
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.imagentoro),
                contentDescription = "Toro UPT",
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = if (step == 1) "Recuperación de cuenta" else "Introduce el código",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = vinoUpt,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = if (step == 1) 
                        "Ingresa tu correo institucional. Te enviaremos un código de 6 dígitos para acceder directamente."
                    else 
                        "Hemos enviado un código a $email. Por favor, introdúcelo para entrar a tu perfil.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )

                if (step == 1) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Correo electrónico") },
                        placeholder = { Text("usuario@upt.edu.mx") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = vinoUpt) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                } else {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { inputString: String -> 
                            if (inputString.length <= 6) code = inputString 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Código de 6 dígitos") },
                        placeholder = { Text("000000") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = vinoUpt) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(textAlign = TextAlign.Center)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (step == 1) {
                            if (email.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Ingresa tu correo") }
                                return@Button
                            }
                            isLoading = true
                            scope.launch {
                                val response = authService.recoverPassword(email.trim().lowercase())
                                isLoading = false
                                if (response.success) {
                                    step = 2
                                    snackbarHostState.showSnackbar("Código enviado a tu correo")
                                } else {
                                    snackbarHostState.showSnackbar(response.message)
                                }
                            }
                        } else {
                            if (code.length != 6) {
                                scope.launch { snackbarHostState.showSnackbar("El código debe ser de 6 dígitos") }
                                return@Button
                            }
                            isLoading = true
                            scope.launch {
                                val response = authService.verifyCode(email.trim().lowercase(), code)
                                isLoading = false
                                if (response.success) {
                                    val u = response.user
                                    if (u != null) {
                                        // Iniciar sesión con los datos recibidos
                                        SessionManager.iniciarSesion(
                                            nombre = u.nombre,
                                            apellidos = u.apellidos,
                                            email = u.email,
                                            rol = u.rol,
                                            unidad = u.numeroUnidad ?: "",
                                            telefono = u.telefono ?: "",
                                            edad = u.edad ?: "",
                                            horario = u.horario ?: ""
                                        )
                                        val rolString = u.rol.toString().lowercase()
                                        onLoginSuccess(rolString)
                                    } else {
                                        snackbarHostState.showSnackbar("Error: Datos de usuario no recibidos")
                                    }
                                } else {
                                    snackbarHostState.showSnackbar(response.message)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = vinoUpt)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (step == 1) "Enviar Código" else "Verificar y Entrar", fontWeight = FontWeight.Bold)
                    }
                }

                if (step == 2) {
                    TextButton(
                        onClick = { step = 1 },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text("¿No recibiste el código? Cambiar correo", color = vinoUpt)
                    }
                }
            }
        }
    }
}
