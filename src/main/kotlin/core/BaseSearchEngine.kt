package org.example.core

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface SearchEngine {
    fun search(query:String):List<ResultRow>
}

abstract class BaseSearchEngine(protected val db: Database):SearchEngine {
    protected fun getTextColumns():List<Column<String>> {
        return transaction(db) {
            db.dialect.allTablesNames().flatMap { tableName ->
                val table = Table(tableName)
                table.columns.filter { it.columnType.sqlType().contains("TEXT", ignoreCase = true) }
            } as List<Column<String>>
        }
    }
}