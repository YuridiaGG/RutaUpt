package com.example.rutaupt.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import com.example.rutaupt.generated.resources.*
import com.example.rutaupt.getPlatform
import com.example.rutaupt.storage.SessionManager

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit // Nuevo parámetro
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val vinoUpt = UPTColors.Vino

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) 
            ) {
                Image(
                    painter = painterResource(Res.drawable.imagentoro),
                    contentDescription = "Toro UPT",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = buildAnnotatedString {
                    append("Bienvenido a ")
                    withStyle(style = SpanStyle(color = vinoUpt, fontWeight = FontWeight.Bold)) {
                        append("RutaUPT")
                    }
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Inicia sesión para continuar",
                color = Color.Gray,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Usuario / Correo") },
                    placeholder = { Text("Ej: admin") },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = vinoUpt) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = vinoUpt,
                        focusedLabelColor = vinoUpt,
                        cursorColor = vinoUpt
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = vinoUpt) },
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = vinoUpt,
                        focusedLabelColor = vinoUpt,
                        cursorColor = vinoUpt
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¿Olvidaste tu contraseña?",
                color = vinoUpt,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onForgotPasswordClick() } // Acción actualizada
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val user = email.lowercase().trim()
                    val pass = password.trim()
                    
                    val target = when {
                        user == "admin" && pass == "123" -> {
                            SessionManager.iniciarSesion("Administrador", email, "admin")
                            "admin"
                        }
                        user == "chofer" && pass == "123" -> {
                            SessionManager.iniciarSesion("Chofer Demo", email, "chofer")
                            "chofer"
                        }
                        user == "estudiante" && pass == "123" -> {
                            SessionManager.iniciarSesion("Estudiante Demo", email, "estudiante")
                            "estudiante"
                        }
                        user.contains("admin") -> {
                            SessionManager.iniciarSesion("Admin " + user.substringBefore("@"), email, "admin")
                            "admin"
                        }
                        user.contains("chofer") -> {
                            SessionManager.iniciarSesion("Chofer " + user.substringBefore("@"), email, "chofer")
                            "chofer"
                        }
                        else -> {
                            SessionManager.iniciarSesion(user.substringBefore("@").replaceFirstChar { it.uppercase() }, email, "estudiante")
                            "estudiante"
                        }
                    }
                    
                    getPlatform().showNotification("RutaUPT", "¡Bienvenido/a ${SessionManager.nombreUsuario}!")
                    onLoginSuccess(target)
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp).height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = vinoUpt)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Iniciar Sesión",
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = buildAnnotatedString {
                    append("¿Nuevo en RutaUPT? ")
                    withStyle(style = SpanStyle(color = vinoUpt, fontWeight = FontWeight.Bold)) {
                        append("Crea una cuenta")
                    }
                },
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.clickable { onRegisterClick() }
            )

            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(vinoUpt, RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
                    .padding(vertical = 25.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(45.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("UPT", color = vinoUpt, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Column {
                        Text("UPT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            text = "Universidad Politécnica de Tulancingo",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
