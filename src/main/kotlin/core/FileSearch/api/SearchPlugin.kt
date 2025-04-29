package org.example.core.FileSearch.api

import java.nio.file.Path

interface SearchPlugin {
    suspend fun match(file: Path, query: String): Boolean
}