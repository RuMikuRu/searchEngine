package org.example.core.FileSearch

import java.nio.file.Path


data class SearchOptions(
    val namePattern: Regex? = null,
    val contentQuery: String? = null,
    val includeDirs: Boolean = true,
    val includeFiles: Boolean = true,
    val fuzzySearch: Boolean = true,
    val customContentMatcher: (suspend (Path, String) -> Boolean)? = null
)