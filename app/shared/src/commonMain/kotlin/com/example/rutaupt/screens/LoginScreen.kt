package com.example.rutaupt.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import com.example.rutaupt.generated.resources.*

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val vinoUpt = UPTColors.Vino

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. IMAGEN GRANDE Y ADAPTADA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.imagentoro),
                    contentDescription = "Toro UPT",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. BIENVENIDA
            Text(
                text = buildAnnotatedString {
                    append("Bienvenido a ")
                    withStyle(style = SpanStyle(color = vinoUpt, fontWeight = FontWeight.Bold)) {
                        append("RutaUPT")
                    }
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Inicia sesión para continuar",
                color = Color.Gray,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. FORMULARIO
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Correo institucional") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
                    suffix = { Text("@upt.edu.mx", color = Color.Gray, fontSize = 12.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = vinoUpt
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
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
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = vinoUpt
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¿Olvidaste tu contraseña?",
                color = vinoUpt,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { /* Acción */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4. BOTÓN INICIAR SESIÓN
            Button(
                onClick = {
                    val user = email.lowercase().trim()
                    when {
                        user.contains("admin") -> onLoginSuccess("admin")
                        user.contains("chofer") -> onLoginSuccess("chofer")
                        else -> onLoginSuccess("estudiante")
                    }
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

            // 5. REGISTRO
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

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(40.dp))

            // 6. FOOTER CURVO
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
