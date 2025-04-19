package org.example.core.FileSearch.indexing

import org.example.core.FileSearch.SearchResult
import java.nio.file.Path

object IndexManager {

    fun listIndexedPaths(): List<Path> = IndexCache.getAllPaths()

    fun getIndexFor(path: Path): List<SearchResult>? = IndexCache.getIndex(path)

    fun forceReindex(path: Path): List<SearchResult> {
        val newIndex = FileSystemIndexer().buildIndex(path)
        IndexCache.putIndex(path, newIndex)
        return newIndex
    }

    fun removeFromCache(path: Path) {
        IndexCache.remove(path)
    }

    fun clearAll() {
        IndexCache.clear()
    }
}