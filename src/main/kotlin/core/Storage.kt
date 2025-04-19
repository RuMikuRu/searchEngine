package org.example.core

import org.example.core.Storage.SearchResult
import kotlin.collections.component1
import kotlin.collections.component2

object Storage {
    private val dataTableMap: HashMap<String, HashMap<String, List<String>>> = hashMapOf()

    fun addData(tableName: String, tableData: HashMap<String, List<String>>) {
        dataTableMap[tableName] = tableData
    }

    fun getDataMap() = dataTableMap

    data class SearchResult(val table: String, val column: String, val value: String)
}

fun search(
    query: String,
    tableFilter: String? = null,
    columnFilter: String? = null
): List<SearchResult> {
    val results = mutableListOf<SearchResult>()
    Storage.getDataMap().forEach { (table, columns) ->
        if (tableFilter != null && table != tableFilter) return@forEach
        columns.forEach { (col, values) ->
            if (columnFilter != null && col != columnFilter) return@forEach
            values.forEach { value ->
                if (value.contains(query, ignoreCase = true)) {
                    results.add(SearchResult(table, col, value))
                }
            }
        }
    }
    return results
}