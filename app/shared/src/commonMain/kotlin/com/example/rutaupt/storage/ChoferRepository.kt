package com.example.rutaupt.storage

import androidx.compose.runtime.mutableStateListOf
import com.example.rutaupt.model.Chofer

object ChoferRepository {

    val choferes = mutableStateListOf<Chofer>()

    fun eliminarChofer(id: Int) {
        choferes.removeAll { it.id == id }
    }

}