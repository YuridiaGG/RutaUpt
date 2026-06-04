package com.example.rutaupt.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*


//  Paleta de colores UPT (vino / guinda)

object UPTColors {
    val Vino        = Color(0xFF6B0F1A)   // Vino principal
    val VinoOscuro  = Color(0xFF4A0A12)   // Fondo degradado
    val VinoClaro   = Color(0xFF8B1A28)   // Hover / énfasis
    val Dorado      = Color(0xFFD4A843)   // Acento dorado UPT
    val DoradoClaro = Color(0xFFECC96A)   // Acento suave
    val Blanco      = Color(0xFFFDF6F0)   // Texto principal
    val BlancoSuave = Color(0xFFE8D5C4)   // Texto secundario
    val Superficie  = Color(0x26FFFFFF)   // Card translúcida
    val Error       = Color(0xFFFF6B6B)
}


//  Tipo de usuario

enum class TipoUsuario { ESTUDIANTE, CHOFER }


//  Pantalla principal de Login

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit = {}
) {
    var tipoSeleccionado by remember { mutableStateOf(TipoUsuario.ESTUDIANTE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(UPTColors.VinoOscuro, UPTColors.Vino, UPTColors.VinoClaro)
                )
            )
    ) {
        // Círculos decorativos de fondo
        DecorativeBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // Logo / encabezado
            LogoUPT()

            Spacer(Modifier.height(36.dp))

            // Selector de tipo de usuario
            SelectorTipoUsuario(
                seleccionado = tipoSeleccionado,
                onSeleccionar = { tipoSeleccionado = it }
            )

            Spacer(Modifier.height(28.dp))

            // Formulario animado según el tipo
            AnimatedContent(
                targetState = tipoSeleccionado,
                transitionSpec = {
                    val direction = if (targetState == TipoUsuario.CHOFER) 1 else -1
                    fadeIn(tween(300)) + slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { fullWidth -> fullWidth * direction }
                    ) togetherWith fadeOut(tween(200))
                },
                label = "login_form"
            ) { tipo ->
                when (tipo) {
                    TipoUsuario.ESTUDIANTE -> FormularioEstudiante { _, _ ->
                        onLoginSuccess("estudiante")
                    }
                    TipoUsuario.CHOFER     -> FormularioChofer { _, _ ->
                        onLoginSuccess("chofer")
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Universidad Politécnica de Tulancingo",
                color = UPTColors.BlancoSuave.copy(alpha = 0.6f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}


//  Fondo decorativo

@Composable
private fun DecorativeBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "offset"
    )

    Box(Modifier.fillMaxSize()) {
        // Círculo superior derecho
        Box(
            Modifier
                .size(260.dp)
                .offset(x = 120.dp, y = (-60 + offset).dp)
                .clip(CircleShape)
                .background(UPTColors.Dorado.copy(alpha = 0.08f))
                .align(Alignment.TopEnd)
        )
        // Círculo inferior izquierdo
        Box(
            Modifier
                .size(200.dp)
                .offset(x = (-80).dp, y = (60 - offset).dp)
                .clip(CircleShape)
                .background(UPTColors.DoradoClaro.copy(alpha = 0.06f))
                .align(Alignment.BottomStart)
        )
    }
}


//  Logo UPT

@Composable
private fun LogoUPT() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Icono del autobús con fondo dorado
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(UPTColors.Dorado, UPTColors.DoradoClaro.copy(alpha = 0.7f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsBus,
                contentDescription = "RutaUPT",
                tint = UPTColors.VinoOscuro,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "RutaUPT",
            color = UPTColors.Blanco,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Text(
            text = "Sistema de Transporte Universitario",
            color = UPTColors.DoradoClaro,
            fontSize = 13.sp,
            letterSpacing = 0.5.sp
        )
    }
}


//  Selector Estudiante / Chofer

@Composable
private fun SelectorTipoUsuario(
    seleccionado: TipoUsuario,
    onSeleccionar: (TipoUsuario) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(Color(0x33000000))
            .padding(4.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            TipoUsuario.values().forEach { tipo ->
                val activo = seleccionado == tipo
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (activo)
                                Brush.horizontalGradient(listOf(UPTColors.Dorado, UPTColors.DoradoClaro))
                            else
                                Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .clickable { onSeleccionar(tipo) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (tipo == TipoUsuario.ESTUDIANTE)
                                Icons.Filled.School else Icons.Filled.DriveEta,
                            contentDescription = null,
                            tint = if (activo) UPTColors.VinoOscuro else UPTColors.BlancoSuave,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (tipo == TipoUsuario.ESTUDIANTE) "Estudiante" else "Chofer",
                            color = if (activo) UPTColors.VinoOscuro else UPTColors.BlancoSuave,
                            fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}


//  Formulario Estudiante

@Composable
fun FormularioEstudiante(onLogin: (String, String) -> Unit) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verPass  by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }

    CardFormulario {
        Text(
            "Acceso Estudiante",
            color = UPTColors.Blanco,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Ingresa con tu correo institucional",
            color = UPTColors.BlancoSuave.copy(alpha = 0.7f),
            fontSize = 13.sp
        )

        Spacer(Modifier.height(24.dp))

        CampoTexto(
            valor = email,
            onValorChange = { email = it },
            label = "Correo institucional",
            placeholder = "matricula@upt.edu.mx",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )

        Spacer(Modifier.height(16.dp))

        CampoTexto(
            valor = password,
            onValorChange = { password = it },
            label = "Contraseña",
            placeholder = "••••••••",
            leadingIcon = Icons.Filled.Lock,
            esPassword = true,
            verPassword = verPass,
            onTogglePassword = { verPass = !verPass }
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = UPTColors.DoradoClaro,
            fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { /* navegar a recuperación */ }
        )

        Spacer(Modifier.height(28.dp))

        BotonLogin(
            texto = "Iniciar Sesión",
            cargando = cargando,
            onClick = {
                cargando = true
                onLogin(email, password)
            }
        )
    }
}


//  Formulario Chofer

@Composable
fun FormularioChofer(onLogin: (String, String) -> Unit) {
    var matricula by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var verPass   by remember { mutableStateOf(false) }
    var cargando  by remember { mutableStateOf(false) }

    CardFormulario {
        Text(
            "Acceso Chofer",
            color = UPTColors.Blanco,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Ingresa con tu número de empleado",
            color = UPTColors.BlancoSuave.copy(alpha = 0.7f),
            fontSize = 13.sp
        )

        Spacer(Modifier.height(24.dp))

        CampoTexto(
            valor = matricula,
            onValorChange = { matricula = it },
            label = "Número de empleado",
            placeholder = "EMP-0000",
            leadingIcon = Icons.Filled.Badge
        )

        Spacer(Modifier.height(16.dp))

        CampoTexto(
            valor = password,
            onValorChange = { password = it },
            label = "Contraseña",
            placeholder = "••••••••",
            leadingIcon = Icons.Filled.Lock,
            esPassword = true,
            verPassword = verPass,
            onTogglePassword = { verPass = !verPass }
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "¿Problemas de acceso? Contacta a TI",
            color = UPTColors.DoradoClaro,
            fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { /* soporte */ }
        )

        Spacer(Modifier.height(28.dp))

        BotonLogin(
            texto = "Ingresar",
            cargando = cargando,
            onClick = {
                cargando = true
                onLogin(matricula, password)
            }
        )
    }
}


//  Componentes reutilizables


@Composable
private fun CardFormulario(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = UPTColors.Superficie),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            content = content
        )
    }
}

