package com.example.rutaupt

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.rutaupt.screens.*
import com.example.rutaupt.model.Chofer
import com.example.rutaupt.storage.SessionManager

@Composable
fun App() {
    var pantallaActual by remember { 
        mutableStateOf(if (SessionManager.rolUsuario.isNotEmpty()) SessionManager.rolUsuario.lowercase() else "login") 
    }
    var choferSeleccionado by remember { mutableStateOf<Chofer?>(null) }
    var adminMessage by remember { mutableStateOf<String?>(null) }

    // Manejo del botón atrás del sistema
    BackHandler(enabled = pantallaActual != "login") {
        when (pantallaActual) {
            "admin", "chofer", "estudiante" -> {
                // Si está en el home de cualquier rol, vuelve al login (cerrar sesión)
                pantallaActual = "login"
            }
            "lista_choferes", "gestionar_horarios", "lista_estudiantes", 
            "ubicacion_micros", "gestionar_paradas", "perfil_admin", "reportes_unidades" -> {
                pantallaActual = "admin"
            }
            "formulario_chofer" -> {
                pantallaActual = "lista_choferes"
            }
            "perfil_chofer", "reportes_estudiantes_chofer" -> {
                pantallaActual = "chofer"
            }
            "perfil_estudiante", "ruta" -> {
                pantallaActual = "estudiante"
            }
            "registro_seleccion", "forgot_password" -> {
                pantallaActual = "login"
            }
            "registro_estudiante", "registro_chofer" -> {
                pantallaActual = "registro_seleccion"
            }
        }
    }

    MaterialTheme {
        when (pantallaActual) {
            "login" -> LoginScreen(
                onLoginSuccess = { pantallaActual = it },
                onRegisterClick = { pantallaActual = "registro_seleccion" },
                onForgotPasswordClick = { pantallaActual = "forgot_password" }
            )
            "forgot_password" -> ForgotPasswordScreen(
                onBack = { pantallaActual = "login" }
            )
            "registro_seleccion" -> RegisterSelectionScreen(
                onBack = { pantallaActual = "login" },
                onSelectEstudiante = { pantallaActual = "registro_estudiante" },
                onSelectChofer = { pantallaActual = "registro_chofer" }
            )
            "registro_estudiante" -> RegisterEstudianteScreen(
                onBack = { pantallaActual = "registro_seleccion" },
                onRegisterSuccess = { 
                    pantallaActual = "estudiante" 
                }
            )
            "registro_chofer" -> RegisterChoferScreen(
                onBack = { pantallaActual = "registro_seleccion" },
                onRegisterSuccess = { 
                    pantallaActual = "chofer" 
                }
            )
            "admin" -> HomeAdminScreen(
                onGestionarChoferes = { pantallaActual = "lista_choferes" },
                onGestionarHorarios = { pantallaActual = "gestionar_horarios" },
                onGestionarEstudiantes = { pantallaActual = "lista_estudiantes" },
                onUbicacionMicros = { pantallaActual = "ubicacion_micros" },
                onGestionarParadas = { pantallaActual = "gestionar_paradas" },
                onConfiguracion = { pantallaActual = "perfil_admin" },
                onLogout = { pantallaActual = "login" },
                onVerReportes = { pantallaActual = "reportes_unidades" },
                mensajeConfirmacion = adminMessage,
                onMensajeMostrado = { adminMessage = null }
            )
            "lista_estudiantes" -> ListaEstudiantesScreen(
                onVolver = { pantallaActual = "admin" }
            )
            "ubicacion_micros" -> UbicacionMicrosScreen(
                onVolver = { pantallaActual = "admin" }
            )
            "gestionar_paradas" -> GestionarParadasScreen(
                onVolver = { pantallaActual = "admin" }
            )
            "perfil_admin" -> PerfilAdminScreen(
                onVolver = { pantallaActual = "admin" },
                onLogout = { pantallaActual = "login" },
                onSaveSuccess = {
                    adminMessage = "¡Cambios guardados Admin!"
                    pantallaActual = "admin"
                }
            )
            "reportes_unidades" -> ReportesUnidadesScreen(
                onVolver = { pantallaActual = "admin" }
            )
            "lista_choferes" -> ListaChoferesScreen(
                onVolver = { pantallaActual = "admin" },
                onAgregarChofer = { 
                    choferSeleccionado = null
                    pantallaActual = "formulario_chofer" 
                },
                onEditarChofer = { chofer: Chofer ->
                    choferSeleccionado = chofer
                    pantallaActual = "formulario_chofer"
                }
            )
            "formulario_chofer" -> FormularioChoferScreen(
                choferElegido = choferSeleccionado,
                onVolver = { pantallaActual = "lista_choferes" }
            )
            "gestionar_horarios" -> GestionarHorariosScreen(
                onVolver = { pantallaActual = "admin" }
            )
            "chofer" -> HomeChoferScreen(
                onLogout = { pantallaActual = "login" },
                onConfiguracion = { pantallaActual = "perfil_chofer" },
                onVerReportes = { pantallaActual = "reportes_estudiantes_chofer" }
            )
            "perfil_chofer" -> PerfilChoferScreen(
                onVolver = { pantallaActual = "chofer" },
                onLogout = { pantallaActual = "login" },
                onSaveSuccess = {
                    pantallaActual = "chofer"
                }
            )
            "reportes_estudiantes_chofer" -> ReportesEstudiantesChoferScreen(
                onVolver = { pantallaActual = "chofer" }
            )
            "estudiante" -> HomeEstudianteScreen(
                onNavigateToProfile = { pantallaActual = "perfil_estudiante" },
                onNavigateToRuta = { pantallaActual = "ruta" }
            )
            "perfil_estudiante" -> PerfilEstudianteScreen(
                onVolver = { pantallaActual = "estudiante" },
                onLogout = { pantallaActual = "login" },
                onSaveSuccess = { pantallaActual = "estudiante" }
            )
            "ruta" -> RutaScreen(
                onVolver = { pantallaActual = "estudiante" }
            )
        }
    }
}
