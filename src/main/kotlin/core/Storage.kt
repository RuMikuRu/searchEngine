import org.example.core.model.IndexStorage
import java.util.concurrent.ConcurrentHashMap

class InMemoryIndexStorage : IndexStorage {
    private val storage = ConcurrentHashMap<String, Map<String, Any>>()
    private val deletedIds = mutableSetOf<String>()

    // Для мониторинга и отладки
    private var indexedCount = 0
    private var updatedCount = 0
    private var deletedCount = 0

    override fun indexDocument(id: String, content: Map<String, Any>) {
        require(!existsDocument(id)) { "Document $id already exists" }
        storage[id] = content
        indexedCount++
        deletedIds.remove(id)
    }

    override fun updateDocument(id: String, content: Map<String, Any>) {
        check(existsDocument(id)) { "Document $id not found" }
        storage[id] = storage[id]!! + content
        updatedCount++
    }

    override fun removeDocument(id: String) {
        if (storage.remove(id) != null) {
            deletedCount++
            deletedIds.add(id)
        }
    }

    override fun commit() {
        // Операция фиксации не требуется для in-memory хранилища
        // Можно добавить логирование статистики
        println("""
            Indexing stats:
            - Total documents: ${storage.size}
            - New indexed: $indexedCount
            - Updated: $updatedCount
            - Deleted: $deletedCount
        """.trimIndent())

        // Сброс счетчиков
        indexedCount = 0
        updatedCount = 0
        deletedCount = 0
    }

    override fun existsDocument(id: String): Boolean {
        return storage.containsKey(id) && !deletedIds.contains(id)
    }

    // Дополнительные методы для доступа к данным
    fun getDocument(id: String): Map<String, Any>? = storage[id]

    fun getAllDocuments(): Map<String, Map<String, Any>> = storage.toMap()

    fun clear() {
        storage.clear()
        deletedIds.clear()
        indexedCount = 0
        updatedCount = 0
        deletedCount = 0
    }
}