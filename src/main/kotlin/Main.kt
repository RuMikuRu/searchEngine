package org.example

import kotlinx.coroutines.runBlocking
import org.example.core.FileSearch.api.FastSearch


fun main() = runBlocking {
    val results = FastSearch {
        path = "C:\\Users\\bkmzo\\Documents\\kotlin\\test1\\app"
        content = "plugins"
        includeFiles = true
        includeDirs = true
    }.run().collect {
        println(it.path)
    }
}