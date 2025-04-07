package org.example

import org.example.core.SearchBuilder


fun main() {
    SearchBuilder().apply {
        urlDataBase = ""
        userDataBase = ""
        passwordDataBase = ""
        timeIndexing = 1000
    }.build()
}