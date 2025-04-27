package org.example.core.FileSearch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.example.core.FileSearch.indexing.FileSystemIndexer
import org.example.core.FileSearch.indexing.IndexCache
import org.example.core.FileSearch.indexing.WatchServiceManager
import org.example.core.utils.FileContentScanner
import java.nio.file.Path

class FileSearchEngine {
    private val indexer = FileSystemIndexer()

    @OptIn(FlowPreview::class)
    suspend fun search(rootDir: Path, options: SearchOptions): Flow<SearchResult> = coroutineScope {
        // Следим за изменениями и пересобираем кэш при необходимости
        WatchServiceManager.startWatching(rootDir) {
            launch {
                println("Filesystem change detected in: $it")
                IndexCache.remove(rootDir)
                println("Index rebuilt for: $rootDir")
            }
        }

        flow {
            val index = IndexCache.getIndex(rootDir) ?: indexer.buildIndex(rootDir).also {
                IndexCache.putIndex(rootDir, it)
            }
            emit(index)
        }.flatMapConcat { index ->
            index.mapNotNull { result ->
                val nameMatches = options.namePattern?.matches(Path.of(result.path).fileName.toString()) != false
                val contentMatches = options.contentQuery?.let { query ->
                    !result.isDirectory && FileContentScanner.containsText(Path.of(result.path), query, useFuzzy = true, 3)
                } != false

                if (nameMatches && contentMatches) result else null
            }.asFlow<SearchResult>()
        }
    }
}