@Composable
private fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    label: String,
    placeholder: String,
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
        label = { Text(label, color = UPTColors.BlancoSuave.copy(alpha = 0.7f)) },
        placeholder = { Text(placeholder, color = UPTColors.BlancoSuave.copy(alpha = 0.35f)) },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = UPTColors.DoradoClaro)
        },
        trailingIcon = if (esPassword && onTogglePassword != null) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (verPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = UPTColors.BlancoSuave.copy(alpha = 0.6f)
                    )
                }
            }
        } else null,
        visualTransformation = if (esPassword && !verPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esPassword) KeyboardType.Password else keyboardType),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = UPTColors.Blanco,
            unfocusedTextColor = UPTColors.Blanco,
            focusedBorderColor = UPTColors.Dorado,
            unfocusedBorderColor = UPTColors.BlancoSuave.copy(alpha = 0.3f),
            cursorColor = UPTColors.Dorado,
            focusedContainerColor = Color(0x1AFFFFFF),
            unfocusedContainerColor = Color(0x0DFFFFFF)
        ),
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
private fun BotonLogin(
    texto: String,
    cargando: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        enabled = !cargando
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(listOf(UPTColors.Dorado, UPTColors.DoradoClaro)),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    color = UPTColors.VinoOscuro,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = texto,
                    color = UPTColors.VinoOscuro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
