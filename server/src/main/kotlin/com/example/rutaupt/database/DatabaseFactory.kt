package com.example.rutaupt.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    fun init() {
        val host = System.getenv("MYSQLHOST") ?: "localhost"
        val port = System.getenv("MYSQLPORT") ?: "3306"
        val dbName = System.getenv("MYSQLDATABASE") ?: "railway"
        val user = System.getenv("MYSQLUSER") ?: "root"
        val password = System.getenv("MYSQLPASSWORD") ?: ""

        val jdbcUrl = "jdbc:mysql://$host:$port/$dbName?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
        
        try {
            val config = HikariConfig().apply {
                driverClassName = "com.mysql.cj.jdbc.Driver"
                this.jdbcUrl = jdbcUrl
                this.username = user
                this.password = password
                maximumPoolSize = 10
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }

            val dataSource = HikariDataSource(config)
            Database.connect(dataSource)

            transaction {
                SchemaUtils.create(Usuarios, Rutas, Paradas, Horarios, Reportes, UbicacionesTiempoReal)
                
                // Seed Usuarios Iniciales (Solo si no existen)
                if (Usuarios.selectAll().where { Usuarios.id eq 1 }.empty()) {
                    Usuarios.insert {
                        it[id] = 1
                        it[nombre] = "Administrador"
                        it[apellidos] = "Sistema"
                        it[email] = "admin@upt.edu.mx"
                        it[Usuarios.password] = "123"
                        it[rol] = "admin"
                    }
                }
                if (Usuarios.selectAll().where { Usuarios.id eq 2 }.empty()) {
                    Usuarios.insert {
                        it[id] = 2
                        it[nombre] = "Chofer Demo"
                        it[apellidos] = "Sistema"
                        it[email] = "chofer@upt.edu.mx"
                        it[Usuarios.password] = "123"
                        it[rol] = "chofer"
                        it[numeroUnidad] = "UPT-05"
                    }
                }
                if (Usuarios.selectAll().where { Usuarios.id eq 3 }.empty()) {
                    Usuarios.insert {
                        it[id] = 3
                        it[nombre] = "Estudiante Demo"
                        it[apellidos] = "Sistema"
                        it[email] = "estudiante@upt.edu.mx"
                        it[Usuarios.password] = "123"
                        it[rol] = "estudiante"
                    }
                }
            }
            logger.info("Base de datos MySQL conectada y usuarios demo verificados.")
        } catch (e: Exception) {
            logger.error("Error al conectar con MySQL: ${e.message}")
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
