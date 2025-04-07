package org.example.core.DataBaseType

import org.example.core.BaseSearchEngine
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect
import kotlin.or

class PostgresSearchEngine(db: Database) : BaseSearchEngine(db) {
    override fun search(query: String): List<ResultRow> = transaction(db) {
        getTextColumns()
            .groupBy { it.table }
            .flatMap { (table, columns) ->
                val searchTerm = "%${query.trim().lowercase()}%"

                val conditions = columns.map { col ->
                    Op.build { CustomFunction("LOWER", TextColumnType(), col) like searchTerm }
                }

                val combinedCondition = conditions.takeIf { it.isNotEmpty() }?.reduce { acc, condition -> acc or condition }

                //TODO возможно не рабочий вариант
                combinedCondition?.let {
                    table.selectAll().filter { it -> it.hasValue(combinedCondition) == true }
                } ?: emptyList()
            }
    }
}