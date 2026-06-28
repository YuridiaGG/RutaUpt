package com.example.rutaupt.database

import org.jetbrains.exposed.sql.Table

object RutasTable : Table("rutas") {
    val id = integer("id_ruta").autoIncrement()
    val nombre = varchar("nombre_ruta", 100)
    val color = varchar("color", 7)
    val activa = bool("activa").default(true)

    override val primaryKey = PrimaryKey(id)
}

object ReportesTable : Table("reportes") {
    val id = long("id").autoIncrement()
    val unidad = varchar("unidad", 20)
    val mensaje = text("mensaje")
    val tiempo = varchar("tiempo", 50)
    val tipo = varchar("tipo", 20) // ALERTA, INFORMACION
    val imagen = text("imagen").nullable()
    val estado = varchar("estado", 50).nullable()
    val validacionAdmin = varchar("validacion_admin", 20).nullable()

    override val primaryKey = PrimaryKey(id)
}
