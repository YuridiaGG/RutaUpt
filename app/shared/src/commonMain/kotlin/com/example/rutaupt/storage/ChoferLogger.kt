package com.example.rutaupt.storage

import com.example.rutaupt.model.Chofer

object ChoferLogger {

    private const val TAG = "CHOFER_ENDPOINT"

    fun agregarChofer(chofer: Chofer) {
        ChoferRepository.choferes.add(chofer)
        println("[$TAG] ✔ Chofer agregado correctamente")
        println("[$TAG] Nombre: ${chofer.nombre}")
        println("[$TAG] Total de choferes: ${ChoferRepository.choferes.size}")
    }

    fun eliminarChofer(chofer: Chofer) {
        ChoferRepository.choferes.remove(chofer)
        println("[$TAG] ✖ Chofer eliminado")
        println("[$TAG] Nombre: ${chofer.nombre}")
        println("[$TAG] Total de choferes: ${ChoferRepository.choferes.size}")
    }

    fun mostrarChoferes() {
        println("[$TAG] ===== LISTA DE CHOFERES =====")
        if (ChoferRepository.choferes.isEmpty()) {
            println("[$TAG] No existen choferes registrados")
        } else {
            ChoferRepository.choferes.forEach {
                println("[$TAG] ${it.id} - ${it.nombre} ${it.apellidos} | Unidad ${it.numeroUnidad}")
            }
        }
        println("[$TAG] ===========================")
    }
}
