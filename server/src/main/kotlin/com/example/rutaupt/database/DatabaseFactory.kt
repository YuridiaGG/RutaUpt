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
        val host = System.getenv("MYSQLHOST")
        val port = System.getenv("MYSQLPORT")
        val dbName = System.getenv("MYSQLDATABASE")
        val user = System.getenv("MYSQLUSER")
        val password = System.getenv("MYSQLPASSWORD")

        if (host == null) {
            logger.warn("MYSQLHOST no detectado. Saltando inicialización de BD para evitar crash.")
            return
        }

        val jdbcUrl = "jdbc:mysql://$host:$port/$dbName?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
        
        try {
            val config = HikariConfig().apply {
                driverClassName = "com.mysql.cj.jdbc.Driver"
                this.jdbcUrl = jdbcUrl
                this.username = user
                this.password = password
                maximumPoolSize = 3
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }

            val dataSource = HikariDataSource(config)
            Database.connect(dataSource)

            transaction {
                SchemaUtils.create(Usuarios, Rutas, Paradas, Horarios, Reportes, UbicacionesTiempoReal)
            }
            logger.info("Base de datos MySQL conectada y tablas listas.")
        } catch (e: Exception) {
            logger.error("Error al conectar con MySQL: ${e.message}. El servidor seguirá intentando arrancar.")
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
