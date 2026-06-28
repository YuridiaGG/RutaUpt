package com.example.rutaupt.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutaupt.generated.resources.Res
import com.example.rutaupt.generated.resources.imagentoro
import com.example.rutaupt.getPlatform
import com.example.rutaupt.api.AuthApiService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val vinoUpt = UPTColors.Vino
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val authService = remember { AuthApiService() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperación", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isLoading) {
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
                modifier = Modifier.fillMaxWidth(),
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
                    "Recuperación de contraseña",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = vinoUpt,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    "Ingresa tu correo institucional. Te enviaremos tus credenciales de acceso registradas en el sistema.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
                    placeholder = { Text("usuario@upt.edu.mx") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = vinoUpt) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = vinoUpt,
                        focusedLabelColor = vinoUpt,
                        cursorColor = vinoUpt
                    )
                )

                OutlinedTextField(
                    value = confirmEmail,
                    onValueChange = { confirmEmail = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirmar correo") },
                    placeholder = { Text("Repite tu correo") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = vinoUpt) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = vinoUpt,
                        focusedLabelColor = vinoUpt,
                        cursorColor = vinoUpt
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || confirmEmail.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Por favor complete todos los campos") }
                            return@Button
                        }
                        if (email.trim().lowercase() != confirmEmail.trim().lowercase()) {
                            scope.launch { snackbarHostState.showSnackbar("Los correos electrónicos no coinciden") }
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            val response = authService.recoverPassword(email.trim().lowercase())
                            isLoading = false
                            if (response.success) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Credenciales enviadas a tu correo")
                                    onBack()
                                }
                            } else {
                                snackbarHostState.showSnackbar(response.message)
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
                        Text("Enviar Contraseña", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
