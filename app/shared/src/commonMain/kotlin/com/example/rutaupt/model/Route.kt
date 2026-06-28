package com.example.rutaupt.model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id_ruta: Int,
    val nombre_ruta: String,
    val color: String,
    val activa: Boolean
)
