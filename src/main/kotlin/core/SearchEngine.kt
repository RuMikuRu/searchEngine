package org.example.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.example.core.extensions.startRepeatingJob
import java.sql.Connection
import java.sql.DriverManager

class SearchEngine {

    companion object {
        private const val TYPE = "TABLE"
        private const val TABLE_NAME_PATTERN = "%"
        private const val TABLE_NAME = "TABLE_NAME"
    }

    val storage: Storage = Storage

    suspend fun run(urlDataBase: String, userDataBase: String, passwordDataBase: String, coroutineScope:CoroutineScope): SearchEngine {
        DriverManager.getConnection(urlDataBase, userDataBase, passwordDataBase).use { connection ->
            val meta = connection.metaData
            val tables = meta.getTables(null, null, TABLE_NAME_PATTERN, arrayOf(TYPE))

            while (tables.next()) {
                val tableName = tables.getString(TABLE_NAME)
                val tableData = getTableData(connection, tableName)
                storage.addData(tableName, tableData)
            }
        }

        coroutineScope.launch(Dispatchers.IO) {
            Indexing.run(DriverManager.getConnection(urlDataBase, userDataBase, passwordDataBase))
        }

        storage.getDataMap().forEach { (tableName, colums) ->
            println("Table: $tableName")

            colums.forEach { (columnName, values) ->
                println("  Column: $columnName -> $values")
            }
        }

        return this
    }

    private fun getTableData(conn: Connection, tableName: String): HashMap<String, List<String>> {
        val columnData: HashMap<String, MutableList<String>> = hashMapOf()

        try {
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("SELECT * FROM \"$tableName\"")

            val meta = rs.metaData
            val columnCount = meta.columnCount

            for (i in 1..columnCount) {
                val columnName = meta.getColumnName(i)
                columnData[columnName] = mutableListOf()
            }

            while (rs.next()) {
                for (i in 1..columnCount) {
                    val columnName = meta.getColumnName(i)
                    val value = rs.getString(i) ?: "NULL"
                    columnData[columnName]?.add(value)
                }
            }
            return columnData.mapValues { it.value.toList() } as HashMap<String, List<String>>
        } catch (e: Exception) {
            e.printStackTrace()
            return hashMapOf()
        }
    }
}

object Indexing {

    private var lastIds: HashMap<String, Long> = hashMapOf()

    suspend fun run(conn: Connection) = coroutineScope {
        val tablesName = Storage.getDataMap().keys
        tablesName.forEach { tableName ->
            val maxId = getMaxId(conn, tableName)
            if (maxId != null) lastIds[tableName] = maxId
        }
        startRepeatingJob(10000) {
            startIndextor(conn, lastIds)
        }
    }

    private fun startIndextor(conn: Connection, lastIds: HashMap<String, Long>) {
        println("Start Indexing")
        lastIds.forEach { (table, lastId) ->
            val newData = getNewRows(conn, table, lastId)
            if (newData.isNotEmpty()) {
                newData.forEach { (col, values) ->
                    val existing = Storage.getDataMap()[table]?.get(col)?.toMutableList() ?: mutableListOf()
                    existing.addAll(values)
                    Storage.addData(table, hashMapOf(col to existing))
                }
                val newMaxId = getMaxId(conn, table)
                if (newMaxId != null && newMaxId > lastId) {
                    lastIds[table] = newMaxId
                }
                println(" MAX ID: $table -> $newMaxId")
                println("  Updated $table with ${newData.values.firstOrNull() ?: 0} new rows")
            }
        }
    }

    private fun getNewRows(conn: Connection, tableName: String, lastId: Long): HashMap<String, List<String>> {
        println("Get new rows $tableName, $lastId")
        val columnData: HashMap<String, MutableList<String>> = hashMapOf()
        val stmt = conn.prepareStatement("SELECT * FROM \"$tableName\" WHERE id > ?")
        stmt.setLong(1, lastId)
        val rs = stmt.executeQuery()

        val meta = rs.metaData
        val columnCount = meta.columnCount

        for (i in 1..columnCount) {
            val columnName = meta.getColumnName(i)
            println("   columnName  $columnName")
            columnData[columnName] = mutableListOf()
        }

        while (rs.next()) {
            for (i in 1..columnCount) {
                println("tyt ${rs.getString(i)}")
                val columnName = meta.getColumnName(i)
                val value = rs.getString(i) ?: "NULL"
                println("  $columnName -> $value")
                columnData[columnName]?.add(value)
            }
        }

        return columnData.mapValues { it.value.toList() } as HashMap<String, List<String>>
    }

    private fun getMaxId(conn: Connection, tableName: String): Long? {
        val stmt = conn.createStatement()
        try {

            val rs = stmt.executeQuery("SELECT MAX(id) FROM \"$tableName\"")
            return if (rs.next()) rs.getLong(1) else null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}