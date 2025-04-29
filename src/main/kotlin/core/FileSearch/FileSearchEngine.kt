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
import org.example.core.FileSearch.api.SearchPlugin
import org.example.core.FileSearch.indexing.FileSystemIndexer
import org.example.core.FileSearch.indexing.IndexCache
import org.example.core.FileSearch.indexing.WatchServiceManager
import org.example.core.utils.FileContentScanner
import java.nio.file.Path

class FileSearchEngine {
    private val indexer = FileSystemIndexer()
    private val plugins = mutableListOf<SearchPlugin>()

    fun registerPlugin(plugin: SearchPlugin) {
        plugins.add(plugin)
    }

    @OptIn(FlowPreview::class)
    suspend fun search(rootDir: Path, options: SearchOptions): Flow<SearchResult> = coroutineScope {
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
                    if (result.isDirectory) {
                        false
                    } else {
                        when {
                            options.customContentMatcher != null -> {
                                options.customContentMatcher.invoke(Path.of(result.path), query)
                            }
                            plugins.isNotEmpty() -> {
                                plugins.any { it.match(Path.of(result.path), query) }
                            }
                            options.fuzzySearch -> {
                                FileContentScanner.containsText(
                                    path = Path.of(result.path),
                                    query = query,
                                    useFuzzy = true,
                                    maxDistance = 3
                                )
                            }
                            else -> {
                                FileContentScanner.containsText(
                                    path = Path.of(result.path),
                                    query = query,
                                    useFuzzy = false
                                )
                            }
                        }
                    }
                } != false

                if (nameMatches && contentMatches) result else null
            }.asFlow()
        }
    }
}