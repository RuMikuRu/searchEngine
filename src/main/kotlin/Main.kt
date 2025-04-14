package org.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.example.core.SearchBuilder


fun main() = runBlocking<Unit> {
    SearchBuilder().apply {
        urlDataBase = "jdbc:postgresql://77.221.159.98:5430/postgres_db"
        userDataBase = "postgres_user"
        passwordDataBase = "postgres_password"
        timeIndexing = 1000
        coroutineScope = this@runBlocking
    }.build()
    delay(1_000_000)
}