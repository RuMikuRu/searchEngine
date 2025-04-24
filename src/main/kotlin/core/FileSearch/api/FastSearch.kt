package org.example.core.FileSearch.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.example.core.FileSearch.FileSearchEngine
import org.example.core.FileSearch.SearchOptions
import org.example.core.FileSearch.SearchResult
import org.example.core.utils.FileUtils
import java.nio.file.Path

class FastSearch private constructor(
    private val root: Path,
    private val options: SearchOptions
) {
    private val fileSearcher = FileSearchEngine()

    /** Executes the search asynchronously and returns matching results. */
    fun run(): Flow<SearchResult> = runBlocking {
        fileSearcher.search(root, options)
    }

    companion object {
        /** DSL-style configuration block */
        operator fun invoke(configure: Builder.() -> Unit): FastSearch {
            val builder = Builder().apply(configure)
            return FastSearch(
                root = FileUtils.resolvePath(builder.path ?: error("path is required")),
                options = SearchOptions(
                    namePattern = builder.mask?.let { FileUtils.wildcardToRegex(it) },
                    contentQuery = builder.content,
                    includeDirs = builder.includeDirs,
                    includeFiles = builder.includeFiles
                )
            )
        }
    }

    class Builder {
        var path: String? = null
        var mask: String? = null
        var content: String? = null
        var includeDirs: Boolean = false
        var includeFiles: Boolean = true
    }
}