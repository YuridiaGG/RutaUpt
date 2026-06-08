package com.example.rutaupt

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.rutaupt.screens.*
import com.example.rutaupt.model.Chofer

@Composable
fun App() {
    var pantallaActual by remember { mutableStateOf("login") }
    var choferSeleccionado by remember { mutableStateOf<Chofer?>(null) }

    MaterialTheme {
        when (pantallaActual) {
            "login" -> LoginScreen(
                onLoginSuccess = { pantallaActual = it },
                onRegisterClick = { pantallaActual = "registro_seleccion" }
            )
            "registro_seleccion" -> RegisterSelectionScreen(
                onBack = { pantallaActual = "login" },
                onSelectEstudiante = { pantallaActual = "registro_estudiante" },
                onSelectChofer = { pantallaActual = "registro_chofer" }
            )
            "registro_estudiante" -> RegisterEstudianteScreen(
                onBack = { pantallaActual = "registro_seleccion" },
                onRegisterSuccess = { pantallaActual = "login" }
            )
            "registro_chofer" -> RegisterChoferScreen(
                onBack = { pantallaActual = "registro_seleccion" },
                onRegisterSuccess = { pantallaActual = "login" }
            )
            "admin" -> HomeAdminScreen(
                onGestionarChoferes = { pantallaActual = "lista_choferes" },
                onGestionarHorarios = { pantallaActual = "gestionar_horarios" }
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
            "chofer" -> HomeChoferScreen()
            "estudiante" -> HomeEstudianteScreen()
        }
    }
}
