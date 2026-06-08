package com.example.rutaupt.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.*
import com.example.micros.R

// ─────────────────────────────────────────────────────────────────────────────
//  Colores UPT
// ─────────────────────────────────────────────────────────────────────────────
object UPTColors {
    val Vino        = Color(0xFF6B0F1A)
    val Blanco      = Color(0xFFFFFFFF)
    val Negro       = Color(0xFF111111)
    val GrisMedio   = Color(0xFF888888)
    val GrisClaro   = Color(0xFFDDDDDD)
    val FondoInput  = Color(0xFFFAFAFA)
    val FondoHero   = Color(0xFFF2F2F2)
    val Pasto       = Color(0xFFC8E6C9)
}

// ─────────────────────────────────────────────────────────────────────────────
//  Pantalla principal de Login
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UPTColors.Blanco)
            .verticalScroll(rememberScrollState())
    ) {
        SeccionHero()
        SeccionLogo()
        SeccionFormulario(onLogin = onLogin)
        SeccionFooter()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Hero: microbús con logo UPT encima
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SeccionHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(UPTColors.FondoHero)
    ) {
        // Pasto en la parte baja
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.BottomCenter)
                .background(UPTColors.Pasto)
        )

        // Microbús centrado
        MicrobusDibujo(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
        )

        // Círculo vino con logo UPT encima del bus
        Box(
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.TopCenter)
                .offset(y = 16.dp)
                .clip(CircleShape)
                .background(UPTColors.Vino),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.img_Logo_MDO),
                contentDescription = "Logo UPT",
                modifier = Modifier
                    .size(50.dp)
                    .padding(6.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Microbús dibujado con Boxes
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MicrobusDibujo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Cuerpo del bus
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(UPTColors.Vino)
        ) {
            // Franja blanca sutil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.Center)
                    .background(Color.White.copy(alpha = 0.18f))
            )

            // Letras UPT en el costado
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 28.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "UPT",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
            }

            // Ventanas (fila superior)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFAED6F1).copy(alpha = 0.85f))
                    )
                }
            }
        }

        // Ruedas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Rueda izquierda
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A))
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCCCCCC))
                        .align(Alignment.Center)
                )
            }
            // Rueda derecha
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A))
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCCCCCC))
                        .align(Alignment.Center)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Logo "RutaUPT" y tagline
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SeccionLogo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(UPTColors.Blanco)
            .padding(top = 14.dp, bottom = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = UPTColors.Vino,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = buildAnnotatedString {
                    append("Ruta")
                    withStyle(SpanStyle(color = UPTColors.Vino, fontWeight = FontWeight.Bold)) {
                        append("UPT")
                    }
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = UPTColors.Negro
            )
        }

        Text(
            text = "— Tu ruta, nuestra pasión —",
            fontSize = 11.sp,
            color = UPTColors.Vino,
            fontStyle = FontStyle.Italic,
            letterSpacing = 0.3.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Formulario de login
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SeccionFormulario(onLogin: (String, String) -> Unit) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verPass  by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(UPTColors.Blanco)
            .padding(horizontal = 22.dp)
    ) {
        Spacer(Modifier.height(6.dp))

        // Título bienvenida
        Text(
            text = buildAnnotatedString {
                append("Bienvenido a ")
                withStyle(SpanStyle(color = UPTColors.Vino)) {
                    append("RutaUPT")
                }
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = UPTColors.Negro
        )

        Text(
            text = "Inicia sesión para continuar",
            fontSize = 13.sp,
            color = UPTColors.GrisMedio,
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        // Campo correo
        CampoTexto(
            valor = email,
            onValorChange = { email = it },
            label = "Correo institucional",
            trailingHint = "@upt.edu.mx",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )

        Spacer(Modifier.height(12.dp))

        // Campo contraseña
        CampoTexto(
            valor = password,
            onValorChange = { password = it },
            label = "Contraseña",
            leadingIcon = Icons.Filled.Lock,
            esPassword = true,
            verPassword = verPass,
            onTogglePassword = { verPass = !verPass }
        )

        // Olvidé contraseña
        Text(
            text = "¿Olvidaste tu contraseña?",
            color = UPTColors.Vino,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp, bottom = 18.dp)
                .clickable { /* TODO: navegar a recuperación */ }
        )

        // Botón Iniciar Sesión
        Button(
            onClick = {
                cargando = true
                onLogin(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = UPTColors.Vino),
            enabled = !cargando
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    color = UPTColors.Blanco,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Iniciar Sesión",
                    color = UPTColors.Blanco,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = UPTColors.Blanco,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "¿No tienes cuenta? Contacta al administrador",
            fontSize = 12.sp,
            color = UPTColors.GrisMedio,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Campo de texto reutilizable
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    label: String,
    trailingHint: String? = null,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    esPassword: Boolean = false,
    verPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, color = Color(0xFFBBBBBB), fontSize = 13.sp)
                if (trailingHint != null) {
                    Spacer(Modifier.weight(1f))
                    Text(trailingHint, color = Color(0xFFCCCCCC), fontSize = 12.sp)
                }
            }
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Color(0xFFBBBBBB)
            )
        },
        trailingIcon = if (esPassword && onTogglePassword != null) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (verPassword) Icons.Filled.VisibilityOff
                        else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = Color(0xFFCCCCCC)
                    )
                }
            }
        } else null,
        visualTransformation = if (esPassword && !verPassword)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (esPassword) KeyboardType.Password else keyboardType
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor        = UPTColors.Negro,
            unfocusedTextColor      = UPTColors.Negro,
            focusedBorderColor      = UPTColors.Vino,
            unfocusedBorderColor    = UPTColors.GrisClaro,
            cursorColor             = UPTColors.Vino,
            focusedContainerColor   = UPTColors.FondoInput,
            unfocusedContainerColor = UPTColors.FondoInput
        )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Footer vino con logo UPT oficial
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SeccionFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(UPTColors.Vino)
            .padding(horizontal = 22.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.img_Logo_MDO),
            contentDescription = "Logo UPT",
            modifier = Modifier.size(38.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = "UPT",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Universidad Politécnica\nde Tulancingo",
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 9.sp,
                lineHeight = 13.sp
            )
        }
    }
}
