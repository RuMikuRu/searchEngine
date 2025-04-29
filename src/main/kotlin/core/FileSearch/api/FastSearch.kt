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
    private val options: SearchOptions,
    private val plugins: List<SearchPlugin> // передаем плагины
) {
    private val fileSearcher = FileSearchEngine().apply {
        plugins.forEach { registerPlugin(it) } // регистрируем
    }

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
                    includeFiles = builder.includeFiles,
                    fuzzySearch = builder.fuzzySearch,
                    customContentMatcher = builder.customContentMatcher
                ),
                plugins = builder.plugins
            )
        }
    }

    class Builder {
        var path: String? = null
        var mask: String? = null
        var content: String? = null
        var includeDirs: Boolean = false
        var includeFiles: Boolean = true
        var fuzzySearch = false
        var customContentMatcher: (suspend (Path, String) -> Boolean)? = null

        internal val plugins = mutableListOf<SearchPlugin>() // список плагинов

        fun plugins(configure: MutableList<SearchPlugin>.() -> Unit) {
            plugins.configure()
        }
    }

    operator fun MutableList<SearchPlugin>.plusAssign(plugin: SearchPlugin) {
        add(plugin)
    }
}