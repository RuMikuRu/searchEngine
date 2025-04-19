package org.example.core.FileSearch.indexing

import org.example.core.FileSearch.SearchResult
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

class FileSystemIndexer : Indexer {
    override fun buildIndex(rootDir: Path): List<SearchResult> {
        val results = mutableListOf<SearchResult>()

        if (!Files.exists(rootDir)) return results

        Files.walk(rootDir).use { stream ->
            stream.forEach { path ->
                if (path.isRegularFile() || path.isDirectory()) {
                    results += SearchResult(path.toString(), path.isDirectory())
                }
            }
        }

        return results
    }
}