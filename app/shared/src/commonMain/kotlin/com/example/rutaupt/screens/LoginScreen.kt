package com.example.rutaupt.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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

//  Tipo de usuario
enum class TipoUsuario { ESTUDIANTE, CHOFER }

//  Pantalla principal de Login
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit
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
                    TipoUsuario.ESTUDIANTE -> FormularioEstudiante(onLoginSuccess)
                    TipoUsuario.CHOFER     -> FormularioChofer(onLoginSuccess)
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
        // Box decorativo sin icono
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
            // Icono removido
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
            TipoUsuario.entries.forEach { tipo ->
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


//  Formulario Estudiante

@Composable
fun FormularioEstudiante(onLoginSuccess: (String) -> Unit) {
    var usuario  by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verPass  by remember { mutableStateOf(false) }

    CardFormulario {
        Text(
            "Acceso Estudiante",
            color = UPTColors.Blanco,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Ingresa con tus credenciales",
            color = UPTColors.BlancoSuave.copy(alpha = 0.7f),
            fontSize = 13.sp
        )

        Spacer(Modifier.height(24.dp))

        CampoTexto(
            valor = usuario,
            onValorChange = { usuario = it },
            label = "Usuario",
            placeholder = "admin, estudiante, chofer",
            keyboardType = KeyboardType.Text
        )

        Spacer(Modifier.height(16.dp))

        CampoTexto(
            valor = password,
            onValorChange = { password = it },
            label = "Contraseña",
            placeholder = "••••••••",
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
                .clickable { }
        )

        Spacer(Modifier.height(28.dp))

        BotonLogin(
            texto = "Iniciar Sesión",
            onClick = {
                if (password == "123") {
                    val rol = usuario.lowercase().trim()
                    if (rol == "admin" || rol == "chofer" || rol == "estudiante") {
                        onLoginSuccess(rol)
                    } else {
                        onLoginSuccess("estudiante")
                    }
                }
            }
        )
    }
}


//  Formulario Chofer

@Composable
fun FormularioChofer(onLoginSuccess: (String) -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var verPass   by remember { mutableStateOf(false) }

    CardFormulario {
        Text(
            "Acceso Chofer",
            color = UPTColors.Blanco,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Ingresa con tus credenciales",
            color = UPTColors.BlancoSuave.copy(alpha = 0.7f),
            fontSize = 13.sp
        )

        Spacer(Modifier.height(24.dp))

        CampoTexto(
            valor = usuario,
            onValorChange = { usuario = it },
            label = "Usuario",
            placeholder = "admin, estudiante, chofer"
        )

        Spacer(Modifier.height(16.dp))

        CampoTexto(
            valor = password,
            onValorChange = { password = it },
            label = "Contraseña",
            placeholder = "••••••••",
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
                .clickable { }
        )

        Spacer(Modifier.height(28.dp))

        BotonLogin(
            texto = "Ingresar",
            onClick = {
                if (password == "123") {
                    val rol = usuario.lowercase().trim()
                    if (rol == "admin" || rol == "chofer" || rol == "estudiante") {
                        onLoginSuccess(rol)
                    } else {
                        onLoginSuccess("chofer")
                    }
                }
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
        trailingIcon = if (esPassword && onTogglePassword != null) {
            {
                IconButton(onClick = onTogglePassword) {
                    Text(
                        text = if (verPassword) "Ocultar" else "Ver",
                        color = UPTColors.BlancoSuave.copy(alpha = 0.6f),
                        fontSize = 12.sp
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
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
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
