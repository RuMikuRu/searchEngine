package org.example.core.model

import java.time.Instant

interface DataSource {
    fun getLastIndexedTimestamp(): java.time.Instant?
    fun getUpdatedRecords(since: Instant?): List<Record>
}

interface IndexStorage {
    fun indexDocument(id: String, content: Map<String, Any>)
    fun updateDocument(id: String, content: Map<String, Any>)
    fun removeDocument(id: String)
    fun commit()
    fun existsDocument(id: String): Boolean
}

// 2. Модель данных
data class Record(
    val id: String,
    val content: Map<String, Any>,
    val lastModified: java.time.Instant,
    val isDeleted: Boolean = false
)
