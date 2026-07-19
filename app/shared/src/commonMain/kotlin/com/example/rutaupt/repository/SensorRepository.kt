package com.example.rutaupt.repository

import com.example.rutaupt.model.Ubicacion
import kotlinx.coroutines.flow.Flow

interface SensorRepository {
    fun getUbicacionActual(): Flow<Ubicacion>
    fun iniciarSeguimiento()
    fun detenerSeguimiento()
}
