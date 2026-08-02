package com.example.rutaupt

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.rutaupt.screens.*
import com.example.rutaupt.model.Chofer
import com.example.rutaupt.storage.SessionManager
import com.example.rutaupt.api.Parada
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    
    // Carga la sesión persistida al iniciar la app para recordar el rol
    remember {
        SessionManager.cargarSesionPersistida()
        true
    }

    // Inicializa la pantalla basándose en el rol recuperado
    var pantallaActual by remember { 
        mutableStateOf(if (SessionManager.rolUsuario.isNotEmpty()) SessionManager.rolUsuario.lowercase() else "login") 
    }
    var choferSeleccionado by remember { mutableStateOf<Chofer?>(null) }
    var paradaSeleccionada by remember { mutableStateOf<Parada?>(null) }
    var adminMessage by remember { mutableStateOf<String?>(null) }

    // Función de navegación segura
    val navegarA: (String) -> Unit = { nuevaPantalla ->
        scope.launch {
            delay(50) 
            pantallaActual = nuevaPantalla
        }
    }

    // Manejo del botón atrás
    BackHandler(enabled = pantallaActual != "login") {
        when (pantallaActual) {
            "admin", "chofer", "estudiante" -> navegarA("login")
            "lista_choferes", "gestionar_horarios", "lista_estudiantes", 
            "ubicacion_micros", "gestionar_paradas", "perfil_admin", "reportes_unidades" -> navegarA("admin")
            "formulario_chofer" -> navegarA("lista_choferes")
            "perfil_chofer", "reportes_estudiantes_chofer" -> navegarA("chofer")
            "perfil_estudiante" -> navegarA("estudiante")
            "ruta" -> {
                val destino = if (SessionManager.rolUsuario.lowercase() == "chofer") "chofer" else "estudiante"
                navegarA(destino)
            }
            "registro_seleccion", "forgot_password" -> navegarA("login")
            "registro_estudiante", "registro_chofer" -> navegarA("registro_seleccion")
        }
    }

    MaterialTheme {
        when (pantallaActual) {
            "login" -> LoginScreen(
                onLoginSuccess = { navegarA(it) },
                onRegisterClick = { navegarA("registro_seleccion") },
                onForgotPasswordClick = { navegarA("forgot_password") }
            )
            "forgot_password" -> ForgotPasswordScreen(
                onBack = { navegarA("login") }
            )
            "registro_seleccion" -> RegisterSelectionScreen(
                onBack = { navegarA("login") },
                onSelectEstudiante = { navegarA("registro_estudiante") },
                onSelectChofer = { navegarA("registro_chofer") }
            )
            "registro_estudiante" -> RegisterEstudianteScreen(
                onBack = { navegarA("registro_seleccion") },
                onRegisterSuccess = { navegarA("estudiante") }
            )
            "registro_chofer" -> RegisterChoferScreen(
                onBack = { navegarA("registro_seleccion") },
                onRegisterSuccess = { navegarA("chofer") }
            )
            "admin" -> HomeAdminScreen(
                onGestionarChoferes = { navegarA("lista_choferes") },
                onGestionarHorarios = { navegarA("gestionar_horarios") },
                onGestionarEstudiantes = { navegarA("lista_estudiantes") },
                onUbicacionMicros = { navegarA("ubicacion_micros") },
                onGestionarParadas = { navegarA("gestionar_paradas") },
                onConfiguracion = { navegarA("perfil_admin") },
                onLogout = { 
                    SessionManager.cerrarSesion()
                    navegarA("login") 
                },
                onVerReportes = { navegarA("reportes_unidades") },
                mensajeConfirmacion = adminMessage,
                onMensajeMostrado = { adminMessage = null }
            )
            "lista_estudiantes" -> ListaEstudiantesScreen(
                onVolver = { navegarA("admin") }
            )
            "ubicacion_micros" -> UbicacionMicrosScreen(
                onVolver = { navegarA("admin") }
            )
            "gestionar_paradas" -> GestionarParadasScreen(
                onVolver = { navegarA("admin") }
            )
            "perfil_admin" -> PerfilAdminScreen(
                onVolver = { navegarA("admin") },
                onLogout = { 
                    SessionManager.cerrarSesion()
                    navegarA("login") 
                },
                onSaveSuccess = {
                    adminMessage = "¡Cambios guardados Admin!"
                    navegarA("admin")
                }
            )
            "reportes_unidades" -> ReportesUnidadesScreen(
                onVolver = { navegarA("admin") }
            )
            "lista_choferes" -> ListaChoferesScreen(
                onVolver = { navegarA("admin") },
                onAgregarChofer = { 
                    choferSeleccionado = null
                    navegarA("formulario_chofer")
                },
                onEditarChofer = { chofer ->
                    choferSeleccionado = chofer
                    navegarA("formulario_chofer")
                }
            )
            "formulario_chofer" -> FormularioChoferScreen(
                choferElegido = choferSeleccionado,
                onVolver = { navegarA("lista_choferes") }
            )
            "gestionar_horarios" -> GestionarHorariosScreen(
                onVolver = { navegarA("admin") }
            )
            "chofer" -> HomeChoferScreen(
                onLogout = { 
                    SessionManager.cerrarSesion()
                    navegarA("login") 
                },
                onConfiguracion = { navegarA("perfil_chofer") },
                onVerReportes = { navegarA("reportes_estudiantes_chofer") },
                onNavigateToRuta = { parada ->
                    paradaSeleccionada = parada
                    navegarA("ruta")
                }
            )
            "perfil_chofer" -> PerfilChoferScreen(
                onVolver = { navegarA("chofer") },
                onLogout = { 
                    SessionManager.cerrarSesion()
                    navegarA("login") 
                },
                onSaveSuccess = {
                    navegarA("chofer")
                }
            )
            "reportes_estudiantes_chofer" -> ReportesEstudiantesChoferScreen(
                onVolver = { navegarA("chofer") }
            )
            "estudiante" -> HomeEstudianteScreen(
                onNavigateToProfile = { navegarA("perfil_estudiante") },
                onNavigateToRuta = { parada ->
                    paradaSeleccionada = parada
                    navegarA("ruta")
                }
            )
            "perfil_estudiante" -> PerfilEstudianteScreen(
                onVolver = { navegarA("estudiante") },
                onLogout = { 
                    SessionManager.cerrarSesion()
                    navegarA("login") 
                },
                onSaveSuccess = { navegarA("estudiante") }
            )
            "ruta" -> RutaScreen(
                initialParada = paradaSeleccionada,
                onVolver = { 
                    paradaSeleccionada = null
                    val destino = if (SessionManager.rolUsuario.lowercase() == "chofer") "chofer" else "estudiante"
                    navegarA(destino)
                }
            )
        }
    }
}
