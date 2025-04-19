package org.example.core.FileSearch.indexing

import org.example.core.FileSearch.SearchResult
import java.nio.file.Path

interface Indexer {
    fun buildIndex(rootDir: Path): List<SearchResult>
}