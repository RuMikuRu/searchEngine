package org.example.core.utils

import java.nio.file.Files
import java.nio.file.Path

object FileContentScanner {
    fun containsText(path: Path, query: String): Boolean {
        return try {
            Files.newBufferedReader(path).useLines { lines ->
                lines.any { it.contains(query, ignoreCase = true) }
            }
        } catch (e: Exception) {
            false
        }
    }
}