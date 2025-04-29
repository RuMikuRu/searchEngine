package org.example

import kotlinx.coroutines.runBlocking
import org.example.core.FileSearch.FileSearchEngine
import org.example.core.FileSearch.api.FastSearch
import org.example.core.FileSearch.api.SearchPlugin
import java.nio.file.Files
import java.nio.file.Path


fun main() = runBlocking {
    val results = FastSearch {
        path = "C:\\Users\\bkmzo\\Documents\\kotlin\\test1\\app"
        content = "plugins"
        includeFiles = true
        includeDirs = true
        plugins {
            add(object : SearchPlugin {
                override suspend fun match(file: Path, query: String): Boolean {
                    // Допустим, ищем в первых 5КБ файла
                    return try {
                        Files.newInputStream(file).buffered().reader().use {
                            val buffer = CharArray(5000)
                            val read = it.read(buffer)
                            val text = String(buffer, 0, read)
                            text.contains(query, ignoreCase = true)
                        }
                    } catch (e: Exception) {
                        false
                    }
                }
            })
        }
    }.run().collect {
        println(it.path)
    }
}