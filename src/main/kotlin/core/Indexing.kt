package org.example.core


import org.example.core.model.DataSource
import org.example.core.model.IndexStorage
import org.example.core.model.Record
import java.time.Instant

class IncrementalIndexer(
    private val dataSource: DataSource,
    private val indexStorage: IndexStorage
) {
    fun performFullIndexing() {
        val records = dataSource.getUpdatedRecords(null)
        processRecords(records)
        indexStorage.commit()
    }

    fun performIncrementalIndexing() {
        val lastIndexed = dataSource.getLastIndexedTimestamp()
        val records = dataSource.getUpdatedRecords(lastIndexed)
        processRecords(records)
        indexStorage.commit()
    }

    private fun processRecords(records: List<org.example.core.model.Record>) {
        records.forEach { record ->
            when {
                record.isDeleted -> indexStorage.removeDocument(record.id)
                indexExists(record.id) -> indexStorage.updateDocument(record.id, record.content)
                else -> indexStorage.indexDocument(record.id, record.content)
            }
        }
    }

    private fun indexExists(id: String): Boolean {
        // Реализация проверки существования документа в индексе
        return false
    }
}

class SQLDataSource(private val connection: java.sql.Connection) : DataSource {
    override fun getLastIndexedTimestamp(): Instant? {
        val sql = "SELECT MAX(last_indexed) FROM index_metadata"
        connection.prepareStatement(sql).use { stmt ->
            val rs = stmt.executeQuery()
            try {
                return if (rs.next()) rs.getTimestamp(1).toInstant() else null
            } catch (e:Exception){
                return throw java.lang.Exception(e)
            }
        }
    }

    override fun getUpdatedRecords(since: Instant?): List<Record> {
        val sql = """
            SELECT id, content, last_modified, is_deleted
            FROM records
            WHERE last_modified > ? OR ? IS NULL
        """.trimIndent()

        return connection.prepareStatement(sql).use { stmt ->
            stmt.setTimestamp(1, since?.let { java.sql.Timestamp.from(it) })
            stmt.setTimestamp(2, since?.let { java.sql.Timestamp.from(it) })
            val rs = stmt.executeQuery()

            val results = mutableListOf<Record>()
            while (rs.next()) {
                results.add(
                    Record(
                        id = rs.getString("id"),
                        content = parseContent(rs.getString("content")),
                        lastModified = rs.getTimestamp("last_modified").toInstant(),
                        isDeleted = rs.getBoolean("is_deleted")
                    )
                )
            }
            results
        }
    }

    private fun parseContent(content: String): Map<String, Any> {
        // Парсинг содержимого документа (например, из JSON)
        return mutableMapOf()
    }
}