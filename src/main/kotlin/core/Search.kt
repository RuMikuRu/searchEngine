package org.example.core

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import java.util.concurrent.TimeUnit

class SearchBuilder {
    var urlDataBase: String? = null
    var userDataBase: String? = null
    var passwordDataBase: String? = null
    var timeIndexing: Long = 5000

    private lateinit var dataBase:DatabaseFactory

    fun setUrldDataBase(url: String) = apply {
        urlDataBase = url
    }

    fun setUserDataBase(username: String) = apply {
        userDataBase = username
    }

    fun setPasswordDataBase(password: String) = apply {
        passwordDataBase = password
    }

    fun setTimeIndexing(time: Long) = apply {
        timeIndexing = time
    }

    fun build() {
        run()
    }

    private fun run() = runBlocking {
        if (!urlDataBase.isNullOrBlank() && !userDataBase.isNullOrBlank() && !passwordDataBase.isNullOrBlank()) {
            // TODO добавить поддержку других баз данных для подключения
            DatabaseFactory.create(Database.connect(url = "jdbc:postgresql://localhost:5432/$urlDataBase", user = userDataBase!!, password = passwordDataBase!!))
        } else {
            throw IllegalArgumentException("")
        }
    }
}

enum class DriverSelect {
    POSTGRES,
    MARIADB,
    SQLITE
}