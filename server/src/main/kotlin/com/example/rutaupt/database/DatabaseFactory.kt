package com.example.rutaupt.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

// 1. DEFINICIÓN DE LAS 6 TABLAS EN KOTLIN (EXPOSED)

object Usuarios : Table("usuarios") {
    val id = integer("id").autoIncrement()
    val nombre = varchar("nombre", 100)
    val apellidos = varchar("apellidos", 100)
    val email = varchar("email", 100).uniqueIndex()
    val password = varchar("password", 255)
    val rol = varchar("rol", 20) // 'admin', 'estudiante', 'chofer'
    val numeroUnidad = varchar("numero_unidad", 20).nullable()
    override val primaryKey = PrimaryKey(id)
}

object Rutas : Table("rutas") {
    val idRuta = integer("id_ruta").autoIncrement()
    val nombreRuta = varchar("nombre_ruta", 100)
    val color = varchar("color", 7)
    val activa = bool("activa")
    override val primaryKey = PrimaryKey(idRuta)
}

object Paradas : Table("paradas") {
    val idParada = integer("id_parada").autoIncrement()
    val idRuta = integer("id_ruta").references(Rutas.idRuta)
    val nombre = varchar("nombre", 100)
    val latitud = decimal("latitud", 10, 8)
    val longitud = decimal("longitud", 11, 8)
    val orden = integer("orden")
    override val primaryKey = PrimaryKey(idParada)
}

object Horarios : Table("horarios") {
    val idHorario = integer("id_horario").autoIncrement()
    val idRuta = integer("id_ruta").references(Rutas.idRuta)
    val idUsuarioChofer = integer("id_usuario_chofer").references(Usuarios.id)
    val horaSalida = varchar("hora_salida", 8) // Formato HH:mm:ss
    val dias = varchar("dias", 50)
    override val primaryKey = PrimaryKey(idHorario)
}

object Reportes : Table("reportes") {
    val id = long("id").autoIncrement()
    val unidad = varchar("unidad", 20)
    val mensaje = text("mensaje")
    val fechaHora = varchar("fecha_hora", 30) 
    val tipo = varchar("tipo", 20) // 'ALERTA' o 'INFORMACION'
    val imagen = text("imagen") // Soporta el LONGTEXT en Base64
    val estado = varchar("estado", 50)
    val validacionAdmin = varchar("validacion_admin", 20).nullable()
    override val primaryKey = PrimaryKey(id)
}

object UbicacionesTiempoReal : Table("ubicaciones_tiempo_real") {
    val numeroUnidad = varchar("numero_unidad", 20)
    val latitud = decimal("latitud", 10, 8)
    val longitud = decimal("longitud", 11, 8)
    val ultimaActualizacion = varchar("ultima_actualizacion", 30)
    override val primaryKey = PrimaryKey(numeroUnidad)
}

// 2. CONEXIÓN AUTOMÁTICA Y CREACIÓN
object DatabaseFactory {
    fun init() {
        val host = System.getenv("MYSQLHOST") ?: "localhost"
        val port = System.getenv("MYSQLPORT") ?: "3306"
        val dbName = System.getenv("MYSQLDATABASE") ?: "railway"
        val user = System.getenv("MYSQLUSER") ?: "root"
        val password = System.getenv("MYSQLPASSWORD") ?: ""

        val config = HikariConfig().apply {
            driverClassName = "com.mysql.cj.jdbc.Driver"
            jdbcUrl = "jdbc:mysql://$host:$port/$dbName?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
            username = user
            this.password = password
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(
                Usuarios, Rutas, Paradas, Horarios, Reportes, UbicacionesTiempoReal
            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
