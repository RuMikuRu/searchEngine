package org.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.example.core.FileSearch.FileSearchEngine
import org.example.core.FileSearch.SearchOptions
import org.example.core.FileSearch.api.FastSearch
import org.example.core.SearchBuilder
import org.example.core.Storage
import org.example.core.search
import org.example.core.utils.FileUtils


fun main() = runBlocking {
    print("Enter directory path: ")
    val dir = readln()

    print("Enter filename pattern (e.g. *.txt): ")
    val namePatternInput = readln().takeIf { it.isNotBlank() }

    print("Enter content search text (or leave blank): ")
    val contentQuery = readln().takeIf { it.isNotBlank() }

    val options = SearchOptions(
        namePattern = namePatternInput?.let { FileUtils.wildcardToRegex(it) },
        contentQuery = contentQuery,
        includeDirs = false,
        includeFiles = true
    )

    val searcher = FileSearchEngine()
    val results = searcher.search(FileUtils.resolvePath(dir), options)

    println("\nSearch results:")
    results.forEach {
        val type = if (it.isDirectory) "DIR " else "FILE"
        println("[$type] ${it.path}")
    }

    val result = FastSearch {
        path = dir
        mask = namePatternInput
        content = contentQuery
    }.run()
}