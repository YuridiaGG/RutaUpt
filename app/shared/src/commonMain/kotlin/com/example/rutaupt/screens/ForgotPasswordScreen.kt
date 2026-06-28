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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    val vinoUpt = UPTColors.Vino
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperación", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.imagentoro),
                    contentDescription = "Toro UPT",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

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
                    "Ingresa el correo electrónico institucional que utilizaste al registrarte. Te enviaremos tus credenciales de acceso.",
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
                            scope.launch {
                                snackbarHostState.showSnackbar("Por favor complete todos los campos")
                            }
                        } else if (email != confirmEmail) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Los correos electrónicos no coinciden")
                            }
                        } else {
                            // Simulamos el envío del correo con el formato solicitado
                            val mensajeCompleto = """
                                Asunto: Recuperación de contraseña – RutaUPT

                                Estimado(a) usuario(a) de RutaUPT:

                                Reciba un cordial saludo.

                                Hemos recibido y procesado correctamente su solicitud de recuperación de contraseña. A continuación, se muestran los datos de acceso asociados a su cuenta:

                                Correo electrónico: $email
                                Contraseña: ********

                                Por motivos de seguridad, le recomendamos cambiar su contraseña después de iniciar sesión. Puede hacerlo fácilmente desde la sección "Mi Perfil" dentro de la aplicación RutaUPT, donde encontrará la opción "Cambiar contraseña" para actualizar sus credenciales de forma segura.

                                Si usted no realizó esta solicitud de recuperación, le recomendamos comunicarse con el equipo de soporte de RutaUPT lo antes posible para proteger su cuenta.

                                Agradecemos su confianza en RutaUPT.

                                Atentamente,

                                Equipo de Desarrollo
                                RutaUPT
                                Universidad Politécnica de Tulancingo
                            """.trimIndent()
                            
                            // Mostramos notificación y log
                            getPlatform().showNotification("RutaUPT", "Correo de recuperación enviado a $email")
                            println(mensajeCompleto)
                            
                            scope.launch {
                                snackbarHostState.showSnackbar("Correo enviado con éxito")
                                onBack() // Regresamos al login
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = vinoUpt)
                ) {
                    Text("Enviar Contraseña", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
