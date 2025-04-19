package org.example.core.FileSearch.indexing

import org.example.core.FileSearch.SearchResult
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

object IndexCache {
    private val cache: MutableMap<Path, List<SearchResult>> = ConcurrentHashMap()

    fun getIndex(path: Path): List<SearchResult>? = cache[path]
    fun putIndex(path: Path, index: List<SearchResult>) { cache[path] = index }
    fun remove(path: Path) { cache.remove(path) }
    fun clear() = cache.clear()
    fun getAllPaths(): List<Path> = cache.keys.toList()
}