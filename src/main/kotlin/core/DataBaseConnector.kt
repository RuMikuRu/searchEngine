package org.example.core

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.example.core.DataBaseType.PostgresSearchEngine
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.vendors.MysqlDialect
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect
import org.jetbrains.exposed.sql.vendors.SQLiteDialect

object DatabaseFactory {
    fun create(db: Database): SearchEngine {
        return when (db.vendor) {
            PostgreSQLDialect.toString() -> PostgresSearchEngine(db)
            //MysqlDialect -> MySQLSearchEngine(db)
            //SQLiteDialect -> SQLiteSearchEngine(db)
            else -> throw UnsupportedOperationException("Unsupported database: ${db.vendor}")
        }
    }
}