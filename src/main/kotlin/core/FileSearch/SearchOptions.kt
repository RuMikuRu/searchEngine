package org.example.core.FileSearch


data class SearchOptions(
    val namePattern: Regex? = null,
    val contentQuery: String? = null,
    val includeDirs: Boolean = true,
    val includeFiles: Boolean = true,
)