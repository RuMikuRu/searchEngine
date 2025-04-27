package org.example.core.utils

import org.example.core.FileSearch.algorithm.LevenshteinFuzzyMatcher
import java.nio.file.Files
import java.nio.file.Path

object FileContentScanner {
    fun containsText(
        path: Path,
        query: String,
        useFuzzy: Boolean = false,
        maxDistance: Int = 2
    ): Boolean {
        return try {
            Files.newBufferedReader(path).useLines { lines ->
                lines.any { line ->
                    if (useFuzzy) {
                        containsFuzzy(line, query, maxDistance)
                    } else {
                        line.contains(query, ignoreCase = true)
                    }
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun containsFuzzy(line: String, query: String, maxDistance: Int): Boolean {
        val words = line.split(Regex("\\W+")) // разделяем по словам
        return words.any { word ->
            LevenshteinFuzzyMatcher.match(word.lowercase(), query.lowercase()) <= maxDistance
        }
    }
}