package com.example.rutaupt.model

data class Chofer(
    val id: Int,
    val nombre: String,
    val apellidos: String,
    val edad: String,
    val telefono: String,
    val numeroUnidad: String,
    val email: String,
    val contrasena: String,
    var horario: String = "A.M." // "A.M." o "P.M."
)
