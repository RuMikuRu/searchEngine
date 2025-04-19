package org.example.core.FileSearch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.example.core.FileSearch.indexing.FileSystemIndexer
import org.example.core.FileSearch.indexing.IndexCache
import org.example.core.FileSearch.indexing.WatchServiceManager
import org.example.core.utils.FileContentScanner
import java.nio.file.Path

class FileSearchEngine {
    private val indexer = FileSystemIndexer()

    suspend fun search(rootDir: Path, options: SearchOptions): List<SearchResult> = coroutineScope {
        // Следим за изменениями и пересобираем кэш при необходимости
        WatchServiceManager.startWatching(rootDir) { changedPath ->
            launch {
                println("Filesystem change detected in: $changedPath")
                IndexCache.putIndex(rootDir, indexer.buildIndex(rootDir))
                println("Index rebuilt for: $rootDir")
            }
        }

        val index = IndexCache.getIndex(rootDir) ?: indexer.buildIndex(rootDir).also {
            IndexCache.putIndex(rootDir, it)
        }

        index.map { result ->
            async(Dispatchers.Default) {
                val nameMatches = options.namePattern?.matches(Path.of(result.path).fileName.toString()) != false
                val contentMatches = options.contentQuery?.let { query ->
                    !result.isDirectory && FileContentScanner.containsText(Path.of(result.path), query)
                } != false

                if (nameMatches && contentMatches) result else null
            }
        }.awaitAll().filterNotNull()
    }
}